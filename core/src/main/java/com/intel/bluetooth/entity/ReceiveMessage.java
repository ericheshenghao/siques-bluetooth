package com.intel.bluetooth.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.entity
 * @Description:
 * @date : 2021/7/24 17:11
 */
public class ReceiveMessage {

    // 发送者
    HashMap<String, List<MessageItem>> container = new HashMap<>();


    static ReceiveMessage deviceMessage;

    public static ReceiveMessage getInstance(){
        if(deviceMessage == null){
            deviceMessage = new ReceiveMessage();
        }
        return deviceMessage;
    }

    public void addMsg(String deviceName,String msg,String type){
        List<MessageItem> list = container.getOrDefault(deviceName, new ArrayList<>());

        if("image".equals(type)){
            ImageMessage imageMessage = new ImageMessage(msg);
            list.add(imageMessage);
        }

        if("text".equals(type)) {
            TextMessage textMessage = new TextMessage(msg);
            list.add(textMessage);
        }

        if("file".equals(type)) {
            FileMessage fileMessage = new FileMessage(msg);
            list.add(fileMessage);
        }
        container.put(deviceName,list);

    }

    public List<MessageItem> getMsgList(String deviceName){
        return container.getOrDefault(deviceName, new ArrayList<>());
    }

}
