package com.bol.mancala.view;

import com.bol.mancala.model.Game;
import com.bol.mancala.model.GameState;
import org.springframework.stereotype.Component;

/**
 * Map Game entity to GameDto to present it in view layer
 *
 * @author Abbas
 */
@Component
public class GameMapper {

    /**
     * Map Game to GameDto
     *
     * @param game a game
     * @return a gameDto
     */
    public GameDto map(Game game) {
        GameDto gameDto = new GameDto();

        gameDto.setGameId(game.getId());
        gameDto.setStatus(game.getStatus());
        gameDto.setCurrentGameState(game.getGameState());
        gameDto.setCurrentPlayer(game.getGameState() != GameState.CREATED ? game.getCurrentPlayer().name() : "Please Start the Game");

        return gameDto;
    }

}
