package com.intel.bluetooth.entity;

import javax.swing.text.html.ImageView;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.entity
 * @Description:
 * @date : 2021/7/25 16:29
 */
public class ImageMessage extends MessageItem {
    String url;

    public ImageMessage(String url){
        this.url= url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getType() {
        return "image";
    }
}
