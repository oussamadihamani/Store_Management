package Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.*;
import java.util.Optional;


public class AddClientCtrl {

    @FXML
    AnchorPane form;

    @FXML
    JFXTextField txtNom;

    @FXML
    JFXTextField txtPrenom;

    @FXML
    JFXTextField txtTelephone;

    @FXML
    JFXTextField txtAmount;

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


        onlytxtField(txtOnly, txtNom);

        onlytxtField(txtOnly, txtPrenom);

        onlynumbersField(numbersOnly, txtTelephone, form);

        onlynumbersField(numbersOnly, txtAmount, form);

    }


    static void onlynumbersField(Tooltip numbersOnly, TextField txtTelephone, AnchorPane form) {
        txtTelephone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtTelephone.setText(newValue.replaceAll("[^\\d]" , ""));

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
                    txtPrenom.setText(newValue.replaceAll("[^a-zA-Z ]" , ""));

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
        txtAmount.clear();
    }


    static void cancelEdit(ActionEvent ae, JFXButton cancelBtn) {
        AddUserCtrl.cancelUserEdit(ae, cancelBtn);
    }


    public void addNewClient(ActionEvent ae) throws SQLException {

        PreparedStatement preparedStatement = null;
        String sql = "insert into Client (clientNom, clientPrenom, clientTelephone, clientAmount) values (?,?,?,?)";

        Alert addClientWarning = new Alert(Alert.AlertType.INFORMATION);
        addClientWarning.setHeaderText("Impossible d'ajouter le client !");

        if (txtNom.getText().equals("") || txtPrenom.getText().equals("") || txtTelephone.getText().equals("") ) {

            if (txtNom.getText().equals("")) addClientWarning.setContentText("Le champ 'Nom' ne doit pas être vide");
            else if (txtPrenom.getText().equals(""))
                addClientWarning.setContentText("Le champ 'Prénom' ne doit pas être vide");
            else if (txtTelephone.getText().equals(""))
                addClientWarning.setContentText("Le champ 'Téléphone' ne doit pas être vide");

            addClientWarning.showAndWait();

        } else if (txtTelephone.getText().length() < 9) {
            addClientWarning.setContentText("Numéro de téléphone érroné !");
            addClientWarning.showAndWait();
        } else {
            try {
                if (txtAmount.getText().equals("")) txtAmount.setText("0");
                preparedStatement = cnx.prepareStatement(sql);
                preparedStatement.setString(1, txtNom.getText());
                preparedStatement.setString(2, txtPrenom.getText());
                preparedStatement.setString(3, txtTelephone.getText());
                preparedStatement.setString(4, txtAmount.getText());
                preparedStatement.execute();

                Alert clientAdded = new Alert(Alert.AlertType.CONFIRMATION);
                clientAdded.setTitle("Confirmation Dialog");
                clientAdded.setHeaderText("Le client à été ajouté avec succés!");
                clientAdded.setContentText("Ajouter un autre client ?");

                Button exitButton = (Button) clientAdded.getDialogPane().lookupButton(
                        ButtonType.OK
                );
                exitButton.setText("Oui");

                Button cancelButton = (Button) clientAdded.getDialogPane().lookupButton(
                        ButtonType.CANCEL
                );
                cancelButton.setText("Non");

                if (Integer.parseInt(txtAmount.getText()) > 0) {
                    try {
                        String getID = "select clientID from Client order by clientID DESC ;";
                        PreparedStatement getNextID = cnx.prepareStatement(getID);
                        ResultSet iDresult = getNextID.executeQuery();
                        iDresult.next();
                        int nxtiD = iDresult.getInt(1);

                        String addOperation = "insert into Operation(operationType, operationAmount, operationClient, operationUser, operationUsername) values (?,?,?,?,?);";
                        PreparedStatement addOp = cnx.prepareStatement(addOperation);
                        addOp.setString(1, "Crédit");
                        addOp.setInt(2, Integer.parseInt(txtAmount.getText()));
                        addOp.setInt(3, nxtiD);
                        addOp.setInt(4, AcceuilCtrl.id);
                        addOp.setString(5,AcceuilCtrl.username);
                        addOp.execute();
                    } catch (SQLException e) {
                    }
                }

                Optional<ButtonType> result = clientAdded.showAndWait();
                if (result.get() == ButtonType.OK) {
                    clearForm();
                } else {
                    ((Node) ae.getTarget()).getScene().getWindow().hide();
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    try {

                        String sqllitelol = "select count(*) from Client where clientNom = '" + txtNom.getText() + "' and clientPrenom ='" + txtPrenom.getText() + "';";

                        Statement checkFullName = cnx.createStatement();
                        ResultSet uniqueFullName = checkFullName.executeQuery(sqllitelol);
                        uniqueFullName.next();

                        PreparedStatement checkTelephone = cnx.prepareStatement("select count(*) from Client where clientTelephone = '" + txtTelephone.getText() + "';");
                        ResultSet uniqueTelephone = checkTelephone.executeQuery();
                        uniqueTelephone.next();

                        if (uniqueFullName.getInt(1) > 0) {
                            addClientWarning.setContentText("Ce client existe déjà !\n" + "Veuillez entrer un autre Nom et/ou Prénom");
                        } else if (uniqueTelephone.getInt(1) > 0) {
                            addClientWarning.setContentText("Le numéro du téléphone est déjà utilisé !\n" + "Veuillez entrer un autre numéro du téléphone");
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }

                addClientWarning.setHeaderText("Information dupliquée");
                addClientWarning.showAndWait();
            }
        }
    }
}