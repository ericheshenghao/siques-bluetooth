package publ.chou.example.bluetooth;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: publ.chou.example.bluetooth
 * @Description:
 * @date : 2021/7/22 13:42
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.bluetooth.RemoteDevice;

public class BluetoothClientTest {
    public static void main(String[] argv) {
        final String serverUUID = "1000110100001000800000805F9B34FB"; //需要与服务端相同

        BluetoothClient client = new BluetoothClient();

        Vector<RemoteDevice> remoteDevices = new Vector<>();

        client.setOnDiscoverListener(remoteDevice -> remoteDevices.add(remoteDevice));

        client.setClientListener(new BluetoothClient.OnClientListener() {

            @Override
            public void onConnected(InputStream inputStream, OutputStream outputStream) {
                System.out.printf("Connected");
                //添加通信代码
            }

            @Override
            public void onConnectionFailed() {
                System.out.printf("Connection failed");
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onClose() {

            }

        });

        try {
            client.find();

            if (remoteDevices.size() > 0) {
                client.startClient(remoteDevices.firstElement(), serverUUID);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}