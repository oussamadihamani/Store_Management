package Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Client {
    
    public SimpleIntegerProperty clientID = new SimpleIntegerProperty();
    public SimpleStringProperty clientNom = new SimpleStringProperty();
    public SimpleStringProperty clientPrenom = new SimpleStringProperty();
    public SimpleStringProperty clientTelephone = new SimpleStringProperty();
    public SimpleIntegerProperty clientAmount = new SimpleIntegerProperty();


    public Client() {
    }

    public int getClientID() {
        return clientID.get();
    }

    public SimpleIntegerProperty clientIDProperty() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID.set(clientID);
    }

    public String getClientNom() {
        return clientNom.get();
    }

    public SimpleStringProperty clientNomProperty() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom.set(clientNom);
    }

    public String getClientPrenom() {
        return clientPrenom.get();
    }

    public SimpleStringProperty clientPrenomProperty() {
        return clientPrenom;
    }

    public void setClientPrenom(String clientPrenom) {
        this.clientPrenom.set(clientPrenom);
    }

    public String getClientTelephone() {
        return clientTelephone.get();
    }

    public SimpleStringProperty clientTelephoneProperty() {
        return clientTelephone;
    }

    public void setClientTelephone(String clientTelephone) {
        this.clientTelephone.set(clientTelephone);
    }

    public int getClientAmount() {
        return clientAmount.get();
    }

    public SimpleIntegerProperty clientAmountProperty() {
        return clientAmount;
    }

    public void setClientAmount(int clientAmount) {
        this.clientAmount.set(clientAmount);
    }
}
