package com.intel.bluetooth;

import com.intel.bluetooth.entity.ReceiveMessage;

import java.io.*;
import java.util.HashMap;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothServer  implements Runnable {

    // 流连接
    private static BluetoothRFCommServerConnection streamConnection = null;

    //接入通知
    private static StreamConnectionNotifier notifier;

    private static InputStream inputStream;

    private static HashMap<Thread, String> connectedClient = new HashMap<Thread, String>();

    public void startServer(String secretUUID) {
        String url = "btspp://localhost:" +  secretUUID;
        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            int discoverable = localDevice.getDiscoverable();

            if (discoverable != DiscoveryAgent.GIAC) {
                localDevice.setDiscoverable(DiscoveryAgent.GIAC);
            }

            notifier = (StreamConnectionNotifier)Connector.open(url);

          new Thread(this,"handleAccept").start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true){
            try {
                System.out.println("监听客户端连接");
                streamConnection =(BluetoothRFCommServerConnection) notifier.acceptAndOpen();
                System.out.println(" 客户端已连接");
                inputStream = streamConnection.openInputStream();

                // 开新线程处理流
                Thread thread = new Thread(() -> {
                    readAndHandle(inputStream,streamConnection);
                    System.out.println("客户端已关闭");
                });
                // 绑定该线程与连接客户端信息

                thread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void readAndHandle(InputStream is, BluetoothRFCommServerConnection streamConnection){
        byte[] bytes = new byte[1024];
        int size = 0;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        RemoteDevice remoteDevice = streamConnection.getRemoteDevice();
        String friendlyName = null;
        try {
              friendlyName = remoteDevice.getFriendlyName(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                if (((size = is.read(bytes)) == -1)) {
                    break;
                }
                os.write(bytes,0,size);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 将接收到的信息，与发送端的名字绑定
            ReceiveMessage.getInstance().addMsg(friendlyName,os.toString());
            System.out.println(" 当前输出："+os.toString());
        }
//        textReceive.setText(os.toString());
    }

}