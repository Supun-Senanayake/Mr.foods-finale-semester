package lk.ijse.Model;

import lk.ijse.DB.DbConnection;
import lk.ijse.dto.CustomerDto;
import lk.ijse.dto.SupplierDto;
import util.CrudUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierModel {
    public static boolean saveSupplier(SupplierDto dto)throws SQLException, ClassNotFoundException  {
        boolean execute = CrudUtil.execute("INSERT INTO supplier VALUES(?, ?, ?, ?)",
                dto.getId(),dto. getName(), dto.getTel(),dto.getDescription());
        return execute;
    }

    public static String generateNextSupplierId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT supplier_id FROM supplier ORDER BY supplier_id DESC LIMIT 1";
        PreparedStatement pstm = connection.prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()) {
            return splitSupplierId(resultSet.getString(1));
        }
        return splitSupplierId(null);
    }

    private static String splitSupplierId(String currentSupplierId) {
        if(currentSupplierId != null) {
            String[] split = currentSupplierId.split("S0");

            int id = Integer.parseInt(split[1]); //01
            id++;
            return "S00" + id;
        } else {
            return "S001";
        }
    }


    public List<SupplierDto> getAllSupplier() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM supplier";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        ArrayList<SupplierDto> dtoList = new ArrayList<>();

        while(resultSet.next()) {
            dtoList.add(
                    new SupplierDto(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4)


                    )
            );
        }
        return dtoList;

    }

    public boolean updateSupplier(SupplierDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();


        String sql = "UPDATE supplier SET  contact_number= ?,name  = ?, description = ? WHERE supplier_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, dto.getTel());
        pstm.setString(2, dto.getName());
        pstm.setString(3, dto.getDescription());
        pstm.setString(4, dto.getId());



        return pstm.executeUpdate() > 0;
    }

    public boolean deleteSupplier(String id) throws SQLException {

        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM supplier WHERE supplier_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public SupplierDto searchSupplierr(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection ();

        String sql = "SELECT * FROM supplier WHERE supplier_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

       SupplierDto dto = null;

        if(resultSet.next()) {
            String sup_id = resultSet.getString(1);
            String sup_tel = resultSet.getString(2);
            String sup_name = resultSet.getString(3);
            String sup_descrption = resultSet.getString(4);


            dto = new SupplierDto(sup_id,sup_tel,sup_name,sup_descrption);
        }
        return dto;
    }
    }

