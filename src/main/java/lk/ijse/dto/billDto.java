package lk.ijse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class billDto {
    private String description;
    private int qty;
    private double unitPrice;
    private double tot;
}
