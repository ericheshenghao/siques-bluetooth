package com.intel.main.controller;


import com.intel.bluetooth.BluetoothClient;
import com.intel.bluetooth.BluetoothServer;
import com.intel.bluetooth.RemoteDeviceDiscovery;
import com.intel.bluetooth.constant.ConnectStatus;
import com.intel.bluetooth.entity.*;
import com.intel.bluetooth.exception.ServiceNotFoundException;
import com.intel.bluetooth.util.Faker;
import com.intel.bluetooth.util.Later;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

        textSend.setOnDragDropped(new EventHandler<DragEvent>() { //拖入后松开鼠标触发的事件
            @Override
            public void handle(DragEvent event) {
                // get drag enter file
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
                    sendImg(fileInputStream,file.getAbsolutePath());
                }
            }
        });

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

                                    TextArea field = new TextArea(((TextMessage) s).getText());

                                    field.setEditable(false);
                                    field.setWrapText(true);
                                    field.setMinHeight(100);
                                    field.setMaxWidth(370);
                                    return field;
                                }else{
                                    ImageMessage s1 = (ImageMessage) s;
                                    String url = s1.getUrl();
                                    Image image1 = new Image(url);
                                    ImageView imageView1 = new ImageView(image1);
                                    return imageView1;
                                }


                            }).collect(Collectors.toList());

                            Later.run(()->{
                                receiveMsg.addAll(collect);
                                receiveMsgList.setItems(receiveMsg);
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

        startServer();
    }

    private void banBtn(){
        textSend.setDisable(true);
        sendBtn.setDisable(true);
        progressBar.setVisible(true);
    }

    private void reBtn(){
        textSend.setDisable(false);
        sendBtn.setDisable(false);
        progressBar.setVisible(false);
    }

    // 发送图片
    private void sendImg(FileInputStream fileInputStream, String path) {
        String suffix = path.substring(path.lastIndexOf(".") + 1);

        // 写出到远端,图片类型，显示实时进度
        banBtn();
        Later.run(()->{
            ConnectionPool.getInstance().writeOut(deviceName.getText(),fileInputStream,progressBar,suffix);
            // 展现到发送处
            reBtn();
        });

        Image image1 = new Image("file:"+path);
        ImageView imageView1 = new ImageView(image1);
        imageView1.setFitWidth(380);
        imageView1.setPreserveRatio(true);
        // TODO 图片的复制事件

        sendMsg.add(imageView1);
        sendMsgList.setItems(sendMsg);
    }

    // 发送文本
    public void doSend(ActionEvent actionEvent)  {
        if(textSend.getText().equals("")) {
            return;
        }
        OutputStream os = ConnectionPool.getInstance().getOS(deviceName.getText());
        if(os != null){
            // 文本发送整个 byte[], 加上一个识别符号
            byte[] bytes = this.textSend.getText().getBytes(Charset.forName("utf-8"));


            try{
                os.flush();
                os.write(bytes);

                SendMessage.getInstance().addMsg(deviceName.getText(),textSend.getText(),"text");
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
            List<MessageItem> msgList = SendMessage.getInstance().getMsgList(deviceName.getText());

            List<Object> collect = msgList.stream().map(s -> {
                String type = s.getType();
                if(type.equals("text"))
                {

                    TextArea field = new TextArea(((TextMessage) s).getText());

                    field.setEditable(false);
                    field.setWrapText(true);
                    field.setMinHeight(100);
                    field.setMaxWidth(380);
                    return field;
                }else{

                    ImageMessage s1 = (ImageMessage) s;
                    String url = "D:\\web\\siques-app\\siques-bluetooth\\core\\src\\main\\resources\\2021-07-23-12-52-43.png";
                    Image image1 = new Image(url);
                    ImageView imageView1 = new ImageView(image1);
                    return imageView1;
                }

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