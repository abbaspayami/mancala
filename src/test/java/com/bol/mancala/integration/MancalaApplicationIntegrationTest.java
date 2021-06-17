package com.bol.mancala.integration;

import com.bol.mancala.common.TestUtils;
import com.bol.mancala.model.Player;
import com.bol.mancala.view.GameDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test
 *
 * @author Abbas
 */
@SpringBootTest
@AutoConfigureMockMvc
class MancalaApplicationIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private GameDto gameDto;

    /**
     * Create new game before each test
     *
     * @throws Exception
     */
    @BeforeEach
    void createNewGame() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(TestUtils.NEW_GAME_URI))
                .andExpect(status().isCreated())
                .andReturn();
        assertNotNull(mvcResult.getResponse());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        gameDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), GameDto.class);

        assertNotNull(gameDto);
        assertNotEquals(0, gameDto.getGameId());
        MatcherAssert.assertThat(TestUtils.initGameStatus(), CoreMatchers.is(gameDto.getStatus()));
    }

    /**
     * Fetch an existing game
     *
     * @throws Exception GameNotFoundException if a game is not existing
     */
    @Test
    void statusExistingGame() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(TestUtils.STATUS_GAME_URI + gameDto.getGameId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameDto.getGameId()))
                .andReturn();

        assertNotNull(mvcResult);
        assertNotNull(mvcResult.getResponse().getContentAsString());

        GameDto existingGame = mapper.readValue(mvcResult.getResponse().getContentAsString(), GameDto.class);
        MatcherAssert.assertThat(gameDto.getStatus(), CoreMatchers.is(existingGame.getStatus()));
    }

    /**
     * checking the true Exception
     *
     * @throws Exception GameNotFoundException if a game is not existing
     */
    @Test
    void statusNonExistingGame() throws Exception {
        mockMvc.perform(get(TestUtils.STATUS_GAME_URI + 0))
                .andExpect(status().isNotFound());
    }

    /**
     * Check illegal selected moves
     *
     * @param pit illegal pit
     * @throws Exception
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 15, -3, 7, 14, Integer.MAX_VALUE})
    void illegalMoveTest(int pit) throws Exception {
        mockMvc.perform(put(TestUtils.MOVE_GAME_URI + gameDto.getGameId() + "&pit=" + pit))
                .andExpect(status().isBadRequest());
    }

    /**
     * Check first move is OK, but the second one is not the current player move.
     *
     * @throws Exception
     */
    @Test
    void notPlayerTurnMoveTest() throws Exception {
        int firstMovePitNumber = 2;
        int secondMovePitNumber = 3;

        MvcResult mvcResult = mockMvc.perform(put(TestUtils.MOVE_GAME_URI + gameDto.getGameId() + "&pit=" + firstMovePitNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameDto.getGameId()))
                .andReturn();

        assertNotNull(mvcResult);
        assertNotNull(mvcResult.getResponse().getContentAsString());

        GameDto movedGame = mapper.readValue(mvcResult.getResponse().getContentAsString(), GameDto.class);
        assertNotNull(movedGame);
        assertEquals(0, movedGame.getStatus().get(firstMovePitNumber));

        mockMvc.perform(put(TestUtils.MOVE_GAME_URI + gameDto.getGameId() + "&pit=" + secondMovePitNumber))
                .andExpect(status().isBadRequest());
    }

    /**
     * @return Game plays with their results
     */
    static Stream<Arguments> gamePlays() {
        return Stream.of(
                Arguments.of(Arrays.asList(2, 13, 3, 11, 4, 10, 1, 13, 5, 9, 1, 12, 2, 8, 3, 11, 4, 10, 5, 9, 1, 12, 2, 3, 8, 4, 9, 6, 13, 5, 10, 1, 9, 2, 11, 4, 12, 1, 13, 1, 10, 2, 3, 8, 9, 4, 10, 5, 8, 6), 18, 54),
                Arguments.of(Arrays.asList(8, 9, 6, 9, 3, 10, 2, 11, 4, 12, 1, 13, 3, 9, 2, 1, 8, 1, 9, 2, 13, 3, 1, 11, 1, 13, 12, 2, 13, 10, 1, 11, 2, 12, 13), 57, 15)
        );
    }

    /**
     * @param moves             A list of valid moves
     * @param playerOneLargePit The player one game result
     * @param PlayerTwoLargePit The player two game result
     */
    @ParameterizedTest
    @MethodSource("gamePlays")
    void playTest(List<Integer> moves, int playerOneLargePit, int PlayerTwoLargePit) {
        AtomicReference<GameDto> movedGame = new AtomicReference<>();

        moves.forEach(pitIndex -> {
            try {
                MvcResult mvcResult = mockMvc.perform(put(TestUtils.MOVE_GAME_URI + gameDto.getGameId() + "&pit=" + pitIndex))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.gameId").value(gameDto.getGameId()))
                        .andReturn();

                assertNotNull(mvcResult);
                assertNotNull(mvcResult.getResponse().getContentAsString());

                movedGame.set(mapper.readValue(mvcResult.getResponse().getContentAsString(), GameDto.class));

                assertNotNull(movedGame);
            } catch (Exception e) {
                fail(e);
            }
        });

        assertEquals(playerOneLargePit, movedGame.get().getStatus().get(Player.PLAYER_ONE.getLargePit()));
        assertEquals(PlayerTwoLargePit, movedGame.get().getStatus().get(Player.PLAYER_TWO.getLargePit()));
    }

}
