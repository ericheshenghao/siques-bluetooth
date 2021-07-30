package com.intel.bluetooth;

import com.intel.bluetooth.entity.ReceiveMessage;
import com.intel.bluetooth.util.Faker;
import com.intel.bluetooth.util.FileType;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private ProgressBar progressBar;

    public void startServer(String secretUUID, ProgressBar progressBar) {
        this.progressBar = progressBar;
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

                // 开新线程处理流，每一个连接的客户端对应一个流
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

    // TODO 编解码问题
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private void readAndHandle(InputStream is, BluetoothRFCommServerConnection streamConnection){

        byte[] b = new byte[1024*1024];
        int size;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream = null;

        RemoteDevice remoteDevice = streamConnection.getRemoteDevice();
        String friendlyName = null;
        String url = null;
        String suffix = null;
        try {
            friendlyName = remoteDevice.getFriendlyName(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 当前读取类型
        int type = 1;
        while(true){
            try {
                // 读取完毕阻塞
                if (((size = is.read(b)) == -1)) {
                    break;
                }
                //  判断是否要读文件,并且获取文件后缀
                if( size > 4 && b[0] == '|' && b[1] == '&'  && b[2] == '#'   ){
                    Faker.fakeProgress(progressBar);
                    byte[] bs = new byte[b[3] - 4];
                    for (int i = 0; i < b[3] - 4; i++) {
                        bs[i] = b[i + 4];
                    }
                    suffix = new String(bs,Charset.forName("utf-8"));
                    System.out.println(suffix);
                    type = 0; // 后续改为读文件
                    url = "./files/"+sdf.format(new Date())+"."+ suffix;
                    fileOutputStream = new FileOutputStream(url);

                    fileOutputStream.write(b,b[3],size - b[3]);

                    continue;
                }

                if(type == 0){
                    fileOutputStream.write(b,0,size);
                }

                if(type == 1){
                    // 将接收到的信息，与发送端的名字绑定，每一条信息只属于他的发送端
                    os.write(b,0,size);
                    ReceiveMessage.getInstance().addMsg(friendlyName,os.toString("utf-8"),"text");
                    System.out.println(" 当前输出："+os.toString("utf-8"));
                    os.reset();
                }

                // 一个字节，读取结束
                if(type == 0 &&   b[size - 1] == '#' && b[size - 2] == '&'&&b[size - 3] == '|') {
                    // 读取结束
                    type = 1;
                    fileOutputStream.close();
                    //判断是否为图片还是文件
                    boolean image = FileType.isImage(suffix);
                    if(image){
                        ReceiveMessage.getInstance().addMsg(friendlyName, url,"image");
                    }else{
                        ReceiveMessage.getInstance().addMsg(friendlyName, url,"file");
                    }
                    Faker.interrupt();
                    continue;
                }





            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}