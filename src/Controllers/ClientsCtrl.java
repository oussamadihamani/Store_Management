package Controllers;

import Models.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
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
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ClientsCtrl {

    @FXML
    AnchorPane clientForm;

    @FXML
    TextField searchClientField;

    @FXML
    TableView<Client> tableview;

    @FXML
    TableColumn<Client, Integer> colClientID;

    @FXML
    TableColumn<Client, String> colClientNom;

    @FXML
    TableColumn<Client, String> colClientPrenom;

    @FXML
    TableColumn<Client, String> colClientTelephone;

    @FXML
    TableColumn<Client, Boolean> colClientOption;

    @FXML
    TableColumn<Client, Boolean> colClientEdit;

    @FXML
    TableColumn<Client, Boolean> colClientDelete;

    Connection cnx = AcceuilCtrl.connect;

    private static ObservableList<Client> data;


    @FXML
    void initialize() {


        /***************************************************************************************************************
         *****************************            Initialize Colmuns values                *****************************
         **************************************************************************************************************/
        colClientID.setCellValueFactory(new PropertyValueFactory<Client, Integer>("clientID"));
        colClientNom.setCellValueFactory(new PropertyValueFactory<Client, String>("clientNom"));
        colClientPrenom.setCellValueFactory(new PropertyValueFactory<Client, String>("clientPrenom"));
        colClientTelephone.setCellValueFactory(new PropertyValueFactory<Client, String>("clientTelephone"));

        colClientEdit.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Client, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Client, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });

        colClientDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Client, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Client, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });


        /***************************************************************************************************************
         ********************************            Set Columns width                 *********************************
         **************************************************************************************************************/
        colClientID.prefWidthProperty().bind(tableview.widthProperty().divide(10));
        colClientNom.prefWidthProperty().bind(tableview.widthProperty().divide(4));
        colClientPrenom.prefWidthProperty().bind(tableview.widthProperty().divide(4));
        colClientTelephone.prefWidthProperty().bind(tableview.widthProperty().divide(4));
        colClientOption.prefWidthProperty().bind(tableview.widthProperty().divide(7));
        colClientEdit.prefWidthProperty().bind(tableview.widthProperty().divide(13.9));
        colClientDelete.prefWidthProperty().bind(tableview.widthProperty().divide(13.9));


        // Delete sub-columns height
        colClientEdit.setStyle("-fx-pref-height: 0;");
        colClientDelete.setStyle("-fx-pref-height: 0;");

        /***************************************************************************************************************
         ****************************            Disnable Sort by columns                 ******************************
         **************************************************************************************************************/
        colClientID.setSortable(false);
        colClientNom.setSortable(false);
        colClientPrenom.setSortable(false);
        colClientTelephone.setSortable(false);
        colClientOption.setSortable(false);
        colClientEdit.setSortable(false);
        colClientDelete.setSortable(false);


        /***************************************************************************************************************
         ****************************            Disable Colmuns Resize                 ****************************
         **************************************************************************************************************/
        colClientID.setResizable(false);
        colClientNom.setResizable(false);
        colClientPrenom.setResizable(false);
        colClientTelephone.setResizable(false);
        colClientOption.setResizable(false);
        colClientEdit.setResizable(false);
        colClientDelete.setResizable(false);


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
            buildData("select * from Client order by clientNom");
        } catch (Exception ce) {
            ce.toString();
        }

    }

    /***************************************************************************************************************
     **************************            Insert Data in the TableView              *******************************
     **************************************************************************************************************/
    private void buildData(String slqStatement) {

        data = FXCollections.observableArrayList();

        colClientEdit.setCellFactory(
                new Callback<TableColumn<Client, Boolean>, TableCell<Client, Boolean>>() {
                    @Override
                    public TableCell<Client, Boolean> call(TableColumn<Client, Boolean> param) {
                        return new buttonEdit(tableview);
                    }
                });

        colClientDelete.setCellFactory(new Callback<TableColumn<Client, Boolean>, TableCell<Client, Boolean>>() {
            @Override
            public TableCell<Client, Boolean> call(TableColumn<Client, Boolean> param) {
                return new buttonDelete(tableview);
            }
        });


        try {

            ResultSet rs = AcceuilCtrl.connect.createStatement().executeQuery(slqStatement);

            while (rs.next()) {

                Client client = new Client();

                client.clientID.set(rs.getInt("clientID"));
                client.clientNom.set(rs.getString("clientNom"));
                client.clientPrenom.set(rs.getString("clientPrenom"));
                client.clientTelephone.set(rs.getString("clientTelephone"));

                data.add(client);
            }

            tableview.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***************************************************************************************************************
     ************************            Refresh Button on Action Function             *****************************
     **************************************************************************************************************/
    public void refreshClientTable(ActionEvent evnt) {
        searchClientField.setText("");
        initialize();
    }


    /***************************************************************************************************************
     **********************            Add Client Button on Action Function             ***************************
     **************************************************************************************************************/
    public void addClient(ActionEvent event) throws IOException {

        Stage newStage = new Stage();
        newStage.initStyle(StageStyle.UNDECORATED);
        AnchorPane anchorPanePopup = FXMLLoader.load(getClass().getResource("/Views/AddClient.fxml"));
        Scene scene = new Scene(anchorPanePopup);
        newStage.setScene(scene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Ajouter un nouveau Produit");
        newStage.showAndWait();
    }


    public void searchClient(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY) {

            String sql = "SELECT * FROM Client WHERE clientNom like '%" + searchClientField.getText() + "%'" +
                    "or clientPrenom like '%" + searchClientField.getText() + "%'" +
                    "or  clientTelephone like '%" + searchClientField.getText() + "%' order by clientNom";

            buildData(sql);
        }
    }


    private class buttonDelete extends TableCell<Client, Boolean> {


        final FontAwesomeIconView deleteIcon = new FontAwesomeIconView();

        buttonDelete(final TableView tblView) {

            deleteIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    if (event.getButton() == MouseButton.PRIMARY) { // check the primary mouse buttonCredit

                        int selectedIndex = getTableRow().getIndex();
                        Client toRemove = (Client) tblView.getItems().get(selectedIndex);

                        int toDeleteID = (toRemove.clientID).intValue();

                        String sqldelete = "delete from Client where clientID = " + toDeleteID + " ;";
                        PreparedStatement delete = null;

                        Alert clientDeleted = new Alert(Alert.AlertType.INFORMATION);
                        clientDeleted.setTitle("Supprimer un client");
                        clientDeleted.setHeaderText(null);
                        clientDeleted.setContentText("Le client à été supprimé avec succès !");

                        String nom = toRemove.clientNom.get();
                        String prenom = toRemove.clientPrenom.get();


                        Alert deleteClientAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        deleteClientAlert.setTitle("Supprimer un client");
                        deleteClientAlert.setHeaderText("Vous êtes entrain se supprimer le client : " + nom + " " + prenom);
                        deleteClientAlert.setContentText("L'historique de tous ces opérations sera supprimé, Continue ?");

                        Button exitButton = (Button) deleteClientAlert.getDialogPane().lookupButton(
                                ButtonType.OK
                        );
                        exitButton.setText("Supprimer");

                        Button cancelButton = (Button) deleteClientAlert.getDialogPane().lookupButton(
                                ButtonType.CANCEL
                        );
                        cancelButton.setText("Annuler");

                        Optional<ButtonType> result = deleteClientAlert.showAndWait();

                        if (result.get() == ButtonType.OK) {
                            try {
                                delete = AcceuilCtrl.connect.prepareStatement(sqldelete);
                                delete.execute();

                                clientDeleted.showAndWait();

                                refreshClientTable(new ActionEvent());
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


    private class buttonEdit extends TableCell<Client, Boolean> {

        final FontAwesomeIconView editIcon = new FontAwesomeIconView();

        buttonEdit(final TableView tblView) {

            editIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    if (event.getButton() == MouseButton.PRIMARY) { // check the primary mouse buttonCredit

                        int selectedIndex = getTableRow().getIndex();
                        Client toEdit = (Client) tblView.getItems().get(selectedIndex);

                        final int toEditID = toEdit.clientID.get();
                        final String oldNom = toEdit.clientNom.get();
                        final String oldPrenom = toEdit.clientPrenom.get();
                        final String oldTelephone = toEdit.clientTelephone.get();

                        Pane head = new Pane();
                        head.setStyle("-fx-background-color:  #1589FF; -fx-pref-width: 450; -fx-pref-height: 100");

                        Label title = new Label("Modifier un client");
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

                        Label nomLabel = new Label("Nom :");
                        nomLabel.setStyle("-fx-font-size: 15");
                        nomLabel.layoutXProperty().setValue(30);
                        nomLabel.layoutYProperty().setValue(175);

                        FontAwesomeIconView nomRequired = new FontAwesomeIconView();
                        nomRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        nomRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        nomRequired.layoutXProperty().setValue(80);
                        nomRequired.layoutYProperty().setValue(175);

                        JFXTextField nomTxt = new JFXTextField();
                        nomTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        nomTxt.layoutXProperty().setValue(245);
                        nomTxt.layoutYProperty().setValue(173);
                        nomTxt.setPromptText("Nom");

                        Label prenomLabel = new Label("Prénom :");
                        prenomLabel.setStyle("-fx-font-size: 15");
                        prenomLabel.layoutXProperty().setValue(30);
                        prenomLabel.layoutYProperty().setValue(275);

                        FontAwesomeIconView prenomRequired = new FontAwesomeIconView();
                        prenomRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        prenomRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        prenomRequired.layoutXProperty().setValue(100);
                        prenomRequired.layoutYProperty().setValue(275);

                        JFXTextField prenomTxt = new JFXTextField();
                        prenomTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        prenomTxt.layoutXProperty().setValue(245);
                        prenomTxt.layoutYProperty().setValue(273);
                        prenomTxt.setPromptText("Prénom");

                        Label telephoneLabel = new Label("Téléphone :");
                        telephoneLabel.setStyle("-fx-font-size: 15");
                        telephoneLabel.layoutXProperty().setValue(30);
                        telephoneLabel.layoutYProperty().setValue(375);

                        FontAwesomeIconView telephoneRequired = new FontAwesomeIconView();
                        telephoneRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        telephoneRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        telephoneRequired.layoutXProperty().setValue(120);
                        telephoneRequired.layoutYProperty().setValue(375);

                        JFXTextField telephoneTxt = new JFXTextField();
                        telephoneTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        telephoneTxt.layoutXProperty().setValue(245);
                        telephoneTxt.layoutYProperty().setValue(373);
                        telephoneTxt.setPromptText("Numéro du téléphone");

                        JFXButton cancelBtn = new JFXButton(" Annuler");
                        cancelBtn.setStyle("-fx-background-color: #F62817; -fx-text-fill: white; -fx-fill: white; -fx-pref-width: 90; -fx-pref-height: 28; -fx-cursor: hand");
                        cancelBtn.layoutXProperty().setValue(120);
                        cancelBtn.layoutYProperty().setValue(490);
                        cancelBtn.getStyleClass().add("cancelBtn");

                        OctIconView cancelIcon = new OctIconView();
                        cancelIcon.setIcon(OctIcon.X);
                        cancelIcon.setStyle("-fx-font-family: \"Octicons\"; -fx-fill: white; -fx-font-size: 16");
                        cancelBtn.setGraphic(cancelIcon);

                        JFXButton confirmBtn = new JFXButton(" Valider");
                        confirmBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-fill: white; -fx-pref-width: 90; -fx-pref-height: 28; -fx-cursor: hand");
                        confirmBtn.layoutXProperty().setValue(240);
                        confirmBtn.layoutYProperty().setValue(490);
                        confirmBtn.getStyleClass().add("confirmBtn");

                        OctIconView confirmIcon = new OctIconView();
                        confirmIcon.setIcon(OctIcon.CHECK);
                        confirmIcon.setStyle("-fx-font-family: \"Octicons\"; -fx-fill: white; -fx-font-size: 17");
                        confirmBtn.setGraphic(confirmIcon);

                        String selectsql = "select * from Client where clientID =" + toEditID;
                        try {
                            PreparedStatement initializ = cnx.prepareStatement(selectsql);
                            ResultSet rs = initializ.executeQuery();
                            if (rs.next()) {

                                nomTxt.setText(rs.getString("clientNom"));
                                prenomTxt.setText(rs.getString("clientPrenom"));
                                telephoneTxt.setText(rs.getString("clientTelephone"));
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (nomTxt.getText().equals(oldNom) && prenomTxt.getText().equals(oldPrenom) && telephoneTxt.getText().equals(oldTelephone)) {
                                    ((Node) event.getTarget()).getScene().getWindow().hide();
                                } else {
                                    AddProductCtrl.cancelEdit(event, cancelBtn);
                                }
                            }
                        });

                        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {

                                PreparedStatement editStatement;

                                if (nomTxt.getText().equals(oldNom) && prenomTxt.getText().equals(oldPrenom) && telephoneTxt.getText().equals(oldTelephone)) {
                                    ((Node) event.getTarget()).getScene().getWindow().hide();
                                } else {

                                    Alert editRejected = new Alert(Alert.AlertType.INFORMATION);
                                    editRejected.setTitle("Information Dialog");
                                    editRejected.setHeaderText("Impossible de modifier l'e client !");

                                    if (nomTxt.getText().equals("") || prenomTxt.getText().equals("") || telephoneTxt.getText().equals("")) {

                                        if (nomTxt.getText().equals(""))
                                            editRejected.setContentText("Le champ 'Nom' ne doit pas être vide");
                                        else if (prenomTxt.getText().equals(""))
                                            editRejected.setContentText("Le champ 'Prénom' ne doit pas être vide");
                                        else if (telephoneTxt.getText().equals(""))
                                            editRejected.setContentText("Le champ 'Téléphone' ne doit pas être vide");

                                        editRejected.showAndWait();

                                    } else if (telephoneTxt.getText().length() < 9) {
                                        editRejected.setContentText("Numéro de téléphone érroné !");
                                        editRejected.showAndWait();
                                    } else {
                                        try {
                                            String editsql = "update Client set clientNom = '" + nomTxt.getText() + "', clientPrenom = '" + prenomTxt.getText() +
                                                    "', clientTelephone = '" + telephoneTxt.getText() + "' where clientID = " + toEditID + ";";

                                            editStatement = cnx.prepareStatement(editsql);
                                            editStatement.execute();

                                            Alert editDone = new Alert(Alert.AlertType.INFORMATION);
                                            editDone.setTitle("Information Dialog");
                                            editDone.setHeaderText(null);
                                            editDone.setContentText("Le client à été modifié avec succès !");

                                            Optional<ButtonType> result = editDone.showAndWait();
                                            if (result.get() == ButtonType.OK) {
                                                ((Node) event.getTarget()).getScene().getWindow().hide();
                                                refreshClientTable(event);
                                            }
                                        } catch (SQLException e) {

                                            if (e.getErrorCode() == 1062) {
                                                try {

                                                    String sqllitelol = "select count(*) from Client where clientNom = '" + nomTxt.getText() + "' and clientPrenom ='" + prenomTxt.getText() + "';";

                                                    Statement checkFullName = cnx.createStatement();
                                                    ResultSet uniqueFullName = checkFullName.executeQuery(sqllitelol);
                                                    uniqueFullName.next();

                                                    PreparedStatement checkTelephone = cnx.prepareStatement("select count(*) from Client where clientTelephone = '" + telephoneTxt.getText() + "';");
                                                    ResultSet uniqueTelephone = checkTelephone.executeQuery();
                                                    uniqueTelephone.next();

                                                    if (uniqueFullName.getInt(1) > 0) {
                                                        editRejected.setContentText("Ce client existe déjà !\n" + "Veuillez entrer un autre Nom et/ou Prénom");
                                                    } else if (uniqueTelephone.getInt(1) > 0) {
                                                        editRejected.setContentText("Le numéro du téléphone est déjà utilisé !\n" + "Veuillez entrer un autre numéro du téléphone");
                                                    }
                                                } catch (SQLException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }

                                            editRejected.setHeaderText("Information dupliquée");
                                            editRejected.showAndWait();
                                        }
                                    }
                                }
                            }
                        });

                        Stage newStage = new Stage();
                        newStage.initStyle(StageStyle.UNDECORATED);

                        AnchorPane anchorPanePopup = new AnchorPane();
                        anchorPanePopup.setStyle("-fx-background-color: white; -fx-pref-width: 450; -fx-pref-height: 550");
                        anchorPanePopup.getChildren().addAll(head, required, nomLabel, nomRequired, nomTxt, prenomLabel, prenomRequired, prenomTxt,
                                telephoneLabel, telephoneRequired, telephoneTxt, cancelBtn, confirmBtn);

                        Scene scene = new Scene(anchorPanePopup);

                        Tooltip txtOnly = new Tooltip("Entrez des lettres Alphabétiques !");
                        Tooltip numbersOnly = new Tooltip("Entrez des chiffres numériques !");

                        nomTxt.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                String newValue) {
                                if (!newValue.matches("[a-zA-Z ]*")) {
                                    nomTxt.setText(newValue.replaceAll("[^a-zA-Z ]", ""));

                                    Bounds boundsInScreen = nomTxt.localToScreen(nomTxt.getBoundsInLocal());

                                    txtOnly.show(clientForm.getScene().getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                                    txtOnly.setAutoHide(true);
                                }
                            }
                        });

                        prenomTxt.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                String newValue) {
                                if (!newValue.matches("[a-zA-Z ]*")) {
                                    prenomTxt.setText(newValue.replaceAll("[^a-zA-Z ]", ""));

                                    Bounds boundsInScreen = prenomTxt.localToScreen(prenomTxt.getBoundsInLocal());

                                    txtOnly.show(clientForm.getScene().getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                                    txtOnly.setAutoHide(true);
                                }
                            }
                        });


                        telephoneTxt.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                String newValue) {
                                if (!newValue.matches("\\d*")) {
                                    telephoneTxt.setText(newValue.replaceAll("[^\\d]", ""));

                                    Bounds boundsInScreen = telephoneTxt.localToScreen(telephoneTxt.getBoundsInLocal());

                                    numbersOnly.show(scene.getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                                    numbersOnly.setAutoHide(true);
                                }
                            }
                        });

                        newStage.setScene(scene);
                        newStage.initModality(Modality.APPLICATION_MODAL);
                        newStage.setTitle("Modifier un utilisateur");
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
