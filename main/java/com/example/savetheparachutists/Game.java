package com.example.savetheparachutists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;


//This class is responsible for setting up the game layers and bind them
public class Game {

    private  GameModel model;
    private GameController controller;
    //private GameDisplay display
    Context context;

    public Game(Context context) {
        model = new GameModel();
        controller = new GameController();
        //display = new GameDisplay();
        controller.setModel(model);
        this.context = context;
        controller.setMessagesReceiver(context);
    }


    public void start() {
        //Controller runs on the backround, GameActivity (display) runs on UI thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                controller.start();
            }
        }).start();
    }

    public void stop() {
        controller.stop();
    }


}

