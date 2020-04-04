
package com.example.savetheparachutists;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

public class GameController {
    private GameModel model;
    private GameReferee referee;
    private Scheduler scheduler;
    private Context context;

    private final int POINTS_ON_START = 0;
    private final int LIVEPOINTS_ON_START = 3;

    public GameController() {
        scheduler = new Scheduler();
        referee = new GameReferee();
    }

    public void setModel(GameModel model) {
        if(model == null) {
            throw new IllegalArgumentException();
        }
        this.model = model;
    }

    //Controller receives messages from the displahy layer (GameActivity) by a BroadcastReceiver.
    //Currently expected to receive a message only when a parachutist finished parachuting
    public void setMessagesReceiver(Context context){
        this.context = context;
        IntentFilter filter = new IntentFilter("ACTION_FINISHED");
        //possible to listen to different types of messages;
        //filter.addAction("SOME OTHER ACTION");
        context.registerReceiver(broadcastReceiver, filter);
    }

    public void start() {
        if(scheduler.working){
            return;
        }

        resetModle();

        updateDataOnDisplay(model.getStatus());

        scheduler.startScheduling();
    }

    public void stop() {
        scheduler.stopScheduling();

        GameFinishedEvent event = new GameFinishedEvent(this);
        Intent intent = new Intent();
        intent.setAction("GAME_FINISHED");
        Bundle args = new Bundle();
        args.putSerializable("FINISHED", (Serializable)event);
        intent.putExtra("DATA", args);

        //The BroadcastReceiver in GameActivity gets this message
        //Any other BroadcastReceiver which listen to GAME_FINISHED action will receive this message.
        context.sendBroadcast(intent);

    }

    private void makeAnAction() {
        //In current version of the game we just want to drop a parachutist,
        //so there is no data to pass. hence, the event is empty.
        ScheduledEvent event = new ScheduledEvent(this);
        Intent intent = new Intent();
        intent.setAction("MAKE_SCHEDULED_ACTION");
        Bundle args = new Bundle();
        args.putSerializable("SCHEDULED", (Serializable)event);
        intent.putExtra("DATA", args);

        //The BroadcastReceiver in GameActivity gets this message
        //Any other BroadcastReceiver which listen to MAKE_SCHEDULED_ACTION action will receive this message.
        context.sendBroadcast(intent);
    }

    private void actionFinished(PerformanceEvent performance) {

        Result result = referee.judge(performance);

        updateStatus(result);

        updateDataOnDisplay(model.getStatus());

        if(gameFinished()){
            stop();
        }
    }

    private void updateStatus(Result result) {

        int currentPoints = model.getStatus().getPoints();
        int pointsToAdd = result.getPoints();
        model.getStatus().setPoints(currentPoints + pointsToAdd);

        int currentLivePoints = model.getStatus().getLivePoints();
        int livePointsToAdd = result.getLivePoints();
        model.getStatus().setLivePoints(currentLivePoints + livePointsToAdd);
    }

    private void updateDataOnDisplay(GameStatus data) {

        GameStatusEvent event = new GameStatusEvent(this, data);
        Intent intent = new Intent();
        intent.setAction("UPDATE_DATA");
        Bundle args = new Bundle();
        args.putSerializable("GAME_STATUS", (Serializable)event);
//--------------------------------------------------------------------
        //Todo: delete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//---------------------------------------------------------------------
        intent.putExtra("DATA", args);

        //The BroadcastReceiver in GameActivity gets this message.
        //Any other BroadcastReceiver which listen to UPDATE_DATA action will receive this message.
        context.sendBroadcast(intent);
    }

    private void resetModle() {
        model.getStatus().setPoints(POINTS_ON_START);
        model.getStatus().setLivePoints(LIVEPOINTS_ON_START);
    }

    private boolean gameFinished() {
        return model.getStatus().getLivePoints() == 0;
    }

    //The scheduler is responsible to schedule the actions.
    //Current policy: random intervals.
    private class Scheduler{
        private boolean working;

        public Scheduler(){
            working = false;
        }

        public void startScheduling(){
            working = true;
            while(working) {

                try {
                    Thread.sleep((int)  (Math.random()*3000) +5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Check again if keepWorking, for a case the game finished while sleeping
                if(working) makeAnAction();
            }
        }

        private void stopScheduling() {
            working = false;
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ACTION_FINISHED")){
                Bundle args = intent.getBundleExtra("DATA");
                PerformanceEvent perfomanceEvent = (PerformanceEvent) args.getSerializable("PERFORMANCE");
                actionFinished(perfomanceEvent);
            }
        }
    };
}
