package com.bol.mancala.exception;

/**
 * To throw when invalid or illegal move has been requested by the user
 *
 * @author Abbas
 */
public class IllegalMoveException extends RuntimeException{

    /**
     * Constructor
     *
     * @param message given message
     */
    public IllegalMoveException(String message) {
        super(message);
    }

}
