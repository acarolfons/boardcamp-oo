package com.boardcamp.boardcamp.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private String name;
    private String image;
    private Integer stockTotal;
    private Integer pricePerDay;
}

