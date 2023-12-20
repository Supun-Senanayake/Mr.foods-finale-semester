package lk.ijse.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.Model.RegisterModel;
import lk.ijse.Model.UserModel;
import lk.ijse.dto.UserDto;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterFormController {


    @FXML
    private JFXButton btnregister;

    @FXML
    private AnchorPane root;

    @FXML
    private JFXTextField txtpassword;

    @FXML
    private JFXTextField txtusername;


    public void btnregisterOnAction(ActionEvent actionEvent) throws IOException, SQLException {

        String userId = generateNextUserId();
        String name = txtusername.getText();
        String password = txtpassword.getText();

        var dto = new UserDto(userId, name, password);

        try {
            boolean isRegister = RegisterModel.registerAdmin(dto);
            if (isRegister) {
                new Alert(Alert.AlertType.CONFIRMATION, "Register Successful").show();
                clearField();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

//        root.getChildren().clear();
//        root.getChildren().add(FXMLLoader.load(getClass().getResource("/view/login_form.fxml")));


    }

    private String generateNextUserId() throws SQLException {
        return UserModel.getNextUserId();
    }

    private void clearField() {
    }

    public void btnbackOnActin(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/login_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.centerOnScreen();
    }
}

