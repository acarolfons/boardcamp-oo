package com.boardcamp.boardcamp.controllers;

import com.boardcamp.boardcamp.dtos.GameDTO;
import com.boardcamp.boardcamp.models.GameModel;
import com.boardcamp.boardcamp.services.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameModel>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @PostMapping
    public ResponseEntity<GameModel> createGame(@RequestBody GameDTO gameDTO) {
        GameModel created = gameService.createGame(gameDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
