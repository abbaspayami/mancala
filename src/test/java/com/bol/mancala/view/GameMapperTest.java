package com.bol.mancala.view;

import com.bol.mancala.common.TestUtils;
import com.bol.mancala.model.Game;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test mapper service for presentation layer
 * @author Abbas
 */
class GameMapperTest {

    /**
     * Map a game to gameDto
     */
    @Test
    @DisplayName("Map Game to GameDto")
    void map() {
        GameMapper gameMapper = new GameMapper();
        Game game = TestUtils.newGame();

        GameDto gameDto = gameMapper.map(game);
        assertNotNull(gameDto);
        assertEquals(game.getId(), gameDto.getGameId());
        assertEquals(game.getStatus(), gameDto.getStatus());

    }

}
