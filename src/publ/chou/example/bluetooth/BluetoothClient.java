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
import java.util.Iterator;
import java.util.Set;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class BluetoothClient {
    private StreamConnection streamConnection;
    private OnDiscoverListener onDiscoverListener = null;
    private OnClientListener onClientListener = null;

    public interface OnClientListener {
        void onConnected(InputStream inputStream, OutputStream outputStream);
        void onConnectionFailed();
        void onDisconnected();
        void onClose();
    }

    public interface OnDiscoverListener {
        void onDiscover(RemoteDevice remoteDevice);
    }


    public BluetoothClient() {
    }


    public OnDiscoverListener getOnDiscoverListener() {
        return onDiscoverListener;
    }


    public void setOnDiscoverListener(OnDiscoverListener onDiscoverListener) {
        this.onDiscoverListener = onDiscoverListener;
    }


    public OnClientListener getClientListener() {
        return onClientListener;
    }


    public void setClientListener(OnClientListener onClientListener) {
        this.onClientListener = onClientListener;
    }

    public void find() throws IOException, InterruptedException {
        Set<RemoteDevice> devicesDiscovered = RemoteDeviceDiscovery.getDevices();

        Iterator<RemoteDevice> itr = devicesDiscovered.iterator();
        while (itr.hasNext()) {
            RemoteDevice remoteDevice = itr.next();

            onDiscoverListener.onDiscover(remoteDevice);
        }
    }

    public void startClient(RemoteDevice remoteDevice, String serviceUUID) throws IOException, InterruptedException {
        String url = RemoteDeviceDiscovery.searchService(remoteDevice, serviceUUID);

        streamConnection = (StreamConnection) Connector.open(url);
        if (this.onClientListener != null) {
            this.onClientListener.onConnected(streamConnection.openInputStream(), streamConnection.openOutputStream());
        }
    }
}
