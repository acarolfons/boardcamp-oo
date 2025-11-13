package com.boardcamp.boardcamp.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private String name;
    private String cpf;
    private String phone;
}
