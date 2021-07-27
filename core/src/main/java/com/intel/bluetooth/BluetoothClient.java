package com.intel.bluetooth;

/**
 * @author:
 * @Title: TODO
 * @Package: publ.chou.example.bluetooth
 * @Description:
 * @date : 2021/7/22 13:42
 */

import com.intel.bluetooth.entity.ConnectionPool;
import com.intel.bluetooth.entity.CustomRemoteDevice;
import com.intel.bluetooth.exception.ServiceNotFoundException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.OutputStream;

public class BluetoothClient {
    public   static StreamConnection  streamConnection;

    public static OutputStream outputStream;

    public static  StreamConnection  startClient(CustomRemoteDevice device, String serviceUUID) throws ServiceNotFoundException {
        streamConnection = null;

        // 服务没启动
        String url = RemoteDeviceDiscovery.searchService(device.getRemoteDevice(), serviceUUID);

        // 服务连不上
        try {

            streamConnection = (StreamConnection  ) Connector.open(url);
            outputStream = streamConnection.openOutputStream();

            // 建立远端与输出流的关系
            ConnectionPool.getInstance()
                    .setOS(device.getDeviceName(),  outputStream);

        } catch (IOException e) {

            throw new ServiceNotFoundException(e);
        }

        return streamConnection;
     }


}

