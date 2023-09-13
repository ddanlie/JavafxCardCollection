package com.example.cardscollection;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;




public class CollectionController implements Initializable
{
    //custom view to know what card has been chosen after imageview click
    public class CardView extends ImageView
    {
        public CardCollection.Card card;

        //gives us knowledge if card was chosen for exploration
        public static boolean fixed = false;
        public CardView(CardCollection.Card card)
        {
            this.card = card;

            //functional settings
            setOnMouseEntered(CollectionController.this::exploreCard);
            setOnMouseExited(CollectionController.this::notExploreCard);
            setOnMouseClicked(CollectionController.this::keepExploringCard);
            //appearance settings
            setSmooth(true);
            setFitHeight(CardCollection.Card.cardPreviewHeight);
            setFitWidth(CardCollection.Card.cardPreviewWidth);
            setImage(card.getImage());
        }


        //empty constructor for
//        public CardView()
//        {
//
//        }
    }

    @FXML
    private TilePane cardsPane;
    @FXML
    private ImageView explorationView;


    //keeps current explored card (represented by tile in tilepane /card from shown card list/ )
    private CardView cardExplorationView = null;

    @FXML
    private TextField cardNameField;
    @FXML
    private TextArea cardDescriptionArea;


    //parent box to make explore window invisible if no card is currently chosen
    @FXML
    private HBox cardExplorer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        cardExplorer.setVisible(false);
        //after main window switches interface and returns back to collection, it is important to have card unfixed
        //for correct interface behaviour
        CardView.fixed = false;
        //appearance settings
        explorationView.setPreserveRatio(false);
        cardsPane.setVgap(15);
        cardsPane.setHgap(10);
        cardsPane.setPrefTileHeight(CardCollection.Card.cardPreviewHeight);
        cardsPane.setPrefTileWidth(CardCollection.Card.cardPreviewWidth);

        //add card views (from static list which is available for all controllers) to tilepane
        for(CardCollection.Card c : MainController.cardCol.cardList)
        {
            CardView cardView = new CardView(c);
            cardsPane.getChildren().add(cardView);
        }
        //interface behaviour settings
        cardNameField.setEditable(false);
        cardDescriptionArea.setEditable(false);
    }

    //mouse event - on mouse enter
    @FXML
    private void exploreCard(MouseEvent event)
    {
        //nothing, maybe will add some effects
    }


    //mouse event - on mouse exit
    @FXML
    private void notExploreCard(MouseEvent event)
    {
        //nothing, maybe will add some effects
    }

    //mouse event - on click
    @FXML
    private void keepExploringCard(MouseEvent event)
    {
        CardView cardView = (CardView)event.getSource();
        //if we click on card amd some card is currently explored
        if(CardView.fixed)
        {
            //if this is the same card we do not explore it again and quit.
            //flag is for finishing card exploration later than comparing it to clicked card
            //because stopCardExplore makes cardExplorationView = null
            boolean ret = false;
            if(cardView.equals(cardExplorationView))
                ret = true;
            stopCardExplore();
            if(ret)
                return;
        }
        //if we are here, we clicked on new card
        CardView.fixed = true;


        //interface behaviour settings
        cardExplorer.setVisible(true);
        cardNameField.setText(cardView.card.getInfo().cardname);
        cardDescriptionArea.setText(cardView.card.getInfo().description);
        explorationView.setImage(cardView.getImage());
        cardView.setFitHeight(cardView.getFitHeight()*0.9);
        cardView.setFitWidth(cardView.getFitWidth()*0.9);

        //set current explored card
        cardExplorationView = cardView;
    }


    //mouse event - on click
    @FXML
    private void stopCardExplore()
    {
        stopEdit();
        //interface behaviour settings
        CardView.fixed = false;
        cardExplorer.setVisible(false);
        cardExplorationView.setFitHeight(CardCollection.Card.cardPreviewHeight);
        cardExplorationView.setFitWidth(CardCollection.Card.cardPreviewWidth);
        cardNameField.setEditable(false);
        cardDescriptionArea.setEditable(false);

        //no current explored card from now
        cardExplorationView = null;
    }

    @FXML
    private void preDeleteCard(MouseEvent e)
    {
        Button del = (Button)e.getSource();
        MainController.updateStyle(del, "-fx-background-color", "#02e9fa");
    }

    @FXML
    private void preEditCard(MouseEvent e)
    {
        Button edit = (Button)e.getSource();
        MainController.updateStyle(edit, "-fx-background-color", "#02e9fa");
    }

    @FXML
    private void deleteCard(MouseEvent e)
    {
        Button del = (Button)e.getSource();
        MainController.updateStyle(del, "-fx-background-color", "#3d434d");
        //this situation never happens but who knows
        if(!CardView.fixed)
            return;

        //change card satus sp database knows it is for deletion
        cardExplorationView.card.forget();
        MainController.cardCol.updateDb();

        //interface settings
        explorationView.setImage(null);
        cardsPane.getChildren().remove((Node)cardExplorationView);
        cardNameField.setText("");
        cardDescriptionArea.setText("");
        stopCardExplore();
    }

    @FXML
    private void editCard(MouseEvent e)
    {
        Button edit = (Button)e.getSource();
        MainController.updateStyle(edit, "-fx-background-color", "#3d434d");
        //this situation never happens but who knows
        if(!CardView.fixed)
            return;
        //we can edit text fields from now
        cardNameField.setEditable(true);
        cardDescriptionArea.setEditable(true);
    }


    private void stopEdit()
    {
        //we CANNOT edit text fields from now
        cardNameField.setEditable(false);
        cardDescriptionArea.setEditable(false);
        //get current explored card and set new info for it. After that card changes status to 'updated'
        CardCollection.Card card = cardExplorationView.card;
        card.setInfo(new DbManager.dbItem("", cardNameField.getText(), cardDescriptionArea.getText()));
        //database already has access to all cards and changes info if card has corresponding status
        MainController.cardCol.updateDb();
    }

}
