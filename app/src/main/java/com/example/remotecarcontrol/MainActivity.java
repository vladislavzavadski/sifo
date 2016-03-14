package com.example.remotecarcontrol;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private RadioButton radioButtonForward;
    private RadioButton radioButtonBack;
    private SeekBar seekBar;
    private Switch aSwitch;
    private final float[] prevValue = new float[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        wifiConnect("BMW M3 COUPE1", "18271827");
        sensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        aSwitch = (Switch)findViewById(R.id.switch1);
        prevValue[0] = 0;
        prevValue[1] = 0;
        prevValue[2] = 0;
        final boolean[] driveEnable = {false};
        radioButtonForward = (RadioButton)findViewById(R.id.radioButtonForward);
        radioButtonBack  = (RadioButton)findViewById(R.id.radioButtonBack);
        radioButtonForward.setChecked(true);
        radioButtonBack.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                moveCar();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                moveCar();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(0);
            }
        });

        sensorEventListener = new SensorEventListener() {

            int step = 5;
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
                        if(Math.abs(prevValue[1]-event.values[1])>step){
                            prevValue[1] = event.values[1];
                            moveCar();
                        }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }


        };
        Sensor def = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(sensorEventListener, def, SensorManager.SENSOR_DELAY_GAME);
    }


    private byte convertIntoSpeed(int seekValue){

        return (byte) ((byte)seekValue*1.27);
    }

    private byte convertAngleToByte(int angle){
        //max right=-37 max left = 37
        int module = Math.abs(angle)>90?180-Math.abs(angle):Math.abs(angle);
        if(module<=4){
            return 0;
        }
        if(module>30){
            return 127;
        }

        return (byte) (module*4.23);
    }

    private void wifiConnect(String ssid, String key){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        System.out.println(wifiConfig.SSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

    }

    private void moveCar(){
        byte[] array = new byte[8];
        int progress = seekBar.getProgress();
        if(radioButtonForward.isChecked()){
            array[0] = convertIntoSpeed(progress);
            String str = Integer.toBinaryString(array[0]);
            for(int i=0; i<str.length(); i++){
                if(str.charAt(i)=='1'){
                    array[i] = 127;
                }
                else if(str.charAt(i)=='0'){
                    array[i] = 0;
                }
            }
           // array[1] = 0;
        }
        else if(radioButtonBack.isChecked()){
           // array[0] = 0;
            //array[1] = convertIntoSpeed(progress);
        }
        if(prevValue[1]>0){
            //array[2] = convertAngleToByte((int) prevValue[1]);
            //array[3] = 0;
        }
        if(prevValue[1]<0){
            //array[2] = 0;
            //array[3] = convertAngleToByte((int) prevValue[1]);
        }
        //array[4] = (byte) (aSwitch.isChecked()?127:0);//сделать проверку на то включался свет или нет.

        RetrieveFeedTask.getInstance().send(array);
    }

}
