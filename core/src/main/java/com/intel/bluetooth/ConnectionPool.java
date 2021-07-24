package com.intel.bluetooth;

import javax.bluetooth.RemoteDevice;
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

}
