package cn.siques.config;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: publ.chou.example.bluetooth
 * @Description:
 * @date : 2021/7/22 13:42
 */

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;

public class BluetoothClient {
    public   static StreamConnection streamConnection;


    public static  StreamConnection  startClient(RemoteDevice remoteDevice, String serviceUUID)  {
           streamConnection = null;
            try {
                String url = RemoteDeviceDiscovery.searchService(remoteDevice, serviceUUID);
                if(url!= "") {
                    streamConnection = (StreamConnection) Connector.open(url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        return streamConnection;
     }



}

