package com.intel.bluetooth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.entity
 * @Description:
 * @date : 2021/7/25 16:28
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TextMessage extends MessageItem {

    String text ;


    public TextMessage(String text){
        this.text = text;
    }

    @Override
    public String  getType() {
        return "text";
    }
}
