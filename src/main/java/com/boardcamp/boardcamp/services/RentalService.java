package com.boardcamp.boardcamp.services;

import com.boardcamp.boardcamp.dtos.RentalDTO;
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
import java.util.Optional;

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
    public Optional<RentalModel> createRental(RentalDTO rentalDTO) {
        if (rentalDTO.getDaysRented() == null || rentalDTO.getDaysRented() <= 0) return Optional.empty();
        if (rentalDTO.getCustomerId() == null || rentalDTO.getGameId() == null) return Optional.empty();

        Optional<CustomerModel> customerOpt = customerRepository.findById(rentalDTO.getCustomerId());
        Optional<GameModel> gameOpt = gameRepository.findById(rentalDTO.getGameId());

        if (customerOpt.isEmpty() || gameOpt.isEmpty()) return Optional.empty();

        CustomerModel customer = customerOpt.get();
        GameModel game = gameOpt.get();

        List<RentalModel> rentedGames = rentalRepository.findByGameIdAndReturnDateIsNull(game.getId());
        if (rentedGames.size() >= game.getStockTotal()) return Optional.empty();

        RentalModel rental = new RentalModel();
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setDaysRented(rentalDTO.getDaysRented());
        rental.setRentDate(LocalDate.now());
        rental.setOriginalPrice(game.getPricePerDay() * rentalDTO.getDaysRented());
        rental.setReturnDate(null);
        rental.setDelayFee(0);

        rentalRepository.save(rental);
        return Optional.of(rental);
    }

    // POST "/rentals/:id/return"
    public Optional<RentalModel> returnRental(Long rentalId) {
        Optional<RentalModel> rentalOpt = rentalRepository.findById(rentalId);
        if (rentalOpt.isEmpty()) return Optional.empty();

        RentalModel rental = rentalOpt.get();
        if (rental.getReturnDate() != null) return Optional.empty();

        rental.setReturnDate(LocalDate.now());
        long daysLate = ChronoUnit.DAYS.between(
                rental.getRentDate().plusDays(rental.getDaysRented()),
                rental.getReturnDate()
        );
        rental.setDelayFee(daysLate > 0 ? (int) (daysLate * rental.getGame().getPricePerDay()) : 0);

        rentalRepository.save(rental);
        return Optional.of(rental);
    }

    // DELETE "/rentals/:id"
    public boolean deleteRental(Long rentalId) {
        Optional<RentalModel> rentalOpt = rentalRepository.findById(rentalId);
        if (rentalOpt.isEmpty()) return false;

        RentalModel rental = rentalOpt.get();
        if (rental.getReturnDate() == null) return false;

        rentalRepository.delete(rental);
        return true;
    }
}