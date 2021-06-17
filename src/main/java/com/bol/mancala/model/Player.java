package com.bol.mancala.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * This class defines 2 players with their pits index, and also their mancala's index
 * as well as find current Player and also find opponent Player
 *
 * @author Abbas
 */
@Getter
@RequiredArgsConstructor
public enum Player {

    PLAYER_ONE(Arrays.asList(1, 2, 3, 4, 5, 6), 7, "PlayerOne"),
    PLAYER_TWO(Arrays.asList(8, 9, 10, 11, 12, 13), 14, "PlayerTwo");

    private final List<Integer> pits;
    private final int largePit;
    private final String name;

    public static final int EMPTY_STONE = 0;
    public static final int FIRST_PIT_INDEX = 1;
    public static final int LAST_PIT_INDEX = 14;
    public static final String START = "Start";

    /**
     * Find the pit's owner
     *
     * @param pitIndex given pitIndex
     * @return Related player
     */
    public static Player findPlayer(Integer pitIndex) {
        if (PLAYER_ONE.getPits().contains(pitIndex))
            return Player.PLAYER_ONE;
        return Player.PLAYER_TWO;
    }

    /**
     * Find the given player's opponent
     *
     * @param player given player
     * @return find Opponent player
     */
    public static Player findOpponent(Player player) {
        return player.equals(PLAYER_ONE) ? PLAYER_TWO : PLAYER_ONE;
    }

}
