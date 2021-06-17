package com.bol.mancala.service;

import com.bol.mancala.exception.FinishedGameMoveException;
import com.bol.mancala.exception.GameNotFoundException;
import com.bol.mancala.exception.IllegalMoveException;
import com.bol.mancala.model.Game;
import com.bol.mancala.model.GameState;
import com.bol.mancala.model.Player;
import com.bol.mancala.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.bol.mancala.model.Player.*;

/**
 * The game logic implementation,
 * {@link GameService} implementation.
 * All changes will rollback by any exception to preserve the game valid status
 *
 * @author Abbas
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;

    /**
     * use for config stones
     */
    @Value("${game.config.initial-stones}")
    private int initialStones;

    /**
     * To create New Game
     *
     * @return A game
     */
    @Override
    public Game start() {
        log.debug("Request new game");

        Game game = new Game();

        Map<Integer, Integer> status = new HashMap<>();

        // Fill the players pits
        Stream.of(Player.PLAYER_ONE.getPits(), Player.PLAYER_TWO.getPits())
                .flatMap(Collection::stream)
                .forEach(pitIndex -> status.put(pitIndex, initialStones));

        // Initial the player's LargePit with zero
        status.put(Player.PLAYER_ONE.getLargePit(), Player.EMPTY_STONE);
        status.put(Player.PLAYER_TWO.getLargePit(), Player.EMPTY_STONE);

        game.setStatus(status);

        game.setGameState(GameState.CREATED);

        log.debug("A game object has been filled successfully.");

        return gameRepository.save(game);
    }

    /**
     * Move stones from pit, and to play a game
     *
     * @param gameId   to find a game
     * @param pitIndex indicate which pit selected
     * @return Game current status
     * @throws com.bol.mancala.exception.GameNotFoundException     if cannot find game
     * @throws com.bol.mancala.exception.IllegalMoveException      if selected pit is invalid or illegal
     * @throws com.bol.mancala.exception.FinishedGameMoveException if the game Already Finished
     */
    @Override
    public Game move(Integer gameId, Integer pitIndex) {
        log.debug("Move pit {} from game {}", pitIndex, gameId);
        Game game = getGame(gameId);

        // Validate user inputs, and throw related exception
        validateMove(game, pitIndex);
        log.debug("The game has been validated.");

        // this variable uses to keep current pit Stones
        int currentPitStones = game.getStatus().get(pitIndex);
        // this variable uses to iterate among pit
        int crossingPit = pitIndex;

        game.setGameState(GameState.PLAYING);
        game.getStatus().replace(pitIndex, Player.EMPTY_STONE);

        Player currentPlayer = Player.findPlayer(pitIndex);
        Player opponentPlayer = Player.findOpponent(currentPlayer);

        crossingPit = sowsStones(game, currentPitStones, crossingPit, opponentPlayer);
        log.debug("pit's stones are moved.");

        capturingStones(game, crossingPit, currentPlayer);
        log.debug("The game capturingStones checked.");

        changeCurrentPlayer(game, currentPlayer, opponentPlayer, crossingPit);
        log.debug("The game CurrentPlayer checked.");

        checkGameEnds(game, currentPlayer, opponentPlayer);
        log.debug("The game finishing rule checked.");

        return gameRepository.save(game);
    }

    /**
     * @param gameId a game
     * @return a game saved in database
     * @throws com.bol.mancala.exception.GameNotFoundException if game not found
     */
    private Game getGame(Integer gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);

        if (!optionalGame.isPresent()) {
            log.debug("Game not found with id {}", gameId);

            throw new GameNotFoundException("Game Not Found.");
        }
        return optionalGame.get();
    }

    /**
     * Check the user inputs, and validate selected pit
     *
     * @param game     current game playing
     * @param pitIndex selected pit index
     * @throws com.bol.mancala.exception.IllegalMoveException if wrong pit was selected by the user
     */
    private void validateMove(Game game, Integer pitIndex) {
        log.debug("validate user inputs for game {} and pit {}", game.getId(), pitIndex);

        checkingGameAlreadyFinished(game);

        if (pitIndex < Player.FIRST_PIT_INDEX || pitIndex > Player.LAST_PIT_INDEX)
            throw new IllegalMoveException("Your selected pit is out of range.");

        if (Player.PLAYER_ONE.getLargePit() == pitIndex || Player.PLAYER_TWO.getLargePit() == pitIndex)
            throw new IllegalMoveException("You cannot move any stones from LargePit.");

        // The first move determines the currentPlayer, so no checking is needed. For the rest moves check turn by selected pin
        if (game.getCurrentPlayer() != null && !game.getCurrentPlayer().getPits().contains(pitIndex))
            throw new IllegalMoveException("It is not your turn.");

        if (game.getStatus().get(pitIndex) == Player.EMPTY_STONE)
            throw new IllegalMoveException("You cannot move any stones from an empty pit.");
    }

    /**
     * before moving this method checking Game Already Finished
     * @param game a game
     * @throws FinishedGameMoveException if the game Already Finished
     */
    private void checkingGameAlreadyFinished(Game game) {
        log.debug("Checking Game Already finished with id {}", game.getId());

        if (!game.getGameState().equals(GameState.CREATED) && !game.getGameState().equals(GameState.PLAYING)) {
            log.debug("Game is already finished with id {}", game.getId());

            String errorMessage = String.format("Game is finished, result: Player one LargePit is %d, Player two LargePit is %d, %s",
                    game.getStatus().get(Player.PLAYER_ONE.getLargePit()),
                    game.getStatus().get(Player.PLAYER_TWO.getLargePit()),
                    game.getGameState());

            throw new FinishedGameMoveException(errorMessage);
        }
    }

    /**
     * sowing Stones to right
     *
     * @param game  a game
     * @param currentPitStones current Pit Stone
     * @param crossingPit iterate to right
     * @param opponentPlayer opponent Player
     * @return the last index that last stone has put on it
     */
    private Integer sowsStones(Game game, Integer currentPitStones, Integer crossingPit, Player opponentPlayer) {
        while (currentPitStones != Player.EMPTY_STONE) {
            // Increase crossing index to move forward until there are no stones to move
            crossingPit++;

            // If we pass the last index of the board it is necessary to reset the index to first one
            if (crossingPit > Player.LAST_PIT_INDEX)
                crossingPit = Player.FIRST_PIT_INDEX;

            // If the iterate pit is the opponent's large pit just jump over it and continue
            if (opponentPlayer.getLargePit() == crossingPit)
                continue;

            // Increase crossing pit stones by one stone
            game.getStatus().computeIfPresent(crossingPit, (key, value) -> value + 1);
            currentPitStones--;
        }
        return crossingPit;
    }

    /**
     * change current Player, if last stone moved to currentPlayer large pit, it must play again
     *
     * @param game           the game
     * @param currentPlayer  currentPlayer
     * @param opponentPlayer opponentPlayer
     * @param crossingPit    crossingPit
     */
    private void changeCurrentPlayer(Game game, Player currentPlayer, Player opponentPlayer, Integer crossingPit) {
        log.debug("Checking And Changing Current Player for gameId {}", game.getId());
        game.setCurrentPlayer(currentPlayer.getLargePit() == crossingPit ? currentPlayer : opponentPlayer);

        log.debug("Set Current Player for gameId {}", game.getId());
    }

    /**
     * The game is over as soon as one of the sides runs out of stones. The player who
     * still has stones in his pits keeps them and puts them in his big pit.
     *
     * @param game           current Game
     * @param currentPlayer  current Player
     * @param opponentPlayer opponentPlayer
     */
    private void checkGameEnds(Game game, Player currentPlayer, Player opponentPlayer) {
        log.debug("Check Game Ends for game {}", game.getId());

        boolean gameOver = isGameOver(game, currentPlayer, opponentPlayer);
        if (gameOver) {
            log.debug("Game Finish for game {}", game.getId());
            moveRemainedStonesToLargePit(game, currentPlayer);
            moveRemainedStonesToLargePit(game, opponentPlayer);
            changeGameState(game);
        }
    }

    /**
     * This method is used to find equivalent pit, if the player move to an empty pit as they last movement then
     * both this stone and all opponent pit's stones move to their LargePit
     *
     * @param game             current game playing
     * @param crossingPitIndex it has last pit index from last movement
     * @param currentPlayer    current player
     */
    private void capturingStones(Game game, Integer crossingPitIndex, Player currentPlayer) {
        log.debug("finding equivalent pit for game {} and pit {}", game.getId(), crossingPitIndex);

        if (game.getStatus().get(crossingPitIndex) == 1 && currentPlayer.getPits().contains(crossingPitIndex)) {
            log.debug("check equivalent pit has done and it was true.");

            // Find the opponent's equivalent pit
            int opponentPlayerPit = Player.LAST_PIT_INDEX - crossingPitIndex;

            int opponentStones = game.getStatus().get(opponentPlayerPit);

            if (opponentStones != 0) {
                // Move all stones to the current player LargePit
                game.getStatus().computeIfPresent(currentPlayer.getLargePit(), (key, value) -> value + opponentStones + 1);

                game.getStatus().replace(opponentPlayerPit, 0);
                game.getStatus().replace(crossingPitIndex, 0);
                log.debug("Move stones to LargePit and clear current pits");
            }
        }
    }

    /**
     * checking if one of the sides runs out of stones
     *
     * @param game         A game
     * @param currentPlayer currentPlayer
     * @param opponentPlayer opponentPlayer
     * @return boolean
     */
    private boolean isGameOver(Game game, Player currentPlayer, Player opponentPlayer) {
        return currentPlayer.getPits().stream().map(game.getStatus()::get).allMatch(stones -> stones == 0)
                || opponentPlayer.getPits().stream().map(game.getStatus()::get).allMatch(stones -> stones == 0);
    }

    /**
     * finding which player has more stones
     *
     * @param game a game
     */
    private void changeGameState(Game game) {
        if (game.getStatus().get(PLAYER_ONE.getLargePit()) > game.getStatus().get(PLAYER_TWO.getLargePit())) {
            game.setGameState(GameState.PLAYER_ONE_WIN);
        } else if (game.getStatus().get(PLAYER_ONE.getLargePit()) < game.getStatus().get(PLAYER_TWO.getLargePit())) {
            game.setGameState(GameState.PLAYER_TWO_WIN);
        } else if (game.getStatus().get(PLAYER_ONE.getLargePit()).equals(game.getStatus().get(PLAYER_TWO.getLargePit()))) {
            game.setGameState(GameState.THE_GAME_EQUALIZED);
        }
    }

    /**
     * This method move all player's stones to the their LargePir.
     *
     * @param game   current Game
     * @param player current Player
     */
    private void moveRemainedStonesToLargePit(Game game, Player player) {
        log.debug("Move remaining stones for game {}", game.getId());

        player.getPits().forEach(pit -> {
                    Integer pitStone = game.getStatus().get(pit);
                    game.getStatus().computeIfPresent(player.getLargePit(), (key, value) -> pitStone + value);
                    game.getStatus().replace(pit, Player.EMPTY_STONE);
                }
        );
    }

    /**
     * To get a saved game
     *
     * @param gameId required game's identifier
     * @return a game
     * @throws com.bol.mancala.exception.GameNotFoundException if can not find a game
     */
    @Override
    public Game getGameStatus(Integer gameId) {
        log.debug("get current game status with id {}", gameId);

        Optional<Game> optionalGame = gameRepository.findById(gameId);
        log.debug("Game not found with id {}", gameId);

        if (!optionalGame.isPresent())
            throw new GameNotFoundException("Game Not Found.");

        log.debug("A game has been loaded with id {}", gameId);

        return optionalGame.get();
    }

}
