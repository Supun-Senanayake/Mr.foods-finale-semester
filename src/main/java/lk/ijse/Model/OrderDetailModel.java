package lk.ijse.Model;

import lk.ijse.DB.DbConnection;
import lk.ijse.dto.tm.CartTm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailModel {
    public boolean saveOrderDetails(String orderId, List<CartTm> cartTmList) throws SQLException {
        for(CartTm tm : cartTmList) {
            if(!saveOrderDetails(orderId, tm)) {
                return false;
            }
        }
        return true;
    }

    private boolean saveOrderDetails(String orderId, CartTm tm) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO order_detail VALUES(?, ?, ?, ?, ?)";
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setInt(1, tm.getQty());
        pstm.setDouble(2, tm.getUnitPrice());
        pstm.setDouble(3, tm.getTot());
        pstm.setString(4, orderId);
        pstm.setString(5, tm.getCode());

        return pstm.executeUpdate() > 0;
    }
}