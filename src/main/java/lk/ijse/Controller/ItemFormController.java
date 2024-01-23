package lk.ijse.Controller;

import com.google.zxing.WriterException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.Model.ItemModel;
import lk.ijse.dto.ItemDto;
import lk.ijse.dto.tm.ItemTm;
import org.controlsfx.control.Notifications;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class ItemFormController {

    @FXML
    private JFXButton btnclear;

    @FXML
    private JFXButton btndelete;

    @FXML
    private JFXButton btnsave;

    @FXML
    private JFXButton btnupdate;


    @FXML
    private TableColumn<?, ?> colaction;

    @FXML
    private TableColumn<?, ?> colcode;

    @FXML
    private TableColumn<?, ?> coldescription;

    @FXML
    private TableColumn<?, ?> colprice;

    @FXML
    private TableColumn<?, ?> colqty;

    @FXML
    private AnchorPane root;

    @FXML
    private JFXTextField txtcode;

    @FXML
    private JFXTextField txtdescription;

    @FXML
    private JFXTextField txtprice;

    @FXML
    private JFXTextField txtqty;
    @FXML
    private TableView<ItemTm> tblItem;


    private ItemModel itemModel = new ItemModel();

    private ObservableList<ItemTm> obList = FXCollections.observableArrayList();

    public void initialize() {
        generateNextItemCode();
        setCellValueFactory();
        loadAllItems();
        tableListener();
    }

    private void generateNextItemCode() {
        try {
            String id = itemModel.generateNextItemCode();
            txtcode.setText(id);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void tableListener() {
        tblItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValued, newValue) -> {
//            System.out.println(newValue);
           setData(newValue);
        });
    }

    private void setData(ItemTm row) {
        txtcode.setText(row.getCode());
        txtdescription.setText(row.getDescription());
        txtprice.setText(String.valueOf(row.getUnitPrice()));
        txtqty.setText(String.valueOf(row.getQtyOnHand()));
    }

    private void setCellValueFactory() {
        colcode.setCellValueFactory(new PropertyValueFactory<>("code"));
        coldescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colprice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colqty.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colaction.setCellValueFactory(new PropertyValueFactory<>("btn"));
    }



   

    private void loadAllItems() {
        var model = new ItemModel();

        ObservableList <ItemTm> obList = FXCollections.observableArrayList();

        try {
            List<ItemDto> dtoList = model.loadAllItems();

            for (ItemDto dto : dtoList) {
                Button btndelete = new Button("Delete");

                setDeleteButtonOnAction(btndelete, dto.getCode());
                obList.add(new ItemTm(
                        dto.getCode(),
                        dto.getDescription(),
                        dto.getUnitPrice(),
                        dto.getQtyOnHand(),
                        btndelete
                ));
            }
            tblItem.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setDeleteButtonOnAction(Button btndelete , String code) {
        btndelete.setOnAction((e) -> {
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove item ?", yes, no).showAndWait();

            if (type.orElse(no) == yes) {
                obList.removeIf(employeeTm -> employeeTm.getCode().equals(code));
                tblItem.refresh();

                DeleteEmployee(code);
            }
        });
    }

    private void DeleteEmployee(String employeeId){
        try {
            boolean isDeleted = ItemModel.deleteItem(employeeId);
            if(isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "Item deleted!").show();
                clearFields();
                loadAllItems();
            } else {
                new Alert(Alert.AlertType.CONFIRMATION, "Item not deleted!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void clearFields() {
        txtcode.setText("");
        txtdescription.setText("");
        txtprice.setText("");
        txtqty.setText("");
    }

    private void setFields(ItemDto dto) {
        txtcode.setText(dto.getCode());
        txtdescription.setText(dto.getDescription());
        txtprice.setText(String.valueOf(dto.getUnitPrice()));
        txtqty.setText(String.valueOf(dto.getQtyOnHand()));
    }

    public void btnsaveOnAction(ActionEvent actionEvent) throws ClassNotFoundException {
        String code = txtcode.getText();
        String description = txtdescription.getText();
        double unitPrice = Double.parseDouble(txtprice.getText());
        int qtyOnHand = Integer.parseInt(txtqty.getText());



//        var model = new ItemModel();
        try {
            if (!validateItem()){
                return;
            }

            var dto = new ItemDto(code, description, unitPrice, qtyOnHand+"");
            boolean isSaved = itemModel.saveItem(dto);

            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "Item saved!").show();
                clearFields();
                loadAllItems();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        new Alert(Alert.AlertType.INFORMATION, "ID no not validate");

    }

    private boolean validateItem() {
        boolean isValidate = true;
        boolean desc = Pattern.matches("[A-Za-z]{5,}", txtdescription.getText());
        if (!desc){
            showErrorNotification("Invalid Description", "The description you entered is invalid");
            isValidate = false;
        }
        boolean qty = Pattern.matches("[0-9]{0,}",txtqty.getText());
        if(!qty){
            showErrorNotification("invalid Qty On Hand","The qty on hand  you entered is invalid");
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
        String code = txtcode.getText();
        String description = txtdescription.getText();
        double unitPrice = Double.parseDouble(txtprice.getText());
        int qtyOnHand = Integer.parseInt(txtqty.getText());

//        var model = new ItemModel();
        try {
            boolean isUpdated = itemModel.updateItem(new ItemDto(code, description, unitPrice,qtyOnHand+""));
            if (isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "item updated").show();
                initialize();
                clearFields();
                loadAllItems();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void btndeleteOnAction(ActionEvent actionEvent) {
        String id = txtcode.getText();

        try {
            boolean isDeleted = itemModel.deleteItem(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "Item deleted!").show();
                clearFields();
                loadAllItems();
            } else {
                new Alert(Alert.AlertType.CONFIRMATION, "Item not deleted!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }


    public void btnclearOnAction(ActionEvent actionEvent) {
        clearFields();
        generateNextItemCode();
    }

    public void itemseachONAction(ActionEvent actionEvent) {
        String code = txtcode.getText();

        try {
            ItemDto dto = itemModel.searchItem(code);
            if (dto != null) {
                setFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "item not found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void getQr(ActionEvent actionEvent) throws WriterException {

        String values =txtcode.getText() + "," + txtdescription.getText() + "," + txtprice + "," + txtqty.getText() ;//QR code ekata watenna oone details tika..

        String filepath = "C:\\Users\\S\\Desktop\\Qr"+ txtcode.getText() +".png"; //Save wenna oone folder eke path eka..
        boolean isGenerated = QR.generateQrCode(values, 1250, 1250, filepath);

        if (isGenerated){
            new Alert(Alert.AlertType.CONFIRMATION, "Generated QR Code ITEM DETAILS").show();
        } else {
            new Alert(Alert.AlertType.WARNING, "Try Again").show();;
        }

    }
}

