package lk.ijse.Model;

import lk.ijse.DB.DbConnection;
import lk.ijse.dto.UserDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel {

    public boolean searchUser(UserDto userDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement("select * from user where user_name=?");
        pstm.setString(1, userDto.getUsername());
        ResultSet resultSet = pstm.executeQuery();
        String userName1 = null;
        String password1 = null;
        if (resultSet.next()) {
            userName1 = resultSet.getString(1);
            password1 = resultSet.getString(2);
        }else {
            return false;
        }
        if (userName1.equals(userDto.getUsername()) && password1.equals(userDto.getPassword())) {
            return true;
        }

        return false;

    }
}
