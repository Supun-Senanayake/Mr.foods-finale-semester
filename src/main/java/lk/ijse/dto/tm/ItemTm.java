package lk.ijse.dto.tm;

import javafx.scene.control.Button;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class ItemTm {
    private String code;
    private String description;
    private double unitPrice;
    private String qtyOnHand;
    private Button btn;

}
