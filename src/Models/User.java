package Models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class User {

    public SimpleIntegerProperty userID = new SimpleIntegerProperty();
    public SimpleStringProperty userNom = new SimpleStringProperty();
    public SimpleStringProperty userPrenom = new SimpleStringProperty();
    public SimpleStringProperty username    = new SimpleStringProperty();
    public SimpleStringProperty userTelephone = new SimpleStringProperty();
    public SimpleStringProperty userPassword = new SimpleStringProperty();
    public SimpleBooleanProperty admin = new SimpleBooleanProperty();


    public User() {
    }

    public Integer getUserID() {

        return userID.get();
    }

    public void setUserID(int userID) {

        this.userID = new SimpleIntegerProperty(userID);
    }

    public String getUserNom() {
        return userNom.get();
    }

    public void setUserNom(String userNom) {

        this.userNom = new SimpleStringProperty(userNom);
    }

    public String getUserPrenom() {

        return userPrenom.get();
    }

    public void setUserPrenom(String userPrenom) {

        this.userPrenom = new SimpleStringProperty(userPrenom);
    }

    public String getUsername() {

        return username.get();
    }

    public void setUsername(String username) {

        this.username = new SimpleStringProperty(username);
    }

    public String getUserTelephone() {

        return userTelephone.get();
    }

    public void setUserTelephone(String userTelephone) {

        this.userTelephone = new SimpleStringProperty(userTelephone);
    }

    public String getUserPassword() {

        return userPassword.get();
    }

    public void setUserPassword(String userPassword) {

        this.userPassword = new SimpleStringProperty(userPassword) ;
    }

    public boolean isAdmin() {
        return admin.get();
    }

    public void setAdmin(boolean admin) {
        this.admin.set(admin);
    }
}
