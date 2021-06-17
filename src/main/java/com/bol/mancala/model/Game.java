package com.bol.mancala.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Map;

/**
 * To store a game data in database
 *
 * @author Abbas
 */

@Data
@Entity
@Table(name = "Game")
public class Game {

    /**
     * An auto generated and sequences identifier for an entity, its integer value increase atomically for creating
     * a new instance of an entity
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    /**
     * indicates the game current player
     */
    @Enumerated(EnumType.STRING)
    private Player currentPlayer;

    /**
     * indicate the game current state
     */
    @Enumerated(EnumType.STRING)
    private GameState gameState;

    /**
     * stores all pits status
     */
    @ElementCollection
    @MapKeyColumn(name = "PIT")
    private Map<Integer, Integer> status;



}
