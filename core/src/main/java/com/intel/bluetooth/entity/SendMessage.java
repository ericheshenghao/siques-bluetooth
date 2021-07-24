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
    HashMap<String, List<String>> container = new HashMap<>();


    static ReceiveMessage deviceMessage;

    public static ReceiveMessage getInstance(){
        if(deviceMessage == null){
            deviceMessage = new ReceiveMessage();
        }
        return deviceMessage;
    }

    public void addMsg(String deviceName,String msg){
        List<String> list = container.getOrDefault(deviceName, new ArrayList<>());
        list.add(msg);
        container.put(deviceName,list);
    }

    public List<String> getMsgList(String deviceName){
        return container.getOrDefault(deviceName, new ArrayList<>());
    }
}
