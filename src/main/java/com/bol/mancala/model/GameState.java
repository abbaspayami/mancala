package com.bol.mancala.model;

/**
 * Indicate a game state, it is CREATED for just new created game,
 * while playing a game the state is PLAYING,
 * and it will be FINISHED after finishing a game
 *
 * @author Abbas
 */
public enum GameState {

    CREATED,
    PLAYING,
    PLAYER_ONE_WIN,
    PLAYER_TWO_WIN,
    THE_GAME_EQUALIZED

}
