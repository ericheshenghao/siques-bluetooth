package com.intel.main.controller;


import com.intel.bluetooth.BluetoothClient;
import com.intel.bluetooth.BluetoothServer;
import com.intel.bluetooth.RemoteDeviceDiscovery;
import com.intel.bluetooth.component.CustomLabelView;
import com.intel.bluetooth.component.ReceiveTextArea;
import com.intel.bluetooth.component.CustomImageView;
import com.intel.bluetooth.constant.ConnectStatus;
import com.intel.bluetooth.entity.*;
import com.intel.bluetooth.exception.ServiceNotFoundException;
import com.intel.bluetooth.util.Faker;
import com.intel.bluetooth.util.FileType;
import com.intel.bluetooth.util.Later;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
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
    private ListView<Object> sendMsgList;

    @FXML
    private ListView<Object> receiveMsgList;

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

    ObservableList<Object> sendMsg  =
            FXCollections.observableArrayList();

    ObservableList<Object> receiveMsg  =
            FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(()->{
            int before = 0;
            int after;
            while (true){
                if(deviceName.getText() != ""){
                    List<MessageItem> msgList = ReceiveMessage.getInstance().getMsgList(deviceName.getText());

                    after = msgList.size();
                    if(before != after){
                        Later.run(()->{
                            receiveMsg.clear();
                        });
                        List<Object> collect = msgList.stream().map(s -> {
                            if(s.getType().equals("text")){
                                return new ReceiveTextArea(((TextMessage) s).getText());
                            }
                            if(s.getType().equals("image")){
                                ImageMessage s1 = (ImageMessage) s;
                                AnchorPane build = CustomImageView.build("file:" + s1.getUrl(), event -> {
                                    System.out.println(s1.getUrl());
                                });

                                return build;
                            }else{
                                FileMessage s1 = (FileMessage) s;
                                return  new CustomLabelView("文件:" + s1.getSuffix());
                            }
                        }).collect(Collectors.toList());

                        Later.run(()->{
                            receiveMsg.addAll(collect);
                            receiveMsgList.setItems(receiveMsg);
                            // 点击处理
                        });
                    }
                    before = after;
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        getToothList();
        textSend.setOnDragOver(new EventHandler<DragEvent>() { //node添加拖入文件事件
            @Override
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);   //这一句必须有，否则setOnDragDropped不会触发
                }
            }
        });

        textSend.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                if(e.getCode()==KeyCode.ENTER) {
                    sendText();
                }
            }
        });

        textSend.setOnDragDropped(new EventHandler<DragEvent>() { //拖入后松开鼠标触发的事件
            @Override
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                if (event.isAccepted()) {
                    File file = dragboard.getFiles().get(0); //获取拖入的文件
                    FileInputStream fileInputStream = null;
                    try {
                         fileInputStream = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    // 发送并建立关连
                    sendFile(fileInputStream,file.getAbsolutePath());
                }
            }
        });
        startServer();
    }

    // 发送图片
    private void sendFile(FileInputStream fileInputStream, String path) {
        String suffix = path.substring(path.lastIndexOf(".") + 1);

        Later.run(()->{
            try {
                ConnectionPool.getInstance().writeOut(deviceName.getText(),fileInputStream,suffix);
            } catch (IOException e) {
                e.printStackTrace();
                clintOffline();
            }
            // 展现到发送处
        });
        boolean image = FileType.isImage(suffix);
        SendMessage.getInstance().addMsg(deviceName.getText(),path,image?"image":"file");
        AnchorPane pane = CustomImageView.build("file:" + path);
        refreshSendList(pane);
    }

    // 发送文本
    public void sendText()  {
        String text = textSend.getText();
        String name = deviceName.getText();
        if(text.equals("")) {
            return;
        }
        Later.run(()->{
            byte[] bytes = text.getBytes(Charset.forName("utf-8"));
            try {
                ConnectionPool.getInstance().writeOut(name,bytes);
            } catch (IOException e) {
                e.printStackTrace();
                clintOffline();
            }
        });

        SendMessage.getInstance().addMsg(name,text,"text");
        ReceiveTextArea area = new ReceiveTextArea(text);
        refreshSendList(area);
        textSend.setText("");
    }

    public void doRefresh(ActionEvent actionEvent) {
        getToothList();
    }

    private void refreshSendList(Object o){
        sendMsg.add(o);
        sendMsgList.setItems(sendMsg);
    }
    private void refreshSendList(Collection o){
        sendMsg.addAll(o);
        sendMsgList.setItems(sendMsg);
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

    private void clintOffline(){
        sendBtn.setDisable(true);
        connectOnline.setVisible(false);
        connectText.setText(ConnectStatus.OFF_LINE.getSt());
        System.out.println("连接已关闭，请重新连接");
    }

    private void clintOnline(String name){
        toothList.setDisable(false);
        refreshBtn.setDisable(false);
        sendBtn.setDisable(false);
        connectOnline.setVisible(true);
        textSend.setDisable(false);
        deviceName.setText(name);
        connectText.setText(ConnectStatus.ON_LINE.getSt());
        initSendList();
    }

    private void clintConnecting(String name){
        toothList.setDisable(true);
        refreshBtn.setDisable(true);
        connectOnline.setVisible(false);
        sendBtn.setDisable(true);
        connectText.setText(ConnectStatus.CONNECTING.getSt());
        deviceName.setText(name);
        progressBar.setVisible(true);
    }

    private void connectFailed( ){
        connectText.setText(ConnectStatus.ERROR_LINE.getSt());
        toothList.setDisable(false);
        refreshBtn.setDisable(false);
        initSendList();
    }

    public void doConnect(MouseEvent actionEvent)  {
        CustomRemoteDevice device = toothList.getSelectionModel().getSelectedItem();
        if(device == null) {
            return;
        }
        String name = device.getDeviceName();
        OutputStream os = ConnectionPool.getInstance().getOS(name);
        if(os != null){
            clintOnline(name);
            return;
        }
        clintConnecting(name);
        Thread fakeTd = Faker.fakeProgress(progressBar);
          new Thread(()->{
              try {
                  BluetoothClient.startClient(device, secretUUID);
                  clintOnline(name);
              } catch (ServiceNotFoundException e) {
                  // 未找到服务
                  connectFailed();
                  e.printStackTrace();
              }finally {
                  fakeTd.interrupt();
              }
          }).start();
    }


    public void closeConnection(ActionEvent actionEvent) {
        CustomRemoteDevice device = toothList.getSelectionModel().getSelectedItem();
        BluetoothClient.closeClient(device);
        clintOffline();
    }

    private void initSendList() {
        // 将发送队列重新读出，放入右列表
        Later.run(()->{
            sendMsg.clear();
            // 清空发送列表
            List<MessageItem> msgList = SendMessage.getInstance().getMsgList(deviceName.getText());

            List<Object> collect = msgList.stream().map(s -> {
                String type = s.getType();
                if(type.equals("text"))
                {
                    ReceiveTextArea area = new ReceiveTextArea(((TextMessage) s).getText());

                    return area;
                }

                if(type.equals("image")){
                    ImageMessage s1 = (ImageMessage) s;
                    return CustomImageView.build("file:" + s1.getUrl());
                }else{
                    FileMessage s1 = (FileMessage) s;
                    return  new CustomLabelView("文件:" + s1.getSuffix());
                }

            }).collect(Collectors.toList());

            refreshSendList(collect);
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