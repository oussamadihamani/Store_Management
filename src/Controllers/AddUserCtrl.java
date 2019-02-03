package Controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import fonts.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;


public class AddUserCtrl {

    @FXML
    AnchorPane form;

    @FXML
    JFXTextField txtNom;

    @FXML
    JFXTextField txtPrenom;

    @FXML
    JFXTextField txtTelephone;

    @FXML
    JFXTextField txtUsername;

    @FXML
    JFXPasswordField txtPassword;

    @FXML
    JFXPasswordField txtPassword1;

    @FXML
    JFXCheckBox isAdmin;

    @FXML
    OctIconView adminInfo = new OctIconView();

    @FXML
    JFXButton resetFormBtn;

    @FXML
    JFXButton addBtn;

    @FXML
    JFXButton cancelBtn;

    Connection cnx = AcceuilCtrl.connect;


    @FXML
    public void initialize() {

        Tooltip txtOnly = new Tooltip("Entrez des lettres Alphabétiques !");
        Tooltip numbersOnly = new Tooltip("Entrez des chiffres numériques !");
        Tooltip adminInfos = new Tooltip("Selectionner cette option permet l'utilisateur de: \n" +
                "- Consulter les informations de tous les utilisateurs \n" +
                "- Ajouter, modifier et supprimer d'autres utilisateurs");
        Tooltip.install(adminInfo, adminInfos);

        onlytxtField(txtOnly, txtNom);

        onlytxtField(txtOnly, txtPrenom);

        onlynumbersField(numbersOnly, txtTelephone, form);

    }


