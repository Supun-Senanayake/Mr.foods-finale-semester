package lk.ijse.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.DB.DbConnection;
import lk.ijse.Model.ExpenditureModel;
import lk.ijse.Model.IncomeModel;
import lk.ijse.Model.OrderModel;
import lk.ijse.dto.ExpenditureDto;
import lk.ijse.dto.IncomeDto;
import lk.ijse.dto.tm.ExpenditureTm;
import lk.ijse.dto.tm.IncomeTm;

import java.io.IOException;
import java.sql.*;
import java.text.CollationElementIterator;
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
    public BarChart <?,?> orderDataChart;
    public Label totalOrders;
    @FXML
    private Label Monthlyincomid;
    private OrderModel model = new OrderModel();
    public TableView<ExpenditureTm> tblExpenditure;
    public TableColumn<?, ?> colExDate;
    public TableColumn<?, ?> colExDesc;
    public TableColumn<?, ?> colExAmount;
    public Label lblIncomeTotal;
    public Label lblExpenditureTotal;
    public JFXButton btnBack;
    public Label lblFinalIncome;
    public TableColumn<?, ?> collnDate;
    public TableColumn<?, ?> collnDesc;
    public TableColumn<?, ?> colonAmount;
    private int year;
    private String month;
    @FXML
    private Label lblEmployee;
    @FXML
    private Label lblSupplier;
    @FXML
    private Label lblCustomer;


    private IncomeModel incomeModel = new IncomeModel();

    private ExpenditureModel expenditureModel = new ExpenditureModel();

    private ObservableList<ExpenditureTm> ExobList = FXCollections.observableArrayList();

    private ObservableList<IncomeTm> InobList = FXCollections.observableArrayList();

    public void initialize() throws IOException, SQLException {
        dateDate.setPromptText(String.valueOf(LocalDate.now()));
        setMonth();
        setYear();
        setCellValueFactory();
        orderChart();
        countOrders();
        /*Monthlyincomid.setText(model.getincome());*/


        month = String.valueOf(cmbMonth.getValue());
        year = Integer.parseInt(txtYear.getText());

        YearMonth currentYearMonth = YearMonth.now();
        int year = currentYearMonth.getYear();
        String monthName = currentYearMonth.getMonth().name();
        countEmployee();
        countSupplier();
        countCustomer();

    }

    public void setMonth() {
        ObservableList<Month> months = FXCollections.observableArrayList(Month.values());
        cmbMonth.setItems(months);
        System.out.println("x");

        cmbMonth.setPromptText("Select a month");
    }

    public void setYear() {
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

        var expenditureTm = new ExpenditureTm(date, desc, amount);

        ExobList.add(expenditureTm);

        tblExpenditure.setItems(ExobList);

        calIExpenditure();
    }

    public void btnIncomeOnAction() {
        String date = String.valueOf(dateDate.getValue());
        String desc = txtDesc.getText();
        double amount = Double.parseDouble(txtAmount.getText());

        var incomeTm = new IncomeTm(date, desc, amount);

        InobList.add(incomeTm);

        tblIncome.setItems(InobList);

        calIncome();
    }

    public void calIncome() {
        double total = 0;
        for (int i = 0; i < tblIncome.getItems().size(); i++) {
            total += (double) colonAmount.getCellData(i);
        }
        lblIncomeTotal.setText(String.valueOf(total));

        calNetIncom();
    }

    public void calIExpenditure() {
        double total = 0;
        for (int i = 0; i < tblExpenditure.getItems().size(); i++) {
            total += (double) colExAmount.getCellData(i);
        }
        lblExpenditureTotal.setText(String.valueOf(total));

        calNetIncom();
    }

    public void calNetIncom() {

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
        var incomeDto = new IncomeDto(desc, amount, year, month, date);
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

        System.out.println("Expencive Details: " + expenditureTmList);
        var expenditureDto = new ExpenditureDto(desc, amount, year, month, date);
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

            for (IncomeTm dto : dtoList) {
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

            for (ExpenditureTm dto : dtoList1) {
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

    private void countEmployee() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement stmt = connection.createStatement();
        //Query to get the number of rows in a table
        String query = "select count(*) from employee";
        //Executing the query
        ResultSet rs = stmt.executeQuery(query);
        //Retrieving the result
        rs.next();
        int count = rs.getInt(1);
        lblEmployee.setText(String.valueOf(count));
    }

    private void countSupplier() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement stmt = connection.createStatement();
        //Query to get the number of rows in a table
        String query = "select count(*) from supplier";
        //Executing the query
        ResultSet rs = stmt.executeQuery(query);
        //Retrieving the result
        rs.next();
        int count = rs.getInt(1);
        lblSupplier.setText(String.valueOf(count));
    }

    private void countCustomer() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement stmt = connection.createStatement();
        //Query to get the number of rows in a table
        String query = "select count(*) from customer";
        //Executing the query
        ResultSet rs = stmt.executeQuery(query);
        //Retrieving the result
        rs.next();
        int count = rs.getInt(1);
        lblCustomer.setText(String.valueOf(count));

    }

    private void countOrders() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement stmt = connection.createStatement();
        //Query to get the number of rows in a table
        String query = "select count(*) from orders";
        //Executing the query
        ResultSet rs = stmt.executeQuery(query);
        //Retrieving the result
        rs.next();
        int count = rs.getInt(1);
        totalOrders.setText(String.valueOf(count));
    }

    public void orderChart() throws SQLException {
        orderDataChart.getData().clear();
        String sql = "SELECT date, COUNT(order_id) FROM orders WHERE date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY date ORDER BY TIMESTAMP(date) ASC LIMIT 7";

        Connection connection = DbConnection.getInstance().getConnection();

        try {
            XYChart.Series chart = new XYChart.Series();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                chart.getData().add(new XYChart.Data(resultSet.getString(1), resultSet.getInt(2)));
            }

            orderDataChart.getData().add(chart);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
