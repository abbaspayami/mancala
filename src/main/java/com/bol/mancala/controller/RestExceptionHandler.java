package com.bol.mancala.controller;

import com.bol.mancala.exception.FinishedGameMoveException;
import com.bol.mancala.exception.GameNotFoundException;
import com.bol.mancala.exception.IllegalMoveException;
import com.bol.mancala.view.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handle exceptions, and return ExceptionDto to present exceptions with the proper format
 *
 * @author Abbas
 */
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Handle IllegalMoveException and FinishedGameMoveException
     *
     * @param ex catch Exception
     * @return An ExceptionDto
     */
    @ExceptionHandler({IllegalMoveException.class, FinishedGameMoveException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleIllegalMove(Exception ex) {
        return new ExceptionDto(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles GameNotFoundException
     *
     * @param ex catch Exception
     * @return An ExceptionDto
     */
    @ExceptionHandler(GameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleGameNotFound(Exception ex) {
        return new ExceptionDto(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handle other unhandled exceptions
     *
     * @param ex catch Exception
     * @return An ExceptionDto
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleOthers(Exception ex) {
        return new ExceptionDto(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
