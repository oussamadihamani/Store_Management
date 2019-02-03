package Controllers;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

import Models.Product;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import fonts.fontawesome.FontAwesomeIcon;
import fonts.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;


public class StockCtrl {

    @FXML
    AnchorPane stockForm;

    @FXML
    JFXComboBox<String> searchTypeCombo = new JFXComboBox<>();

    @FXML
    JFXComboBox<String> searchMarqueCombo = new JFXComboBox<>();

    @FXML
    JFXComboBox<String> searchPiecesCombo = new JFXComboBox<String>();

    @FXML
    JFXComboBox<String> searchPriceCombo = new JFXComboBox<String>();

    @FXML
    JFXTextField searchPriceTxt = new JFXTextField();

    @FXML
    JFXComboBox<String> searchQuantityCombo = new JFXComboBox<>();

    @FXML
    JFXTextField searchQuantityTxt = new JFXTextField();

    @FXML
    TableView<Product> tableview;

    @FXML
    TableColumn<Product, Integer> colProductID;

    @FXML
    TableColumn<Product, String> colProductType;

    @FXML
    TableColumn<Product, String> colProductMarque;

    @FXML
    TableColumn<Product, Integer> colPieces;

    @FXML
    TableColumn<Product, Integer> colProductPrice;

    @FXML
    TableColumn<Product, Integer> colProductQuantity;

    @FXML
    TableColumn<Product, Boolean> colProductOption;

    @FXML
    TableColumn<Product, Boolean> colProductEdit;

    @FXML
    TableColumn<Product, Boolean> colProductDelete;

    Connection cnx = AcceuilCtrl.connect;

    private static ObservableList<Product> data;

