package com.example.cardscollection;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class CardCollection
{
    public static class Card
    {
        final static int cardPreviewWidth = 144;
        final static int cardPreviewHeight = 190;

        final static int cardExploreWidth = 280;
        final static int cardExploreHeight = 377;

        private DbManager.dbItem info;

        private Image image;

        private boolean updated = false;
        private boolean deleted = false;
        public Card(Image image, DbManager.dbItem info)
        {
            this.image = image;
            this.info = info;
        }

        public Card(Image image, String filename)
        {
            this.info = new DbManager.dbItem(filename, "", "");
            this.image = image;
        }

        public DbManager.dbItem getInfo()
        {
            return info;
        }

        public Image getImage()
        {
            return image;
        }


        public void setInfo(DbManager.dbItem info)
        {
            this.info.cardname = info.cardname;
            this.info.description = info.description;
            updated = true;
        }

        public void forget()
        {
            deleted = true;
        }
    }


    public List<Card> cardList;
    private final String collectionFolder = "./src/database/cardPics/";
    private final String lastGeneratedIdFile = "./src/database/dbmeta/lastid.txt";
    public CardCollection()
    {
        refreshCollection();
    }

    private void refreshCollection()
    {
        if(cardList != null)
            cardList.clear();
        cardList = loadImages();
    }

    public void updateDb()
    {
        DbManager dbManager = DbManager.getManager();
        for(Card c : cardList)
        {
            if(c.updated)
            {
                dbManager.update(c.getInfo());
                c.updated = false;
            }
            if(c.deleted)
            {
                 dbManager.delete(c.getInfo().filename);
                 Path delPath = Paths.get(collectionFolder+c.getInfo().filename);
                 try
                 {
                     Files.delete(delPath);
                 }
                 catch(Exception e)
                 {
                    e.printStackTrace();
                 }
            }
        }
        cardList.removeIf(card -> card.deleted);
    }

    public static boolean isImage(File file)
    {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg");
    }


    //TODO: add loadImage function to reduce addImage function time(replace refreshCollection on the end of addCard)
    private List<Card> loadImages()
    {
        List<Card> cards = new ArrayList<>();
        DbManager dbmanager = DbManager.getManager();
        ArrayList<DbManager.dbItem> collinfo = (ArrayList<DbManager.dbItem>)dbmanager.dbExpose();
        File folder = new File(collectionFolder);

        for(DbManager.dbItem item : collinfo)
        {
            File imgfile = new File(folder, item.filename);
            System.out.println("Reading "+collectionFolder+item.filename);
            if(imgfile.isFile() && isImage(imgfile))
            {
                Image img = new Image(imgfile.toURI().toString(), Card.cardExploreWidth, Card.cardExploreHeight, false, true, true);
                cards.add(new Card(img, item));
            }
            else
            {
                System.out.println("File? - "+imgfile.isFile()+"Is image? - "+isImage(imgfile));
            }
        }

        return cards;
    }

    public void addCard(Card card)
    {
        //write last id
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(lastGeneratedIdFile, false)))
        {
            bw.write(uniqueId.toString()+"\n");
            bw.flush();
        }catch(Exception e){ e.printStackTrace();}

        //add card to database
        DbManager manager = DbManager.getManager();
        manager.update(new DbManager.dbItem(card.getInfo().filename, card.getInfo().cardname, card.getInfo().description));

        File imgFile = new File(collectionFolder+card.getInfo().filename);
        BufferedImage bimg = SwingFXUtils.fromFXImage(card.getImage(), null);
        try
        {
            ImageIO.write(bimg, "png", imgFile);
            bimg.flush();
        }catch(Exception e){ e.printStackTrace(); }

        //TODO: replace by loadImage
        refreshCollection();
//        for(DbManager.dbItem item : manager.dbExpose())
//        {
//            System.out.println(item);
//        }

    }

    //functions below are supposed to be in some kind of EditorController class ????
    private static Integer uniqueId = -1;

    private int generateId()
    {
        if(uniqueId == -1)
        {
            FileReader fr = null;
            try
            {
                fr = new FileReader(lastGeneratedIdFile);
            }
            catch(FileNotFoundException e1)
            {
                try(BufferedWriter bw = new BufferedWriter(new FileWriter(lastGeneratedIdFile)))
                {
                    bw.write("0\n");
                    bw.flush();
                }catch(Exception e2){ e2.printStackTrace(); }
            }
            if(fr != null)
            {
                try {fr.close();} catch(Exception e){}
            }
            try(BufferedReader br = new BufferedReader(new FileReader(lastGeneratedIdFile)))
            {
                uniqueId = Integer.parseInt(br.readLine());
            }catch(Exception e){ e.printStackTrace(); }
        }
        uniqueId++;
        return uniqueId;
    }
    public Card createCard(File imgFile)
    {
        Image img = new Image(imgFile.toURI().toString(), CardCollection.Card.cardExploreWidth, CardCollection.Card.cardExploreHeight, false, true, false);
        return new Card(img, new DbManager.dbItem("card"+generateId()+".png", "", ""));
    }


}
