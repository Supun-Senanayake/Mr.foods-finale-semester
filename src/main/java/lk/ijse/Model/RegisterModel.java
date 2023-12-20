package lk.ijse.Model;

import lk.ijse.DB.DbConnection;
import lk.ijse.dto.UserDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterModel {
    public static boolean registerAdmin(UserDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();


        String sql = "INSERT INTO user VALUES(?,?,?)";
        PreparedStatement ptm = connection.prepareStatement(sql);

        ptm.setString(1, dto.getUserId());
        ptm.setString(2, dto.getUsername());
        ptm.setString(3, dto.getPassword());

        return ptm.executeUpdate() > 0;
    }
}
