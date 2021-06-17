package com.bol.mancala.controller;

import com.bol.mancala.service.GameService;
import com.bol.mancala.view.GameDto;
import com.bol.mancala.view.GameMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * mancala Rest endpoint
 * To create, move, and get the game current status
 *
 * @author Abbas
 */
@RestController
@Slf4j
@RequestMapping("/mancala")
@RequiredArgsConstructor
public class GameApiController {
    private final GameService gameService;
    private final GameMapper gameMapper;

    /**
     * start a new game
     *
     * @return new game
     */
    @ApiOperation(value = "Start New Game")
    @PostMapping(value = "/start")
    public ResponseEntity<GameDto> start() {
        return new ResponseEntity<>(gameMapper.map(gameService.start()), HttpStatus.CREATED);
    }

    /**
     * moving stones from selected pit
     *
     * @param gameId   find game by pitIndex
     * @param pitIndex moving stones from this pit
     * @return game current status
     */
    @ApiOperation(value = "Move stones from pits in the game")
    @PutMapping(value = "/move")
    public ResponseEntity<GameDto> move(@RequestParam("game") int gameId, @RequestParam("pit") int pitIndex) {
        log.debug("Request Move gameId {} and pitIndex {}", gameId, pitIndex);
        return new ResponseEntity<>(gameMapper.map(gameService.move(gameId, pitIndex)), HttpStatus.OK);
    }

    /**
     * indicates the current status
     *
     * @param gameId find game by gameId
     * @return current status
     */
    @ApiOperation(value = "Find a game by its Id")
    @GetMapping(value = "/gameStatus")
    public ResponseEntity<GameDto> getStatus(@RequestParam("game") int gameId) {
        log.debug("Request get Current the Game Status gameId {} : ", gameId);
        return new ResponseEntity<>(gameMapper.map(gameService.getGameStatus(gameId)), HttpStatus.OK);
    }

}
