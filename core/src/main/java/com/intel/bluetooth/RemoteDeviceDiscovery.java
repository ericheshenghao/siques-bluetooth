package com.intel.bluetooth;

import java.io.IOException;
import java.util.*;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import javax.bluetooth.DataElement;
import javax.bluetooth.UUID;

/**
 * @author he
 */
public class RemoteDeviceDiscovery {
    public  static Vector<RemoteDevice> devicesDiscovered = new Vector<>();

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


        @Override
        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
            devicesDiscovered.add(remoteDevice);

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


    public static Vector<RemoteDevice> findDevices()   {
        try {
            LocalDevice ld = LocalDevice.getLocalDevice();
            System.out.println("#本机蓝牙名称:" + ld.getFriendlyName());
            synchronized (discoveryLock){
                devicesDiscovered.clear();
                boolean started  = ld.getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
                if(started){
                    discoveryLock.wait();
                    ld.getDiscoveryAgent().cancelInquiry(listener);
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return devicesDiscovered;
    }



    public static String searchService(RemoteDevice btDevice, String serviceUUID) throws IOException {
        UUID[] searchUuidSet = new UUID[] { new UUID(serviceUUID, false) };

        int[] attrIDs =  new int[] {0x0100};

        synchronized (searchLock){
            System.out.println("search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
            LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
            try {
                searchLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return  serviceFound.size() == 0 ? "" :serviceFound.elementAt(0);

    }
}
