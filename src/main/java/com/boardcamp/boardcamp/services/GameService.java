package com.boardcamp.boardcamp.services;

import com.boardcamp.boardcamp.dtos.GameDTO;
import com.boardcamp.boardcamp.models.GameModel;
import com.boardcamp.boardcamp.repositories.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    // GET "/games"
    public List<GameModel> getAllGames() {
        return gameRepository.findAll();
    }

    // POST "/games"
    public Optional<GameModel> createGame(GameDTO gameDTO) {
        if (gameDTO.getName() == null || gameDTO.getName().isBlank()) return Optional.empty();
        if (gameDTO.getStockTotal() == null || gameDTO.getStockTotal() <= 0) return Optional.empty();
        if (gameDTO.getPricePerDay() == null || gameDTO.getPricePerDay() <= 0) return Optional.empty();
        if (gameRepository.existsByName(gameDTO.getName())) return Optional.empty();

        GameModel game = new GameModel();
        game.setName(gameDTO.getName());
        game.setImage(gameDTO.getImage());
        game.setStockTotal(gameDTO.getStockTotal());
        game.setPricePerDay(gameDTO.getPricePerDay());

        gameRepository.save(game);
        return Optional.of(game);
    }
}
