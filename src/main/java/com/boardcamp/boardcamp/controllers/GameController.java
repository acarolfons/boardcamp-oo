package com.boardcamp.boardcamp.controllers;

import com.boardcamp.boardcamp.dtos.GameDTO;
import com.boardcamp.boardcamp.models.GameModel;
import com.boardcamp.boardcamp.services.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameModel>> getAllGames() {
        List<GameModel> games = gameService.getAllGames();
        return ResponseEntity.status(HttpStatus.OK).body(games);
    }

    @PostMapping
    public ResponseEntity<Object> createGame(@RequestBody GameDTO gameDTO) {
        Optional<GameModel> game = gameService.createGame(gameDTO);

        if (!game.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid game data or game already exists");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(game.get());
    }
}
