package com.example.savetheparachutists;

//Referee calculates the scores according to the player performance.
//Current policy: Catched a parachuter: +10 points. Missed a parachuter: -1 livePoints
public class GameReferee {

    public Result judge(PerformanceEvent performance) {

        Result result = new Result();

        if(performance.isCatched()) result.setPoints(10);

        else result.setLivePoints(-1);

        return result;
    }

}
