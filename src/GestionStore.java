import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.applet.Main;

import java.net.URL;
import java.util.Optional;

public class GestionStore extends Application {


    @Override
    public void start(Stage stage) throws Exception {

        //set Acceuil.fxml as default window
        //
        //
        //
        // /*
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/Acceuil.fxml"));
        AnchorPane rootpane = loader.load();
        String sceneFile = "/Views/Acceuil.fxml";
        Parent root;
        URL url;

        url  = getClass().getResource( sceneFile );
        root = FXMLLoader.load( url );


        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Views/Acceuil.fxml"));
        Parent content = loader.load();

        Scene scene = new Scene(content);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        //set the window in the center of the screen
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);

        //popup alert confirmation before window clsoe
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Alert closeAlert = new Alert(Alert.AlertType.CONFIRMATION);
                closeAlert.setTitle("Fermer l'application");
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

    public static void main (String[] args) {

        launch(args);
    }

}