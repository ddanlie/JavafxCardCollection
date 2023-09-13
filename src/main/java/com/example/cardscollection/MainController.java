package com.example.cardscollection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable
{

    @FXML
    private HBox mainMenu;
    @FXML
    private Button collPageButton;
    @FXML
    private AnchorPane pagePane;

    public static CardCollection cardCol = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        cardCol = new CardCollection();
        collPageButton.fire();
    }

    public static void updateStyle(Button menu, String property, String value) {
        String currentStyle = menu.getStyle();
        String newStyle = currentStyle.replaceAll(property + ": [^;]*;", "");
        newStyle += property + ": " + value + ";";
        menu.setStyle(newStyle);
    }

    @FXML
    public void menuSelected(ActionEvent event)
    {
        Button clickedMenu = (Button)event.getSource();
        for(Node b : mainMenu.getChildren())
        {
            if(b instanceof Button)
                updateStyle((Button)b, "-fx-background-color", "white");
        }
        updateStyle(clickedMenu, "-fx-background-color", "#02e9fa");
        loadPage(clickedMenu.getText());
    }


    @FXML
    public void imgPreAdd(MouseEvent e)
    {
        Button b = (Button)e.getSource();
        updateStyle(b, "-fx-background-color", "#02e9fa");
    }
    @FXML
    public void addCard(MouseEvent e)
    {
        Button b = (Button)e.getSource();
        updateStyle(b, "-fx-background-color", "white");

        FileChooser imgChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("images","*.jpg", "*.jpeg", "*.png");
        imgChooser.getExtensionFilters().add(filter);
        File imgToAdd = imgChooser.showOpenDialog(MainApp.primaryStage);
        if(imgToAdd != null)
        {
            //add picture editor (get info and image)
            CardCollection.Card card = cardCol.createCard(imgToAdd);
            //save card (may take some time, check TODO in CardCollection class)
            cardCol.addCard(card);
        }
        //update page by just going to it again
        collPageButton.fire();
    }


    private void loadPage(String pageName)
    {
        Parent root = null;
        try
        {
            root = FXMLLoader.load(getClass().getResource(pageName.toLowerCase()+"page.fxml"));
        }
        catch(Exception e){e.printStackTrace();}

        pagePane.getChildren().clear();
        if(root != null)
        {
            pagePane.getChildren().add(root);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            AnchorPane.setTopAnchor(root, 0.0);
        }
    }

}