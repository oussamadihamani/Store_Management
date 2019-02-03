package Controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompteCtrl {

    @FXML
    Label txtNom;

    @FXML
    Label txtPrenom;

    @FXML
    Label txtTelephone;

    @FXML
    Label txtUsername;

    @FXML
    Hyperlink personalInfos;

    @FXML
    Hyperlink passwordEdit;

    @FXML
    void initialize() {

        String sql = "select * from User where username = '"+AcceuilCtrl.username+"'";

        try {
            ResultSet rs = AcceuilCtrl.connect.createStatement().executeQuery(sql);

            while (rs.next()) {
                txtNom.setText(rs.getString("userNom"));
                txtPrenom.setText(rs.getString("userPrenom"));
                txtTelephone.setText(rs.getString("userTelephone"));
                txtUsername.setText(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  void actionPerformed(ActionEvent event) throws IOException {
        Stage newStage = new Stage();
        newStage.initStyle(StageStyle.UNDECORATED);
        AnchorPane anchorPanePopup;
        if (event.getSource() == personalInfos) anchorPanePopup = FXMLLoader.load(getClass().getResource("/Views/EditInfo.fxml"));
        else anchorPanePopup = FXMLLoader.load(getClass().getResource("/Views/EditPassword.fxml"));
        Scene scene = new Scene(anchorPanePopup);
        newStage.setScene(scene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Ajouter un nouveau Produit");
        newStage.showAndWait();
    }


}
