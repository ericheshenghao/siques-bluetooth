package com.intel.bluetooth;

import com.intel.bluetooth.entity.CustomRemoteDevice;
import com.intel.bluetooth.exception.ServiceNotFoundException;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.*;

import javax.bluetooth.*;
import javax.bluetooth.UUID;

/**
 * @author he
 */
public class RemoteDeviceDiscovery {
    public  static Vector<CustomRemoteDevice> devicesDiscovered = new Vector<>();

    public  static Vector<String> serviceFound = new Vector<>();

    final  static Object discoveryLock = new Object();

    final  static Object searchLock = new Object();

    private static DiscoveryListener listener = new DiscoveryListener() {
        @Override
        public void inquiryCompleted(int discType) {
            System.out.println("#" + "搜索完成");
            synchronized (discoveryLock){
                discoveryLock.notifyAll();
            }
        }


        @SneakyThrows
        @Override
        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {

            CustomRemoteDevice device = CustomRemoteDevice.builder().remoteDevice(remoteDevice)
                    .address(remoteDevice.getBluetoothAddress())
                    .deviceName(remoteDevice.getFriendlyName(false))
                    .deviceClass(deviceClass).build();
            devicesDiscovered.add(device);

        }
        @Override
        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            for (int i = 0; i < servRecord.length; i++) {
                String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                if (url == null) {
                    continue;
                }
                serviceFound.add(url);
                DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
            }
        }

        @Override
        public void serviceSearchCompleted(int arg0, int arg1) {
            System.out.println("#" + "serviceSearchCompleted");
            synchronized (searchLock){
                searchLock.notifyAll();
            }
        }
    };


    public static Vector<CustomRemoteDevice> findDevices()   {

        LocalDevice ld = null;
        try {
            ld = LocalDevice.getLocalDevice();

        System.out.println("#本机蓝牙名称:" + ld.getFriendlyName());
            synchronized (discoveryLock){
                devicesDiscovered.clear();
                boolean started  = ld.getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
                if(started){
                    discoveryLock.wait();
                    ld.getDiscoveryAgent().cancelInquiry(listener);
                }

            }
        } catch (BluetoothStateException | InterruptedException e) {
            e.printStackTrace();
        }
          return devicesDiscovered;
    }



    public static String searchService(RemoteDevice btDevice, String serviceUUID) throws ServiceNotFoundException{
        UUID[] searchUuidSet = new UUID[] { new UUID(serviceUUID, false) };

        int[] attrIDs =  new int[] {0x0100};

        synchronized (searchLock){
            try {
                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
                searchLock.wait();
            } catch (BluetoothStateException | InterruptedException e) {
                throw new ServiceNotFoundException(e);
            }
        }
        if(serviceFound.size()==0) {
            throw new ServiceNotFoundException("服务未启动");
        }

        return serviceFound.elementAt(0);

    }
}
