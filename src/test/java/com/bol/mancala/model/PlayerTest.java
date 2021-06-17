package com.bol.mancala.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * It is important to test findPlayer method based on the user input
 *
 * @author Abbas
 */
class PlayerTest {

    /**
     *
     * @return players pits
     */
    static Stream<Arguments> playersPits() {
        return Stream.of(
                Arguments.of(Player.PLAYER_ONE, Player.PLAYER_ONE.getPits()),
                Arguments.of(Player.PLAYER_TWO, Player.PLAYER_TWO.getPits())
        );
    }

    /**
     * Test valid pit numbers
     *
     * @param player Given player
     * @param pits  A list of pits
     */
    @ParameterizedTest
    @MethodSource("playersPits")
    void findPlayerTest(Player player, List<Integer> pits) {
        pits.forEach(pit ->
                assertEquals(player, Player.findPlayer(pit)));
    }

    /**
     * Test find Opponent Player
     *
     * @param player a player
     */
    @ParameterizedTest
    @EnumSource(value = Player.class, names = {"PLAYER_ONE", "PLAYER_TWO"})
    void findOpponent(Player player) {
        Player opponent = Player.findOpponent(player);
        assertNotEquals(player, opponent);
    }

}
