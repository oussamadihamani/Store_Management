package Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class AcceuilCtrl implements Initializable {

    @FXML
    JFXTextField usernameTxt;

    @FXML
    JFXPasswordField passwordTxt;

    @FXML
    JFXButton connecterBtn;

    @FXML
    Label emptyWarning;

    @FXML
    Label shortWarning;

    @FXML
    Label usernameWarning;

    @FXML
    Label passwordWarning;

    public static String username;

    public static int id;


    public static Connection connect = MySQLAccess.ConnectDB();
    PreparedStatement preparedStatement = null;
    String sql = null;
    ResultSet rs = null;
    int rowCount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameTxt.setLabelFloat(true);
        passwordTxt.setLabelFloat(true);
    }


    public void accessAdmin(ActionEvent evnt) {

        emptyWarning.setVisible(false);
        shortWarning.setVisible(false);
        usernameWarning.setVisible(false);
        passwordWarning.setVisible(false);

        if (usernameTxt.getText().equals("") || passwordTxt.getText().equals("")) {
            emptyWarning.setVisible(true);
        } else if (passwordTxt.getText().length() < 6) {
            shortWarning.setVisible(true);
        } else {

            sql = "select count(*) from User where username = '" + usernameTxt.getText() + "';";

            try {

                System.out.println(connect+"e");
                preparedStatement = connect.prepareStatement(sql);
                rs = preparedStatement.executeQuery();

                while (rs.next()) {

                    rowCount = rs.getInt("count(*)");
                }


                System.out.println(rowCount);


                if (rowCount == 0) {
                    usernameWarning.setVisible(true);
                } else {

                    sql = "select count(*) from User where username = '" + usernameTxt.getText() + "' and userPassword = '" + passwordTxt.getText() + "';";

                    preparedStatement = connect.prepareStatement(sql);
                    rs = preparedStatement.executeQuery(sql);

                    while (rs.next()) {
                        rowCount = rs.getInt("count(*)");
                    }

                    if (rowCount == 0) {
                        passwordWarning.setVisible(true);
                    } else {

                        username = usernameTxt.getText();

                        sql = "select userID from User where username = '" + username + "';";
                        preparedStatement = connect.prepareStatement(sql);
                        rs = preparedStatement.executeQuery(sql);
                        rs.next();

                        id = rs.getInt(1);

                        Parent dashParent = FXMLLoader.load(getClass().getResource("/Views/Dashbord.fxml"));

                        Scene dashScene = new Scene(dashParent);
                        Stage dashStage = (Stage) ((Node) evnt.getSource()).getScene().getWindow();

                        dashStage.setScene(dashScene);
                        dashStage.show();


                        /***********************************************************************************************************
                         *******************           make the window in the center of the screen            **********************
                         **********************************************************************************************************/
                        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
                        dashStage.setX((primScreenBounds.getWidth() - dashStage.getWidth()) / 2);
                        dashStage.setY((primScreenBounds.getHeight() - dashStage.getHeight()) / 2);
                        dashStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                Alert closeAlert = new Alert(Alert.AlertType.CONFIRMATION);
                                closeAlert.setTitle("Quitter l'application");
                                closeAlert.setHeaderText(null);
                                closeAlert.setContentText("Veuillez vous vraiment quitter ?");

                                Button exitButton = (Button) closeAlert.getDialogPane().lookupButton(
                                        ButtonType.OK
                                );
                                exitButton.setText("Quitter");

                                Button cancelButton = (Button) closeAlert.getDialogPane().lookupButton(
                                        ButtonType.CANCEL
                                );
                                cancelButton.setText("Annuler");

                                Optional<ButtonType> result = closeAlert.showAndWait();

                                if (result.get() == ButtonType.OK) System.exit(0);

                                else event.consume();
                            }
                        });
                    }
                }

            } catch (Exception e) {
                System.out.println(e + " Error acceuil");
            }
        }
    }

    @FXML
    public void onEnter(ActionEvent ae) {
        if (ae.getSource() == usernameTxt || ae.getSource() == passwordTxt) accessAdmin(ae);
    }
}