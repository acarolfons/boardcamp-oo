package com.boardcamp.boardcamp.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.boardcamp.boardcamp.dtos.RentalDTO;
import com.boardcamp.boardcamp.exceptions.BadRequestException;
import com.boardcamp.boardcamp.exceptions.NotFoundException;
import com.boardcamp.boardcamp.exceptions.UnprocessableEntityException;
import com.boardcamp.boardcamp.models.CustomerModel;
import com.boardcamp.boardcamp.models.GameModel;
import com.boardcamp.boardcamp.models.RentalModel;
import com.boardcamp.boardcamp.repositories.CustomerRepository;
import com.boardcamp.boardcamp.repositories.GameRepository;
import com.boardcamp.boardcamp.repositories.RentalRepository;
import com.boardcamp.boardcamp.services.RentalService;

@ExtendWith(MockitoExtension.class)
class RentalUnitTest {

    @InjectMocks
    private RentalService rentalService;

    @Mock
    private RentalRepository rentals;

    @Mock
    private CustomerRepository customers;

    @Mock
    private GameRepository games;

    private RentalDTO dto;
    private CustomerModel customer;
    private GameModel game;
    private RentalModel rental;

    @BeforeEach
    void setup() {

        dto = new RentalDTO(1L, 1L, 5);

        customer = new CustomerModel();
        customer.setId(1L);

        game = new GameModel();
        game.setId(1L);
        game.setStockTotal(3);
        game.setPricePerDay(1000);

        rental = new RentalModel();
        rental.setId(1L);
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setDaysRented(5);
        rental.setRentDate(LocalDate.now());
        rental.setOriginalPrice(5000);
    }

    @Test
    void createRental_validData_returnsRental() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(games.findById(1L)).thenReturn(Optional.of(game));
        when(rentals.findByGameIdAndReturnDateIsNull(1L)).thenReturn(List.of());
        when(rentals.save(any())).thenReturn(rental);

        RentalModel result = rentalService.createRental(dto);

        assertNotNull(result);
        assertEquals(5000, result.getOriginalPrice());
    }

    @Test
    void createRental_nonExistingCustomer_throwsNotFound() {
        when(customers.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> rentalService.createRental(dto));
    }

    @Test
    void createRental_nonExistingGame_throwsNotFound() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(games.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> rentalService.createRental(dto));
    }

    @Test
    void createRental_zeroDays_throwsBadRequest() {
        RentalDTO bad = new RentalDTO(1L, 1L, 0);
        assertThrows(BadRequestException.class, () -> rentalService.createRental(bad));
    }

    @Test
    void createRental_gameUnavailable_throwsUnprocessable() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(games.findById(1L)).thenReturn(Optional.of(game));
        when(rentals.findByGameIdAndReturnDateIsNull(1L))
            .thenReturn(List.of(new RentalModel(), new RentalModel(), new RentalModel()));

        assertThrows(UnprocessableEntityException.class, () -> rentalService.createRental(dto));
    }

    @Test
    void returnRental_valid_updatesReturnDate() {
        rental.setRentDate(LocalDate.now().minusDays(5));

        when(rentals.findById(1L)).thenReturn(Optional.of(rental));
        when(rentals.save(any())).thenReturn(rental);

        RentalModel returned = rentalService.returnRental(1L);

        assertNotNull(returned.getReturnDate());
    }

    @Test
    void returnRental_nonExisting_throwsNotFound() {
        when(rentals.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> rentalService.returnRental(1L));
    }

    @Test
    void returnRental_alreadyReturned_throwsUnprocessable() {
        rental.setReturnDate(LocalDate.now());
        when(rentals.findById(1L)).thenReturn(Optional.of(rental));
        assertThrows(UnprocessableEntityException.class, () -> rentalService.returnRental(1L));
    }

    @Test
    void deleteRental_finished_deletesSuccessfully() {
        rental.setReturnDate(LocalDate.now());
        when(rentals.findById(1L)).thenReturn(Optional.of(rental));

        assertDoesNotThrow(() -> rentalService.deleteRental(1L));
        verify(rentals).delete(rental);
    }

    @Test
    void deleteRental_nonExisting_throwsNotFound() {
        when(rentals.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> rentalService.deleteRental(1L));
    }

    @Test
    void deleteRental_notFinished_throwsBadRequest() {
        rental.setReturnDate(null);
        when(rentals.findById(1L)).thenReturn(Optional.of(rental));
        assertThrows(BadRequestException.class, () -> rentalService.deleteRental(1L));
    }

    @Test
    void getAllRentals_returnsList() {
        when(rentals.findAll()).thenReturn(List.of(rental));
        assertEquals(1, rentalService.getAllRentals().size());
    }
}
