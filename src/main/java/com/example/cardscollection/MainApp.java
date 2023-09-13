package com.example.cardscollection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;



import java.io.IOException;

public class MainApp extends Application
{
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception
    {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("mainwindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cards Collection");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}