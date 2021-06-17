package com.bol.mancala.service;


import com.bol.mancala.model.Game;

/**
 * Service Layer interface, introduces business logic
 *
 * @author Abbas
 */
public interface GameService {

    /**
     * To create new game
     *
     * @return a game
     */
    Game start();

    /**
     * move stones from specific pitIndex
     *
     * @param gameId   to find a game
     * @param pitIndex indicate which pit selected
     * @return to show a Game current status
     */
    Game move(Integer gameId, Integer pitIndex);


    /**
     * To get a saved game
     *
     * @param gameId to find a game
     * @return current status
     */
    Game getGameStatus(Integer gameId);
}
