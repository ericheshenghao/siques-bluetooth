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
                if(url!= "") {
                    BluetoothStack bluetoothStack = BlueCoveImpl.instance().getBluetoothStack();


                    long address = RemoteDeviceHelper.getAddress(remoteDevice.getBluetoothAddress());
                    BluetoothConnectionParams params = new BluetoothConnectionParams(address, 1, false, false);
                    BluetoothRFCommClientConnection connection = new BluetoothRFCommClientConnection(bluetoothStack, params);
                    boolean closed = connection.isClosed();

                    if(!closed){
                        System.out.println("未关闭，打开输出流");
                        outputStream = connection.openOutputStream();
                    }else{
                        System.out.println("已关闭");
                        streamConnection = (BluetoothRFCommClientConnection ) Connector.open(url);
                        outputStream = streamConnection.openOutputStream();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        return streamConnection;
     }



}

