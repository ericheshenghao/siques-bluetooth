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
    private static Thread progress;
    public static void fakeProgress(ProgressIndicator indicator){
        indicator.setVisible(true);
        indicator.setProgress(0);
        progress = new Thread(() -> {
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
        progress.start();

    }

    public static void interrupt(){
        if(progress != null && !progress.isInterrupted()){
            progress.interrupt();
        }
    }


}
