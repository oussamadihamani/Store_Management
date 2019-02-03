package Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class EditPassword {

    @FXML
    JFXPasswordField oldPassword;

    @FXML
    JFXPasswordField newPassword;

    @FXML
    JFXPasswordField newPassword1;

    @FXML
    JFXButton cancelBtn;

    @FXML
    JFXButton confirmBtn;

    @FXML
    void initialize() {
        oldPassword.setLabelFloat(true);
        newPassword.setLabelFloat(true);
        newPassword1.setLabelFloat(true);
    }

    public void editPassword(ActionEvent event) throws Exception{

        String sql = "select count(*) from User where username = '"+AcceuilCtrl.username+"' and userPassword = '"+oldPassword.getText()+"';";

        ResultSet rs = AcceuilCtrl.connect.createStatement().executeQuery(sql);

        rs.next();

        Alert alertEdit = new Alert(Alert.AlertType.INFORMATION);
        alertEdit.setTitle("Opération échouée");
        alertEdit.setHeaderText("Impossible de modifier votre mot de passe !");

        if (oldPassword.getText().equals("")) {
            alertEdit.setContentText("Veuillez entrez votre mot de passe actuel");
            alertEdit.showAndWait();
        } else if (rs.getInt(1) == 0 ) {
            alertEdit.setContentText("Mot de passe éronné\n  \nVeuillez vérifier votre mot de passe actuel");
            alertEdit.showAndWait();
        } else if (newPassword.getText().equals("")) {
            alertEdit.setContentText("Veuillez entrez un nouveau mot de passe");
            alertEdit.showAndWait();
        } else if (newPassword.getLength() < 6) {
            alertEdit.setContentText("Mot de passe doit être au minimum 06 caractères");
            alertEdit.showAndWait();
        } else if (newPassword1.getText().equals("")) {
            alertEdit.setContentText("Veuillez confirmer votre nouveau mot de passe");
            alertEdit.showAndWait();
        } else if (!newPassword1.getText().equals(newPassword.getText())) {
            alertEdit.setContentText("Les mots de passe ne sont pas identiques");
            alertEdit.showAndWait();
        } else {
            sql = "update User set userPassword = '"+newPassword.getText()+"' where username = '"+AcceuilCtrl.username+"';";
            PreparedStatement statement = AcceuilCtrl.connect.prepareStatement(sql);
            statement.execute();

            alertEdit.setHeaderText(null);
            alertEdit.setContentText("Votre mot de passe à été modifié avec succès !");
            alertEdit.showAndWait();

            ((Node) event.getTarget()).getScene().getWindow().hide();
        }
    }


    public void cancelEdit(ActionEvent event) {

        AddProductCtrl.cancelEdit(event, cancelBtn);
    }


}
