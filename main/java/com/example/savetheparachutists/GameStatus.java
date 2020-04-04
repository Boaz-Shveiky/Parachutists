package com.example.savetheparachutists;

import java.io.Serializable;

//Game scores and maybe some other data about the current game status, stored in the model layer.
public class GameStatus implements Serializable {
    private int points;
    private int livePoints;

    public GameStatus() {

    }

    public int getPoints() {
        return points;
    }

    public int getLivePoints() {
        return livePoints;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setLivePoints(int livePoints) {
        this.livePoints = livePoints;
    }




}
