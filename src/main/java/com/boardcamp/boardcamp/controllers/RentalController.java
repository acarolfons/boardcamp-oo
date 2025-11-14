package com.boardcamp.boardcamp.controllers;

import com.boardcamp.boardcamp.dtos.RentalDTO;
import com.boardcamp.boardcamp.models.RentalModel;
import com.boardcamp.boardcamp.services.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<List<RentalModel>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @PostMapping
    public ResponseEntity<RentalModel> createRental(@RequestBody RentalDTO rentalDTO) {
        RentalModel created = rentalService.createRental(rentalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<RentalModel> returnRental(@PathVariable Long id) {
        RentalModel rental = rentalService.returnRental(id);
        return ResponseEntity.ok(rental);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}
