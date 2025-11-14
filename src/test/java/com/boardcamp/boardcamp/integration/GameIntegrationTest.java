package com.boardcamp.boardcamp.integration;

import com.boardcamp.boardcamp.dtos.GameDTO;
import com.boardcamp.boardcamp.models.GameModel;
import com.boardcamp.boardcamp.repositories.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class GameIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void clear() {
        gameRepository.deleteAll();
    }

    private GameDTO sample() {
        return new GameDTO(
                "Exploradores do Vale",
                "http://images.com/vale.jpg",
                4,
                1800
        );
    }

    @Test
    void createGame_valid_returnsCreated() throws Exception {
        var dto = sample();
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.getName()));

        Assertions.assertEquals(1, gameRepository.count());
    }

    @Test
    void createGame_duplicatedName_conflict() throws Exception {
        gameRepository.save(new GameModel(null, "Exploradores do Vale", "a", 3, 1200));

        var dto = sample();
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict());

        Assertions.assertEquals(1, gameRepository.count());
    }

    @Test
    void getAllGames_returnsList() throws Exception {
        gameRepository.save(new GameModel(null, "Roda Mística", "x", 2, 1500));

        mockMvc.perform(get("/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Roda Mística"));
    }
}