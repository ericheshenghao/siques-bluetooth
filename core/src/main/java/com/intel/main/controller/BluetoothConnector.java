package com.intel.main.controller;


import com.intel.bluetooth.*;
import com.intel.bluetooth.constant.ConnectStatus;
import com.intel.bluetooth.entity.ConnectionPool;
import com.intel.bluetooth.entity.CustomRemoteDevice;
import com.intel.bluetooth.entity.ReceiveMessage;
import com.intel.bluetooth.entity.SendMessage;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.stream.Collectors;

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
    private ListView<TextArea> sendMsgList;

    @FXML
    private ListView<TextArea> receiveMsgList;

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

    ObservableList<TextArea> sendMsg  =
            FXCollections.observableArrayList();

    ObservableList<TextArea> receiveMsg  =
            FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getToothList();

            new Thread(()->{
                while (true){
                    if(deviceName.getText() != ""){
                        List<String> msgList = ReceiveMessage.getInstance().getMsgList(deviceName.getText());
                        Later.run(()->{
                            receiveMsg.clear();
                        });
                        List<TextArea> collect = msgList.stream().map(s -> {
                            TextArea field = new TextArea(s);

                            field.setEditable(false);
                            field.setWrapText(true);
                            field.setMinHeight(100);
                            field.setMaxWidth(380);
                            return field;
                        }).collect(Collectors.toList());

                        Later.run(()->{
                            receiveMsg.addAll(collect);
                            receiveMsgList.setItems(receiveMsg);
                        });
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        startServer();
    }


    public void doSend(ActionEvent actionEvent)  {
        if(textSend.getText().equals("")) {
            return;
        }
        OutputStream os = ConnectionPool.getInstance().getOS(deviceName.getText());
        if(os != null){
            byte[] bytes = this.textSend.getText().getBytes(Charset.forName("utf-8"));
            try{
                os.flush();
                os.write(bytes);

                SendMessage.getInstance().addMsg(deviceName.getText(),textSend.getText());
                TextArea field = new TextArea(textSend.getText());

                field.setEditable(false);
                field.setWrapText(true);
                field.setMinHeight(100);
                field.setMaxWidth(380);

                sendMsg.add(field);
                sendMsgList.setItems(sendMsg);
                textSend.setText("");
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
            initSendList();
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
                  initSendList();
              }

          }).start();
    }

    private void initSendList() {
        // 将发送队列重新读出，放入右列表
        Later.run(()->{
            sendMsg.clear();
            // 清空发送列表
            List<String> msgList = SendMessage.getInstance().getMsgList(deviceName.getText());

            List<TextArea> collect = msgList.stream().map(s -> {
                TextArea field = new TextArea(s);

                field.setEditable(false);
                field.setWrapText(true);
                field.setMinHeight(100);
                field.setMaxWidth(380);
                return field;
            }).collect(Collectors.toList());

            sendMsg.addAll(collect);
            sendMsgList.setItems(sendMsg);
        });
    }

    private void startServer() {
        Thread thread = new Thread(() -> {
            BluetoothServer bluetoothServer = new BluetoothServer();
            bluetoothServer.startServer(secretUUID);
        });
        thread.start();
    }



}