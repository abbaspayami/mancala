package com.bol.mancala.exception;

/**
 * To throw when a move requested on a finished game
 *
 * @author Abbas
 */
public class FinishedGameMoveException extends RuntimeException{

    /**
     * Constructor
     *
     * @param message given message
     */
    public FinishedGameMoveException(String message) {
        super(message);
    }

}
