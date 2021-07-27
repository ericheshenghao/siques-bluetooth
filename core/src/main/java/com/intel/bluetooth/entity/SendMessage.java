package com.intel.bluetooth.entity;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.entity
 * @Description:
 * @date : 2021/7/24 21:20
 */
public class SendMessage {

    //   此端 -- 》 接收者
    HashMap<String, List<MessageItem>> container = new HashMap<>();


    static SendMessage deviceMessage;

    public static SendMessage getInstance(){
        if(deviceMessage == null){
            deviceMessage = new SendMessage();
        }
        return deviceMessage;
    }

    public void addMsg(String deviceName,String msg,String type){
        List<MessageItem> list = container.getOrDefault(deviceName, new ArrayList<>());

         if(type.equals("image")){
             ImageMessage imageMessage = new ImageMessage(msg);
             list.add(imageMessage);
         }
        if(type.equals("text")){
             TextMessage textMessage = new TextMessage(msg);
             list.add(textMessage);
         }

        if(type.equals("file")){
            FileMessage fileMessage = new FileMessage(msg);
            list.add(fileMessage);
        }
         //
        container.put(deviceName,list);
    }

    public List<MessageItem> getMsgList(String deviceName){
        return container.getOrDefault(deviceName, new ArrayList<>());
    }
}
