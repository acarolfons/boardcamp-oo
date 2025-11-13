package com.boardcamp.boardcamp.controllers;

import com.boardcamp.boardcamp.dtos.RentalDTO;
import com.boardcamp.boardcamp.models.RentalModel;
import com.boardcamp.boardcamp.services.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<List<RentalModel>> getAllRentals() {
        return ResponseEntity.status(HttpStatus.OK).body(rentalService.getAllRentals());
    }

    @PostMapping
    public ResponseEntity<Object> createRental(@RequestBody RentalDTO rentalDTO) {
        Optional<RentalModel> rental = rentalService.createRental(rentalDTO);

        if (!rental.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid rental data or game unavailable");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(rental.get());
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Object> returnRental(@PathVariable Long id) {
        Optional<RentalModel> rental = rentalService.returnRental(id);

        if (!rental.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Rental not found or already returned");
        }

        return ResponseEntity.status(HttpStatus.OK).body(rental.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRental(@PathVariable Long id) {
        boolean deleted = rentalService.deleteRental(id);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot delete an active rental or rental not found");
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
