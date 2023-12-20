package lk.ijse.Controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.DB.DbConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ChartController implements Initializable {


    public BarChart chart;
    @FXML
    private JFXButton btnback;

    @FXML
    private AnchorPane root;



    public void btnbackOnActin(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.centerOnScreen();

    }

    public void orderChart() throws SQLException {
        chart.getData().clear();
        String sql = "SELECT order_id,SUM(current_unit_price) FROM order_detail GROUP BY order_id ORDER BY TIMESTAMP(date) ASC LIMT6";

        Connection connection = DbConnection.getInstance().getConnection();

        try {
            XYChart.Series chart = new XYChart.Series();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = ((PreparedStatement) preparedStatement).executeQuery();

            while (resultSet.next()) {
                chart.getData().add(new XYChart.Data(resultSet.getString(1), resultSet.getInt(2)));
            }

            chart.getData().add(chart);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            orderChart();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

