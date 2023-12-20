package lk.ijse.Model;

import lk.ijse.DB.DbConnection;
import lk.ijse.dto.CustomerDto;
import util.CrudUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerModel {

    public static String generateNextCustomerId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT customer_id FROM customer ORDER BY customer_id DESC LIMIT 1";
        PreparedStatement pstm = connection.prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()) {
            return splitCustomerId(resultSet.getString(1));
        }
        return splitCustomerId(null);
    }

    private static String splitCustomerId(String currentCustomrerId) {
        if(currentCustomrerId != null) {
            String[] split = currentCustomrerId.split("C0");

            int id = Integer.parseInt(split[1]); //01
            id++;
            return "C00" + id;
        } else {
            return "C001";
        }
    }

    public boolean saveCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException {

        boolean execute = CrudUtil.execute("INSERT INTO customer(customer_id,name,address,contact_Number) VALUES(?, ?, ?,?)",
                dto.getId(), dto.getName(), dto.getAddress(), dto.getTel());
        return execute;

    }

    public boolean updateCustomer(CustomerDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();


        String sql = "UPDATE customer SET  name = ?, address = ?,contact_number = ? WHERE customer_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, dto.getName());
        pstm.setString(2, dto.getAddress());
        pstm.setString(3, dto.getTel());
        pstm.setString(4, dto.getId());



        return pstm.executeUpdate() > 0;
    }

    public boolean deleteCustomer(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM customer WHERE customer_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public List<CustomerDto> getAllCustomer() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM customer ";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        ArrayList<CustomerDto> dtoList = new ArrayList<>();

        while(resultSet.next()) {
            dtoList.add(
                    new CustomerDto(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4)


                    )
            );
        }
        return dtoList;
    }

    public CustomerDto searchCustomer(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection ();

        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        CustomerDto dto = null;


        if(resultSet.next()) {

            dto=new CustomerDto(
                    resultSet.getString(1),
                resultSet.getString(2),
             resultSet.getString(3),
            resultSet.getString(4)

            );
//            String cus_id = resultSet.getString(1);
//            String cus_name = resultSet.getString(2);
//            String cus_address = resultSet.getString(3);
//            String cus_tel = resultSet.getString(4);


           // dto = new CustomerDto(cus_id,cus_name,cus_address,cus_tel);
        }
        return dto;
    }
}