    static void onlynumbersField(Tooltip numbersOnly, TextField txtTelephone, AnchorPane form) {
        txtTelephone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtTelephone.setText(newValue.replaceAll("[^\\d]", ""));

                    Bounds boundsInScreen = txtTelephone.localToScreen(txtTelephone.getBoundsInLocal());

                    numbersOnly.show(form.getScene().getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                    numbersOnly.setAutoHide(true);
                }
            }
        });
    }


    private void onlytxtField(Tooltip txtOnly, JFXTextField txtPrenom) {
        txtPrenom.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("[a-zA-Z ]*")) {
                    txtPrenom.setText(newValue.replaceAll("[^a-zA-Z ]", ""));

                    Bounds boundsInScreen = txtPrenom.localToScreen(txtPrenom.getBoundsInLocal());

                    txtOnly.show(form.getScene().getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                    txtOnly.setAutoHide(true);
                }
            }
        });
    }


    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == resetFormBtn) clearForm();

        cancelEdit(ae, cancelBtn);
    }


    private void clearForm() {
        txtNom.clear();
        txtPrenom.clear();
        txtTelephone.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtPassword1.clear();
        isAdmin.setSelected(false);
    }


    static void cancelEdit(ActionEvent ae, JFXButton cancelBtn) {
        cancelUserEdit(ae, cancelBtn);
    }

    static void cancelUserEdit(ActionEvent ae, JFXButton cancelBtn) {
        if (ae.getSource() == cancelBtn) {

            Alert cancelForm = new Alert(Alert.AlertType.CONFIRMATION);
            cancelForm.setTitle("Annuler l'opération");
            cancelForm.setHeaderText(null);
            cancelForm.setContentText("Veuillez vous vraiment annuler ?");

            Button exitButton = (Button) cancelForm.getDialogPane().lookupButton(
                    ButtonType.OK
            );
            exitButton.setText("Oui");

            Button cancelButton = (Button) cancelForm.getDialogPane().lookupButton(
                    ButtonType.CANCEL
            );
            cancelButton.setText("Non");

            Optional<ButtonType> result = cancelForm.showAndWait();
            if (result.get() == ButtonType.OK) ((Node) ae.getTarget()).getScene().getWindow().hide();
        }
    }


    public void addNewUser(ActionEvent ae) throws SQLException {

        PreparedStatement preparedStatement = null;
        String sql = "insert into User (userNom, userPrenom, userTelephone, username, userPassword, admin) values (?,?,?,?,?,?)";

        Alert addUserWarning = new Alert(Alert.AlertType.INFORMATION);
        addUserWarning.setTitle("Opération échouée");
        addUserWarning.setHeaderText("Impossible d'ajouter l'utilisateur !");

        if (txtNom.getText().equals("") || txtPrenom.getText().equals("") || txtTelephone.getText().equals("") || txtUsername.getText().equals("")
                || txtPassword.getText().equals("") || txtPassword1.getText().equals("")) {

            if (txtNom.getText().equals("")) addUserWarning.setContentText("Le champ 'Nom' ne doit pas être vide");
            else if (txtPrenom.getText().equals(""))
                addUserWarning.setContentText("Le champ 'Prénom' ne doit pas être vide");
            else if (txtTelephone.getText().equals(""))
                addUserWarning.setContentText("Le champ 'Téléphone' ne doit pas être vide");
            else if (txtUsername.getText().equals(""))
                addUserWarning.setContentText("Le champ 'Nom d'utilisateur' ne doit pas être vide");
            else if (txtPassword.getText().equals(""))
                addUserWarning.setContentText("Le champ 'Mot de passe' ne doit pas être vide");
            else if (txtPassword1.getText().equals(""))
                addUserWarning.setContentText("Veuillez entrez votre mot de passe à nouveau");

            addUserWarning.showAndWait();

        } else if (txtTelephone.getText().length() < 9) {
            addUserWarning.setContentText("Numéro de téléphone érroné !");
            addUserWarning.showAndWait();
        } else if (txtPassword.getText().length() < 6) {
            addUserWarning.setContentText("Le mot de passe doit être\nau minimum 06 caractères !");
            addUserWarning.showAndWait();
        } else if (!txtPassword.getText().equals(txtPassword1.getText())) {
            addUserWarning.setContentText("Les mots de passe ne sont pas identiques !");
            addUserWarning.showAndWait();
        } else {
            try {
                preparedStatement = cnx.prepareStatement(sql);
                preparedStatement.setString(1, txtNom.getText());
                preparedStatement.setString(2, txtPrenom.getText());
                preparedStatement.setString(3, txtTelephone.getText());
                preparedStatement.setString(4, txtUsername.getText());
                preparedStatement.setString(5, txtPassword.getText());
                preparedStatement.setBoolean(6, isAdmin.isSelected());
                preparedStatement.execute();

                Alert userAdded = new Alert(Alert.AlertType.CONFIRMATION);
                userAdded.setTitle("Ajouter un utilisateur");
                userAdded.setHeaderText("L'utilisateur à été ajouté avec succés!");
                userAdded.setContentText("Ajouter un autre utilisateur ?");

                Button exitButton = (Button) userAdded.getDialogPane().lookupButton(
                        ButtonType.OK
                );
                exitButton.setText("Oui");

                Button cancelButton = (Button) userAdded.getDialogPane().lookupButton(
                        ButtonType.CANCEL
                );
                cancelButton.setText("Non");

                Optional<ButtonType> result = userAdded.showAndWait();
                if (result.get() == ButtonType.OK) {
                    clearForm();
                } else {
                    ((Node) ae.getTarget()).getScene().getWindow().hide();
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    try {

                        String sqllitelol = "select count(*) from User where userNom = '" + txtNom.getText() + "' and userPrenom ='" + txtPrenom.getText() + "';";

                        Statement checkFullName = cnx.createStatement();
                        ResultSet uniqueFullName = checkFullName.executeQuery(sqllitelol);
                        uniqueFullName.next();

                        PreparedStatement checkTelephone = cnx.prepareStatement("select count(*) from User where userTelephone = '" + txtTelephone.getText() + "';");
                        ResultSet uniqueTelephone = checkTelephone.executeQuery();
                        uniqueTelephone.next();

                        PreparedStatement checkUsername = cnx.prepareStatement("select count(*) from User where username = '" + txtUsername.getText() + "';");
                        ResultSet uniqueUsername = checkUsername.executeQuery();
                        uniqueUsername.next();

                        if (uniqueFullName.getInt(1) > 0) {
                            addUserWarning.setContentText("Cet utilisateur existe déjà !\n" + "Veuillez entrer un autre Nom et/ou Prénom");
                        } else if (uniqueTelephone.getInt(1) > 0) {
                            addUserWarning.setContentText("Le numéro du téléphone est déjà utilisé !\n" + "Veuillez entrer un autre numéro du téléphone");
                        } else if (uniqueUsername.getInt(1) > 0) {
                            addUserWarning.setContentText("Le nom d'utilisateur n'est pas disponible !\n" + "Veuillez choisir un autre nom d'utilisateur");
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }

                addUserWarning.setHeaderText("Information dupliquée");
                addUserWarning.showAndWait();
            }
        }
    }
}

