# JavafxCardCollection
Project is made to get familiar with javafx. It is card collection app, which stores pictures in the same format. Application creates and updates simple sqlite database 

# How to build
- Download files
- Download maven
- Go to folder containing downloaded files and run `mvn package`
- You can find `cardsCollection-1.0-SNAPSHOT.jar` file in `target` folder
- You can copy this file wherever you want. Before running, create this folder structure in the `.jar` parent folder: `src -> database -> cardPics, dbmeta`.
This record means that `src` folder contains `database` folder, which contains `cardPics` and `dbmeta` folders

# How to run
- Download Java RE
- Download JavaFX SDK
- Go to `.jar` file folder and run `java --module-path <javafx-sdk-path>\lib --add-modules javafx.controls,javafx.swing,javafx.fxml -jar cardsCollection-1.0-SNAPSHOT.jar`.
Where `<javafx-sdk-path>` is a path to JavaFX SDK you downloaded and `\lib` is folder containing `javafx.controls,javafx.swing,javafx.fxml` .jar files

# How to use
- You can add image and explore it by clicking on it.
- While exploring you can `delete` `edit` or `close` to stop exploring picture
- When `edit` button pressed, you can change title and description which are text areas under the exploration picture
- Edit is ended if you `close` exploration window or start exploring new picture by clicking on it 
