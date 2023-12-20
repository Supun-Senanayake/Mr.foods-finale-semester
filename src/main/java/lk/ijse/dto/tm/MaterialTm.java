package lk.ijse.dto.tm;


import javafx.scene.control.Button;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MaterialTm {
    private String code;
    private String description;
    private double qtyOnHand;
    private Button btn;


}
