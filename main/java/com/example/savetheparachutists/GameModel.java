package com.example.savetheparachutists;

import java.util.Map;

public class GameModel {
    private GameStatus status;

    public GameModel() {

        status = new GameStatus();
    }

    public GameStatus getStatus() {
        return status;
    }
}
