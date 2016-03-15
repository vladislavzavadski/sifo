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
    private volatile Socket socket = null;
    private RetrieveFeedTask(){
        Thread thread = new Thread(){
          @Override
        public void run(){
              try {
                  socket = new Socket("172.16.99.101", 4848);
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static RetrieveFeedTask getInstance(){
        return retrieveFeedTask==null? new RetrieveFeedTask():retrieveFeedTask;
    }
    @Override
    public void run(){
        try {
            synchronized (socket) {
                System.out.println("vse norm!!!!");
                // Socket socket = new Socket("192.168.4.1", 12337);
                System.out.println("vse super!!!!");
                if (socket == null || socket.isClosed()) {
                    System.out.println("nenanfuiwe!!!!!!!");
                    return;
                }
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(paramsToSend);
                outputStream.flush();
                // outputStream.close();
                //socket.close();
                System.out.println("vse norm!!!!");
                for (byte i : paramsToSend) {
                    System.out.print(i + " ");
                }
                System.out.println();
                Thread.sleep(100);
            }
             } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send(byte[] paramsToSend){
        this.paramsToSend = paramsToSend;
        start();
    }
}
