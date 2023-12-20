package lk.ijse.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.Model.ExpenditureModel;
import lk.ijse.Model.IncomeModel;
import lk.ijse.dto.ExpenditureDto;
import lk.ijse.dto.IncomeDto;
import lk.ijse.dto.tm.ExpenditureTm;
import lk.ijse.dto.tm.IncomeTm;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class HomePageController {

    public AnchorPane root;
    public JFXComboBox<Month> cmbMonth;
    public JFXTextField txtYear;
    public DatePicker dateDate;
    public JFXTextField txtDesc;
    public JFXTextField txtAmount;
    public JFXButton btnExpenditure;
    public JFXButton btnIncome;
    public TableView<IncomeTm> tblIncome;

    public TableView<ExpenditureTm> tblExpenditure;
    public TableColumn<?,?> colExDate;
    public TableColumn<?,?> colExDesc;
    public TableColumn<?,?> colExAmount;
    public Label lblIncomeTotal;
    public Label lblExpenditureTotal;
    public JFXButton btnBack;
    public Label lblFinalIncome;
    public TableColumn<?,?> collnDate;
    public TableColumn<?,?> collnDesc;
    public TableColumn<?,?> colonAmount;
    private int year;
    private String month;




    private IncomeModel incomeModel = new IncomeModel();

    private ExpenditureModel expenditureModel = new ExpenditureModel();

    private ObservableList<ExpenditureTm> ExobList = FXCollections.observableArrayList();

    private ObservableList<IncomeTm> InobList = FXCollections.observableArrayList();

    public void initialize() throws IOException {
        dateDate.setPromptText(String.valueOf(LocalDate.now()));
        setMonth();
        setYear();
        setCellValueFactory();

        month = String.valueOf(cmbMonth.getValue());
        year = Integer.parseInt(txtYear.getText());

        YearMonth currentYearMonth = YearMonth.now();
        int year = currentYearMonth.getYear();
        String monthName = currentYearMonth.getMonth().name();
    }

    public void setMonth() {
        ObservableList<Month> months = FXCollections.observableArrayList(Month.values());
        cmbMonth.setItems(months);
        System.out.println("x");

        cmbMonth.setPromptText("Select a month");
    }

    public void setYear(){
        int currentYear = Year.now().getValue();
        txtYear.setText(String.valueOf(currentYear));
    }

    private void setCellValueFactory() {
        collnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        collnDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        colonAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        colExDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colExDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        colExAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }



    public void btnExpenditureOnAction(ActionEvent actionEvent) {
        String desc = txtDesc.getText();
        double amount = Double.parseDouble(txtAmount.getText());

        String date = String.valueOf(dateDate.getValue());

        var expenditureTm = new ExpenditureTm(date,desc,amount);

        ExobList.add(expenditureTm);

        tblExpenditure.setItems(ExobList);

        calIExpenditure();
    }

    public void btnIncomeOnAction() {
        String date = String.valueOf(dateDate.getValue());
        String desc = txtDesc.getText();
        double amount = Double.parseDouble(txtAmount.getText());

        var incomeTm = new IncomeTm(date,desc,amount);

        InobList.add(incomeTm);

        tblIncome.setItems(InobList);

        calIncome();
    }

    public void calIncome(){
        double total = 0;
        for (int i = 0; i < tblIncome.getItems().size(); i++) {
            total += (double) colonAmount.getCellData(i);
        }
        lblIncomeTotal.setText(String.valueOf(total));

        calNetIncom();
    }

    public void calIExpenditure(){
        double total = 0;
        for (int i = 0; i < tblExpenditure.getItems().size(); i++) {
            total += (double) colExAmount.getCellData(i);
        }
        lblExpenditureTotal.setText(String.valueOf(total));

        calNetIncom();
    }

    public void calNetIncom(){

        try {
            double incomeTotal = Double.parseDouble(lblIncomeTotal.getText());
            double expenditureTotal = Double.parseDouble(lblExpenditureTotal.getText());
            double netIncome = incomeTotal - expenditureTotal;
            lblFinalIncome.setText(String.valueOf(netIncome));
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid format in lblIncomeTotal or lblExpenditureTotal");
            e.printStackTrace();
        }
    }


    public void btnSaveInOnAction(ActionEvent actionEvent) {
        String desc = txtDesc.getText();
        double amount = Double.parseDouble(txtAmount.getText());
        int year = Integer.parseInt(txtYear.getText());
        String month = String.valueOf(cmbMonth.getValue());
        String date = dateDate.getPromptText();

        List<IncomeTm> incomeTmList = new ArrayList<>();
        for (int i = 0; i < tblIncome.getItems().size(); i++) {
            IncomeTm incomeTm = InobList.get(i);

            incomeTmList.add(incomeTm);
        }

        System.out.println("Income Details: " + incomeTmList);
        var incomeDto = new IncomeDto(desc,amount,year,month,date);
        try {
            boolean isSuccess = IncomeModel.addIncome(incomeDto);
            if (isSuccess) {
                new Alert(Alert.AlertType.CONFIRMATION, "Incomes Save Success!").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnSaveExOnAction(ActionEvent actionEvent) {
        String desc = txtDesc.getText();
        double amount = Double.parseDouble(txtAmount.getText());
        int year = Integer.parseInt(txtYear.getText());
        String month = String.valueOf(cmbMonth.getValue());
        String date = dateDate.getPromptText();

        List<ExpenditureTm> expenditureTmList = new ArrayList<>();
        for (int i = 0; i < tblExpenditure.getItems().size(); i++) {
            ExpenditureTm expenditureTm = ExobList.get(i);

            expenditureTmList.add(expenditureTm);
        }

        System.out.println("Expencive Details: " +expenditureTmList);
        var expenditureDto = new ExpenditureDto(desc,amount,year,month,date);
        try {
            boolean isSuccess = ExpenditureModel.addExpenditure(expenditureDto);
            if (isSuccess) {
                new Alert(Alert.AlertType.CONFIRMATION, "Expenditure Payment Save Success!").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cmbMonthOnAction(ActionEvent actionEvent) {
        try {
            Month selectedMonth = cmbMonth.getValue();
            if (selectedMonth != null) {
                month = selectedMonth.name();
            }
            List<IncomeTm> dtoList = IncomeModel.searchIncome(year, month);

            for ( IncomeTm dto : dtoList) {
                InobList.add(
                        new IncomeTm(
                                dto.getDate(),
                                dto.getDesc(),
                                dto.getAmount()
                        )
                );
            }

            tblIncome.setItems(InobList);

            List<ExpenditureTm> dtoList1 = ExpenditureModel.searchExpenditure(year, month);

            for ( ExpenditureTm dto : dtoList1) {
                ExobList.add(
                        new ExpenditureTm(
                                dto.getDate(),
                                dto.getDesc(),
                                dto.getAmount()
                        )
                );
            }

            tblExpenditure.setItems(ExobList);

        } catch (SQLException e) {
            throw new RuntimeException();
        }
        calIncome();
        calIExpenditure();
        calNetIncom();
    }

    public void btnchartOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/chart_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.centerOnScreen();
    }
}
