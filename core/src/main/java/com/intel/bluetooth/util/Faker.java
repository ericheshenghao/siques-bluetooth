package com.intel.bluetooth.util;

import javafx.scene.control.Control;
import javafx.scene.control.ProgressIndicator;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.util
 * @Description:
 * @date : 2021/7/24 17:18
 */
public class Faker {

    public static Thread fakeProgress(ProgressIndicator indicator){
        indicator.setProgress(0);
        Thread fakeProgress = new Thread(() -> {
            for (int i = 0; i < 125; i++) {
                indicator.setProgress(0.008d*i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    indicator.setVisible(false);
                    break;
                }
            }
            indicator.setVisible(false);
        });
        fakeProgress.start();
        return fakeProgress;
    }
}
