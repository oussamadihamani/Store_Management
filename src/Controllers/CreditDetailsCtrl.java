package Controllers;

import Models.Client;
import Models.Operation;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import fonts.fontawesome.FontAwesomeIcon;
import fonts.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Optional;

public class CreditDetailsCtrl {

    @FXML
    AnchorPane creditForm;

    @FXML
    Label creditInfosLabel;

    @FXML
    JFXComboBox<String> searchTypeCombo;

    @FXML
    JFXDatePicker searchDateTxt;

    @FXML
    JFXTextField searchUserTxt;

    @FXML
    TableView<Operation> tableview;

    @FXML
    TableColumn<Operation, String> colOperationType;

    @FXML
    TableColumn<Operation, Integer> colOperationAmount;

    @FXML
    TableColumn<Operation, Date> colOperationDate;

    @FXML
    TableColumn<Operation, Time> colOperationTime;

    @FXML
    TableColumn<Operation, String> colOperationUser;

    SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");

    Connection cnx = AcceuilCtrl.connect;

    private static ObservableList<Operation> data;


    @FXML
    void initialize() {

        creditInfosLabel.setText(CreditCtrl.currentClientNom + " " + CreditCtrl.currentClientPrenom + " :     " + CreditCtrl.currentClientAmount + " DA");

        searchTypeCombo.getItems().setAll("Crédit", "Débit");

        searchTypeCombo.setValue(null);
        searchDateTxt.setValue(null);
        searchUserTxt.setText("");

        searchDateTxt.setEditable(false);

        searchTypeCombo.setLabelFloat(true);
        searchUserTxt.setLabelFloat(true);


        /***************************************************************************************************************
         *****************************            Initialize Colmuns values                *****************************
         **************************************************************************************************************/
        colOperationType.setCellValueFactory(new PropertyValueFactory<Operation, String>("operationType"));
        colOperationAmount.setCellValueFactory(new PropertyValueFactory<Operation, Integer>("operationAmount"));
        colOperationDate.setCellValueFactory(new PropertyValueFactory<Operation, Date>("operationDate"));
        colOperationTime.setCellValueFactory(new PropertyValueFactory<Operation, Time>("operationTime"));
        colOperationUser.setCellValueFactory(new PropertyValueFactory<Operation, String>("operationUsername"));


        /***************************************************************************************************************
         ********************************            Set Columns width                 *********************************
         **************************************************************************************************************/
        colOperationType.prefWidthProperty().bind(tableview.widthProperty().divide(4.9));
        colOperationAmount.prefWidthProperty().bind(tableview.widthProperty().divide(5));
        colOperationDate.prefWidthProperty().bind(tableview.widthProperty().divide(5.1));
        colOperationTime.prefWidthProperty().bind(tableview.widthProperty().divide(5.1));
        colOperationUser.prefWidthProperty().bind(tableview.widthProperty().divide(5));


        /***************************************************************************************************************
         ****************************            Disnable Sort by columns                 ******************************
         **************************************************************************************************************/
        colOperationType.setSortable(false);
        colOperationAmount.setSortable(false);
        colOperationDate.setSortable(false);
        colOperationTime.setSortable(false);
        colOperationUser.setSortable(false);

        /***************************************************************************************************************
         ****************************            Disable Colmuns Resize                 ****************************
         **************************************************************************************************************/
        colOperationType.setResizable(false);
        colOperationAmount.setResizable(false);
        colOperationDate.setResizable(false);
        colOperationTime.setResizable(false);
        colOperationUser.setSortable(false);


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
            buildData("select * from Operation where operationClient  = " + CreditCtrl.currentClientID + " order by operationDate DESC");
        } catch (Exception ce) {
            ce.toString();
        }
    }


    /***************************************************************************************************************
     **************************            Insert Data in the TableView              *******************************
     **************************************************************************************************************/
    private void buildData(String slqStatement) {

        data = FXCollections.observableArrayList();

        try {

            ResultSet rs = AcceuilCtrl.connect.createStatement().executeQuery(slqStatement);

            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");

            while (rs.next()) {

                Operation operation = new Operation();

                operation.operationType.set(rs.getString("operationType"));
                operation.operationAmount.set(rs.getInt("operationAmount"));
                operation.operationDate = dateformat.format(rs.getTimestamp("operationDate"));
                operation.operationTime = timeformat.format(rs.getTimestamp("operationDate"));
                operation.operationUsername.set(rs.getString("operationUsername"));

                data.add(operation);
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

        String date;
        java.sql.Date sqlDate = null;

        if (searchTypeCombo.getValue() == null) {
            searchTypeCombo.setValue("");
        }

        if (searchDateTxt.getValue() == null) {
            date = "";
        } else {
            sqlDate = java.sql.Date.valueOf(searchDateTxt.getValue());
            date = String.valueOf(sqlDate);
        }

        String sql = "SELECT * FROM Operation WHERE operationType like '%" + searchTypeCombo.getValue() + "%' and operationDate like '%" + date +
                "%' and operationUsername like '%" + searchUserTxt.getText() + "%' and operationClient = " + CreditCtrl.currentClientID + " order by operationDate;";

        System.out.println(sql);

        buildData(sql);
    }

}
