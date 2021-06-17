package com.bol.mancala.exception;

/**
 * To throw if requested game does not exists in database
 *
 * @author Abbas
 */
public class GameNotFoundException extends RuntimeException{

    /**
     * constructor
     *
     * @param message given message
     */
    public GameNotFoundException(String message) {
        super(message);
    }

}
