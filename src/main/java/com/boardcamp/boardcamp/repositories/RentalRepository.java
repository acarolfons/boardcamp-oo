package com.boardcamp.boardcamp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boardcamp.boardcamp.models.RentalModel;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<RentalModel, Long> {
    List<RentalModel> findByGameIdAndReturnDateIsNull(Long gameId);
}

