package com.intel.bluetooth.entity;

import lombok.Builder;
import lombok.Data;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.entity
 * @Description:
 * @date : 2021/7/24 16:20
 */
@Data
@Builder
public class CustomRemoteDevice {
    private RemoteDevice remoteDevice;
    private String deviceName;
    private String address;
    private DeviceClass deviceClass;


}
