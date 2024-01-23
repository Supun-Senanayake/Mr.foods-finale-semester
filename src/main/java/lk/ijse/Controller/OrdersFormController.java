package lk.ijse.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
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
import lk.ijse.DB.DbConnection;
import lk.ijse.Model.CustomerModel;
import lk.ijse.Model.ItemModel;
import lk.ijse.Model.OrderModel;
import lk.ijse.Model.PlaceOrderModel;
import lk.ijse.dto.CustomerDto;
import lk.ijse.dto.ItemDto;
import lk.ijse.dto.PlaceOrderDto;
import lk.ijse.dto.billDto;
import lk.ijse.dto.tm.CartTm;
import lk.ijse.dto.tm.CustomerTm;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;


import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class OrdersFormController {

    public JFXButton btnAddToCart;
    public Label lblDescriptin;
    public Label lblcustomerName;
    public Label lblDate;
    public Label lblqtynHand;
    public Label lblqty;
    public Label lblnettotal;
    public JFXButton btnnercustomer;
    public JFXComboBox cmbcusnumid;
    public TextField txtContactNo;
    public TextField txtContactNumber;
    @FXML
    private JFXComboBox<String> cmbCustomerId;

    @FXML
    private JFXComboBox<String> cmbItemCode;

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colaction;

    @FXML
    private TableColumn<?, ?> colcode;

    @FXML
    private TableColumn<?, ?> colqty;

    @FXML
    private TableColumn<?, ?> coltotal;

    @FXML
    private TableColumn<?, ?> colunitPrice;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<CartTm> tblOrder;

    @FXML
    private JFXTextField txtQty;


    @FXML
    private Label lblorderId;


    @FXML
    private Label lblunitPrice;


    private CustomerModel customerModel = new CustomerModel();
    private ItemModel itemModel = new ItemModel();
    private OrderModel orderModel = new OrderModel();
    private PlaceOrderModel placeOrderModel = new PlaceOrderModel();

    private ObservableList<CartTm> obList = FXCollections.observableArrayList();

    public void initialize() {
        setCellValueFactory();
        generateNextOrderId();
        setDate();
        loadCustomerIds();
        loadItemCodes();
    }

    private void setCellValueFactory() {
        colcode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colqty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colunitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        coltotal.setCellValueFactory(new PropertyValueFactory<>("tot"));
        colaction.setCellValueFactory(new PropertyValueFactory<>("btn"));
    }

    private void generateNextOrderId() {
        try {
            String orderId = orderModel.generateNextOrderId();
            lblorderId.setText(orderId);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }


    private void loadItemCodes() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<ItemDto> itemDto = itemModel.loadAllItems();

            for (ItemDto dto : itemDto) {
                obList.add(dto.getCode());

            }
            cmbItemCode.setItems(obList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerIds() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<CustomerDto> idList = customerModel.getAllCustomer();

            for (CustomerDto dto : idList) {
                obList.add(dto.getId());
            }

            cmbCustomerId.setItems(obList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setDate() {
        lblDate.setText(String.valueOf(LocalDate.now()));
    }


    public void cmbItemCodeOnAction(ActionEvent actionEvent) {
        String code = cmbItemCode.getValue();
        try {
            ItemDto itemDto = itemModel.searchItem(code);
            lblqtynHand.setText(itemDto.getQtyOnHand());
            lblDescriptin.setText(itemDto.getDescription());
            lblunitPrice.setText(String.valueOf(itemDto.getUnitPrice()));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnAddToCartOnAction(ActionEvent actionEvent) {
        String code = cmbItemCode.getValue();
        String description = lblDescriptin.getText();
        int qty = Integer.parseInt(txtQty.getText());
        double unitPrice = Double.parseDouble(lblunitPrice.getText());
        double tot = unitPrice * qty;
        Button btn = new Button("Remove");

        setRemoveBtnAction(btn);
        btn.setCursor(Cursor.HAND);


        if (!obList.isEmpty()) {
            for (int i = 0; i < tblOrder.getItems().size(); i++) {
                if (colcode.getCellData(i).equals(code)) {
                    int col_qty = (int) colqty.getCellData(i);
                    qty += col_qty;
                    tot = unitPrice * qty;

                    obList.get(i).setQty(qty);
                    obList.get(i).setTot(tot);

                    calculateTotal();
                    tblOrder.refresh();
                    return;
                }
            }
        }
        var cartTm = new CartTm(code, description, qty, unitPrice, tot, btn);

        obList.add(cartTm);

        tblOrder.setItems(obList);
        calculateTotal();
        txtQty.clear();
    }

    private void setRemoveBtnAction(Button btn) {
        btn.setOnAction((e) -> {
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();
            initialize();
            if (type.orElse(no) == yes) {
                int focusedIndex = tblOrder.getSelectionModel().getSelectedIndex();

                obList.remove(focusedIndex);
                tblOrder.refresh();
                calculateTotal();
            }
        });
    }

    private String calculateTotal() {
        double total = 0;
        for (int i = 0; i < tblOrder.getItems().size(); i++) {
            total += (double) coltotal.getCellData(i);
        }
        lblnettotal.setText(String.valueOf(total));
        return null;
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) throws JRException {
        String orderId = lblorderId.getText();
        LocalDate date = LocalDate.parse(lblDate.getText());
        String customerId = cmbCustomerId.getValue();
        String totalprice = lblnettotal.getText();

        List<CartTm> cartTmList = new ArrayList<>();
        for (int i = 0; i < tblOrder.getItems().size(); i++) {
            CartTm cartTm = obList.get(i);

            cartTmList.add(cartTm);
        }

        System.out.println("Place order form controller: " + cartTmList);
        var placeOrderDto = new PlaceOrderDto(orderId, date, customerId, totalprice, cartTmList);
        try {
            boolean isSuccess = placeOrderModel.placeOrder(placeOrderDto);
            if (isSuccess) {
                new Alert(Alert.AlertType.CONFIRMATION, "Order Success!").show();
                printBill();
            }else{
                new Alert(Alert.AlertType.ERROR,"Order failed").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ArrayList<billDto> bill =new ArrayList<>();

        for (CartTm data: obList) {
            bill.add(new billDto(
                    data.getDescription(),
                    data.getQty(),
                    data.getUnitPrice(),
                    data.getTot()
            ));

        }

    }

    private void printBill() throws JRException, SQLException {
        InputStream resourceAsStream = getClass().getResourceAsStream("/report/Invoice.jrxml");
        JasperDesign load = JRXmlLoader.load(resourceAsStream);

        JasperReport jasperReport = JasperCompileManager.compileReport(load);
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                null,
                DbConnection.getInstance().getConnection()
        );
        JasperViewer.viewReport(jasperPrint, false);
    }



    public void txtQtyOnAction(ActionEvent actionEvent) {
        btnAddToCartOnAction(actionEvent);
    }

    public void btnnercustomerOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/customerpage_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.centerOnScreen();

    }

    public void btnbackOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.centerOnScreen();
    }

    public void cmbcusnumAction(ActionEvent actionEvent) {
    }

    public void cmbCustomeridOnAction(ActionEvent actionEvent) {
    }

    public void cmbCustomeridAction(ActionEvent actionEvent) {
        String id = cmbCustomerId.getValue();

        try {
            CustomerDto dto = CustomerModel.searchCustomer(id);
            lblcustomerName.setText(dto.getName());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void btnSearchOnAction(ActionEvent actionEvent) {
            String txtContactNoText = txtContactNo.getText();
            try {
                CustomerDto dto = CustomerModel.searchCustomer(txtContactNoText);
                lblcustomerName.setText(dto.getName());

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    private void fillData(CustomerDto customer) {
        txtContactNo.setText(customer.getTel());
    }
}



   /* public void txtQtyOnAction(ActionEvent actionEvent) {
    }

    public void btnbackOnAction(ActionEvent actionEvent) {
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
    }

    public void btnnercustomerOnAction(ActionEvent actionEvent) {
    }

    public void cmbCustomeridOnAction(ActionEvent actionEvent) {
    }

    public void btnAddToCartOnAction(ActionEvent actionEvent) {
    }

    public void cmbcusnumAction(ActionEvent actionEvent) {
    }*/
