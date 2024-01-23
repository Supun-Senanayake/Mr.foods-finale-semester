package lk.ijse.Controller;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.DB.DbConnection;
import lk.ijse.Model.CustomerModel;
import lk.ijse.Model.EmployeeModel;
import lk.ijse.dto.CustomerDto;
import lk.ijse.dto.EmployeeDto;
import lk.ijse.dto.tm.CustomerTm;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerPageController {
    public AnchorPane root;
    public JFXTextField serchCustomer;


    @FXML
    private JFXTextField txtContactNo;

    @FXML
    private JFXTextField txtid;
    @FXML
    private JFXTextField txtAddress;
    @FXML
    private JFXTextField txtName;

    @FXML
    private TableView<CustomerTm> tblCustomer;

    @FXML
    private TableColumn<?, ?> colId;
    @FXML
    private TableColumn<?, ?> colCusName;
    @FXML
    private TableColumn<?, ?> colAddress;
    @FXML
    private TableColumn<?, ?> colContactNo;
    private CustomerModel customerModel = new CustomerModel();

    public void initialize() {
        generateNextCustomerId();
        setCellValueFactory();
        loadAllCustomer();
        tableListener();
        itemSerachOnAction();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCusName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContactNo.setCellValueFactory(new PropertyValueFactory<>("tel"));
    }

    private void generateNextCustomerId() {
        try {
            String id = customerModel.generateNextCustomerId();
            txtid.setText(id);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
    private  void genarateNextCustomerContact(){
        try{
            String ContactNo = customerModel.generateNextcustomerContactNo();
            txtContactNo.setText(ContactNo);
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void tableListener() {
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValued, newValue) -> {
//            System.out.println(newValue);
            setData(newValue);
        });
    }

    private void setData(CustomerTm row) {
        txtid.setText(row.getId());
        txtName.setText(row.getName());
        txtContactNo.setText(row.getTel());
        txtAddress.setText(row.getAddress());
    }

    private void loadAllCustomer() {
        var model = new CustomerModel();

        ObservableList<CustomerTm> obList = FXCollections.observableArrayList();

        try {
            List<CustomerDto> dtoList = model.getAllCustomer();

            for (CustomerDto dto : dtoList) {
                obList.add(
                        new CustomerTm(
                                dto.getId(),
                                dto.getName(),
                                dto.getAddress(),
                                dto.getTel()
                        )
                );
            }

            tblCustomer.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFields() {
        txtid.setText("");
        txtName.setText("");
        txtAddress.setText("");
        txtContactNo.setText("");
    }


    public void btnsaveOnAction(ActionEvent actionEvent) {
        String id = txtid.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String tel = txtContactNo.getText();

        var dto = new CustomerDto(id, name, address, tel);


        try {
            if (!validateCustomer()){
                return;
            }
            boolean isSaved = customerModel.saveCustomer(dto);

            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer saved!").show();
                initialize();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        new Alert(Alert.AlertType.INFORMATION, "ID no not validate");

    }

    private boolean validateCustomer() {
        boolean isValidate = true;
        boolean name = Pattern.matches("[A-Za-z]{5,}", txtName.getText());
        if (!name){
            showErrorNotification("Invalid Customer Name", "The Customer name you entered is invalid");
            isValidate = false;
        }
        boolean address = Pattern.matches("[A-Za-z]{5,}",txtAddress.getText());
        if(!address){
            showErrorNotification("invalid address","The address you entered is invalid");
            isValidate = false;
        }
        boolean tel = Pattern.matches("^([0-9]{9}|[0-9]{10})$",txtContactNo.getText());
        if (!tel){
            showErrorNotification("Invalid Contact Number", "The Contact Number you entered is invalid");
            isValidate = false;
        }
        return isValidate;
    }

    private void showErrorNotification(String title, String text) {
        Notifications.create()
                .title(title)
                .text(text)
                .showError();
    }
    public void btnupdateOnAction(ActionEvent actionEvent) throws ClassNotFoundException {
        String id = txtid.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String tel = txtContactNo.getText();

        var dto = new CustomerDto(id, name, address, tel);
        try {
            if (!validateCustomer()){
                return;
            }
            boolean isSaved = customerModel.updateCustomer(dto);

            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer update!").show();
                initialize();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        new Alert(Alert.AlertType.INFORMATION, "ID no not validate");

    }


    public void btndeleteOnAction(ActionEvent actionEvent) {
        String id = txtid.getText();

        try {
            boolean isDeleted = customerModel.deleteCustomer(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer deleted!").show();
                initialize();
            } else {
                new Alert(Alert.AlertType.CONFIRMATION, "customer not deleted!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void btnclearOnAction(ActionEvent actionEvent) {
        clearFields();
    }

    public void txtseachcustomerOnAction(ActionEvent actionEvent) {
        String id = txtid.getText();

//        var model = new CustomerModel();
        try {

            CustomerDto customerDto = customerModel.searchCustomer(id);
//            System.out.println(customerDto);
            if (customerDto != null) {
                txtid.setText(customerDto.getId());
                txtName.setText(customerDto.getName());
                txtAddress.setText(customerDto.getAddress());
                txtContactNo.setText(customerDto.getTel());
            } else {
                new Alert(Alert.AlertType.INFORMATION, "customer not found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void btnbackOnActin(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/orders_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Orders");
        stage.centerOnScreen();
    }

    public void btnprintOnAction(ActionEvent actionEvent) throws JRException {
        String id = txtid.getText();

        try {
            CustomerDto dto = customerModel.searchCustomer(id);
            if(dto!=null){
                viewCustomerReport(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void viewCustomerReport(CustomerDto dto) throws JRException {

            HashMap hashMap = new HashMap();
            hashMap.put("id",dto.getId());
            hashMap.put("name",dto.getName());
            hashMap.put("address",dto.getAddress());
            hashMap.put("tel",dto.getTel());


            InputStream resourceAsStream =  getClass().getResourceAsStream("/report/Customer-report.jrxml");
            JasperDesign load = JRXmlLoader.load(resourceAsStream);
            JasperReport jasperReport= JasperCompileManager.compileReport(load);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    hashMap,
                    new JREmptyDataSource()
            );

            JasperViewer.viewReport(jasperPrint,false);

    }

    public void customerOnAction(ActionEvent actionEvent) {
        String id = txtid.getText();
        System.out.println("seach customer");

//        var model = new CustomerModel();
        try {

            CustomerDto customerDto = customerModel.searchCustomer(id);
//            System.out.println(customerDto);
            if (customerDto != null) {
                txtid.setText(customerDto.getId());
                txtName.setText(customerDto.getName());
                txtAddress.setText(customerDto.getAddress());
                txtContactNo.setText(customerDto.getTel());
            } else {
                new Alert(Alert.AlertType.INFORMATION, "customer not found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void btncustomerreportOnAction(ActionEvent actionEvent) throws JRException, SQLException {
        InputStream resourceAsStream = getClass().getResourceAsStream("/report/Customer.jrxml");
        JasperDesign load = JRXmlLoader.load(resourceAsStream);

        JasperReport jasperReport = JasperCompileManager.compileReport(load);
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                null,
                DbConnection.getInstance().getConnection()
        );
        JasperViewer.viewReport(jasperPrint, false);

    }

    public void itemSerachOnAction() {
        FilteredList<CustomerTm> filteredData = new FilteredList<>(tblCustomer.getItems(), b -> true);
        serchCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String serchKey = newValue.toLowerCase();

                if (item.getTel().toString().contains(serchKey)) {
                    return true;
                } else if (item.getId().toLowerCase().contains(serchKey)) {
                    return true;
                } else return false;
            });
        });

        SortedList<CustomerTm> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(tblCustomer.comparatorProperty());
        tblCustomer.setItems(sortedList);
    }

}