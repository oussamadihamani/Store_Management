package Models;

import javafx.beans.property.*;

public class Product {
    
    public SimpleIntegerProperty productID       = new SimpleIntegerProperty() ;
    public SimpleStringProperty productType      = new SimpleStringProperty() ;
    public SimpleIntegerProperty nombrePiece     = new SimpleIntegerProperty();
    public SimpleStringProperty productMarque    = new SimpleStringProperty() ;
    public SimpleIntegerProperty productPrice     = new SimpleIntegerProperty() ;
    public SimpleIntegerProperty productQuantity = new SimpleIntegerProperty() ;

    public Product() {
    }

    public Integer getProductID() {

        return productID.get();
    }

    public void setProductID(int productID) {

        this.productID = new SimpleIntegerProperty(productID);
    }

    public String getProductType() {

        return productType.get();
    }

    public void setProductType(String productType) {

        this.productType = new SimpleStringProperty(productType);
    }

    public Integer getNombrePiece() {

        return nombrePiece.get();
    }

    public void setNombrePiece(int nombrePiece) {

        this.nombrePiece = new SimpleIntegerProperty(nombrePiece);
    }

    public String getProductMarque() {
        return productMarque.get();
    }

    public void setProductMarque(String productMarque) {
        this.productMarque = new SimpleStringProperty(productMarque);
    }

    public double getProductPrice() {
        return productPrice.get();
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = new SimpleIntegerProperty(productPrice);
    }

    public Integer getProductQuantity() {
        return productQuantity.get();
    }

    public void setProductQuantity(int productQuantity) {

        this.productQuantity = new SimpleIntegerProperty(productQuantity);
    }
}

