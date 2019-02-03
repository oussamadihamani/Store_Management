package Controllers;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashbordCtrl implements Initializable {

    @FXML
    Label welcomeLabel;

    @FXML
    Pane stockpane;

    @FXML
    Label stocklabel;

    @FXML
    Pane creditpane;

    @FXML
    Pane clientspane;

    @FXML
    Pane comptepane;

    @FXML
    Pane propospane;

    @FXML
    Pane logoutpane;

    @FXML
    Pane userspane;

    @FXML
    AnchorPane content;

    @FXML
    Pane menu;

    PreparedStatement statement = null;
    ResultSet rs = null;
    String sql = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Rectangle r1 = new Rectangle(50, 300, 200, 2);
        r1.setFill(Color.BLUE);
        content.getChildren().add(r1);

        TranslateTransition translate = new TranslateTransition(Duration.millis(900));
        translate.setToX(720);
        translate.setToY(0);

        FillTransition fill = new FillTransition(Duration.millis(900));
        fill.setToValue(Color.DARKRED);

        ScaleTransition scale = new ScaleTransition(Duration.millis(900));
        scale.setToX(0.1);
        scale.setToY(0.1);

        ParallelTransition transition1 = new ParallelTransition(r1, translate, fill, scale);
        transition1.setCycleCount(Timeline.INDEFINITE);
        transition1.setAutoReverse(false);

        transition1.play();

        sql = "select * from User where username ='" + AcceuilCtrl.username + "'";
        try {
            statement = AcceuilCtrl.connect.prepareStatement(sql);
            rs = statement.executeQuery();

            while (rs.next()) {
                String userprenom = rs.getString("userPrenom");
                Boolean isAdmin = rs.getBoolean("admin");
                welcomeLabel.setText("Bienvenue " + userprenom + " !");
                if (!isAdmin) userspane.setVisible(false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void menuChange(MouseEvent event) throws Exception {

        if (event.getButton() == MouseButton.PRIMARY) {
            menu.getChildren().forEach(Pane -> Pane.getStyleClass().remove("active"));

            if (event.getSource() == stockpane) showStock();
            else if (event.getSource() == creditpane) showCredit();
            else if (event.getSource() == clientspane) showClients();
            else if (event.getSource() == comptepane) showCompte();
            else if (event.getSource() == userspane) showUsers();
        }

    }

    private void showStock() {

        try {
            content.getChildren().setAll(Collections.singleton(FXMLLoader.load(getClass().getResource("/Views/Stock.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stockpane.getStyleClass().add("active");
    }


    private void showCredit() {

        try {
            content.getChildren().setAll(Collections.singleton(FXMLLoader.load(getClass().getResource("/Views/Credit.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        creditpane.getStyleClass().add("active");

    }


    private void showClients() {

        try {
            content.getChildren().setAll(Collections.singleton(FXMLLoader.load(getClass().getResource("/Views/Clients.fxml"))));

        } catch (IOException e) {
            e.printStackTrace();
        }

        clientspane.getStyleClass().add("active");
    }


    private void showCompte() {

        try {
            content.getChildren().setAll(Collections.singleton(FXMLLoader.load(getClass().getResource("/Views/Compte.fxml"))));

        } catch (IOException e) {
            e.printStackTrace();
        }
        comptepane.getStyleClass().add("active");
    }


    private void showUsers() {

        try {
            content.getChildren().setAll(Collections.singleton(FXMLLoader.load(getClass().getResource("/Views/Users.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        userspane.getStyleClass().add("active");
    }


    public void showPropos(ActionEvent event) throws Exception {

        Stage newStage = new Stage();
        newStage.initStyle(StageStyle.UNDECORATED);
        AnchorPane anchorPanePopup = FXMLLoader.load(getClass().getResource("/Views/Propos.fxml"));
        Scene scene = new Scene(anchorPanePopup);
        newStage.setScene(scene);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.showAndWait();
    }


    /**
     * Logout and return to the login window
     */
    public void handleClose(ActionEvent event) throws Exception {

        Alert logoutAlert = new Alert(Alert.AlertType.CONFIRMATION);
        logoutAlert.setTitle("Déconnexion");
        logoutAlert.setHeaderText(null);
        logoutAlert.setContentText("Veuillez vous vraiment déconnecter ?");

        Button exitButton = (Button) logoutAlert.getDialogPane().lookupButton(
                ButtonType.OK
        );
        exitButton.setText("Déconnecter");

        Button cancelButton = (Button) logoutAlert.getDialogPane().lookupButton(
                ButtonType.CANCEL
        );
        cancelButton.setText("Annuler");

        Optional<ButtonType> result = logoutAlert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Parent dashParent = FXMLLoader.load(getClass().getResource("/Views/Acceuil.fxml"));
            Scene dashScene = new Scene(dashParent);
            Stage dashStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            dashStage.setScene(dashScene);
            dashStage.show();

            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            dashStage.setX((primScreenBounds.getWidth() - dashStage.getWidth()) / 2);
            dashStage.setY((primScreenBounds.getHeight() - dashStage.getHeight()) / 2);
        }
    }
}


