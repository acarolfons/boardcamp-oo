package com.boardcamp.boardcamp.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "rentals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerModel customer;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private GameModel game;

    @Column(nullable = false)
    private LocalDate rentDate;

    @Column(nullable = false)
    private Integer daysRented;

    private LocalDate returnDate;

    @Column(nullable = false)
    private Integer originalPrice;

    private Integer delayFee;
}
