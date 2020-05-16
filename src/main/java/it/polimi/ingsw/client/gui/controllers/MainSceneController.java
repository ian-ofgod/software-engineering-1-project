package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUIController;
import it.polimi.ingsw.client.gui.GUIEngine;
import it.polimi.ingsw.model.Position;
import it.polimi.ingsw.model.board.BlockType;
import it.polimi.ingsw.utility.messages.sets.ChosenBlockTypeSetMessage;
import it.polimi.ingsw.utility.messages.sets.ChosenPositionSetMessage;
import it.polimi.ingsw.utility.messages.sets.SelectedPawnSetMessage;
import it.polimi.ingsw.view.modelview.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.scene.image.ImageView;

import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainSceneController extends GUIController {

    private static final double BOARD_PADDING_RATIO = 0.74;
    private static final double BOARD_PADDING_PERCENTAGE = 0.13;
    private static final int BOARD_SIZE = 5;

    /* ===== FXML elements ===== */
    @FXML
    private Label phaseLabel;
    @FXML
    private HBox blockTypesHBox;
    @FXML
    private GridPane mainGridPane;

    @FXML
    private GridPane boardGridPane;
    @FXML
    private AnchorPane boardAnchorPane;

    @FXML
    private Label clientPlayerNameLabel;
    @FXML
    private Label enemy1PlayerNameLabel;
    @FXML
    private Label enemy2PlayerNameLabel;

    @FXML
    private ImageView clientPlayerCardImageView;
    @FXML
    private ImageView enemy1PlayerCardImageView;
    @FXML
    private ImageView enemy2PlayerCardImageView;


    /* ===== FXML Properties ===== */
    private DoubleProperty boardPaddingPercentage = new SimpleDoubleProperty(BOARD_PADDING_PERCENTAGE);
    private ImageView[][] enlightenedImageViewsArray = new ImageView[BOARD_SIZE][BOARD_SIZE];

    /* ===== Variables ===== */
    private ArrayList<Position> clickableCells = new ArrayList<>();

    /* ===== FXML Set Up and Bindings ===== */
   @FXML
    public void initialize() {
       phaseLabel.setText("");

       //player cards dimensions bindings
       clientPlayerCardImageView.fitWidthProperty().bind(mainGridPane.widthProperty().divide(6));
       clientPlayerCardImageView.fitHeightProperty().bind(mainGridPane.heightProperty().divide(2.5));
       enemy1PlayerCardImageView.fitWidthProperty().bind(mainGridPane.widthProperty().divide(12));
       enemy1PlayerCardImageView.fitHeightProperty().bind(mainGridPane.heightProperty().divide(5));
       enemy2PlayerCardImageView.fitWidthProperty().bind(mainGridPane.widthProperty().divide(12));
       enemy2PlayerCardImageView.fitHeightProperty().bind(mainGridPane.heightProperty().divide(5));

       //board dimensions bindings
       boardAnchorPane.setPrefWidth(mainGridPane.heightProperty().multiply(0.5).getValue());
       boardAnchorPane.setPrefHeight(mainGridPane.heightProperty().multiply(0.5).getValue());
       boardAnchorPane.maxWidthProperty().bind(mainGridPane.heightProperty().multiply(0.5));
       boardAnchorPane.maxHeightProperty().bind(boardAnchorPane.widthProperty());

       //TODO: improve padding management
       boardGridPane.paddingProperty().bind((Bindings.createObjectBinding(() -> new Insets(boardGridPane.widthProperty().multiply(BOARD_PADDING_PERCENTAGE).doubleValue()), boardGridPane.widthProperty().multiply(BOARD_PADDING_PERCENTAGE))));
       System.out.println(boardGridPane.paddingProperty());

       //binds HBox (used for BlockType selection) padding and internal spacing
       blockTypesHBox.spacingProperty().bind(blockTypesHBox.widthProperty().divide(10));
       //TODO: add padding to the blockType selection HBox
//       blockTypesHBox.paddingProperty().bind((Bindings.createObjectBinding(() -> new Insets(blockTypesHBox.widthProperty().divide(10).doubleValue()), blockTypesHBox.widthProperty().divide(10))));
   }


   public void buildMainScene() {
       updatePlayersName();
       updatePlayersCard();
       updateBoard();
       loadEnlightenedImageViews();

       //TODO: remove the following 3 lines, just testing
//       ((GUIEngine)clientView.getUserInterface()).updateModelView();
//       clientView.getUserInterface().refreshView();
   }

   //TODO: remove this method and the relative button. It is just for test
   public void updateBoardTest() {
       ArrayList<BlockType> availableBlockTypes = new ArrayList<>();
       availableBlockTypes.add(BlockType.LEVEL1);
//       availableBlockTypes.add(BlockType.DOME);
       clientView.getUserInterface().onChosenBlockTypeRequest(availableBlockTypes);
   }


    /**
     * clears the boardGridPane and renders the updated board from the modelView loading first the block types imageViews
     * and then the pawns imageViews
     */
   public void updateBoard() {
       boardGridPane.getChildren().clear();
       ModelView modelView = clientView.getModelView();
       for(int i = 0; i < modelView.getMatrix().length; i++){
           for(int  j = 0; j < modelView.getMatrix()[0].length; j++){
               if (modelView.getMatrix()[i][j].getPeek() != BlockType.TERRAIN) {
                   System.out.println("blockType: " + modelView.getMatrix()[i][j].getPeek().getLevel());
                   Image blockImage = new Image("images/board/block_lv_" + modelView.getMatrix()[i][j].getPeek().getLevel() +".png");
                   ImageView blockImageView = new ImageView(blockImage);
                   blockImageView.setPreserveRatio(true);
                   blockImageView.fitWidthProperty().bind(boardGridPane.widthProperty().divide(BOARD_SIZE).multiply(BOARD_PADDING_RATIO));
                   blockImageView.fitHeightProperty().bind(boardGridPane.heightProperty().divide(BOARD_SIZE).multiply(BOARD_PADDING_RATIO));
                   boardGridPane.add(blockImageView, j, i);
               }
           }
       }
       updatePawns();
   }

   private void updatePawns() {
       ModelView modelView = clientView.getModelView();
       ArrayList<PawnView> pawnsList = modelView.getPawns();
       for (PawnView pawn: pawnsList) {
           Image pawnImage = new Image("images/board/pawn_" + pawn.getColor() + ".png");
           ImageView pawnImageView = new ImageView(pawnImage);
           pawnImageView.setPreserveRatio(true);
           pawnImageView.fitWidthProperty().bind(boardGridPane.widthProperty().divide(BOARD_SIZE).multiply(BOARD_PADDING_RATIO));
           pawnImageView.fitHeightProperty().bind(boardGridPane.heightProperty().divide(BOARD_SIZE).multiply(BOARD_PADDING_RATIO));
           boardGridPane.add(pawnImageView, pawn.getPawnPosition().getY(), pawn.getPawnPosition().getX());
       }

   }

   public void updatePlayersName() {
       ModelView modelView = clientView.getModelView();
       clientPlayerNameLabel.setText(clientView.getName());
       ArrayList<String> enemiesNames = modelView.getEnemiesNames(clientView.getName());
       if (enemiesNames != null) {
           if (enemiesNames.size() >= 1) enemy1PlayerNameLabel.setText(enemiesNames.get(0));
           if (enemiesNames.size() == 2) enemy2PlayerNameLabel.setText(enemiesNames.get(1));
       }
   }

   public void updatePlayersCard() {
       ModelView modelView = clientView.getModelView();

       //gets the Card of the client player from the modelView and renders the proper Image, Name and Description
       CardView clientPlayerCard = modelView.getClientPlayerCard(clientView.getName());
       if (clientPlayerCard != null) {
           Image clientPlayerCardImage = new Image("images/cards/card_" + clientPlayerCard.getId() + ".png");
           clientPlayerCardImageView.setImage(clientPlayerCardImage);
       }
       ArrayList<CardView> enemiesCards = modelView.getEnemiesCards(clientView.getName());
       if (enemiesCards != null) {
           if (enemiesCards.size() >= 1)  {
               Image enemy1CardImage = new Image("images/cards/card_" + enemiesCards.get(0).getId() + ".png");
               enemy1PlayerCardImageView.setImage(enemy1CardImage); //enemiesCards.get(0)
           }
           if (enemiesCards.size() == 2) {
               Image enemy2CardImage = new Image("images/cards/card_" + enemiesCards.get(1).getId() + ".png");
               enemy2PlayerCardImageView.setImage(enemy2CardImage);
           }
       }
   }

   private void loadEnlightenedImageViews() {
       ModelView modelView = clientView.getModelView();
       String playerColor = modelView.getPlayerColor(clientView.getName());
       //loads the panes that will be used to enlighten the board cells
       for (int i = 0; i < BOARD_SIZE; i++) {
           for (int j = 0; j < BOARD_SIZE; j++) {
               Image enlightenedImage = new Image("images/board/enlightened_cell_" + playerColor +".png");
               ImageView enlightenedImageView = new ImageView(enlightenedImage);
               enlightenedImageView.setOpacity(0.5);
               enlightenedImageViewsArray[i][j] = enlightenedImageView;
               enlightenedImageView.setPreserveRatio(true);
               enlightenedImageView.setVisible(false);
               enlightenedImageView.fitWidthProperty().bind(boardGridPane.widthProperty().divide(BOARD_SIZE).multiply(BOARD_PADDING_RATIO));
               enlightenedImageView.fitHeightProperty().bind(boardGridPane.heightProperty().divide(BOARD_SIZE).multiply(BOARD_PADDING_RATIO));
               boardGridPane.add(enlightenedImageView, j, i);
           }
       }
   }


   public void enablePawnSelection(ArrayList<Position> availablePositions) {
       phaseLabel.setText("Select one of your pawns!");
       for (Position position : availablePositions) {
           //makes the ImageViews visible
           enlightenedImageViewsArray[position.getX()][position.getY()].setVisible(true);
           //adds an action recognizer to the ImageView
           enlightenedImageViewsArray[position.getX()][position.getY()].setOnMouseClicked(e -> {
               Node source = (Node)e.getSource();
               int colIndex = GridPane.getColumnIndex(source);
               int rowIndex = GridPane.getRowIndex(source);
               //cols -> x, rows -> y
               chosenPawn(new Position(rowIndex, colIndex));
           });
       }
   }

   private void chosenPawn(Position chosenPawnPosition) {
       phaseLabel.setText("");
       System.out.println("chosenPawnPosition:" + chosenPawnPosition.getX() + " " + chosenPawnPosition.getY());
       //TODO: scommentare la riga successiva, è corretta

//        clientView.update(new SelectedPawnSetMessage(chosenPawnPosition));
       clearEnlightenedImageViews();
   }

    private void clearEnlightenedImageViews() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                enlightenedImageViewsArray[i][j].setVisible(false);
                enlightenedImageViewsArray[i][j].setOnMouseClicked(null);
            }
        }
    }

    public void chooseMovePosition(ArrayList<Position> availablePositions) {
       phaseLabel.setText("Choose a position to Move!");
       enablePositionSelection(availablePositions);
    }

    public void chooseConstructPosition(ArrayList<Position> availablePositions) {
       phaseLabel.setText("Choose a position to Build!");
       enablePositionSelection(availablePositions);
    }

    public void enablePositionSelection(ArrayList<Position> availablePositions) {
        for (Position position : availablePositions) {
            //makes the ImageViews visible
            enlightenedImageViewsArray[position.getX()][position.getY()].setVisible(true);
            //adds an action recognizer to the ImageView
            enlightenedImageViewsArray[position.getX()][position.getY()].setOnMouseClicked(e -> {
                Node source = (Node)e.getSource();
                int colIndex = GridPane.getColumnIndex(source);
                int rowIndex = GridPane.getRowIndex(source);
                //cols -> x, rows -> y
                setPosition(new Position(rowIndex, colIndex));
            });
        }
    }

    private void setPosition(Position chosenPosition) {
        phaseLabel.setText("");
        System.out.println("chosenPosition:" + chosenPosition.getX() + " " + chosenPosition.getY());
        //TODO: scommentare la riga successiva, è corretta
//        clientView.update(new ChosenPositionSetMessage(chosenPosition));
        clearEnlightenedImageViews();
    }

    public void chooseBlockType(ArrayList<BlockType> availableBlockTypes) {
        phaseLabel.setText("Choose a Block Type!");

        for (BlockType blockType : availableBlockTypes) {
            Image blockTypeImage = new Image("images/board/block_lv_" + blockType.getLevel() + ".png");
            ImageView blockTypeImageView = new ImageView(blockTypeImage);
            blockTypeImageView.setPreserveRatio(true);
            blockTypeImageView.setId(String.valueOf(blockType.getLevel()));
            blockTypeImageView.fitWidthProperty().bind(blockTypesHBox.widthProperty().divide(availableBlockTypes.size()));
            blockTypeImageView.fitHeightProperty().bind(blockTypesHBox.heightProperty());
            blockTypesHBox.getChildren().add(blockTypeImageView);
            blockTypeImageView.setOnMouseClicked(e -> {
                Node source = (Node)e.getSource();
                int blockTypeLevel = Integer.parseInt(source.getId());
                System.out.printf("blockType: %d %n", blockTypeLevel);
                setBlockType(blockTypeLevel, availableBlockTypes);
            });
        }

        blockTypesHBox.setVisible(true);
    }

    private void setBlockType(int chosenBlockTypeLevel, ArrayList<BlockType> availableBlockTypes) {
       phaseLabel.setText("");
       BlockType chosenBlockType = BlockType.LEVEL1;
       for(BlockType blockType : availableBlockTypes) {
           if (blockType.getLevel() == chosenBlockTypeLevel) chosenBlockType = blockType;
       }

       System.out.println("chosenBlockType.getLevel(): " + chosenBlockType.getLevel());
       //TODO: scommentare la riga successiva, è corretta
//       clientView.update(new ChosenBlockTypeSetMessage(chosenBlockType));

        blockTypesHBox.getChildren().clear();
        blockTypesHBox.setVisible(false);
    }
}
