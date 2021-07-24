package com.intel.main.controller;


import com.intel.bluetooth.*;
import com.intel.bluetooth.constant.ConnectStatus;
import com.intel.bluetooth.entity.CustomRemoteDevice;
import com.intel.bluetooth.exception.ServiceNotFoundException;
import com.intel.bluetooth.util.Faker;
import com.intel.bluetooth.util.Later;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
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
    private ListView<CustomRemoteDevice> toothList;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button sendBtn;

    @FXML Label deviceName;

    @FXML
    private TextArea textSend;


    @FXML
    private Circle connectOnline;

    @FXML
    private Text connectText;

    ObservableList<CustomRemoteDevice> data  =
            FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getToothList();
        startServer();
    }


    public void doSend(ActionEvent actionEvent)  {
        OutputStream os = ConnectionPool.getInstance().getOS(deviceName.getText());
        if(os != null){
            byte[] bytes = this.textSend.getText().getBytes(Charset.forName("utf-8"));
            try{
                os.flush();
                os.write(bytes);
            }catch (Exception e){
                sendBtn.setDisable(true);
                connectOnline.setVisible(false);
                connectText.setText(ConnectStatus.OFF_LINE.getSt());
                //删除流
                ConnectionPool.getInstance().deleteOS(deviceName.getText());
                System.out.println("连接已关闭，请重新连接");
            }
        }
    }

    public void doRefresh(ActionEvent actionEvent) {
        getToothList();
    }

    private void getToothList() {
        if(progressIndicator.isVisible()) {
            return;
        }
        progressIndicator.setVisible(true);
        Thread fakeTd = Faker.fakeProgress(progressIndicator);
        new Thread(() -> {
            Later.run(()->{
                data.clear();
                toothList.setItems(data);
            });
            Vector<CustomRemoteDevice> devices = RemoteDeviceDiscovery.findDevices();
            Later.run(()->{
                fakeTd.interrupt();

                data.addAll(devices);
                toothList.setItems(data);

                toothList.setCellFactory((ListView<CustomRemoteDevice> l) -> new ListCell<CustomRemoteDevice>(){

                    @Override
                    protected void updateItem(CustomRemoteDevice item, boolean empty) {
                        setOnMouseClicked((event -> {
                            event.getSource();
                            doConnect(event);
                        }
                        ));
                        super.updateItem(item, empty);

                        if(null != item ){
                            setText(item.getDeviceName());
                        }
                    }
                });
            });
        }).start();

    }


    public void doConnect(MouseEvent actionEvent)  {
        CustomRemoteDevice device = toothList.getSelectionModel().getSelectedItem();
        if(device == null) {
            return;
        }
        OutputStream os = ConnectionPool.getInstance().getOS(device.getDeviceName());
        if(os != null){
            sendBtn.setDisable(false);
            connectOnline.setVisible(true);
            deviceName.setText(device.getDeviceName());
            connectText.setText(ConnectStatus.ON_LINE.getSt());
            return;
        }
        toothList.setDisable(true);
        refreshBtn.setDisable(true);
        connectOnline.setVisible(false);
        sendBtn.setDisable(true);
        connectText.setText(ConnectStatus.CONNECTING.getSt());
        deviceName.setText(device.getDeviceName());
        progressBar.setVisible(true);
        Thread fakeTd = Faker.fakeProgress(progressBar);
          new Thread(()->{

              try {
                  BluetoothClient.startClient(device, secretUUID);
                  connectText.setText(ConnectStatus.ON_LINE.getSt());
                  connectOnline.setVisible(true);
                  sendBtn.setDisable(false);
                  textSend.setDisable(false);
              } catch (ServiceNotFoundException e) {
                  // 未找到服务
                  e.printStackTrace();
                  connectText.setText(ConnectStatus.ERROR_LINE.getSt());
              }finally {
                  fakeTd.interrupt();
                  toothList.setDisable(false);
                  refreshBtn.setDisable(false);
              }

          }).start();
    }

    private void startServer() {
        Thread thread = new Thread(() -> {
            BluetoothServer bluetoothServer = new BluetoothServer();
            bluetoothServer.startServer(secretUUID);
        });
        thread.start();
    }



}