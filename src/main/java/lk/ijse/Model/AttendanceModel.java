package lk.ijse.Model;

import lk.ijse.DB.DbConnection;
import lk.ijse.dto.AttendanceDto;
import lk.ijse.dto.ItemDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AttendanceModel {
    public static boolean addAttendance(AttendanceDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();


        String sql = "INSERT INTO attendance  (date,name,employee_id) VALUES(?,?,?)";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, dto.getDate());
        pstm.setString(2, dto.getId());
        pstm.setString(3, dto.getName());


        return pstm.executeUpdate() > 0;
    }


   public static boolean addAttendanceList(List<AttendanceDto> attendanceDtoList) throws SQLException {
       Connection connection = DbConnection.getInstance().getConnection();

       String sql = "INSERT INTO attendance (date,name,employee_id) VALUES (?,?,?)";

       try (PreparedStatement pstm = connection.prepareStatement(sql)) {
           connection.setAutoCommit(false);

           for (AttendanceDto dto : attendanceDtoList) {
               pstm.setString(1,dto.getDate());
               pstm.setString(2,dto.getName());
               pstm.setString(3,dto.getId());

               pstm.addBatch();
           }

           int[] result = pstm.executeBatch();
           connection.commit();

           for (int i:result) {
               if (i<=0) {
                   return false;
               }
           }
           return true;
       } finally {
           connection.setAutoCommit(true);
       }
   }
}
