package lk.ijse.Model;

import javafx.scene.control.Alert;
import lk.ijse.DB.DbConnection;
import lk.ijse.dto.CustomerDto;
import lk.ijse.dto.MaterialDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class MaterialModel {

    public static String generateNextRawMaterialCode() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT rm_code FROM raw_material ORDER BY rm_code DESC LIMIT 1";
        PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet resultSet = pstm.executeQuery();

            if (resultSet.next()) {
                return splitRMCode(resultSet.getString(1));
            }
            return splitRMCode(null);

    }

    public static String splitRMCode(String currentRmCode) {
        if (currentRmCode != null) {
            String[] split = currentRmCode.split("R");

            int id = Integer.parseInt(split[1]);
            id++;
            return "R00" + id;
        } else {
            return "R001";
        }
    }

    public static boolean deleteMaterial(String rmCode) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM raw_material WHERE rm_code = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, rmCode);

        return pstm.executeUpdate() > 0;
    }

    public static boolean saveMaterial(MaterialDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        String sql = "INSERT INTO raw_material VALUES(?, ?, ?)";
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, dto.getCode());
        pstm.setString(2, dto.getDescription());
        pstm.setDouble(3, dto.getQtyOnHand());


        return pstm.executeUpdate() > 0;
    }

    public List<MaterialDto> loadAllMaterials() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM raw_material";
        PreparedStatement pstm = connection.prepareStatement(sql);

        List<MaterialDto> materialList = new ArrayList<>();

        ResultSet resultSet = pstm.executeQuery();
        while (resultSet.next()) {
            materialList.add(new MaterialDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getDouble(3)
            ));
        }

        return materialList;
    }


    public boolean updateMaterial(MaterialDto materialDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE raw_material SET description = ?,  qty_on_hand = ? WHERE rm_code = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(3, materialDto.getCode());
        pstm.setString(1, materialDto.getDescription());
        pstm.setInt(2, (int) materialDto.getQtyOnHand());

        return pstm.executeUpdate() > 0;
    }


    public MaterialDto searchRawMaterial(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection ();

        String sql = "SELECT * FROM raw_material WHERE rm_code = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        MaterialDto dto = null;


        if(resultSet.next()) {

            dto=new MaterialDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getDouble(3)

            );

        }
        return dto;
    }
}




