package lk.ijse.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.DB.DbConnection;
import lk.ijse.Model.ItemModel;
import lk.ijse.Model.MaterialModel;
import lk.ijse.dto.CustomerDto;
import lk.ijse.dto.MaterialDto;
import lk.ijse.dto.SupplierDto;
import lk.ijse.dto.tm.MaterialTm;
import org.controlsfx.control.Notifications;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


public class RawMaterialFormContrller {


    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private TableColumn<?, ?> colaction;

    @FXML
    private TableColumn<?, ?> colcode;

    @FXML
    private TableColumn<?, ?> coldescription;

    @FXML
    private TableColumn<?, ?> colqty;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<MaterialTm> tblMaterials;

    @FXML
    private JFXTextField txtdescription;

    @FXML
    private JFXTextField txtqty;

    @FXML
    private JFXTextField txtrawcode;

    private MaterialModel materialModel = new MaterialModel();

    private ObservableList<MaterialTm> obList = FXCollections.observableArrayList();

    public void initialize() {
        generateNextRawMaterialCode();
        setCellValueFactory();
        loadAllRawMaterial();
        tableListener();
    }

    private void generateNextRawMaterialCode() {
        try {
            String rawCode = MaterialModel.generateNextRawMaterialCode();
            txtrawcode.setText(rawCode);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();

            //throw new RuntimeException(e);
        }
    }

    private void setData(MaterialTm row) {
        txtrawcode.setText(row.getCode());
        txtdescription.setText(row.getDescription());
        txtqty.setText(String.valueOf(row.getQtyOnHand()));
    }

    private void setCellValueFactory() {
        colcode.setCellValueFactory(new PropertyValueFactory<>("code"));
        coldescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colqty.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colaction.setCellValueFactory(new PropertyValueFactory<>("btn"));
    }

    private void tableListener() {
        tblMaterials.getSelectionModel().selectedItemProperty().addListener((observable, oldValued, newValue) -> {
//            System.out.println(newValue);
            setData(newValue);
        });
    }

    private boolean validateRawMaterial() {
        boolean isValidate = true;
        boolean desc = Pattern.matches("[A-Za-z]{5,}", txtdescription.getText());
        if (!desc) {
            showErrorNotification("Invalid description", "The description you entered is invalid");
            isValidate = false;
        }
        boolean qty = Pattern.matches("[0-9]{0,}", txtqty.getText());
        if (!qty) {
            showErrorNotification("invalid qty on hand", "The qty on hand you entered is invalid");
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


    private void loadAllRawMaterial() {

        try {
            List<MaterialDto> dtoList = materialModel.loadAllMaterials();

            for (MaterialDto dto : dtoList) {
                Button btndelete = new Button("Delete");

                setDeleteButtonOnAction(btndelete, dto.getCode());
                obList.add(new MaterialTm(
                        dto.getCode(),
                        dto.getDescription(),
                        dto.getQtyOnHand(),
                        btndelete
                ));
            }
            tblMaterials.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    private void setDeleteButtonOnAction(Button btndelete, String code) {
        btndelete.setOnAction((e) -> {
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove material ?", yes, no).showAndWait();

            if (type.orElse(no) == yes) {
                obList.removeIf(materialTm -> materialTm.getCode().equals(code));
                tblMaterials.refresh();

                DeleteMaterial(code);
            }
        });
    }

    private void DeleteMaterial(String rm_code) {
        try {
            boolean isDeleted = MaterialModel.deleteMaterial(rm_code);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "Raw Material deleted!").show();
            } else {
                new Alert(Alert.AlertType.CONFIRMATION, "Raw Material not deleted!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        tblMaterials.refresh();

    }


    public void btnUpdateOnAction(ActionEvent actionEvent) throws SQLException {
        String code = txtrawcode.getText();
        String description = txtdescription.getText();
        double qtyOnHand = Double.parseDouble(txtqty.getText());

//        var model = new ItemModel();
        boolean isUpdated = materialModel.updateMaterial(new MaterialDto(code, description, qtyOnHand));
        if (isUpdated) {
            new Alert(Alert.AlertType.CONFIRMATION, "Raw Material is updated").show();
            initialize();
        }
    }

    public void btnClearOnAction(ActionEvent actionEvent) {
        clearFields();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) throws SQLException {

        String code = txtrawcode.getText();
        String description = txtdescription.getText();
        int qtyOnHand = Integer.parseInt(txtqty.getText());

        var dto = new MaterialDto(code, description, qtyOnHand);

//        var model = new ItemModel();
        try {
            if (!validateRawMaterial()) {
                return;
            }
            boolean isSaved = MaterialModel.saveMaterial(dto);

            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "Material saved!").show();
                initialize();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        new Alert(Alert.AlertType.INFORMATION, "ID no not validate");

    }

    private void clearFields() {

        txtrawcode.setText("");
        txtdescription.setText("");
        txtqty.setText("");
    }

    public MaterialDto seachrawmaterialOnAction(ActionEvent actionEvent) throws SQLException {
        String id = txtrawcode.getText();

//        var model = new CustomerModel();
        try {
            MaterialDto materialDto =materialModel.searchRawMaterial(id);
//            System.out.println(customerDto);
            if (materialDto != null) {
               txtqty.setText(materialDto.getCode());
                txtdescription.setText(materialDto.getDescription());
              txtqty.setText(String.valueOf(materialDto.getQtyOnHand()));

            } else {
                new Alert(Alert.AlertType.INFORMATION, "Supplier not found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        return new MaterialDto();
    }





        public void btndeleteOnAction (ActionEvent actionEvent){
            String id = txtrawcode.getText();

            try {
                boolean isDeleted = materialModel.deleteMaterial(id);
                if (isDeleted) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Raw Material deleted!").show();
                    initialize();
                } else {
                    new Alert(Alert.AlertType.CONFIRMATION, "Raw Material not deleted!").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }
