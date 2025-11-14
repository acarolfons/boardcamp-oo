package com.boardcamp.boardcamp.services;

import com.boardcamp.boardcamp.dtos.RentalDTO;
import com.boardcamp.boardcamp.exceptions.BadRequestException;
import com.boardcamp.boardcamp.exceptions.NotFoundException;
import com.boardcamp.boardcamp.exceptions.UnprocessableEntityException;
import com.boardcamp.boardcamp.models.RentalModel;
import com.boardcamp.boardcamp.models.GameModel;
import com.boardcamp.boardcamp.models.CustomerModel;
import com.boardcamp.boardcamp.repositories.RentalRepository;
import com.boardcamp.boardcamp.repositories.GameRepository;
import com.boardcamp.boardcamp.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final GameRepository gameRepository;
    private final CustomerRepository customerRepository;

    public RentalService(RentalRepository rentalRepository, GameRepository gameRepository, CustomerRepository customerRepository) {
        this.rentalRepository = rentalRepository;
        this.gameRepository = gameRepository;
        this.customerRepository = customerRepository;
    }

    // GET "/rentals"
    public List<RentalModel> getAllRentals() {
        return rentalRepository.findAll();
    }

    // POST "/rentals"
    public RentalModel createRental(RentalDTO rentalDTO) {
        if (rentalDTO.getDaysRented() == null || rentalDTO.getDaysRented() <= 0) throw new BadRequestException("Days rented must be > 0");
        if (rentalDTO.getCustomerId() == null || rentalDTO.getGameId() == null) throw new BadRequestException("CustomerId and GameId are required");

        CustomerModel customer = customerRepository.findById(rentalDTO.getCustomerId())
            .orElseThrow(() -> new NotFoundException("Customer not found"));
        GameModel game = gameRepository.findById(rentalDTO.getGameId())
            .orElseThrow(() -> new NotFoundException("Game not found"));

        int rentalsOpen = rentalRepository.findByGameIdAndReturnDateIsNull(game.getId()).size();    
        if (rentalsOpen >= game.getStockTotal()) throw new UnprocessableEntityException("No games available");

        RentalModel rental = new RentalModel();
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setDaysRented(rentalDTO.getDaysRented());
        rental.setRentDate(LocalDate.now());
        rental.setOriginalPrice(game.getPricePerDay() * rentalDTO.getDaysRented());
        rental.setReturnDate(null);
        rental.setDelayFee(0);

        return rentalRepository.save(rental);
    }

    // POST "/rentals/:id/return"
    public RentalModel returnRental(Long rentalId) {
        RentalModel rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Rental not found"));
        if (rental.getReturnDate() != null) throw new UnprocessableEntityException("Rental already finished");

        rental.setReturnDate(LocalDate.now());
        long daysLate = ChronoUnit.DAYS.between(
                rental.getRentDate().plusDays(rental.getDaysRented()),
                rental.getReturnDate()
        );

        if (daysLate > 0) {
            rental.setDelayFee((int) (daysLate * rental.getGame().getPricePerDay()));
        } else {
            rental.setDelayFee(0);
        }

        return rentalRepository.save(rental);
    }

    // DELETE "/rentals/:id"
    public void deleteRental(Long rentalId) {
        RentalModel rental = rentalRepository.findById(rentalId)
            .orElseThrow(() -> new NotFoundException("Rental not found"));
        if (rental.getReturnDate() == null) throw new BadRequestException("Rental not finished yet");

        rentalRepository.delete(rental);
    }
}