package com.intel.bluetooth.component;



import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.component
 * @Description:
 * @date : 2021/7/27 19:50
 */
public class CustomImageView  {

    public static AnchorPane build(String url, EventHandler<MouseEvent> handler){
        AnchorPane pane = build(url);
        pane.setOnMouseClicked(handler);
        return pane;
    }

    public static AnchorPane build(String url) {
        Image image = new Image(url);
        ImageView view = new  ImageView(image);
        view.setFitWidth(380);
        view.setPreserveRatio(true);
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(view);
        return pane;
    }
}
