package cn.siques.util;

import cn.siques.config.RemoteDeviceDiscovery;
import javafx.application.Platform;

import javax.bluetooth.RemoteDevice;
import java.util.Vector;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: cn.siques.util
 * @Description:
 * @date : 2021/7/22 22:24
 */
public class Later {

    public  static  void run(Runnable runnable){
        Platform.runLater(runnable);
    }

}
