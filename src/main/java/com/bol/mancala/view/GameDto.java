package com.bol.mancala.view;

import com.bol.mancala.model.GameState;
import lombok.Data;

import java.util.Map;

/**
 * Presents game information for view layer
 *
 * @author Abbas
 */
@Data
public class GameDto {
    private int gameId;
    private String currentPlayer;
    private GameState currentGameState;
    private Map<Integer, Integer> status;
}
