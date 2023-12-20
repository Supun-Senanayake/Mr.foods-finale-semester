package lk.ijse.dto.tm;

import javafx.scene.control.Button;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class AttendanceTm {
    private String id;
    private String name;
    private Button btn;
    private boolean isPresent;

    public AttendanceTm(String Id, String Name, Button Btn) {
        this.id = Id;
        this.name= Name;
        this.btn = Btn;
    }

}
