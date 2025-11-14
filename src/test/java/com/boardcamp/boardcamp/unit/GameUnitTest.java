package com.boardcamp.boardcamp.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.boardcamp.boardcamp.dtos.GameDTO;
import com.boardcamp.boardcamp.exceptions.GameConflictException;
import com.boardcamp.boardcamp.models.GameModel;
import com.boardcamp.boardcamp.repositories.GameRepository;
import com.boardcamp.boardcamp.services.GameService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameUnitTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    private GameDTO dto;
    private GameModel game;

    @BeforeEach
    void setup() {
        dto = new GameDTO("Jogo Teste", "http://image.com/test.jpg", 5, 2000);

        game = new GameModel();
        game.setId(1L);
        game.setName(dto.getName());
    }

    @Test
    void createGame_whenValid_returnsGame() {
        when(gameRepository.existsByName(dto.getName())).thenReturn(false);
        when(gameRepository.save(any())).thenReturn(game);

        GameModel result = gameService.createGame(dto);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
    }

    @Test
    void createGame_whenNameExists_throwsConflict() {
        when(gameRepository.existsByName(dto.getName())).thenReturn(true);

        assertThrows(GameConflictException.class, () -> gameService.createGame(dto));
    }

    @Test
    void getAllGames_returnsList() {
        when(gameRepository.findAll()).thenReturn(List.of(game));

        var list = gameService.getAllGames();

        assertEquals(1, list.size());
    }
}
