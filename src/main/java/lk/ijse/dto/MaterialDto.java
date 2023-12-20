package lk.ijse.dto;

import javafx.scene.control.Button;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MaterialDto {
    private String code;
    private String description;
    private double qtyOnHand;
    private Button btn;

    public MaterialDto(String code, String description, double qtyOnHand) {
        this.code = code;
        this.description = description;
        this.qtyOnHand = qtyOnHand;
    }

}
