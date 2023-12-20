package lk.ijse.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class EmployeeDto {
    private String id;
    private String name;
    private String address;
    private String tel;
}

