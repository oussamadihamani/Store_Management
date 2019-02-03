package Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;

import java.sql.*;

public class EditInfoCtrl {

    @FXML
    JFXTextField txtNom;

    @FXML
    JFXTextField txtPrenom;

    @FXML
    JFXTextField txtTelephone;

    @FXML
    JFXTextField txtUsername;

    @FXML
    JFXButton cancelBtn;

    private Connection cnx = AcceuilCtrl.connect;

    String oldNom;

    String oldPrenom;

    String oldTelephone;

    String oldUsername;

    @FXML
    void initialize() {

        String sql = "select * from User where username = '" + AcceuilCtrl.username + "'";

        try {
            ResultSet rs = AcceuilCtrl.connect.createStatement().executeQuery(sql);

            while (rs.next()) {
                txtNom.setText(rs.getString("userNom"));
                txtPrenom.setText(rs.getString("userPrenom"));
                txtTelephone.setText(rs.getString("userTelephone"));
                txtUsername.setText(rs.getString("username"));

                oldNom = rs.getString("userNom");
                oldPrenom = rs.getString("userPrenom");
                oldTelephone = rs.getString("userTelephone");
                oldUsername = rs.getString("username");

                Tooltip numbersOnly = new Tooltip("Entrez des chiffres numériques !");


                txtTelephone.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        if (!newValue.matches("(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$")) {
                            txtTelephone.setText(newValue.replaceAll("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$", ""));

                            Bounds boundsInScreen = txtTelephone.localToScreen(txtTelephone.getBoundsInLocal());

                            numbersOnly.show(txtTelephone.getScene().getWindow(), boundsInScreen.getMinX(), boundsInScreen.getMaxY());
                            numbersOnly.setAutoHide(true);
                        }
                    }
                });


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editInfo(ActionEvent event) {

        if (txtNom.getText().equals(oldNom) && txtPrenom.getText().equals(oldPrenom)
                && txtTelephone.getText().equals(oldTelephone) && txtUsername.getText().equals(oldUsername)) {
            ((Node) event.getTarget()).getScene().getWindow().hide();
        } else {

            PreparedStatement preparedStatement = null;
            String sql = "update User set userNom = ?, userPrenom = ?, userTelephone = ?, username = ? where userID = "+AcceuilCtrl.id;

            Alert addUserWarning = new Alert(Alert.AlertType.INFORMATION);
            addUserWarning.setTitle("Opération échouée");
            addUserWarning.setHeaderText("Impossible de modifier vos informations !");

            if (txtNom.getText().equals("") || txtPrenom.getText().equals("") || txtTelephone.getText().equals("") || txtUsername.getText().equals("")) {

                if (txtNom.getText().equals("")) addUserWarning.setContentText("Le champ 'Nom' ne doit pas être vide");
                else if (txtPrenom.getText().equals(""))
                    addUserWarning.setContentText("Le champ 'Prénom' ne doit pas être vide");
                else if (txtTelephone.getText().equals(""))
                    addUserWarning.setContentText("Le champ 'Téléphone' ne doit pas être vide");
                else if (txtUsername.getText().equals(""))
                    addUserWarning.setContentText("Le champ 'Nom d'utilisateur' ne doit pas être vide");

                addUserWarning.showAndWait();

            } else if (txtTelephone.getText().length() < 9) {
                addUserWarning.setContentText("Numéro de téléphone érroné !");
                addUserWarning.showAndWait();
            } else {
                try {
                    preparedStatement = cnx.prepareStatement(sql);
                    preparedStatement.setString(1, txtNom.getText());
                    preparedStatement.setString(2, txtPrenom.getText());
                    preparedStatement.setString(3, txtTelephone.getText());
                    preparedStatement.setString(4, txtUsername.getText());
                    preparedStatement.execute();

                    Alert editDone = new Alert(Alert.AlertType.INFORMATION);
                    editDone.setTitle("Modifier Informations");
                    editDone.setHeaderText(null);
                    editDone.setContentText("Vos informations ont été modifiées avec succés!");

                    editDone.showAndWait();

                    ((Node) event.getTarget()).getScene().getWindow().hide();

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

    public void cancelEdit(ActionEvent event) {
        if (txtNom.getText().equals(oldNom) && txtPrenom.getText().equals(oldPrenom)
                && txtTelephone.getText().equals(oldTelephone) && txtUsername.getText().equals(oldUsername)) {
            ((Node) event.getTarget()).getScene().getWindow().hide();
        } else AddProductCtrl.cancelEdit(event, cancelBtn);
    }
}
