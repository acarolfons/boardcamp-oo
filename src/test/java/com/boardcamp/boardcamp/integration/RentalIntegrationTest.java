package com.boardcamp.boardcamp.integration;

import com.boardcamp.boardcamp.models.CustomerModel;
import com.boardcamp.boardcamp.models.GameModel;
import com.boardcamp.boardcamp.repositories.CustomerRepository;
import com.boardcamp.boardcamp.repositories.GameRepository;
import com.boardcamp.boardcamp.repositories.RentalRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RentalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GameRepository gameRepository;

    private CustomerModel user;
    private GameModel game;

    @BeforeEach
    void setup() {
        rentalRepository.deleteAll();
        gameRepository.deleteAll();
        customerRepository.deleteAll();

        user = customerRepository.save(
                new CustomerModel(null, "Carla Novaes", "22211133399", "21988001234")
        );

        game = gameRepository.save(
                new GameModel(null, "Viagem ao Topo", "img", 2, 900)
        );
    }

    @Test
    void createRental_valid_returnsCreated() throws Exception {
        String body = """
                {
                    "customerId": %d,
                    "gameId": %d,
                    "daysRented": 3
                }
                """.formatted(user.getId(), game.getId());

        mockMvc.perform(post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.daysRented").value(3))
                .andExpect(jsonPath("$.originalPrice").value(2700));

        Assertions.assertEquals(1, rentalRepository.count());
    }

    @Test
    void createRental_customerNotFound_returns404() throws Exception {
        String body = """
                {
                    "customerId": 9999,
                    "gameId": %d,
                    "daysRented": 2
                }
                """.formatted(game.getId());

        mockMvc.perform(post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRental_gameUnavailable_returns422() throws Exception {
        for (int i = 0; i < 2; i++) {
            String b = """
                    {
                        "customerId": %d,
                        "gameId": %d,
                        "daysRented": 1
                    }
                    """.formatted(user.getId(), game.getId());

            mockMvc.perform(post("/rentals")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(b))
                    .andExpect(status().isCreated());
        }
        
        String last = """
                {
                    "customerId": %d,
                    "gameId": %d,
                    "daysRented": 1
                }
                """.formatted(user.getId(), game.getId());

        mockMvc.perform(post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(last))
                .andExpect(status().isUnprocessableEntity());

        Assertions.assertEquals(2, rentalRepository.count());
    }

    @Test
    void createRental_invalidDays_returns400() throws Exception {
        String body = """
                {
                    "customerId": %d,
                    "gameId": %d,
                    "daysRented": 0
                }
                """.formatted(user.getId(), game.getId());

        mockMvc.perform(post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }
}
