package com.intel.bluetooth.entity;

import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * 2 * @Author: Gosin
 * 3 * @Date: 2021/7/24 15:00
 * 4
 */
public class ConnectionPool {


      HashMap<String, OutputStream> pool = new HashMap<>();

      static ConnectionPool connectionPool;

      public static ConnectionPool getInstance(){
          if(connectionPool==null){
              connectionPool = new ConnectionPool();
          }
          return connectionPool;
      }

      public OutputStream  getOS(String deviceName){
          return pool.get(deviceName);
      }

    public void setOS(String deviceName,OutputStream os){
          pool.put(deviceName,os);
    }

    public void deleteOS(String deviceName){
          pool.remove(deviceName);
    }

    public void writeOut(String deviceName, InputStream inputStream, ProgressBar progressBar){
        OutputStream outputStream = pool.get(deviceName);
        int available = 0;
        try {
            available = inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = new byte[1024*10];
        bytes[0]  = 1;
         int size;
         int sendBytes = 0;
        try {
         while (true){

                 if (!((size = inputStream.read(bytes,1, bytes.length))!=-1)) {
                     break;
                 }
                 outputStream.write(bytes,0,size);
                 sendBytes += size;
                 Double x = Double.valueOf(sendBytes / available);
                 progressBar.setProgress(x);
         }
           progressBar.setProgress(1);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
