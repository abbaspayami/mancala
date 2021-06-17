package com.bol.mancala.service;

import com.bol.mancala.common.TestUtils;
import com.bol.mancala.exception.FinishedGameMoveException;
import com.bol.mancala.exception.GameNotFoundException;
import com.bol.mancala.exception.IllegalMoveException;
import com.bol.mancala.model.Game;
import com.bol.mancala.model.GameState;
import com.bol.mancala.model.Player;
import com.bol.mancala.repository.GameRepository;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testing business logic layer
 *
 * @author Abbas
 */
@ExtendWith(SpringExtension.class)
class GameServiceTest {

    @MockBean
    private GameRepository gameRepository;

    private GameService gameService;

    private Game game;

    /**
     * run Before Each test
     */
    @BeforeEach
    void setUp() {
        gameService = new GameServiceImpl(gameRepository);

        game = TestUtils.newGame();

        when(gameRepository.findById(TestUtils.EXISTING_GAME_ID)).thenReturn(Optional.of(game));
        when(gameRepository.findById(TestUtils.NON_EXISTING_GAME_ID)).thenReturn(Optional.empty());
        when(gameRepository.save(any(Game.class))).thenReturn(game);

    }

    /**
     * loading existing game
     */
    @Test
    void loadExistingGame() {
        Game game = gameService.getGameStatus(1);

        assertEquals(TestUtils.EXISTING_GAME_ID, game.getId());
        assertEquals(GameState.CREATED, game.getGameState());

    }

