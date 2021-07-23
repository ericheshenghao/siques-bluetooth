package cn.siques.controller;


import cn.siques.config.BluetoothClient;
import cn.siques.config.BluetoothServer;
import cn.siques.config.RemoteDeviceDiscovery;
import cn.siques.util.Later;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.rmi.Remote;
import java.rmi.server.RemoteServer;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: cn.siques
 * @Description:
 * @date : 2021/7/21 22:06
 */
public class BluetoothConnector implements Initializable {
    final String secretUUID = "1000110100001000800000805F9B34FB";

    @FXML
    private ListView<RemoteDevice> toothList;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private TextArea textSend;

    @FXML
    private TextArea textReceive;

    ObservableList<RemoteDevice> data  =
            FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getToothList();
        startServer();
    }




    public void doConnect(ActionEvent actionEvent) {
        RemoteDevice device = toothList.getSelectionModel().getSelectedItem();
        if(device!=null){
            BluetoothClient.startClient(device, secretUUID);
        }
    }

    public void doSend(ActionEvent actionEvent) throws IOException {
        StreamConnection  con =  BluetoothClient.streamConnection;
        if(con!=null){
            OutputStream outputStream = con.openOutputStream();
            byte[] bytes = this.textSend.getText().getBytes(Charset.forName("utf-8"));
            outputStream.write(bytes);
        }
    }

    public void doRefresh(ActionEvent actionEvent) {
        getToothList();
    }

    private void getToothList() {
        Thread thread = new Thread(() -> {
            Vector<RemoteDevice> devices = RemoteDeviceDiscovery.findDevices();
            Later.run(()->{
                data.clear();
                data.addAll(devices);
                toothList.setItems(data);
                toothList.setCellFactory((ListView<RemoteDevice> l) -> new ListCell<RemoteDevice>(){
                    @Override
                    protected void updateItem(RemoteDevice item, boolean empty) {
                        super.updateItem(item, empty);
                        try {
                            if(null != item ){
                                setText(item.getFriendlyName(false));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        });
        thread.start();
    }


    private void startServer() {
        Thread thread = new Thread(() -> {
            BluetoothServer bluetoothServer = new BluetoothServer();
            bluetoothServer.startServer(secretUUID);
        });
        thread.start();
    }


}