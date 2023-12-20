package lk.ijse.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.Model.CustomerModel;
import lk.ijse.Model.EmployeeModel;
import lk.ijse.dto.CustomerDto;
import lk.ijse.dto.EmployeeDto;
import lk.ijse.dto.ItemDto;
import lk.ijse.dto.tm.CustomerTm;
import lk.ijse.dto.tm.EmployeeTm;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class EmployeeFormController {


    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnclear;

    @FXML
    private JFXButton btndelete;

    @FXML
    private JFXButton btnupdate;

    @FXML
    private TableColumn<?, ?> coladdress;

    @FXML
    private TableColumn<?, ?> colid;

    @FXML
    private TableColumn<?, ?> colname;

    @FXML
    private TableColumn<?, ?> coltel;

    @FXML
    private AnchorPane root;

    @FXML
    private JFXTextField txtaddress;

    @FXML
    private JFXTextField txtname;

    @FXML
    private JFXTextField txtseachId;

    @FXML
    private JFXTextField txttel;

    @FXML
    private TableView<EmployeeTm> tblemployee;
    private EmployeeModel employeeModel = new EmployeeModel();

    private ObservableList<EmployeeTm> obList = FXCollections.observableArrayList();

    public void initialize(){
        generateNextEmployeeId();
        setCellValueFactory();
        loadAllEmployeeDetails();
        tableListener();
    }

    private void tableListener() {
        tblemployee.getSelectionModel().selectedItemProperty().addListener((observable, oldValued, newValue) -> {
//            System.out.println(newValue);
            setData(newValue);
        });
    }

    private void setData(EmployeeTm row) {
        txtseachId.setText(row.getId());
        txtname.setText(row.getName());
        txtaddress.setText(String.valueOf(row.getAddress()));
        txttel.setText(String.valueOf(row.getTel()));
    }
    private void generateNextEmployeeId() {
        try {
            String id = EmployeeModel.generateEmployeeId();
            txtseachId.setText(id);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void setCellValueFactory() {
        colid.setCellValueFactory(new PropertyValueFactory<>("id"));
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));
        coladdress.setCellValueFactory(new PropertyValueFactory<>("address"));
        coltel.setCellValueFactory(new PropertyValueFactory<>("tel"));
    }

    private void loadAllEmployeeDetails() {
        var model = new EmployeeModel();

        try {
            List<EmployeeDto> dtoList = model.getAllEmployee();

            for (EmployeeDto dto : dtoList) {
                obList.add(
                        new EmployeeTm(
                                dto.getId(),
                                dto.getName(),
                                dto.getAddress(),
                                dto.getTel()
                        )
                );
            }
            tblemployee.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void txtseachIdOnAction(ActionEvent actionEvent) {
        String code = txtseachId.getText();

        try {
            EmployeeDto dto = employeeModel.searchEmployee(code);
            if (dto != null) {
                setFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "item not found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void setFields(EmployeeDto dto) {
        txtseachId.setText(dto.getId());
        txtname.setText(dto.getName());
        txtaddress.setText(String.valueOf(dto.getAddress()));
        txttel.setText(String.valueOf(dto.getTel()));
    }




    public void btnSaveOnAction(ActionEvent actionEvent) {
        String id = txtseachId.getText();
        String name = txtname.getText();
        String address = txtaddress.getText();
        String tel = txttel.getText();

        try {
            if (!validateEmployee()){
                return;
            }
            var dto = new EmployeeDto(id,name,address,tel);
            boolean isSaved = EmployeeModel.saveEmployee(dto);

            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "Employee Saved!").show();
                initialize();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        //} else {
        new Alert(Alert.AlertType.INFORMATION, "ID no not validate");

    }

    private boolean validateEmployee() {
        boolean isValidate = true;
        boolean name = Pattern.matches("[A-Za-z]{5,}", txtname.getText());
        if (!name){
            showErrorNotification("Invalid Employee Name", "The Employee name you entered is invalid");
            isValidate = false;
        }
        boolean address = Pattern.matches("[A-Za-z]{4,}",txtaddress.getText());
        if(!address){
            showErrorNotification("invalid address","The address you entered is invalid");
            isValidate = false;
        }
        boolean NIC = Pattern.matches("^([0-9]{9}|[0-9]{10})$",txttel.getText());
        if (!NIC){
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


    public void btnupdateOnAction(ActionEvent actionEvent) {
        String id = txtseachId.getText();
        String name = txtname.getText();
        String address = txtaddress.getText();
        String tel = txttel.getText();

        var dto1 = new EmployeeDto(id, name, address, tel);
        try {
            if (!validateEmployee()){
                return;
            }
            var dto = new EmployeeDto(id,name,address,tel);
            boolean isSaved = EmployeeModel.updateEmployee(dto);

            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "Employee Update!").show();
                initialize();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        //} else {
        new Alert(Alert.AlertType.INFORMATION, "ID no not validate");

    }


    public void btndeleteOnAction(ActionEvent actionEvent) {
        String id = txtseachId.getText();

        try {
            boolean isDeleted = employeeModel.deleteEmployee(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "employee deleted!").show();
                initialize();
            } else {
                new Alert(Alert.AlertType.CONFIRMATION, "employee not deleted!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }


    public void btnclearOnAction(ActionEvent actionEvent) {
        clearFields();
    }

    private void clearFields() {
        txtseachId.setText("");
        txtname.setText("");
        txtaddress.setText("");
        txttel.setText("");

    }

    public void btnattendanceOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/manageattendance_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.centerOnScreen();
    }

}