    @FXML
    void initialize() {

        searchTypeCombo.getItems().clear();
        searchMarqueCombo.getItems().clear();
        searchPiecesCombo.getItems().clear();

        String productType = "select distinct productType from Product order by productType;";

        try {
            ResultSet rs = cnx.createStatement().executeQuery(productType);
            while (rs.next()) {
                searchTypeCombo.getItems().add(rs.getString("productType"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        searchTypeCombo.setEditable(true);

        String productMarque = "select distinct productMarque from Product order by productMarque;";

        try {
            ResultSet res = cnx.createStatement().executeQuery(productMarque);
            while (res.next()) {
                searchMarqueCombo.getItems().add(res.getString("productMarque"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        searchMarqueCombo.setEditable(true);

        String productPieces = "select distinct nombrePiece from Product order by nombrePiece;";

        try {
            ResultSet res = cnx.createStatement().executeQuery(productPieces);
            while (res.next()) {
                searchPiecesCombo.getItems().add(res.getString("nombrePiece"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        searchPiecesCombo.setEditable(true);

        searchPriceCombo.getItems().setAll("<", "<=", "=", ">=", ">");

        searchQuantityCombo.getItems().setAll("<", "<=", "=", ">=", ">");

        searchTypeCombo.setValue(null);
        searchMarqueCombo.setValue(null);
        searchPiecesCombo.setValue(null);
        searchPriceCombo.setValue(null);
        searchPriceTxt.setText("");
        searchQuantityCombo.setValue(null);
        searchQuantityTxt.setText("");

        searchTypeCombo.setLabelFloat(true);
        searchMarqueCombo.setLabelFloat(true);
        searchPiecesCombo.setLabelFloat(true);
        searchPriceCombo.setLabelFloat(true);
        searchPriceTxt.setLabelFloat(true);
        searchQuantityCombo.setLabelFloat(true);
        searchQuantityTxt.setLabelFloat(true);

        Tooltip tooltip = new Tooltip();
        tooltip.setText("Entrez des chiffres numériques !");

        searchPiecesCombo.getEditor().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    searchPiecesCombo.getEditor().setText(newValue.replaceAll("[^\\d]", ""));

                    Bounds boundsInScreen = searchPiecesCombo.localToScreen(searchPiecesCombo.getBoundsInLocal());

                    tooltip.show(stockForm.getScene().getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                    tooltip.setAutoHide(true);
                }
            }
        });

        AddUserCtrl.onlynumbersField(tooltip, searchPriceTxt, stockForm);

        AddUserCtrl.onlynumbersField(tooltip, searchQuantityTxt, stockForm);


        /***************************************************************************************************************
         *****************************            Initialize Colmuns values                *****************************
         **************************************************************************************************************/
        colProductID.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));
        colProductType.setCellValueFactory(new PropertyValueFactory<Product, String>("productType"));
        colProductMarque.setCellValueFactory(new PropertyValueFactory<Product, String>("productMarque"));
        colPieces.setCellValueFactory(new PropertyValueFactory<Product, Integer>("nombrePiece"));
        colProductPrice.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productPrice"));
        colProductQuantity.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productQuantity"));

        colProductEdit.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Product, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });

        colProductDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Product, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });


        /***************************************************************************************************************
         ********************************            Set Columns width                 *********************************
         **************************************************************************************************************/
        colProductID.prefWidthProperty().bind(tableview.widthProperty().divide(10));
        colProductType.prefWidthProperty().bind(tableview.widthProperty().divide(6));
        colProductMarque.prefWidthProperty().bind(tableview.widthProperty().divide(6));
        colPieces.prefWidthProperty().bind(tableview.widthProperty().divide(6));
        colProductPrice.prefWidthProperty().bind(tableview.widthProperty().divide(8));
        colProductQuantity.prefWidthProperty().bind(tableview.widthProperty().divide(8));
        colProductOption.prefWidthProperty().bind(tableview.widthProperty().divide(6.9));
        colProductEdit.prefWidthProperty().bind(tableview.widthProperty().divide(13.8));
        colProductDelete.prefWidthProperty().bind(tableview.widthProperty().divide(13.8));

        // Delete sub-columns height
        colProductEdit.setStyle("-fx-pref-height: 0;");
        colProductDelete.setStyle("-fx-pref-height: 0;");


        /***************************************************************************************************************
         ****************************            Disnable Sort by columns                 ******************************
         **************************************************************************************************************/
        colProductID.setSortable(false);
        colProductType.setSortable(false);
        colProductMarque.setSortable(false);
        colPieces.setSortable(false);
        colProductPrice.setSortable(false);
        colProductQuantity.setSortable(false);
        colProductOption.setSortable(false);
        colProductEdit.setSortable(false);
        colProductDelete.setSortable(false);


        /***************************************************************************************************************
         ****************************            Disable Colmuns Resize                 ****************************
         **************************************************************************************************************/
        colProductID.setResizable(false);
        colProductType.setResizable(false);
        colProductMarque.setResizable(false);
        colPieces.setResizable(false);
        colProductPrice.setResizable(false);
        colProductQuantity.setResizable(false);
        colProductOption.setResizable(false);
        colProductEdit.setResizable(false);
        colProductDelete.setResizable(false);


        /***************************************************************************************************************
         ****************************            Disable Colmuns Reorder                 *******************************
         **************************************************************************************************************/
        tableview.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) {
                TableHeaderRow header = (TableHeaderRow) tableview.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });

        //Call the function that fills the tableview
        try {
            buildData("select * from Product order by productType");
        } catch (Exception ce) {
            ce.toString();
        }
/*
        tableview.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 1) {
                onEdit();
            }
        });
*/

    }


    /***************************************************************************************************************
     **************************            Insert Data in the TableView              *******************************
     **************************************************************************************************************/
    private void buildData(String slqStatement) {

        data = FXCollections.observableArrayList();

        colProductEdit.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
                    @Override
                    public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
                        return new buttonEdit(tableview);
                    }
                });

        colProductDelete.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
                    @Override
                    public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
                        return new buttonDelete(tableview);
                    }
                });

        try {

            ResultSet rs = AcceuilCtrl.connect.createStatement().executeQuery(slqStatement);

            while (rs.next()) {

                Product product = new Product();

                product.productID.set(rs.getInt("productID"));
                product.productType.set(rs.getString("productType"));
                product.productMarque.set(rs.getString("productMarque"));
                product.nombrePiece.set(rs.getInt("nombrePiece"));
                product.productPrice.set(rs.getInt("productPrice"));
                product.productQuantity.set(rs.getInt("productQuantity"));

                data.add(product);
            }

            tableview.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }


    /***************************************************************************************************************
     ************************            Refresh Button on Action Function             *****************************
     **************************************************************************************************************/
    public void refreshProductTable(ActionEvent evnt) {
        initialize();
    }


    /***************************************************************************************************************
     ****************************           Open Add New Product Window            *********************************
     **************************************************************************************************************/
    public void addProduct(ActionEvent event) throws IOException {

        Stage newStage = new Stage();
        newStage.initStyle(StageStyle.UNDECORATED);
        AnchorPane anchorPanePopup = FXMLLoader.load(getClass().getResource("/Views/AddProduct.fxml"));
        Scene scene = new Scene(anchorPanePopup);
        newStage.setScene(scene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.showAndWait();
    }


    public void searchProduct(ActionEvent event) {

        String sql;
        String priceEvaluation;
        String quantityEvaluation;

        if (searchPriceCombo.getValue() != null && !searchPriceTxt.getText().equals("")) {
            priceEvaluation = searchPriceCombo.getValue()+" "+searchPriceTxt.getText();
        } else {
            priceEvaluation = "like '%"+searchPriceTxt.getText()+"%'";
        }

        if (searchQuantityCombo.getValue() != null && !searchQuantityTxt.getText().equals("")) {
            quantityEvaluation = searchQuantityCombo.getValue() + " " + searchQuantityTxt.getText();
        } else {
            quantityEvaluation = "like '%"+searchQuantityTxt.getText()+"%'";
        }

        sql = "SELECT * FROM Product WHERE productType like '%" + searchTypeCombo.getEditor().getText() + "%'" +
                    " and productMarque like '%" + searchMarqueCombo.getEditor().getText() + "%'" +
                    " and  nombrePiece like '%" + searchPiecesCombo.getEditor().getText() + "%'" +
                    " and productPrice "+ priceEvaluation +" and productQuantity "+ quantityEvaluation +" order by productType";


        buildData(sql);
    }


    private class buttonDelete extends TableCell<Product, Boolean> {


        final FontAwesomeIconView deleteIcon = new FontAwesomeIconView();

        buttonDelete(final TableView<Product> tblView) {

            deleteIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    if (event.getButton() == MouseButton.PRIMARY) { // check the primary mouse buttonCredit

                        int selectedIndex = getTableRow().getIndex();
                        Product toRemove = tblView.getItems().get(selectedIndex);

                        String type = toRemove.productType.get();
                        String marque = toRemove.productMarque.get();
                        int nombrePieces = toRemove.nombrePiece.get();

                        int toDeleteID = (toRemove.productID).intValue();

                        String sqldelete = "delete from Product where productID = " + toDeleteID + " ;";

                        Alert deleteProductAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        deleteProductAlert.setTitle("Supprimer un produit");
                        deleteProductAlert.setHeaderText(null);
                        deleteProductAlert.setContentText("Voulez vous supprimer le produit suivant ? \n \n Type : " + type + " \n Marque : " + marque + " \n Nombre de pièces : " + nombrePieces);

                        Button exitButton = (Button) deleteProductAlert.getDialogPane().lookupButton(
                                ButtonType.OK
                        );
                        exitButton.setText("Supprimer");

                        Button cancelButton = (Button) deleteProductAlert.getDialogPane().lookupButton(
                                ButtonType.CANCEL
                        );
                        cancelButton.setText("Annuler");


                        Optional<ButtonType> result = deleteProductAlert.showAndWait();

                        if (result.get() == ButtonType.OK) {
                            try {
                                PreparedStatement delete = AcceuilCtrl.connect.prepareStatement(sqldelete);
                                delete.execute();

                                Alert productDeleted = new Alert(Alert.AlertType.INFORMATION);
                                productDeleted.setTitle("Supprimer un produit");
                                productDeleted.setHeaderText(null);
                                productDeleted.setContentText("Le produit à été supprimé avec succès !");
                                Optional<ButtonType> rslt = productDeleted.showAndWait();

                                refreshProductTable(new ActionEvent());
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            });
        }

        //Display icon if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            deleteIcon.setIcon(FontAwesomeIcon.ICON_TRASH);
            deleteIcon.getStyleClass().add("deleteIcon");
            super.updateItem(t, empty);
            if (!empty) setGraphic(deleteIcon);

        }
    }


    private class buttonEdit extends TableCell<Product, Boolean> {

        final FontAwesomeIconView editIcon = new FontAwesomeIconView();

        buttonEdit(final TableView<Product> tblView) {

            editIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    if (event.getButton() == MouseButton.PRIMARY) { // check the primary mouse buttonCredit

                        int selectedIndex = getTableRow().getIndex();
                        Product toEdit = tblView.getItems().get(selectedIndex);
                        final int toEditID = (toEdit.productID).intValue();
                        final String oldPrice = (toEdit.productPrice.getValue()).toString();
                        final String oldQuantity = (toEdit.productQuantity.getValue()).toString();

                        Pane head = new Pane();
                        head.setStyle("-fx-background-color:  #1589FF; -fx-pref-width: 450; -fx-pref-height: 100");

                        Label title = new Label("Modifier un produit");
                        title.setStyle("-fx-font-size: 22px; -fx-text-fill: white");
                        title.layoutXProperty().setValue(20);
                        title.layoutYProperty().bind(head.heightProperty().subtract(title.heightProperty()).divide(2));

                        FontAwesomeIconView titleIcon = new FontAwesomeIconView();
                        titleIcon.setIcon(FontAwesomeIcon.ICON_EDIT);
                        titleIcon.layoutXProperty().setValue(390);
                        titleIcon.layoutYProperty().bind(head.heightProperty().subtract(titleIcon.heightProperty()).divide(2));
                        titleIcon.setStyle("-fx-font-family:\"FontAwesome\"; -fx-font-size: 35; -fx-text-fill: white");

                        head.getChildren().addAll(title, titleIcon);

                        Label required = new Label("(*) Champ Obligatoire");
                        required.setStyle("-fx-font-size: 10; -fx-text-fill: #eb3730");
                        required.layoutXProperty().setValue(30);
                        required.layoutYProperty().setValue(110);

                        Label typeLabel = new Label("Type :");
                        typeLabel.setStyle("-fx-font-size: 15");
                        typeLabel.layoutXProperty().setValue(30);
                        typeLabel.layoutYProperty().setValue(175);

                        FontAwesomeIconView typeRequired = new FontAwesomeIconView();
                        typeRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        typeRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        typeRequired.layoutXProperty().setValue(80);
                        typeRequired.layoutYProperty().setValue(175);

                        JFXComboBox<String> typeCombo = new JFXComboBox<String>();
                        typeCombo.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        typeCombo.layoutXProperty().setValue(245);
                        typeCombo.layoutYProperty().setValue(172);
                        typeCombo.setEditable(true);
                        typeCombo.setDisable(true);

                        Label marqueLabel = new Label("Marque :");
                        marqueLabel.setStyle("-fx-font-size: 15");
                        marqueLabel.layoutXProperty().setValue(30);
                        marqueLabel.layoutYProperty().setValue(265);

                        FontAwesomeIconView marqueRequired = new FontAwesomeIconView();
                        marqueRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        marqueRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        marqueRequired.layoutXProperty().setValue(100);
                        marqueRequired.layoutYProperty().setValue(265);

                        JFXComboBox<String> marqueCombo = new JFXComboBox<String>();
                        marqueCombo.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        marqueCombo.layoutXProperty().setValue(245);
                        marqueCombo.layoutYProperty().setValue(262);
                        marqueCombo.setEditable(true);
                        marqueCombo.setDisable(true);

                        Label piecesLabel = new Label("Nombre de pièces :");
                        piecesLabel.setStyle("-fx-font-size: 15");
                        piecesLabel.layoutXProperty().setValue(30);
                        piecesLabel.layoutYProperty().setValue(355);

                        FontAwesomeIconView piecesRequired = new FontAwesomeIconView();
                        piecesRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        piecesRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        piecesRequired.layoutXProperty().setValue(182);
                        piecesRequired.layoutYProperty().setValue(355);

                        JFXTextField piecesTxt = new JFXTextField();
                        piecesTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        piecesTxt.layoutXProperty().setValue(245);
                        piecesTxt.layoutYProperty().setValue(355);
                        piecesTxt.setDisable(true);

                        Label priceLabel = new Label("Prix :");
                        priceLabel.setStyle("-fx-font-size: 15");
                        priceLabel.layoutXProperty().setValue(30);
                        priceLabel.layoutYProperty().setValue(445);

                        FontAwesomeIconView priceRequired = new FontAwesomeIconView();
                        priceRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        priceRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        priceRequired.layoutXProperty().setValue(72);
                        priceRequired.layoutYProperty().setValue(445);

                        JFXTextField priceTxt = new JFXTextField();
                        priceTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        priceTxt.layoutXProperty().setValue(245);
                        priceTxt.layoutYProperty().setValue(445);

                        Label Da = new Label("Da");
                        Da.setStyle("-fx-font-size: 13px");
                        Da.layoutXProperty().setValue(382);
                        Da.layoutYProperty().setValue(449);
                        Da.setDisable(true);

                        Label quantityLabel = new Label("Quantité :");
                        quantityLabel.setStyle("-fx-font-size: 15");
                        quantityLabel.layoutXProperty().setValue(30);
                        quantityLabel.layoutYProperty().setValue(535);

                        FontAwesomeIconView quantityRequired = new FontAwesomeIconView();
                        quantityRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        quantityRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        quantityRequired.layoutXProperty().setValue(112);
                        quantityRequired.layoutYProperty().setValue(535);

                        JFXTextField quantityTxt = new JFXTextField();
                        quantityTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        quantityTxt.layoutXProperty().setValue(245);
                        quantityTxt.layoutYProperty().setValue(535);
                        quantityTxt.setPromptText("Quantité Disponible");

                        JFXButton cancelBtn = new JFXButton(" Annuler");
                        cancelBtn.setStyle("-fx-background-color: #F62817; -fx-text-fill: white; -fx-fill: white; -fx-pref-width: 90; -fx-pref-height: 28; -fx-cursor: hand");
                        cancelBtn.layoutXProperty().setValue(120);
                        cancelBtn.layoutYProperty().setValue(615);
                        cancelBtn.getStyleClass().add("cancelBtn");

                        OctIconView cancelIcon = new OctIconView();
                        cancelIcon.setIcon(OctIcon.X);
                        cancelIcon.setStyle("-fx-font-family: \"Octicons\"; -fx-fill: white; -fx-font-size: 16");
                        cancelBtn.setGraphic(cancelIcon);

                        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (priceTxt.getText().equals(oldPrice) && quantityTxt.getText().equals(oldQuantity)) {
                                    ((Node) event.getTarget()).getScene().getWindow().hide();
                                } else {
                                    AddProductCtrl.cancelEdit(event, cancelBtn);
                                }
                            }
                        });

                        JFXButton confirmBtn = new JFXButton(" Valider");
                        confirmBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-fill: white; -fx-pref-width: 90; -fx-pref-height: 28; -fx-cursor: hand");
                        confirmBtn.layoutXProperty().setValue(240);
                        confirmBtn.layoutYProperty().setValue(615);
                        confirmBtn.getStyleClass().add("confirmBtn");

                        OctIconView confirmIcon = new OctIconView();
                        confirmIcon.setIcon(OctIcon.CHECK);
                        confirmIcon.setStyle("-fx-font-family: \"Octicons\"; -fx-fill: white; -fx-font-size: 17");
                        confirmBtn.setGraphic(confirmIcon);

                        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                String editsql = "update Product set productPrice = " + priceTxt.getText() + ", productQuantity = " + quantityTxt.getText() + " where productID = " + toEditID + ";";
                                PreparedStatement editStatement;

                                if (priceTxt.getText().equals(oldPrice) && quantityTxt.getText().equals(oldQuantity)) {
                                    ((Node) event.getTarget()).getScene().getWindow().hide();
                                } else {
                                    try {
                                        editStatement = cnx.prepareStatement(editsql);
                                        editStatement.execute();
                                        Alert editDone = new Alert(Alert.AlertType.INFORMATION);
                                        editDone.setTitle("Modifier un produit");
                                        editDone.setHeaderText(null);
                                        editDone.setContentText("Le produit à été modifié avec succès !");

                                        Optional<ButtonType> result = editDone.showAndWait();
                                        if (result.get() == ButtonType.OK) {
                                            ((Node) event.getTarget()).getScene().getWindow().hide();
                                            refreshProductTable(event);
                                        }
                                    } catch (SQLException e) {
                                        Alert editRejected = new Alert(Alert.AlertType.INFORMATION);
                                        editRejected.setTitle("Opération échouée");
                                        editRejected.setHeaderText("Impossible de modifier le produit !");

                                        if (priceTxt.getText().equals("")) {
                                            editRejected.setContentText("le champ 'Prix' ne doit pas être vide");
                                        } else if (quantityTxt.getText().equals("") || priceTxt.getText().equals("")) {
                                            editRejected.setContentText("le champ 'Quantité' ne doit pas être vide");
                                        }

                                        editRejected.showAndWait();
                                    }
                                }
                            }
                        });

                        String selectsql = "select * from Product where productID =" + toEditID;
                        try {
                            PreparedStatement initializ = cnx.prepareStatement(selectsql);
                            ResultSet rs = initializ.executeQuery();
                            if (rs.next()) {
                                typeCombo.getSelectionModel().select(rs.getString("productTYpe"));
                                marqueCombo.getSelectionModel().select(rs.getString("productMarque"));
                                piecesTxt.setText(rs.getString("nombrePiece"));
                                priceTxt.setText(rs.getString("productPrice"));
                                quantityTxt.setText(rs.getString("productQuantity"));
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        Stage newStage = new Stage();
                        newStage.initStyle(StageStyle.UNDECORATED);

                        AnchorPane anchorPanePopup = new AnchorPane();
                        anchorPanePopup.setStyle("-fx-background-color: white; -fx-pref-width: 450; -fx-pref-height: 670");
                        anchorPanePopup.getChildren().addAll(head, required, typeLabel, typeRequired, typeCombo, marqueLabel, marqueRequired, marqueCombo,
                                piecesLabel, piecesRequired, piecesTxt, priceLabel, priceRequired, priceTxt, quantityLabel, quantityRequired, quantityTxt,
                                cancelBtn, confirmBtn);

                        Scene scene = new Scene(anchorPanePopup);

                        Tooltip tooltip = new Tooltip("Ce champ ne doit contenir que des chiffres !");

                        priceTxt.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                String newValue) {
                                if (!newValue.matches("\\d*")) {
                                    priceTxt.setText(newValue.replaceAll("[^\\d]", ""));


                                    Bounds boundsInScreen = priceTxt.localToScreen(priceTxt.getBoundsInLocal());

                                    tooltip.show(scene.getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                                    tooltip.setAutoHide(true);
                                }
                            }
                        });

                        quantityTxt.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                String newValue) {
                                if (!newValue.matches("\\d*")) {
                                    quantityTxt.setText(newValue.replaceAll("[^\\d]", ""));

                                    Bounds boundsInScreen = quantityTxt.localToScreen(priceTxt.getBoundsInLocal());

                                    tooltip.show(scene.getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                                    tooltip.setAutoHide(true);
                                }
                            }
                        });

                        newStage.setScene(scene);
                        newStage.initModality(Modality.APPLICATION_MODAL);
                        newStage.setTitle("Modifier un produit");
                        newStage.showAndWait();
                    }
                }
            });
        }

        //Display icon if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            editIcon.setIcon(FontAwesomeIcon.ICON_EDIT);
            editIcon.getStyleClass().add("editIcon");
            super.updateItem(t, empty);
            if (!empty) setGraphic(editIcon);
        }
    }

}