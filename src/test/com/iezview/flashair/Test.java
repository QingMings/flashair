package com.iezview.flashair;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

/**
 * Created by shishifanbuxie on 2017/4/17.
 */
public class Test  extends Application{

    @Override
    public void start(Stage primaryStage) {
        TextField ipTextField = new TextField();
        String regex = makePartialIPRegex();
        final UnaryOperator<Change> ipAddressFilter = c -> {
            String text = c.getControlNewText();
            if  (text.matches(regex)) {
                return c ;
            } else {
                return null ;
            }
        };
//        String a =(Integer)"0";
        ipTextField.setTextFormatter(new TextFormatter<>(ipAddressFilter));
        StackPane root = new StackPane(ipTextField);
        Scene scene = new Scene(root, 350, 120);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String makePartialIPRegex() {
        String partialBlock = "(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))" ;
        String subsequentPartialBlock = "(\\."+partialBlock+")" ;
        String ipAddress = partialBlock+"?"+subsequentPartialBlock+"{0,3}";
        return "^"+ipAddress ;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
