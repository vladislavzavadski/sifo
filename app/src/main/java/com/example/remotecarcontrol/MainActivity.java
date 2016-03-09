package com.example.remotecarcontrol;

import android.content.Context;
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

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        byte bb = 127;
        int[] bg = new int[10];
        wifiConnect("infolan25645", "32325645");
        sensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  TextView tv = (TextView)findViewById(R.id.name);//1337
                tv.setText("1231231");*/

                RetrieveFeedTask retrieveFeedTask = new RetrieveFeedTask();
                retrieveFeedTask.start();

            }
        });
        sensorEventListener = new SensorEventListener() {
            float[] prevValue = null;
            int step = 10;
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
                    if(prevValue==null){
                        prevValue = new float[3];
                        prevValue[0] = event.values[0];
                        prevValue[1] = event.values[1];
                        prevValue[2] = event.values[2];
                        renewView("X: "+(int)prevValue[0]+"\nY: "+(int)prevValue[1]+"\nZ: "+(int)prevValue[2]);
                    }
                    else {
                        String xAxis, yAxis, zAxis;
                        xAxis = "X: " + ((Math.abs(prevValue[0]-event.values[0])>200) ? event.values[0] : prevValue[0])+"\n";
                        if(Math.abs(prevValue[0]-event.values[0])>step){
                            xAxis = "X: "+(int)event.values[0]+"\n";
                            prevValue[0] = event.values[0];
                        }
                        else{
                            xAxis = "X: "+(int)prevValue[0]+"\n";
                        }

                        if(Math.abs(prevValue[1]-event.values[1])>step){
                            yAxis = "Y: "+(int)event.values[1]+"\n";
                            prevValue[1] = event.values[1];
                        }
                        else{
                            yAxis = "Y: "+(int)prevValue[1]+"\n";
                        }

                        if(Math.abs(prevValue[2]-event.values[2])>step){
                            zAxis = "Z: "+(int)event.values[2]+"\n";
                            prevValue[2] = event.values[2];
                        }
                        else{
                            zAxis = "Z: "+(int)prevValue[2]+"\n";
                        }

                        renewView(xAxis+yAxis+zAxis);

                    }

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //System.out.print("fewf");
            }

            private void renewView(String info){
                TextView tv = (TextView)findViewById(R.id.name);//1337
                tv.setText(info);
            }
        };
        Sensor def = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(sensorEventListener, def, SensorManager.SENSOR_DELAY_GAME);
    }

    private void wifiConnect(String ssid, String key){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        System.out.println(wifiConfig.SSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

    }

}
