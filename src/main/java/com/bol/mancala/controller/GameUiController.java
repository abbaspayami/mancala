package com.bol.mancala.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Loading ui Mancala Game
 *
 * @author Abbas
 */
@Controller
public class GameUiController {

    /**
     * Loading ui Mancala Game
     *
     * @return index.html
     */
    @GetMapping
    public String uiStart() {
        return "index";
    }

}
