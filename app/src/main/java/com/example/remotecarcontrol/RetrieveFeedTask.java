package com.example.remotecarcontrol;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Владислав on 09.03.2016.
 */
class RetrieveFeedTask extends Thread{
    private static RetrieveFeedTask retrieveFeedTask = null;
    private byte[] paramsToSend;
    private RetrieveFeedTask(){

    }
    public static RetrieveFeedTask getInstance(){
        return retrieveFeedTask==null? new RetrieveFeedTask():retrieveFeedTask;
    }
    @Override
    public void run(){
        try {
            System.out.println("vse norm!!!!");
            Socket socket = new Socket("172.16.99.101", 4848);
            System.out.println("vse super!!!!");
            if(socket==null||socket.isClosed()){
                System.out.println("nenanfuiwe!!!!!!!");
                return;
            }
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(paramsToSend);
            outputStream.flush();
            outputStream.close();
            socket.close();
            System.out.println("vse norm!!!!");
            System.out.println("sended " + paramsToSend[0]+" "+paramsToSend[1]+" "+paramsToSend[2]+" "+paramsToSend[3]+" "+paramsToSend[4]+" ");
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send(byte[] paramsToSend){
        this.paramsToSend = paramsToSend;
        start();
    }
}
