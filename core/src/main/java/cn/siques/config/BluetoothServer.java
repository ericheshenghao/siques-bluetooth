package cn.siques.config;

import java.io.*;
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

public class BluetoothServer  implements Runnable {

    // 流连接
    private static StreamConnection streamConnection = null;

    //接入通知
    private static StreamConnectionNotifier notifier;

    private static InputStream inputStream;


    public void startServer(String secretUUID) {

        String url = "btspp://localhost:" +  secretUUID;
        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            if (!localDevice.setDiscoverable(DiscoveryAgent.GIAC))
                System.out.println("请将蓝牙设置为可被发现");

            notifier = (StreamConnectionNotifier)Connector.open(url);
            streamConnection = notifier.acceptAndOpen();
            inputStream = streamConnection.openInputStream();

          new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true){
            readAndSet(inputStream);
            System.out.println();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void readAndSet(InputStream is){
        byte[] bytes = new byte[1024];
        int size = 0;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while(true){
            try {
                if (((size = is.read(bytes)) == -1)) break;
                os.write(bytes,0,size);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("当前输出："+os.toString());
        }
//        textReceive.setText(os.toString());
    }

}