package com.intel.bluetooth.entity;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.entity
 * @Description:
 * @date : 2021/7/25 16:29
 */
public class FileMessage extends MessageItem {

    String url;

    public FileMessage(String url){
        this.url = url;
    }

    public String getSuffix() {
        return url.substring(url.lastIndexOf("/")+1);
    }

    public String getUrl(){
        return url;
    }
    @Override
    public String getType() {
        return "file";
    }
}
