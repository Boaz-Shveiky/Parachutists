package com.example.savetheparachutists;

import java.io.Serializable;

public class GameStatusEvent extends Controller_Display_Communication_Event implements Serializable {

    private GameStatus status;

    public GameStatusEvent(Object source, GameStatus status) {
        super(source);
        this.status = status;
    }

    public GameStatus getStatus() {
        return status;
    }

}
