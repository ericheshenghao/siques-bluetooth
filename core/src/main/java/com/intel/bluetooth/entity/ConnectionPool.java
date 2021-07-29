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

        byte[] b = new byte[1024*1024];
         int size;
            // 传文件开始
            // 后缀
            byte[] sb = suffix.getBytes();
            b[0] = '&';
            b[1] = (byte) sb.length;
            for (int i = 2; i < sb.length; i++) {
                b[i] = sb[i - 2];
            }

         while (true){
             if (((size = is.read(b,10,b.length - 10)) == -1)) {
                 break;
             }
             os.write(b,0,size);
             b[0] = '0';
         }
            is.close();
            // 传图结束
            b[0] = '$';
            os.write(b);
    }


}
