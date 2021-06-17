package com.bol.mancala.repository;

import com.bol.mancala.common.TestUtils;
import com.bol.mancala.model.Game;
import com.bol.mancala.model.GameState;
import com.bol.mancala.model.Player;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Data Layer testing
 *
 * @author Abbas
 */
@DataJpaTest
class GameRepositoryTest {
    @Autowired
    private GameRepository gameRepository;

    /**
     * First scenario: Just save a game then fetch from database
     * Second scenario: fetch a non existing game
     */
    @Test
    @DisplayName("save and find Game Test")
    void saveAndFindGameTest() {
        Game game = TestUtils.newGame();

        Game savedGame = gameRepository.save(game);
        assertNotNull(savedGame);

        // Fetch saved game
        Optional<Game> optionalGame = gameRepository.findById(savedGame.getId());

        // Validate fetched game
        assertTrue(optionalGame.isPresent());

        Game game1 = optionalGame.get();
        assertEquals(TestUtils.EXISTING_GAME_ID, game.getId());
        assertEquals(Player.PLAYER_ONE, game1.getCurrentPlayer());
        assertEquals(GameState.CREATED, game1.getGameState());
        MatcherAssert.assertThat(game1.getStatus(), CoreMatchers.is(TestUtils.initGameStatus()));

        // Fetch non existing game
        Optional<Game> gameOptional = gameRepository.findById(TestUtils.NON_EXISTING_GAME_ID);
        assertFalse(gameOptional.isPresent());
    }

    /**
     * Clear database after test
     */
    @AfterEach
    void clearDatabase() {
        gameRepository.deleteAll();
    }

}
