package com.intel.bluetooth.constant;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.constant
 * @Description:
 * @date : 2021/7/24 17:39
 */
public enum ConnectStatus {

    ON_LINE("已连接"),
    CONNECTING("连接中"),
    OFF_LINE("未连接"),
    ERROR_LINE("连接失败");
    String st ;
    ConnectStatus(String st) {
        this.st = st;
    }

    public String getSt() {
        return st;
    }
}
