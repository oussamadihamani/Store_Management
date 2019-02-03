package Controllers;


import Models.User;
import com.jfoenix.controls.*;
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


public class UsersCtrl {

    @FXML
    AnchorPane userForm;

    @FXML
    TextField searchUserField;

    @FXML
    JFXRadioButton allUsersRadio;

    @FXML
    JFXRadioButton adminRadio;

    @FXML
    JFXRadioButton userRadio;

    ToggleGroup userType = new ToggleGroup();

    @FXML
    TableView<User> tableview;

    @FXML
    TableColumn<User, Integer> colUserID;

    @FXML
    TableColumn<User, String> colUserNom;

    @FXML
    TableColumn<User, String> colUserPrenom;

    @FXML
    TableColumn<User, String> colUserTelephone;

    @FXML
    TableColumn<User, String> colUsername;

    @FXML
    TableColumn<User, Boolean> colAdmin;

    @FXML
    TableColumn<User, Boolean> colUserOption;

    @FXML
    TableColumn<User, Boolean> colUserEdit;

    @FXML
    TableColumn<User, Boolean> colUserDelete;

    Connection cnx = AcceuilCtrl.connect;

    private static ObservableList<User> data;


    @FXML
    void initialize() {

        userType.getToggles().setAll(allUsersRadio, adminRadio, userRadio);

        allUsersRadio.setSelected(true);

        /***************************************************************************************************************
         *****************************            Initialize Colmuns values                *****************************
         **************************************************************************************************************/
        colUserID.setCellValueFactory(new PropertyValueFactory<User, Integer>("userID"));
        colUserNom.setCellValueFactory(new PropertyValueFactory<User, String>("userNom"));
        colUserPrenom.setCellValueFactory(new PropertyValueFactory<User, String>("userPrenom"));
        colUserTelephone.setCellValueFactory(new PropertyValueFactory<User, String>("userTelephone"));
        colUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        colAdmin.setCellValueFactory(new PropertyValueFactory<User, Boolean>("admin"));

        colUserEdit.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<User, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });

        colUserDelete.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<User, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });


        /***************************************************************************************************************
         ********************************            Set Columns width                 *********************************
         **************************************************************************************************************/
        colUserID.prefWidthProperty().bind(tableview.widthProperty().divide(10));
        colUserNom.prefWidthProperty().bind(tableview.widthProperty().divide(6));
        colUserPrenom.prefWidthProperty().bind(tableview.widthProperty().divide(6));
        colUserTelephone.prefWidthProperty().bind(tableview.widthProperty().divide(6));
        colUsername.prefWidthProperty().bind(tableview.widthProperty().divide(8));
        colAdmin.prefWidthProperty().bind(tableview.widthProperty().divide(8));
        colUserOption.prefWidthProperty().bind(tableview.widthProperty().divide(6.9));
        colUserEdit.prefWidthProperty().bind(tableview.widthProperty().divide(13.8));
        colUserDelete.prefWidthProperty().bind(tableview.widthProperty().divide(13.8));

        // Delete sub-columns height
        colUserEdit.setStyle("-fx-pref-height: 0;");
        colUserDelete.setStyle("-fx-pref-height: 0;");


        /***************************************************************************************************************
         ****************************            Disnable Sort by columns                 ******************************
         **************************************************************************************************************/
        colUserID.setSortable(false);
        colUserNom.setSortable(false);
        colUserPrenom.setSortable(false);
        colUserTelephone.setSortable(false);
        colUsername.setSortable(false);
        colAdmin.setSortable(false);
        colUserOption.setSortable(false);
        colUserEdit.setSortable(false);
        colUserDelete.setSortable(false);


        /***************************************************************************************************************
         ****************************            Disable Colmuns Resize                 ****************************
         **************************************************************************************************************/
        colUserID.setResizable(false);
        colUserNom.setResizable(false);
        colUserPrenom.setResizable(false);
        colUserTelephone.setResizable(false);
        colUsername.setResizable(false);
        colAdmin.setResizable(false);
        colUserOption.setResizable(false);
        colUserEdit.setResizable(false);
        colUserDelete.setResizable(false);


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
            buildData("select * from User order by userNom");
        } catch (Exception ce) {
            ce.toString();
        }
    }


    /***************************************************************************************************************
     **************************            Insert Data in the TableView              *******************************
     **************************************************************************************************************/
    private void buildData(String slqStatement) {

        data = FXCollections.observableArrayList();

        colUserEdit.setCellFactory(
                new Callback<TableColumn<User, Boolean>, TableCell<User, Boolean>>() {
                    @Override
                    public TableCell<User, Boolean> call(TableColumn<User, Boolean> param) {
                        return new buttonEdit(tableview);
                    }
                });

        colUserDelete.setCellFactory(new Callback<TableColumn<User, Boolean>, TableCell<User, Boolean>>() {
            @Override
            public TableCell<User, Boolean> call(TableColumn<User, Boolean> param) {
                return new buttonDelete(tableview);
            }
        });


        try {

            ResultSet rs = AcceuilCtrl.connect.createStatement().executeQuery(slqStatement);

/*
            if (!(rs.next())) {
                Alert nothFound = new Alert(Alert.AlertType.INFORMATION);
                nothFound.setTitle("Résultats de recherche");
                nothFound.setHeaderText(null);
                nothFound.setContentText("\n Aucun utilisateur n'à été trouvé !");

                Optional<ButtonType> result = nothFound.showAndWait();
            } else {*/

            while (rs.next()) {

                User user = new User();


                user.userID.set(rs.getInt("userID"));
                user.userNom.set(rs.getString("userNom"));
                user.userPrenom.set(rs.getString("userPrenom"));
                user.userTelephone.set(rs.getString("userTelephone"));
                user.username.set(rs.getString("username"));
                user.admin.set(rs.getBoolean("admin"));


                data.add(user);
            }

            tableview.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***************************************************************************************************************
     ************************            Refresh Button on Action Function             *****************************
     **************************************************************************************************************/
    public void refreshUserTable(ActionEvent evnt) {
        searchUserField.setText("");
        initialize();
    }


    /***************************************************************************************************************
     **********************            Add User Button on Action Function             ***************************
     **************************************************************************************************************/
    public void addUser(ActionEvent event) throws IOException {

        Stage newStage = new Stage();
        newStage.initStyle(StageStyle.UNDECORATED);
        AnchorPane anchorPanePopup = FXMLLoader.load(getClass().getResource("/Views/AddUser.fxml"));
        Scene scene = new Scene(anchorPanePopup);
        newStage.setScene(scene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Ajouter un nouveau Produit");
        newStage.showAndWait();
    }


    public void searchUser(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY) {

            String sql = "SELECT * FROM User WHERE userNom like '%" + searchUserField.getText() + "%'" +
                    "or userPrenom like '%" + searchUserField.getText() + "%'" +
                    "or  userTelephone like '%" + searchUserField.getText() + "%'" +
                    "or username like '%" + searchUserField.getText() + "%' order by userNom";

            if (adminRadio.isSelected()) {
                sql = "SELECT * FROM User WHERE admin = 1 and (userNom like '%" + searchUserField.getText() + "%'" +
                        "or userPrenom like '%" + searchUserField.getText() + "%'" +
                        "or  userTelephone like '%" + searchUserField.getText() + "%'" +
                        "or username like '%" + searchUserField.getText() + "%'" +
                        ") order by userNom";
            } else if (userRadio.isSelected()) {
                sql = "SELECT * FROM User WHERE admin = 0 and (userNom like '%" + searchUserField.getText() + "%'" +
                        "or userPrenom like '%" + searchUserField.getText() + "%'" +
                        "or  userTelephone like '%" + searchUserField.getText() + "%'" +
                        "or username like '%" + searchUserField.getText() + "%'" +
                        ") order by userNom";
            }

            buildData(sql);
        }
    }


    private class buttonDelete extends TableCell<User, Boolean> {


        final FontAwesomeIconView deleteIcon = new FontAwesomeIconView();

        buttonDelete(final TableView tblView) {

            deleteIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    if (event.getButton() == MouseButton.PRIMARY) { // check the primary mouse buttonCredit

                        int selectedIndex = getTableRow().getIndex();
                        User toRemove = (User) tblView.getItems().get(selectedIndex);
                        int toDeleteID = toRemove.userID.intValue();

                        String toDeleteUsername = (toRemove.username).get();

                        String sqldelete = "delete from User where userID = " + toDeleteID + " ;";
                        PreparedStatement delete = null;

                        Alert userDeleted = new Alert(Alert.AlertType.INFORMATION);
                        userDeleted.setTitle("Supprimer un utilisateur");
                        userDeleted.setHeaderText(null);
                        userDeleted.setContentText("L'utilisateur à été supprimé avec succès !");

                        if (toDeleteUsername.equals(AcceuilCtrl.username)) {
                            Alert deleteUserWarning = new Alert(Alert.AlertType.CONFIRMATION);
                            deleteUserWarning.setTitle("Supprimer un utilisateur");
                            deleteUserWarning.setHeaderText(null);
                            deleteUserWarning.setContentText("Voulez vous vraiment supprimer votre propre compte ?");

                            Button exitButton = (Button) deleteUserWarning.getDialogPane().lookupButton(
                                    ButtonType.OK
                            );
                            exitButton.setText("Supprimer");

                            Button cancelButton = (Button) deleteUserWarning.getDialogPane().lookupButton(
                                    ButtonType.CANCEL
                            );
                            cancelButton.setText("Annuler");


                            Optional<ButtonType> result = deleteUserWarning.showAndWait();

                            if (result.get() == ButtonType.OK) {

                                try {
                                    delete = AcceuilCtrl.connect.prepareStatement(sqldelete);
                                    delete.execute();

                                    Optional<ButtonType> rslt = userDeleted.showAndWait();
                                    //if (result.get() == ButtonType.OK);
                                    Parent dashParent = FXMLLoader.load(getClass().getResource("/Views/Acceuil.fxml"));
                                    Scene dashScene = new Scene(dashParent);
                                    Stage dashStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                                    dashStage.setScene(dashScene);
                                    dashStage.show();

                                    Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
                                    dashStage.setX((primScreenBounds.getWidth() - dashStage.getWidth()) / 2);
                                    dashStage.setY((primScreenBounds.getHeight() - dashStage.getHeight()) / 2);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                            String nom = toRemove.username.get();
                            String prenom = toRemove.userPrenom.get();


                            Alert deleteUserAlert = new Alert(Alert.AlertType.CONFIRMATION);
                            deleteUserAlert.setTitle("Supprimer un utilisateur");
                            deleteUserAlert.setHeaderText(null);
                            deleteUserAlert.setContentText("Voulez vous supprimer l'utilisateur suivant ? \n \n" + nom + " " + prenom);

                            Button exitButton = (Button) deleteUserAlert.getDialogPane().lookupButton(
                                    ButtonType.OK
                            );
                            exitButton.setText("Supprimer");

                            Button cancelButton = (Button) deleteUserAlert.getDialogPane().lookupButton(
                                    ButtonType.CANCEL
                            );
                            cancelButton.setText("Annuler");


                            Optional<ButtonType> result = deleteUserAlert.showAndWait();

                            if (result.get() == ButtonType.OK) {
                                try {
                                    delete = cnx.prepareStatement(sqldelete);
                                    delete.execute();

                                    userDeleted.showAndWait();

                                    refreshUserTable(new ActionEvent());
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
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


    private class buttonEdit extends TableCell<User, Boolean> {

        final FontAwesomeIconView editIcon = new FontAwesomeIconView();

        buttonEdit(final TableView tblView) {

            editIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    if (event.getButton() == MouseButton.PRIMARY) { // check the primary mouse buttonCredit

                        int selectedIndex = getTableRow().getIndex();
                        User toEdit = (User) tblView.getItems().get(selectedIndex);

                        final int toEditID = toEdit.userID.get();
                        final String oldNom = toEdit.userNom.get();
                        final String oldPrenom = toEdit.userPrenom.get();
                        final String oldTelephone = toEdit.userTelephone.get();
                        final String oldUsername = toEdit.username.get();
                        String oldPassword = null;
                        final Boolean oldAdminStatus = toEdit.admin.get();

                        Pane head = new Pane();
                        head.setStyle("-fx-background-color:  #1589FF; -fx-pref-width: 450; -fx-pref-height: 100");

                        Label title = new Label("Modifier un utilisateur");
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
                        nomLabel.layoutYProperty().setValue(155);

                        FontAwesomeIconView nomRequired = new FontAwesomeIconView();
                        nomRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        nomRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        nomRequired.layoutXProperty().setValue(80);
                        nomRequired.layoutYProperty().setValue(155);

                        JFXTextField nomTxt = new JFXTextField();
                        nomTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        nomTxt.layoutXProperty().setValue(245);
                        nomTxt.layoutYProperty().setValue(153);
                        nomTxt.setPromptText("Nom");

                        Label prenomLabel = new Label("Prénom :");
                        prenomLabel.setStyle("-fx-font-size: 15");
                        prenomLabel.layoutXProperty().setValue(30);
                        prenomLabel.layoutYProperty().setValue(225);

                        FontAwesomeIconView prenomRequired = new FontAwesomeIconView();
                        prenomRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        prenomRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        prenomRequired.layoutXProperty().setValue(105);
                        prenomRequired.layoutYProperty().setValue(225);

                        JFXTextField prenomTxt = new JFXTextField();
                        prenomTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        prenomTxt.layoutXProperty().setValue(245);
                        prenomTxt.layoutYProperty().setValue(223);
                        prenomTxt.setPromptText("Prénom");

                        Label telephoneLabel = new Label("Téléphone :");
                        telephoneLabel.setStyle("-fx-font-size: 15");
                        telephoneLabel.layoutXProperty().setValue(30);
                        telephoneLabel.layoutYProperty().setValue(295);

                        FontAwesomeIconView telephoneRequired = new FontAwesomeIconView();
                        telephoneRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        telephoneRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        telephoneRequired.layoutXProperty().setValue(125);
                        telephoneRequired.layoutYProperty().setValue(295);

                        JFXTextField telephoneTxt = new JFXTextField();
                        telephoneTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        telephoneTxt.layoutXProperty().setValue(245);
                        telephoneTxt.layoutYProperty().setValue(293);
                        telephoneTxt.setPromptText("Numéro du téléphone");

                        Label usernameLabel = new Label("Nom d'utilisateur :");
                        usernameLabel.setStyle("-fx-font-size: 15");
                        usernameLabel.layoutXProperty().setValue(30);
                        usernameLabel.layoutYProperty().setValue(365);

                        FontAwesomeIconView usernameRequired = new FontAwesomeIconView();
                        usernameRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        usernameRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        usernameRequired.layoutXProperty().setValue(175);
                        usernameRequired.layoutYProperty().setValue(365);

                        JFXTextField usernameTxt = new JFXTextField();
                        usernameTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        usernameTxt.layoutXProperty().setValue(245);
                        usernameTxt.layoutYProperty().setValue(363);
                        usernameTxt.setPromptText("Nom d'utilisateur");

                        Label passwordLabel = new Label("Mot de passe :");
                        passwordLabel.setStyle("-fx-font-size: 15");
                        passwordLabel.layoutXProperty().setValue(30);
                        passwordLabel.layoutYProperty().setValue(435);

                        FontAwesomeIconView passwordRequired = new FontAwesomeIconView();
                        passwordRequired.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        passwordRequired.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        passwordRequired.layoutXProperty().setValue(145);
                        passwordRequired.layoutYProperty().setValue(435);

                        JFXPasswordField passwordTxt = new JFXPasswordField();
                        passwordTxt.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        passwordTxt.layoutXProperty().setValue(245);
                        passwordTxt.layoutYProperty().setValue(433);
                        passwordTxt.setPromptText("Mot de passe");

                        Label passwordLabel1 = new Label("Répétez le mot de passe :");
                        passwordLabel1.setStyle("-fx-font-size: 15");
                        passwordLabel1.layoutXProperty().setValue(30);
                        passwordLabel1.layoutYProperty().setValue(505);

                        FontAwesomeIconView passwordRequired1 = new FontAwesomeIconView();
                        passwordRequired1.setIcon(FontAwesomeIcon.ICON_ASTERISK);
                        passwordRequired1.setStyle("-fx-font-family:\"FontAwesome\"; -fx-text-fill: #eb3730; -fx-font-size: 6");
                        passwordRequired1.layoutXProperty().setValue(230);
                        passwordRequired1.layoutYProperty().setValue(505);

                        JFXPasswordField passwordTxt1 = new JFXPasswordField();
                        passwordTxt1.setStyle("-fx-pref-width: 155;-jfx-unfocus-color: #1589ff; -jfx-focus-color: #1569c7;");
                        passwordTxt1.layoutXProperty().setValue(245);
                        passwordTxt1.layoutYProperty().setValue(503);
                        passwordTxt1.setPromptText("Répétez mot de passe");

                        JFXCheckBox isAdmin = new JFXCheckBox("Administrateur");
                        isAdmin.layoutXProperty().setValue(130);
                        isAdmin.layoutYProperty().setValue(565);
                        isAdmin.setStyle("-jfx-checked-color: #1589ff; -jfx-unchecked-color: #1569c7;");

                        OctIconView adminInfo = new OctIconView();
                        adminInfo.setIcon(OctIcon.INFO);
                        adminInfo.setStyle("-fx-font-family: \"Octicons\"; -fx-fill: #1589ff; -fx-font-size: 18");
                        adminInfo.layoutXProperty().setValue(304);
                        adminInfo.layoutYProperty().setValue(582);

                        JFXButton cancelBtn = new JFXButton(" Annuler");
                        cancelBtn.setStyle("-fx-background-color: #F62817; -fx-text-fill: white; -fx-fill: white; -fx-pref-width: 90; -fx-pref-height: 28; -fx-cursor: hand");
                        cancelBtn.layoutXProperty().setValue(120);
                        cancelBtn.layoutYProperty().setValue(615);
                        cancelBtn.getStyleClass().add("cancelBtn");

                        OctIconView cancelIcon = new OctIconView();
                        cancelIcon.setIcon(OctIcon.X);
                        cancelIcon.setStyle("-fx-font-family: \"Octicons\"; -fx-fill: white; -fx-font-size: 16");
                        cancelBtn.setGraphic(cancelIcon);

                        JFXButton confirmBtn = new JFXButton(" Valider");
                        confirmBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-fill: white; -fx-pref-width: 90; -fx-pref-height: 28; -fx-cursor: hand");
                        confirmBtn.layoutXProperty().setValue(240);
                        confirmBtn.layoutYProperty().setValue(615);
                        confirmBtn.getStyleClass().add("confirmBtn");

                        OctIconView confirmIcon = new OctIconView();
                        confirmIcon.setIcon(OctIcon.CHECK);
                        confirmIcon.setStyle("-fx-font-family: \"Octicons\"; -fx-fill: white; -fx-font-size: 17");
                        confirmBtn.setGraphic(confirmIcon);

                        String selectsql = "select * from User where userID =" + toEditID;
                        try {
                            PreparedStatement initializ = cnx.prepareStatement(selectsql);
                            ResultSet rs = initializ.executeQuery();
                            if (rs.next()) {

                                oldPassword = rs.getString("userPassword");

                                nomTxt.setText(rs.getString("userNom"));
                                prenomTxt.setText(rs.getString("userPrenom"));
                                telephoneTxt.setText(rs.getString("userTelephone"));
                                usernameTxt.setText(rs.getString("username"));
                                passwordTxt.setText(rs.getString("userPassword"));
                                passwordTxt1.setText(rs.getString("userPassword"));
                                isAdmin.setSelected(rs.getBoolean("admin"));
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        String finalOldPassword = oldPassword;
                        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (nomTxt.getText().equals(oldNom) && prenomTxt.getText().equals(oldPrenom) && telephoneTxt.getText().equals(oldTelephone)
                                        && usernameTxt.getText().equals(oldUsername) && passwordTxt.getText().equals(finalOldPassword) && passwordTxt1.getText().equals(finalOldPassword)
                                        && isAdmin.isSelected() == oldAdminStatus) {
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

                                if (nomTxt.getText().equals(oldNom) && prenomTxt.getText().equals(oldPrenom) && telephoneTxt.getText().equals(oldTelephone)
                                        && usernameTxt.getText().equals(oldUsername) && passwordTxt.getText().equals(finalOldPassword) && passwordTxt1.getText().equals(finalOldPassword)
                                        && isAdmin.isSelected() == oldAdminStatus) {
                                    ((Node) event.getTarget()).getScene().getWindow().hide();
                                } else {

                                    Alert editRejected = new Alert(Alert.AlertType.INFORMATION);
                                    editRejected.setTitle("Opération échouée");
                                    editRejected.setHeaderText("Impossible de modifier l'utilisateur !");

                                    if (nomTxt.getText().equals("") || prenomTxt.getText().equals("") || telephoneTxt.getText().equals("") || usernameTxt.getText().equals("")
                                            || passwordTxt.getText().equals("") || passwordTxt1.getText().equals("")) {

                                        if (nomTxt.getText().equals(""))
                                            editRejected.setContentText("Le champ 'Nom' ne doit pas être vide");
                                        else if (prenomTxt.getText().equals(""))
                                            editRejected.setContentText("Le champ 'Prénom' ne doit pas être vide");
                                        else if (telephoneTxt.getText().equals(""))
                                            editRejected.setContentText("Le champ 'Téléphone' ne doit pas être vide");
                                        else if (usernameTxt.getText().equals(""))
                                            editRejected.setContentText("Le champ 'Nom d'utilisateur' ne doit pas être vide");
                                        else if (passwordTxt.getText().equals(""))
                                            editRejected.setContentText("Le champ 'Password' ne doit pas être vide");
                                        else if (passwordTxt1.getText().equals(""))
                                            editRejected.setContentText("Veuillez entrez votre mot de passe à nouveau");

                                        editRejected.showAndWait();

                                    } else if (telephoneTxt.getText().length() < 9) {
                                        editRejected.setContentText("Numéro de téléphone érroné !");
                                        editRejected.showAndWait();
                                    } else if (passwordTxt.getText().length() < 6) {
                                        editRejected.setContentText("Le mot de passe doit être\nau minimum 06 caractères !");
                                        editRejected.showAndWait();
                                    } else if (!passwordTxt.getText().equals(passwordTxt1.getText())) {
                                        editRejected.setContentText("Les mots de passe ne sont pas identiques !");
                                        editRejected.showAndWait();
                                    } else {
                                        try {
                                            String editsql = "update User set userNom = '" + nomTxt.getText() + "', userPrenom = '" + prenomTxt.getText() + "', userTelephone = '" +
                                                    telephoneTxt.getText() + "', username = '" + usernameTxt.getText() +
                                                    "', userPassword = '" + passwordTxt.getText() + "', admin = " + isAdmin.isSelected() + " where userID = " + toEditID + ";";

                                            editStatement = cnx.prepareStatement(editsql);
                                            editStatement.execute();

                                            Alert editDone = new Alert(Alert.AlertType.INFORMATION);
                                            editDone.setTitle("Modifier un utilisateur");
                                            editDone.setHeaderText(null);
                                            editDone.setContentText("L'utilisateur à été modifié avec succès !");

                                            Optional<ButtonType> result = editDone.showAndWait();
                                            if (result.get() == ButtonType.OK) {
                                                ((Node) event.getTarget()).getScene().getWindow().hide();
                                                refreshUserTable(event);
                                            }
                                        } catch (SQLException e) {

                                            if (e.getErrorCode() == 1062) {
                                                try {

                                                    String sqllitelol = "select count(*) from User where userNom = '" + nomTxt.getText() + "' and userPrenom ='" + prenomTxt.getText() + "' and userID !=" + toEditID;

                                                    Statement checkFullName = cnx.createStatement();
                                                    ResultSet uniqueFullName = checkFullName.executeQuery(sqllitelol);
                                                    uniqueFullName.next();

                                                    PreparedStatement checkTelephone = cnx.prepareStatement("select count(*) from User where userTelephone = '" + telephoneTxt.getText() + "' and userID !=" + toEditID);
                                                    ResultSet uniqueTelephone = checkTelephone.executeQuery();
                                                    uniqueTelephone.next();

                                                    PreparedStatement checkUsername = cnx.prepareStatement("select count(*) from User where username = '" + usernameTxt.getText() + "' and userID !=" + toEditID);
                                                    ResultSet uniqueUsername = checkUsername.executeQuery();
                                                    uniqueUsername.next();

                                                    if (uniqueFullName.getInt(1) > 0) {
                                                        editRejected.setContentText("Cet utilisateur existe déjà !\n" + "Veuillez entrer un autre Nom et/ou Prénom");
                                                    } else if (uniqueTelephone.getInt(1) > 0) {
                                                        editRejected.setContentText("Le numéro du téléphone est déjà utilisé !\n" + "Veuillez entrer un autre numéro du téléphone");
                                                    } else if (uniqueUsername.getInt(1) > 0) {
                                                        editRejected.setContentText("Le nom d'utilisateur n'est pas disponible !\n" + "Veuillez choisir un autre nom d'utilisateur");
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
                        anchorPanePopup.setStyle("-fx-background-color: white; -fx-pref-width: 450; -fx-pref-height: 670");
                        anchorPanePopup.getChildren().addAll(head, required, nomLabel, nomRequired, nomTxt, prenomLabel, prenomRequired, prenomTxt,
                                telephoneLabel, telephoneRequired, telephoneTxt, usernameLabel, usernameRequired, usernameTxt, passwordLabel, passwordRequired, passwordTxt,
                                passwordLabel1, passwordRequired1, passwordTxt1, isAdmin, adminInfo, cancelBtn, confirmBtn);

                        Scene scene = new Scene(anchorPanePopup);

                        Tooltip txtOnly = new Tooltip("Entrez des lettres Alphabétiques !");
                        Tooltip numbersOnly = new Tooltip("Entrez des chiffres numériques !");
                        Tooltip adminInfos = new Tooltip("Selectionner cette option permet l'utilisateur de: \n" +
                                "- Consulter les informations de tous les utilisateurs \n" +
                                "- Ajouter, modifier et supprimer d'autres utilisateurs");
                        Tooltip.install(adminInfo, adminInfos);

                        nomTxt.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                                String newValue) {
                                if (!newValue.matches("[a-zA-Z ]*")) {
                                    nomTxt.setText(newValue.replaceAll("[^a-zA-Z ]", ""));

                                    Bounds boundsInScreen = nomTxt.localToScreen(nomTxt.getBoundsInLocal());

                                    txtOnly.show(userForm.getScene().getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
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

                                    txtOnly.show(userForm.getScene().getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
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