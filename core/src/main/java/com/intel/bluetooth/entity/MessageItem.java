package com.intel.bluetooth.entity;

import javafx.scene.control.TextArea;
import lombok.Data;

import javax.swing.text.Element;
import javax.swing.text.html.ImageView;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.entity
 * @Description:
 * @date : 2021/7/25 16:16
 */

public abstract class MessageItem    {

    public MessageItem() {

    }


    public abstract String getType();


}