    /**
     * checking non existing game
     */
    @Test
    void loadNonExistingGame() {
        Exception exception = assertThrows(GameNotFoundException.class, () -> {
            gameService.getGameStatus(TestUtils.NON_EXISTING_GAME_ID);
        });
        String expectedMessage = "Game Not Found.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * Creating new game test
     */
    @Test
    void newGame() {
        Game game = gameService.start();
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        assertNotNull(game);
        assertEquals(TestUtils.EXISTING_GAME_ID, game.getId());
        assertEquals(Player.PLAYER_ONE, game.getCurrentPlayer());
        assertEquals(GameState.CREATED, game.getGameState());
        MatcherAssert.assertThat(TestUtils.initGameStatus(), CoreMatchers.is(game.getStatus()));
    }

    /**
     * Do first move and check the game status and next player
     */
    @Test
    void moveFirst() {
        Game movedGame = gameService.move(game.getId(), 2);

        assertNotNull(movedGame);
        assertEquals(Player.PLAYER_TWO, movedGame.getCurrentPlayer());
        assertEquals(GameState.PLAYING, movedGame.getGameState());
    }

    /**
     * Test and catch out of range pit numbers exception
     *
     * @param pitNumber out of range pit numbers
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 15, -3, Integer.MAX_VALUE})
    void moveOutOfRangePit(int pitNumber) {
        Exception exception = assertThrows(IllegalMoveException.class, () -> {
            gameService.move(TestUtils.EXISTING_GAME_ID, pitNumber);
        });

        String expectedMessage = "Your selected pit is out of range.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * Test and catch out of turn exception
     */
    @ParameterizedTest
    @ValueSource(ints = {8, 9, 10, 11, 12, 13})
    void moveOutOfTurnPit(int pitNumber) {
        assertNotNull(game.getId());
        Integer gameId = game.getId();
        Exception exception = assertThrows(IllegalMoveException.class, () -> {
            gameService.move(gameId, pitNumber);
        });

        String expectedMessage = "It is not your turn.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * Test and catch move from LargePit exception
     */
    @ParameterizedTest
    @ValueSource(ints = {7, 14})
    void moveFromLargePit(int pitNumber) {
        assertNotNull(game.getId());
        Integer gameId = game.getId();
        Exception exception = assertThrows(IllegalMoveException.class, () -> {
            gameService.move(gameId, pitNumber);
        });

        String expectedMessage = "You cannot move any stones from LargePit.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * Test and catch move from an empty pit exception
     */
    @Test
    void moveFromEmptyPit() {
        game.getStatus().replace(1, 0);
        assertNotNull(game.getId());
        Integer gameId = game.getId();
        Exception exception = assertThrows(IllegalMoveException.class, () -> {
            gameService.move(gameId, 1);
        });

        String expectedMessage = "You cannot move any stones from an empty pit.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * Test and catch move a finished game
     */
    @Test
    void moveFinishedGame() {
        game.setGameState(GameState.PLAYER_ONE_WIN);
        assertNotNull(game.getId());
        Integer gameId = game.getId();
        Exception exception = assertThrows(FinishedGameMoveException.class, () -> {
            gameService.move(gameId, 1);
        });

        String expectedMessage = "Game is finished";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Move a non existing game, and catch the relevant exception
     */
    @Test
    void moveNonExistingGame() {
        Exception exception = assertThrows(GameNotFoundException.class, () -> {
            gameService.move(TestUtils.NON_EXISTING_GAME_ID, 1);
        });

        String expectedMessage = "Game Not Found.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * the game is equalized
     */
    @Test
    void gameEqualized() {
        Map<Integer, Integer> status = new HashMap<>();

        status.put(1, 0);
        status.put(2, 0);
        status.put(3, 0);
        status.put(4, 0);
        status.put(5, 0);
        status.put(6, 1);
        status.put(7, 24);
        status.put(8, 0);
        status.put(9, 0);
        status.put(10, 0);
        status.put(11, 0);
        status.put(12, 0);
        status.put(13, 1);
        status.put(14, 24);
        game.setStatus(status);

        Game gameMove = gameService.move(game.getId(), 6);
        assertNotNull(gameMove.getId());
        Integer gameId = gameMove.getId();
        Exception exception = assertThrows(FinishedGameMoveException.class, () -> {
           gameService.move(gameId, 6);
        });
        String expectedMessage = "Game is finished";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        assertEquals(GameState.THE_GAME_EQUALIZED, gameMove.getGameState());
    }

    /**
     * Test move empty stone
     */
    @Test
    void moveWithEmptyStone() {
        Map<Integer, Integer> status = new HashMap<>();

        status.put(1, 1);
        status.put(2, 0);

        game.setStatus(status);
        assertNotNull(game.getId());
        Integer gameId = game.getId();
        Exception exception = assertThrows(IllegalMoveException.class, () -> {
            gameService.move(gameId, 2);
        });

        String expectedMessage = "You cannot move any stones from an empty pit.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    /**
     * Test find Equivalent rule
     */
    @Test
    void moveWithEquivalentRule() {
        Map<Integer, Integer> status = new HashMap<>();

        status.put(1, 1);
        status.put(2, 0);
        status.put(3, 4);
        status.put(4, 5);
        status.put(5, 4);
        status.put(6, 3);
        status.put(7, 5);
        status.put(8, 8);
        status.put(9, 7);
        status.put(10, 11);
        status.put(11, 8);
        status.put(12, 9);
        status.put(13, 7);
        status.put(14, 0);

        game.setStatus(status);

        Game movedGame = gameService.move(game.getId(), 1);

        assertNotNull(movedGame);
        assertEquals(0, movedGame.getStatus().get(1));
        assertEquals(0, movedGame.getStatus().get(2));
        assertEquals(0, movedGame.getStatus().get(12));
        assertEquals(15, movedGame.getStatus().get(7));
    }

    /**
     * Test last move which leads to finish the game
     */
    @Test
    void moveFinishingGame() {
        Map<Integer, Integer> status = new HashMap<>();

        status.put(1, 0);
        status.put(2, 0);
        status.put(3, 0);
        status.put(4, 0);
        status.put(5, 0);
        status.put(6, 1);
        status.put(7, 19);
        status.put(8, 8);
        status.put(9, 7);
        status.put(10, 10);
        status.put(11, 8);
        status.put(12, 9);
        status.put(13, 7);
        status.put(14, 3);

        game.setStatus(status);

        Game movedGame = gameService.move(game.getId(), 6);

        assertNotNull(movedGame);

        Stream.of(Player.PLAYER_ONE.getPits(), Player.PLAYER_TWO.getPits())
                .flatMap(Collection::stream)
                .forEach(pit -> {
                    assertEquals(0, movedGame.getStatus().get(pit));
                });

        assertEquals(20, movedGame.getStatus().get(Player.PLAYER_ONE.getLargePit()));
        assertEquals(52, movedGame.getStatus().get(Player.PLAYER_TWO.getLargePit()));
    }

}
