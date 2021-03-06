package com.intel.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: cn.siques
 * @Description:
 * @date : 2021/7/22 20:53
 */
public class Main extends Application {

      private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("蓝牙文件传输");
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("App.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
        super.stop();
    }

    public static void main(String[] args) {
        mkdir();
        launch(args);
    }

    private static void mkdir(){
        String url = "./files";
        File file = new File(url);
        if(!file.exists()){
            file.mkdir();
        }
    }
}
