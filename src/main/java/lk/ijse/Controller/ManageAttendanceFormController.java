package lk.ijse.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.Model.AttendanceModel;
import lk.ijse.Model.EmployeeModel;
import lk.ijse.dto.AttendanceDto;
import lk.ijse.dto.CustomerDto;
import lk.ijse.dto.EmployeeDto;
import lk.ijse.dto.ItemDto;
import lk.ijse.dto.tm.AttendanceTm;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ManageAttendanceFormController {

    @FXML
    private JFXButton btnback;

    @FXML
    private JFXButton btnmark;

    @FXML
    private JFXButton btnmrkallattendance;

    @FXML
    private JFXComboBox<String> cmbemployeeId;

    @FXML
    private TableColumn<?, ?> colaction;

    @FXML
    private TableColumn<?, ?> colid;

    @FXML
    private TableColumn<?, ?> colname;

    @FXML
    private Label lblemployeeName;

    @FXML
    private AnchorPane root;


    @FXML
    private DatePicker dateDate;

    @FXML
    private TableView<AttendanceTm> tblattendance;

    private ObservableList<AttendanceTm> obList = FXCollections.observableArrayList();


    public void initialize(){
        setDate();
        loadEmployeeIds();
        setCellValueFactory();

    }


    private void setCellValueFactory() {
        colid.setCellValueFactory(new PropertyValueFactory<>("id"));
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));
        colaction.setCellValueFactory(new PropertyValueFactory<>("btn"));
    }

    private void loadEmployeeIds() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<EmployeeDto> idList = EmployeeModel.getAllEmployee();

            for (EmployeeDto dto : idList) {
                obList.add(dto.getId());
            }

            cmbemployeeId.setItems(obList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void btnbackOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.centerOnScreen();

    }

    private void setDate() {
        dateDate.setPromptText(String.valueOf(LocalDate.now()));
    }
    public void btnmarkOnAction(ActionEvent actionEvent) {
        String id = cmbemployeeId.getValue();
        String name = lblemployeeName.getText();
        String date = dateDate.getPromptText();
        Button btn = new Button("Remove");

        setRemoveBtnAction(btn);
        btn.setCursor(Cursor.HAND);


       if (!obList.isEmpty()) {
            for (int i = 0; i < tblattendance.getItems().size(); i++) {
                if (colid.getCellData(i).equals(id)) {

                    tblattendance.refresh();
                    return;
                }
            }
        }
        var attendanceTm = new AttendanceTm(id,name,btn);

        obList.add(attendanceTm);

        tblattendance.setItems(obList);
    }

    private void setRemoveBtnAction(Button btn) {
    }

    public void cmbemployeeIdOnAction(ActionEvent actionEvent) {
        String id = cmbemployeeId.getValue();
        try {
            EmployeeDto employeeDto = EmployeeModel.searchEmployee(id);
            lblemployeeName.setText(employeeDto.getName());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnmrkallattendanceOnAction(ActionEvent actionEvent) {
        String date = dateDate.getPromptText();

        List<AttendanceDto> attendanceDtoList = new ArrayList<>();
        for (AttendanceTm attendanceTm : obList) {
            String id = attendanceTm.getId();
            String name = attendanceTm.getName();
            boolean isPresent = attendanceTm.isPresent();

            AttendanceDto attendanceDto = new AttendanceDto(date,name,id, isPresent);
            attendanceDtoList.add(attendanceDto);
        }

        try {
            boolean isSuccess = AttendanceModel.addAttendanceList(attendanceDtoList);
            if (isSuccess) {
                new Alert(Alert.AlertType.CONFIRMATION, "Attendance Save Success!").show();
                obList.clear();
                tblattendance.setItems(obList);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
