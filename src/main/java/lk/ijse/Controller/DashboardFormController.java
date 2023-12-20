package lk.ijse.Controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class DashboardFormController {


    @FXML
    private AnchorPane rootnode;
    public JFXButton Home;
    public JFXButton Customer;
    public JFXButton Order;
    public JFXButton Item;
    public JFXButton Employee;
    public JFXButton RawMaterial;
    public JFXButton Suppliers;




    @FXML
    private AnchorPane root;

    public void initialize() throws IOException {
       btnHomeOnAction(null);
    }
    void setForms(String forms) throws IOException {
        String [] formArrays = {"/view/chart_form.fxml,/view/home_form.fxml","/view/customerpage_form.fxml","/view/orders_form.fxml",
                "/view/item_form.fxml","/view/employee_form.fxml","/view/rawmaterial_form.fxml","/view/supplier_form.fxml"
        };

        JFXButton[] btnArray = {Home,Customer,Order,Item,Employee,RawMaterial,Suppliers};

        AnchorPane load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(forms)));
        root.getChildren().clear();
        root.getChildren().add(load);
        for (int i = 0; i < formArrays.length; i++) {
            btnArray[i].setStyle("-fx-background-color:  #800080; -fx-font-size: 12");

            if (forms.equals(formArrays[i])){
                btnArray[i].setStyle("-fx-background-color: #ff6f6f; -fx-text-fill: #000000");
            }
        }
    }

    public void btnHomeOnAction(ActionEvent actionEvent) throws IOException {
       //AnchorPane root = FXMLLoader.load(this.getClass().getResource("/View/home_form.fxml"));
       root.getChildren().clear();
       root.getChildren().add(FXMLLoader.load(this.getClass().getResource("/View/home_form.fxml")));
    }

    public void btnCustomerOnAction(ActionEvent actionEvent) throws IOException {
       // AnchorPane root1 = FXMLLoader.load(getClass().getResource("/view/customerpage_form.fxml"));
        root.getChildren().clear();
        root.getChildren().add(FXMLLoader.load(getClass().getResource("/view/customerpage_form.fxml")));
    }

    public void btnOrderOnAction(ActionEvent actionEvent) throws IOException {

        //AnchorPane root1 = FXMLLoader.load(getClass().getResource("/view/orders_form.fxml"));
        root.getChildren().clear();
        root.getChildren().add(FXMLLoader.load(getClass().getResource("/view/orders_form.fxml")));

    }

    public void btnItemOnAction(ActionEvent actionEvent) throws IOException {
        //AnchorPane root = FXMLLoader.load(getClass().getResource("/view/item_form.fxml"));
        root.getChildren().clear();
        root.getChildren().add(FXMLLoader.load(getClass().getResource("/view/item_form.fxml")));


    }

    public void btnEmployeeOnAction(ActionEvent actionEvent) throws IOException {

        //AnchorPane root1 = FXMLLoader.load(getClass().getResource("/view/employee_form.fxml"));
        root.getChildren().clear();
        root.getChildren().add(FXMLLoader.load(getClass().getResource("/view/employee_form.fxml")));

    }

    public void btnRawMaterialOnAction(ActionEvent actionEvent) throws IOException {

       // AnchorPane root1 = FXMLLoader.load(getClass().getResource("/view/rawmaterial_form.fxml"));
        root.getChildren().clear();
        root.getChildren().add(FXMLLoader.load(getClass().getResource("/view/rawmaterial_form.fxml")));


    }

    public void btnSuppliersOnAction(ActionEvent actionEvent) throws IOException {

        //AnchorPane root1 = FXMLLoader.load(getClass().getResource("/view/supplier_form.fxml"));
        root.getChildren().clear();
        root.getChildren().add(FXMLLoader.load(getClass().getResource("/view/supplier_form.fxml")));

    }


    public void btnlogoutOnAction(ActionEvent actionEvent) throws IOException {
        /*AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/login_form.fxml"));
        Scene scene = new Scene(anchorPane);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Log out");
        stage.centerOnScreen();*/
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/login_form.fxml"));
        Scene scene = new Scene(anchorPane);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login Page");
        stage.centerOnScreen();


    }


    public void btnstockOnAction(ActionEvent actionEvent) throws IOException {
        root.getChildren().clear();
        root.getChildren().add(FXMLLoader.load(getClass().getResource("/view/currentock_form.fxml")));

    }

    public void btnprofileOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/mail_form.fxml"));
        Scene scene = new Scene(anchorPane);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("profile");
        stage.centerOnScreen();
    }
}
