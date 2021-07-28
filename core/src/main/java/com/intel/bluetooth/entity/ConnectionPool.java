package com.intel.bluetooth.entity;



import javax.microedition.io.StreamConnection;
import java.io.*;
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

    public ConnectionPool setOS(String deviceName,OutputStream os){
          pool.put(deviceName,os);
          return this;
    }

    public void deleteOs(String deviceName){
        pool.remove(deviceName);
    }

    public void writeOut(String deviceName, byte[] bytes) throws IOException {
        OutputStream os = pool.get(deviceName);
        os.write(bytes);
    }

    public void writeOut(String deviceName, FileInputStream is, String suffix) throws IOException {
        OutputStream os = pool.get(deviceName);

        byte[] bytes = new byte[1024*1024];
         int size;
            // 传文件开始
            // 后缀
            byte[] b2 = suffix.getBytes();
            byte[] b = new byte[b2.length + 3];
            b[0] = '!';
            b[1] = '&';
            b[2] = (byte) b.length;
            for (int i = 3; i < b.length; i++) {
                b[i] = b2[i - 3];
            }
            os.write(b);
            os.flush();
         while (true){
                 if (((size = is.read(bytes)) == -1)) {
                     break;
                 }
                 os.write(bytes,0,size);
         }
            is.close();
            // 传图结束
            os.flush();
            os.write(new byte[]{'$'});
            os.flush();
    }


}
