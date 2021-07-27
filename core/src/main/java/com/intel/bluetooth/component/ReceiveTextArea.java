package com.intel.bluetooth.component;


import javafx.scene.control.TextArea;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.component
 * @Description:
 * @date : 2021/7/27 19:50
 */
public class ReceiveTextArea extends TextArea {

    public ReceiveTextArea(String text){
        super(text);
        setEditable(false);
        setWrapText(true);
        setMinHeight(100);
        setMaxWidth(380);
    }
}
