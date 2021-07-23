package com.intel.bluetooth.util;

import javafx.application.Platform;

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
