package com.example.remotecarcontrol;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Владислав on 09.03.2016.
 */
class RetrieveFeedTask extends Thread{
    @Override
    public void run(){
        try {
            System.out.println("vse norm!!!!");
            Socket socket = new Socket("172.16.99.101", 4848);
            if(socket==null||socket.isClosed()){
                System.out.println("nenanfuiwe!!!!!!!");
                return;
            }
            OutputStream outputStream = socket.getOutputStream();
            byte[] bytes = new byte[2];
            bytes[0] = 52;
            bytes[1] = 53;
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            socket.close();
            System.out.println("vse norm!!!!");
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
