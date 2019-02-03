package Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;


public class AddProductCtrl {

    @FXML
    AnchorPane form;

    @FXML
    JFXComboBox txtProductType = new JFXComboBox();

    @FXML
    JFXComboBox txtProductMarque = new JFXComboBox();

    @FXML
    JFXTextField txtProductPieces = new JFXTextField();

    @FXML
    JFXTextField txtProductPrice = new JFXTextField();

    @FXML
    JFXTextField txtProductQuanity = new JFXTextField();

    @FXML
    JFXButton resetFormBtn;

    @FXML
    JFXButton addBtn;

    @FXML
    JFXButton cancelBtn;

    Connection cnx = AcceuilCtrl.connect;


    @FXML
    public void initialize() {


        String productType = "select distinct productType from Product;";

        try {
            ResultSet rs = cnx.createStatement().executeQuery(productType);
            while (rs.next()) {
                txtProductType.getItems().add(rs.getString("productType"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtProductType.setEditable(true);


        String productMarque = "select distinct productMarque from Product;";

        try {
            ResultSet res = cnx.createStatement().executeQuery(productMarque);
            while (res.next()) {
                txtProductMarque.getItems().add(res.getString("productMarque"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtProductMarque.setEditable(true);


        Tooltip tooltip = new Tooltip();
        tooltip.setText("Ce champ ne doit contenir que des chiffres !");

        AddUserCtrl.onlynumbersField(tooltip, txtProductPrice, form);

        AddUserCtrl.onlynumbersField(tooltip, txtProductPieces, form);

        AddUserCtrl.onlynumbersField(tooltip, txtProductQuanity, form);
    }


    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == resetFormBtn) clearForm();

        cancelEdit(ae, cancelBtn);
    }

    private void clearForm() {
        resetForm(txtProductType, txtProductMarque, txtProductPieces, txtProductPrice, txtProductQuanity);
    }

    static void resetForm(JFXComboBox txtProductType, JFXComboBox txtProductMarque, JFXTextField txtProductPieces, JFXTextField txtProductPrice, JFXTextField txtProductQuanity) {
        txtProductType.setValue(null);
        txtProductMarque.setValue(null);
        txtProductPieces.clear();
        txtProductPrice.clear();
        txtProductQuanity.clear();
    }


    static void cancelEdit(ActionEvent ae, JFXButton cancelBtn) {
        AddUserCtrl.cancelUserEdit(ae, cancelBtn);
    }

    public void addNewProduct(ActionEvent ae) {

        PreparedStatement preparedStatement = null;
        String sql = "insert into Product (productType, productMarque, nombrePiece, productPrice, productQuantity) values (?,?,?,?,?)";

        if ((txtProductType.getSelectionModel().isEmpty() && txtProductType.getEditor().getText().equals("")) || (txtProductMarque.getSelectionModel().isEmpty() && txtProductMarque.getEditor().getText().equals(""))
                || txtProductPieces.getText().equals("") || txtProductPrice.getText().equals("") || txtProductQuanity.getText().equals("")) {

            Alert productAdded = new Alert(Alert.AlertType.INFORMATION);
            productAdded.setTitle("Information Dialog");
            productAdded.setHeaderText("Impossible d'ajouter le produit !");

            if (txtProductType.getSelectionModel().isEmpty() && txtProductType.getEditor().getText().equals(""))
                productAdded.setContentText("Le champ 'Type' ne doit pas être vide");
            else if (txtProductMarque.getSelectionModel().isEmpty() && txtProductMarque.getEditor().getText().equals(""))
                productAdded.setContentText("Le champ 'Marque' ne doit pas être vide");
            else if (txtProductPieces.getText().equals(""))
                productAdded.setContentText("Le champ 'Nombre de pièces' ne doit pas être vide");
            else if (txtProductPrice.getText().equals(""))
                productAdded.setContentText("Le champ 'Prix' ne doit pas être vide");
            else if (txtProductQuanity.getText().equals(""))
                productAdded.setContentText("Le champ 'Quantité' ne doit pas être vide");

            Optional<ButtonType> result = productAdded.showAndWait();

        } else {
            try {
                preparedStatement = cnx.prepareStatement(sql);
                preparedStatement.setString(1, txtProductType.getEditor().getText());
                preparedStatement.setString(2, (String) txtProductMarque.getValue());
                preparedStatement.setString(3, txtProductPieces.getText());
                preparedStatement.setString(4, txtProductPrice.getText());
                preparedStatement.setString(5, txtProductQuanity.getText());
                preparedStatement.execute();

                Alert productAdded = new Alert(Alert.AlertType.CONFIRMATION);
                productAdded.setTitle("Ajouter un produit");
                productAdded.setHeaderText("Le produit à été ajouté avec succés!");
                productAdded.setContentText("Ajouter un autre produit ?");

                Button exitButton = (Button) productAdded.getDialogPane().lookupButton(
                        ButtonType.OK
                );
                exitButton.setText("Oui");

                Button cancelButton = (Button) productAdded.getDialogPane().lookupButton(
                        ButtonType.CANCEL
                );
                cancelButton.setText("Non");

                Optional<ButtonType> result = productAdded.showAndWait();
                if (result.get() == ButtonType.OK) {
                    clearForm();
                } else {
                    ((Node) ae.getTarget()).getScene().getWindow().hide();
                }

            } catch (SQLIntegrityConstraintViolationException e) {
                Alert duplicateProduce = new Alert(Alert.AlertType.INFORMATION);
                duplicateProduce.setTitle("Opération échouée");
                duplicateProduce.setHeaderText("Ce produit est déjà créé !");
                duplicateProduce.setContentText("Veuillez vérifier vos informations");

                Optional<ButtonType> result = duplicateProduce.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}