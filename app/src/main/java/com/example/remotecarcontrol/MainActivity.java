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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        wifiConnect("infolan25645", "32325645");
        sensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        final Button button = (Button)findViewById(R.id.button);
      /*  button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveFeedTask retrieveFeedTask = new RetrieveFeedTask();
                retrieveFeedTask.start();

            }
        });*/
        final float[] prevValue = new float[3];
       // prevValue[0] = event.values[0];
       // prevValue[1] = event.values[1];
       // prevValue[2] = event.values[2];
        prevValue[0] = 0;
        prevValue[1] = 0;
        prevValue[2] = 0;
        final boolean[] driveEnable = {false};
        sensorEventListener = new SensorEventListener() {

            int step = 5;
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){

                        int xAxis, yAxis, zAxis;
                        boolean needToSend = false;
                        if(driveEnable[0] !=button.isPressed()){
                            driveEnable[0] = button.isPressed();
                            needToSend = true;
                        }
                        if(Math.abs(prevValue[0]-event.values[0])>step){
                            xAxis = (int)event.values[0];
                            prevValue[0] = event.values[0];
                            needToSend = true;
                        }
                        else{
                            xAxis = (int)prevValue[0];
                        }

                        if(Math.abs(prevValue[1]-event.values[1])>step){
                            yAxis = (int)event.values[1];
                            prevValue[1] = event.values[1];
                            needToSend = true;
                        }
                        else{
                            yAxis = (int)prevValue[1];
                        }

                        if(Math.abs(prevValue[2]-event.values[2])>step){
                            zAxis = (int)event.values[2];
                            prevValue[2] = event.values[2];
                            needToSend = true;
                        }
                        else{
                            zAxis = (int)prevValue[2];//z уменьшается от 90 до 45, при 45 поставить максимальную скорость
                        }
                        if(needToSend)
                            renewCarParameters(xAxis, yAxis, zAxis, driveEnable[0]);

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }


        };
        Sensor def = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(sensorEventListener, def, SensorManager.SENSOR_DELAY_GAME);
    }

    private void renewCarParameters(int xAxis, int yAxis, int zAxis, boolean driveEnable){
        TextView tv = (TextView)findViewById(R.id.name);//1337
        byte speed;
        if(driveEnable) {
            speed = convertIntoSpeed(zAxis);
            tv.setText(xAxis+"\n"+yAxis+"\n"+zAxis+"\n speed:" +speed);
            RetrieveFeedTask retrieveFeedTask = RetrieveFeedTask.getInstance();
            byte[] array = new byte[5];
            for (int i = 1; i < array.length; i++) {
                   array[i] = 0;
            }
            array[0] = speed;
            retrieveFeedTask.send(array);
        }
        else{
            byte[] array = new byte[5];
            for(int i=0; i<array.length; i++){
                array[i] = 0;
            }
            RetrieveFeedTask.getInstance().send(array);
        }


    }

    private byte convertIntoSpeed(int angle){
        byte speed = 0;
        if(angle>80){
            speed = 0;
        }
        if(angle<=80&&angle>75){
            speed = 20;
        }
        if(angle<=75&&angle>70){
            speed = 40;
        }
        if(angle<=70&&angle>65){
            speed = 60;
        }
        if(angle<=65&&angle>60){
            speed = 80;
        }
        if(angle<=60&&angle>55){
            speed = 100;
        }
        if(angle<=55){
            speed = 127;
        }
        return speed;
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

}
