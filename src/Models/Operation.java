package Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Operation {

    public SimpleIntegerProperty operationID = new SimpleIntegerProperty();
    public SimpleStringProperty operationType = new SimpleStringProperty();
    public String operationDate;
    public String operationTime;
    public SimpleIntegerProperty operationAmount = new SimpleIntegerProperty();
    public SimpleIntegerProperty operationClient = new SimpleIntegerProperty();
    public SimpleStringProperty operationUsername = new SimpleStringProperty();


    public Operation() {
    }

    public int getOperationID() {
        return operationID.get();
    }

    public SimpleIntegerProperty operationIDProperty() {
        return operationID;
    }

    public void setOperationID(int operationID) {
        this.operationID.set(operationID);
    }

    public String getOperationType() {
        return operationType.get();
    }

    public SimpleStringProperty operationTypeProperty() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType.set(operationType);
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    public int getOperationAmount() {
        return operationAmount.get();
    }

    public SimpleIntegerProperty operationAmountProperty() {
        return operationAmount;
    }

    public void setOperationAmount(int operationAmount) {
        this.operationAmount.set(operationAmount);
    }

    public int getOperationClient() {
        return operationClient.get();
    }

    public SimpleIntegerProperty operationClientProperty() {
        return operationClient;
    }

    public void setOperationClient(int operationClient) {
        this.operationClient.set(operationClient);
    }

    public String getOperationUser() {
        return operationUsername.get();
    }

    public SimpleStringProperty operationUsernameProperty() {
        return operationUsername;
    }

}
