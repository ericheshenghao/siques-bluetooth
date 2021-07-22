package cn.siques.config;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothServer {

    // 流连接
    private static StreamConnection streamConnection = null;

    //接入通知
    private static StreamConnectionNotifier notifier;


    public static void startServer(String secretUUID,OnServerListener onServerListener) {

        String url = "btspp://localhost:" +  secretUUID;
        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            if (!localDevice.setDiscoverable(DiscoveryAgent.GIAC))
                System.out.println("请将蓝牙设置为可被发现");

            notifier = (StreamConnectionNotifier)Connector.open(url);
            onServerListener.onConnected(notifier.acceptAndOpen());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public interface OnServerListener {
        void onConnected(StreamConnection streamConnection);
    }

}