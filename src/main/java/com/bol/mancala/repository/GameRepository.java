package com.bol.mancala.repository;

import com.bol.mancala.model.Game;
import org.springframework.data.repository.CrudRepository;

/**
 * Data Layer, CRUD actions to access the game data
 *
 * @author Abbas
 */
public interface GameRepository extends CrudRepository<Game, Integer> {
}
