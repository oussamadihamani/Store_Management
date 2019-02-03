package Controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;

public class ProposCtrl {

    public void close(ActionEvent event) {
        ((Node) event.getTarget()).getScene().getWindow().hide();
    }
}
