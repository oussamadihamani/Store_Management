package Controllers;

import Models.Client;
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
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
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

public class CreditCtrl {

    @FXML
    AnchorPane creditForm;

    @FXML
    JFXTextField searchClientTxt;

    @FXML
    JFXTextField searchAmountTxt;

    @FXML
    JFXComboBox<String> searchAmountCombo;

    @FXML
    TableView<Client> tableview;

    @FXML
    TableColumn<Client, Integer> colClientID;

    @FXML
    TableColumn<Client, String> colClientNom;

    @FXML
    TableColumn<Client, String> colClientPrenom;

    @FXML
    TableColumn<Client, Integer> colClientAmount;

    @FXML
    TableColumn<Client, Boolean> colCrediter;

    @FXML
    TableColumn<Client, Boolean> colDebiter;

    Connection cnx = AcceuilCtrl.connect;

    public static int currentClientID;

    public static String currentClientNom;

    public static String currentClientPrenom;

    public static int currentClientAmount;

    private static ObservableList<Client> data;


    @FXML
    void initialize() {

        searchAmountCombo.getItems().setAll("<", "<=", "=", ">=", ">");

        searchClientTxt.setText("");
        searchAmountTxt.setText("");
        searchAmountCombo.setValue(null);

        searchClientTxt.setLabelFloat(true);
        searchAmountTxt.setLabelFloat(true);

        Tooltip numbersOnly = new Tooltip("Entrez des chiffres numériques !");

        searchAmountTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    searchAmountTxt.setText(newValue.replaceAll("[^\\d]", ""));

                    Bounds boundsInScreen = searchAmountTxt.localToScreen(searchAmountTxt.getBoundsInLocal());

                    numbersOnly.show(creditForm.getScene().getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                    numbersOnly.setAutoHide(true);
                }
            }
        });

        /***************************************************************************************************************
         *****************************            Initialize Colmuns values                *****************************
         **************************************************************************************************************/
        colClientID.setCellValueFactory(new PropertyValueFactory<Client, Integer>("clientID"));
        colClientNom.setCellValueFactory(new PropertyValueFactory<Client, String>("clientNom"));
        colClientPrenom.setCellValueFactory(new PropertyValueFactory<Client, String>("clientPrenom"));
        colClientAmount.setCellValueFactory(new PropertyValueFactory<Client, Integer>("clientAmount"));
        colCrediter.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Client, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Client, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });
        colDebiter.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Client, Boolean>, ObservableValue<Boolean>>() {
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
        colClientAmount.prefWidthProperty().bind(tableview.widthProperty().divide(5.1));
        colCrediter.prefWidthProperty().bind(tableview.widthProperty().divide(10));
        colDebiter.prefWidthProperty().bind(tableview.widthProperty().divide(10));

        // Delete sub-columns height
        colCrediter.setStyle("-fx-pref-height: 0;");
        colDebiter.setStyle("-fx-pref-height: 0;");


        /***************************************************************************************************************
         ****************************            Disnable Sort by columns                 ******************************
         **************************************************************************************************************/
        colClientID.setSortable(false);
        colClientNom.setSortable(false);
        colClientPrenom.setSortable(false);
        colClientAmount.setSortable(false);
        colCrediter.setSortable(false);
        colDebiter.setSortable(false);


        /***************************************************************************************************************
         ****************************            Disable Colmuns Resize                 ****************************
         **************************************************************************************************************/
        colClientID.setResizable(false);
        colClientNom.setResizable(false);
        colClientPrenom.setResizable(false);
        colClientAmount.setResizable(false);
        colCrediter.setResizable(false);
        colDebiter.setResizable(false);


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

        /***************************************************************************************************************
         ********************            Show Credit details on double clicking row                 ********************
         **************************************************************************************************************/
        tableview.setRowFactory(tv -> {
            TableRow<Client> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {

                    Client currentClient = row.getItem();

                    currentClientID = currentClient.clientID.get();
                    currentClientNom = currentClient.clientNom.get();
                    currentClientPrenom = currentClient.clientPrenom.get();
                    currentClientAmount = currentClient.clientAmount.get();

                    try {

                        Stage newStage = new Stage();
                        AnchorPane anchorPanePopup = FXMLLoader.load(getClass().getResource("/Views/CreditDetails.fxml"));
                        Scene scene = new Scene(anchorPanePopup);
                        newStage.setScene(scene);
                        newStage.initModality(Modality.APPLICATION_MODAL);
                        newStage.setTitle("Historique des opérations");
                        newStage.setResizable(false);
                        newStage.showAndWait();

                        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
                        newStage.setX((primScreenBounds.getWidth() - newStage.getWidth()) / 2);
                        newStage.setY((primScreenBounds.getHeight() - newStage.getHeight()) / 2);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

    /***************************************************************************************************************
     **************************            Insert Data in the TableView              *******************************
     **************************************************************************************************************/
    private void buildData(String slqStatement) {

        data = FXCollections.observableArrayList();

        colCrediter.setCellFactory(
                new Callback<TableColumn<Client, Boolean>, TableCell<Client, Boolean>>() {
                    @Override
                    public TableCell<Client, Boolean> call(TableColumn<Client, Boolean> param) {
                        return new buttonCredit(tableview);
                    }
                });

        colDebiter.setCellFactory(new Callback<TableColumn<Client, Boolean>, TableCell<Client, Boolean>>() {
            @Override
            public TableCell<Client, Boolean> call(TableColumn<Client, Boolean> param) {
                return new buttonDebit(tableview);
            }
        });


        try {

            ResultSet rs = AcceuilCtrl.connect.createStatement().executeQuery(slqStatement);

            while (rs.next()) {

                Client client = new Client();

                client.clientID.set(rs.getInt("clientID"));
                client.clientNom.set(rs.getString("clientNom"));
                client.clientPrenom.set(rs.getString("clientPrenom"));
                client.clientAmount.set(rs.getInt("clientAmount"));

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
    public void refreshCreditTable(ActionEvent evnt) {
        initialize();
    }


    public void searchCredit(ActionEvent event) {

        String sql = "SELECT * FROM Client WHERE (clientNom like '%" + searchClientTxt.getText() + "%' " +
                "or clientPrenom like '%" + searchClientTxt.getText() + "%') " +
                "and clientAmount like '%" + searchAmountTxt.getText() + "%' order by clientNom";


        if (searchAmountCombo.getValue() != null && !searchAmountTxt.getText().equals("")) {
            sql = "SELECT * FROM Client WHERE (clientNom like '%" + searchClientTxt.getText() + "%' " +
                    "or clientPrenom like '%" + searchClientTxt.getText() + "%') " +
                    "and clientAmount " + searchAmountCombo.getValue() + " " + searchAmountTxt.getText() + " order by clientNom";
        }

        buildData(sql);
    }


    private class buttonDebit extends TableCell<Client, Boolean> {


        final FontAwesomeIconView debitIcon = new FontAwesomeIconView();

        buttonDebit(final TableView<Client> tblView) {

            debitIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    if (event.getButton() == MouseButton.PRIMARY) { // check the primary mouse buttonCredit

                        int selectedIndex = getTableRow().getIndex();
                        Client toChange = tblView.getItems().get(selectedIndex);

                        int client_id = (toChange.clientID).intValue();
                        String nom = toChange.clientNom.get();
                        String prenom = toChange.clientPrenom.get();

                        JFXTextField debiterTxt = new JFXTextField();
                        debiterTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        debiterTxt.layoutXProperty().setValue(70);
                        debiterTxt.layoutYProperty().setValue(60);
                        debiterTxt.setPromptText("Somme à créditer");
                        debiterTxt.setLabelFloat(true);

                        JFXButton confirmBtn = new JFXButton(" Valider");
                        confirmBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-fill: white; -fx-pref-width: 90; -fx-pref-height: 28; -fx-cursor: hand");
                        confirmBtn.layoutXProperty().setValue(295);
                        confirmBtn.layoutYProperty().setValue(60);
                        confirmBtn.getStyleClass().add("confirmBtn");

                        OctIconView confirmIcon = new OctIconView();
                        confirmIcon.setIcon(OctIcon.CHECK);
                        confirmIcon.setStyle("-fx-font-family: \"Octicons\"; -fx-fill: white; -fx-font-size: 17");
                        confirmBtn.setGraphic(confirmIcon);

                        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {

                                PreparedStatement editStatement;

                                if (debiterTxt.getText().equals("") || Integer.parseInt(debiterTxt.getText()) == 0) {
                                    ((Node) event.getTarget()).getScene().getWindow().hide();
                                } else if (Integer.parseInt(debiterTxt.getText()) > (toChange.clientAmount).intValue()) {

                                    Alert editRejected = new Alert(Alert.AlertType.INFORMATION);
                                    editRejected.setTitle("Opération échouée");
                                    editRejected.setHeaderText("Impossible de terminer l'opération !");
                                    editRejected.setContentText("La somme entrée est grande que le crédit du client");
                                    editRejected.showAndWait();
                                } else {

                                    int amount = Integer.parseInt(debiterTxt.getText());

                                    Alert confirmEdit = new Alert(Alert.AlertType.CONFIRMATION);
                                    confirmEdit.setTitle("Modifier crédit");
                                    confirmEdit.setHeaderText(null);
                                    confirmEdit.setContentText("Vous êtes entrain de retirer " + amount + " DA de la somme de : " + nom + " " + prenom + ", Continue ?");

                                    Button exitButton = (Button) confirmEdit.getDialogPane().lookupButton(
                                            ButtonType.OK
                                    );
                                    exitButton.setText("Continuer");

                                    Button cancelButton = (Button) confirmEdit.getDialogPane().lookupButton(
                                            ButtonType.CANCEL
                                    );
                                    cancelButton.setText("Annuler");

                                    Optional<ButtonType> result = confirmEdit.showAndWait();

                                    if (result.get() == ButtonType.OK) {

                                        try {
                                            String addOperation = "insert into Operation(operationType, operationAmount, operationClient, operationUser, operationUsername) values (?,?,?,?,?);";

                                            PreparedStatement addOp = cnx.prepareStatement(addOperation);
                                            addOp.setString(1, "Débit");
                                            addOp.setInt(2, amount);
                                            addOp.setInt(3, toChange.clientID.intValue());
                                            addOp.setInt(4, AcceuilCtrl.id);
                                            addOp.setString(5, AcceuilCtrl.username);
                                            addOp.execute();
                                        } catch (SQLException e) {
                                            System.out.println(e.getErrorCode());
                                        }

                                        try {
                                            String editsql = "update Client set clientAmount = clientAmount -" + amount + " where clientID = " + client_id + ";";

                                            editStatement = cnx.prepareStatement(editsql);
                                            editStatement.execute();

                                            Alert editDone = new Alert(Alert.AlertType.INFORMATION);
                                            editDone.setTitle("Modifier un crédit");
                                            editDone.setHeaderText(null);
                                            editDone.setContentText("L'opération à été effectuée avec succès !");
                                            editDone.showAndWait();

                                            ((Node) event.getTarget()).getScene().getWindow().hide();
                                            refreshCreditTable(event);


                                        } catch (SQLException e) {

                                        }
                                    }
                                }
                            }
                        });

                        Stage newStage = new Stage();

                        AnchorPane anchorPanePopup = new AnchorPane();
                        anchorPanePopup.setStyle("-fx-background-color: white; -fx-pref-width: 452; -fx-pref-height: 150");
                        anchorPanePopup.getChildren().addAll(debiterTxt, confirmBtn);

                        Scene scene = new Scene(anchorPanePopup);

                        Tooltip numbersOnly = new Tooltip("Entrez des chiffres numériques !");

                        debiterTxt.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                String newValue) {
                                if (!newValue.matches("\\d*")) {
                                    debiterTxt.setText(newValue.replaceAll("[^\\d]", ""));

                                    Bounds boundsInScreen = debiterTxt.localToScreen(debiterTxt.getBoundsInLocal());

                                    numbersOnly.show(scene.getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                                    numbersOnly.setAutoHide(true);
                                }
                            }
                        });

                        newStage.setScene(scene);
                        newStage.initModality(Modality.APPLICATION_MODAL);
                        newStage.setTitle("Modifier Crédit");
                        newStage.showAndWait();
                    }
                }
            });
        }


        //Display icon if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            debitIcon.setIcon(FontAwesomeIcon.ICON_MINUS);
            debitIcon.setStyle("-fx-text-fill: green; -fx-font-family: \"FontAwesome\"; -fx-font-size: 1.3em; -fx-cursor: hand;");
            super.updateItem(t, empty);
            if (!empty) setGraphic(debitIcon);
        }
    }


    private class buttonCredit extends TableCell<Client, Boolean> {

        final FontAwesomeIconView creditIcon = new FontAwesomeIconView();

        buttonCredit(final TableView<Client> tblView) {

            creditIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    if (event.getButton() == MouseButton.PRIMARY) { // check the primary mouse buttonCredit

                        int selectedIndex = getTableRow().getIndex();
                        Client toChange = tblView.getItems().get(selectedIndex);

                        int client_id = (toChange.clientID).intValue();
                        String nom = toChange.clientNom.get();
                        String prenom = toChange.clientPrenom.get();

                        JFXTextField debiterTxt = new JFXTextField();
                        debiterTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        debiterTxt.layoutXProperty().setValue(70);
                        debiterTxt.layoutYProperty().setValue(60);
                        debiterTxt.setPromptText("Somme à payer");
                        debiterTxt.setLabelFloat(true);

                        JFXButton confirmBtn = new JFXButton(" Valider");
                        confirmBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-fill: white; -fx-pref-width: 90; -fx-pref-height: 28; -fx-cursor: hand");
                        confirmBtn.layoutXProperty().setValue(295);
                        confirmBtn.layoutYProperty().setValue(60);
                        confirmBtn.getStyleClass().add("confirmBtn");

                        OctIconView confirmIcon = new OctIconView();
                        confirmIcon.setIcon(OctIcon.CHECK);
                        confirmIcon.setStyle("-fx-font-family: \"Octicons\"; -fx-fill: white; -fx-font-size: 17");
                        confirmBtn.setGraphic(confirmIcon);

                        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {

                                PreparedStatement editStatement;

                                if (debiterTxt.getText().equals("") || Integer.parseInt(debiterTxt.getText()) == 0) {
                                    ((Node) event.getTarget()).getScene().getWindow().hide();
                                } else {

                                    int amount = Integer.parseInt(debiterTxt.getText());

                                    Alert confirmEdit = new Alert(Alert.AlertType.CONFIRMATION);
                                    confirmEdit.setTitle("Modifier crédit");
                                    confirmEdit.setHeaderText(null);
                                    confirmEdit.setContentText("Vous êtes entrain d'ajouter " + amount + " DA à la somme de : " + nom + " " + prenom + ", Continue ?");

                                    Button exitButton = (Button) confirmEdit.getDialogPane().lookupButton(
                                            ButtonType.OK
                                    );
                                    exitButton.setText("Continuer");

                                    Button cancelButton = (Button) confirmEdit.getDialogPane().lookupButton(
                                            ButtonType.CANCEL
                                    );
                                    cancelButton.setText("Annuler");

                                    Optional<ButtonType> result = confirmEdit.showAndWait();

                                    if (result.get() == ButtonType.OK) {

                                        try {
                                            String addOperation = "insert into Operation(operationType, operationAmount, operationClient, operationUser, operationUsername) values (?,?,?,?,?);";

                                            PreparedStatement addOp = cnx.prepareStatement(addOperation);
                                            addOp.setString(1, "Crédit");
                                            addOp.setInt(2, amount);
                                            addOp.setInt(3, toChange.clientID.intValue());
                                            addOp.setInt(4, AcceuilCtrl.id);
                                            addOp.setString(5, AcceuilCtrl.username);
                                            addOp.execute();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                        try {

                                            String editsql = "update Client set clientAmount = clientAmount +" + amount + " where clientID = " + client_id + ";";

                                            editStatement = cnx.prepareStatement(editsql);
                                            editStatement.execute();


                                            Alert editDone = new Alert(Alert.AlertType.INFORMATION);
                                            editDone.setTitle("Modifier un crédit");
                                            editDone.setHeaderText(null);
                                            editDone.setContentText("L'opération à été effectuée avec succès !");
                                            editDone.showAndWait();

                                            ((Node) event.getTarget()).getScene().getWindow().hide();
                                            refreshCreditTable(event);


                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });

                        Stage newStage = new Stage();

                        AnchorPane anchorPanePopup = new AnchorPane();
                        anchorPanePopup.setStyle("-fx-background-color: white; -fx-pref-width: 452; -fx-pref-height: 150");
                        anchorPanePopup.getChildren().addAll(debiterTxt, confirmBtn);

                        Scene scene = new Scene(anchorPanePopup);

                        Tooltip numbersOnly = new Tooltip("Entrez des chiffres numériques !");

                        debiterTxt.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                String newValue) {
                                if (!newValue.matches("\\d*")) {
                                    debiterTxt.setText(newValue.replaceAll("[^\\d]", ""));

                                    Bounds boundsInScreen = debiterTxt.localToScreen(debiterTxt.getBoundsInLocal());

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
            creditIcon.setIcon(FontAwesomeIcon.ICON_PLUS);
            creditIcon.getStyleClass().add("editIcon");
            super.updateItem(t, empty);
            if (!empty) setGraphic(creditIcon);
        }
    }

}
