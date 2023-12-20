package lk.ijse.dto;

import lk.ijse.dto.tm.AttendanceTm;
import lk.ijse.dto.tm.CartTm;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class AttendanceDto {
    private String date;
    private String name;
    private String id;
    //private List<AttendanceTm> attendanceTmList = new ArrayList<>();
    private boolean isPresent;
}
