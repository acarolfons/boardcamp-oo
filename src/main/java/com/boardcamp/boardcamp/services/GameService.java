package com.boardcamp.boardcamp.services;

import com.boardcamp.boardcamp.dtos.GameDTO;
import com.boardcamp.boardcamp.exceptions.BadRequestException;
import com.boardcamp.boardcamp.exceptions.GameConflictException;
import com.boardcamp.boardcamp.models.GameModel;
import com.boardcamp.boardcamp.repositories.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public GameModel createGame(GameDTO gameDTO) {
        if (gameDTO.getName() == null || gameDTO.getName().isBlank()) throw new BadRequestException("Name is required");
        if (gameDTO.getStockTotal() == null || gameDTO.getStockTotal() <= 0) throw new BadRequestException("Stock must be > 0");
        if (gameDTO.getPricePerDay() == null || gameDTO.getPricePerDay() <= 0) throw new BadRequestException("Price must be > 0");
        if (gameRepository.existsByName(gameDTO.getName())) throw new GameConflictException("Game already exists");

        GameModel game = new GameModel();
        game.setName(gameDTO.getName());
        game.setImage(gameDTO.getImage());
        game.setStockTotal(gameDTO.getStockTotal());
        game.setPricePerDay(gameDTO.getPricePerDay());

        return gameRepository.save(game);
    }
}
