package com.bol.mancala.common;

import com.bol.mancala.model.Game;
import com.bol.mancala.model.GameState;
import com.bol.mancala.model.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * initiate Test
 *
 * @author Abbas
 */
public class TestUtils {

    // Sample game ids
    public static final int EXISTING_GAME_ID = 1;
    public static final int NON_EXISTING_GAME_ID = 2;

    // Test game configurations
    public static final int INITIAL_STONES = 6;

    // Test urls
    public static final String BASE_URL = "http://localhost:8080/mancala";
    public static final String NEW_GAME_URI = BASE_URL + "/start";
    public static final String STATUS_GAME_URI = BASE_URL + "/gameStatus?game=";
    public static final String MOVE_GAME_URI = BASE_URL + "/move?game=";

    public static Map<Integer, Integer> initGameStatus() {
        Map<Integer, Integer> status = new HashMap<>();

        status.put(1, INITIAL_STONES);
        status.put(2, INITIAL_STONES);
        status.put(3, INITIAL_STONES);
        status.put(4, INITIAL_STONES);
        status.put(5, INITIAL_STONES);
        status.put(6, INITIAL_STONES);
        status.put(7, 0);
        status.put(8, INITIAL_STONES);
        status.put(9, INITIAL_STONES);
        status.put(10, INITIAL_STONES);
        status.put(11, INITIAL_STONES);
        status.put(12, INITIAL_STONES);
        status.put(13, INITIAL_STONES);
        status.put(14, 0);

        return status;
    }

    public static Game newGame() {
        Game game = new Game();

        game.setId(EXISTING_GAME_ID);
        game.setGameState(GameState.CREATED);
        game.setCurrentPlayer(Player.PLAYER_ONE);
        game.setStatus(initGameStatus());

        return game;
    }

}
