package com.example.savetheparachutists;

//This class describes what shoud be done due to the player performance, according to the referee decision.
public class Result {

    //amount of points the player earned/lost due to the relevant performance
    private int points;

    //amount of livePoints the player earned/lost due to the relevant performance
    private int livePoints;

    public Result() {
        points = 0;
        livePoints = 0;
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
