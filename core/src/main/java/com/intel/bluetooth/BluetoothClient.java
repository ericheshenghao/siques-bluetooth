package com.intel.bluetooth;

/**
 * @author:
 * @Title: TODO
 * @Package: publ.chou.example.bluetooth
 * @Description:
 * @date : 2021/7/22 13:42
 */

import com.intel.main.controller.BluetoothConnector;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.Remote;

public class BluetoothClient {
    public   static BluetoothRFCommClientConnection streamConnection;

    public static OutputStream outputStream;

    public static  StreamConnection  startClient(RemoteDevice remoteDevice, String serviceUUID)  {
           streamConnection = null;
            try {
                String url = RemoteDeviceDiscovery.searchService(remoteDevice, serviceUUID);
                if(url != null) {
                    streamConnection = (BluetoothRFCommClientConnection ) Connector.open(url);
                    outputStream = streamConnection.openOutputStream();

                }else{
                    System.out.println("搜索失败");
                }
            } catch (IOException e) {
                System.out.println("客户端已连接");
                e.printStackTrace();
            }

        return streamConnection;
     }



}

