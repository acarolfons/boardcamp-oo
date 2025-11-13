package com.boardcamp.boardcamp.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
    private Long customerId;
    private Long gameId;
    private Integer daysRented;
}

