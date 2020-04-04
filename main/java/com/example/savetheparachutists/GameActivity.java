package com.example.savetheparachutists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;


public class GameActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private ImageView airPlaneView;
    private ImageView boatView;
    private TextView livePointsView;
    private TextView pointsView;

    private static final int START_PARACHUTIST_THREAD = 1;
    private static final int PARACHUTING_FINISHED = 2;

    //Screen size
    private  int screenWitdh;
    private int screenHeight;

    private boolean gameFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initializeFields();
        setMessagesReceiver();

        movePlaneContinuously();
        addBoatFunctionality();
    }

    private void initializeFields(){

        gameFinished = false;

        layout = (ConstraintLayout)findViewById(R.id.constraint_layout);
        airPlaneView = (ImageView)findViewById(R.id.airplane);
        boatView = (ImageView)findViewById(R.id.boat);
        livePointsView = (TextView)findViewById(R.id.lifes);
        pointsView = (TextView)findViewById(R.id.points);
        pointsView.setTextSize(30);
        livePointsView.setTextSize(30);

        //Get screen size
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWitdh = size.x;
        screenHeight = size.y;
    }

    //Register a BroadcastRecevier in order to get messages from GameController
    private void setMessagesReceiver(){
        IntentFilter filter = new IntentFilter("MAKE_SCHEDULED_ACTION");
        filter.addAction("UPDATE_DATA");
        filter.addAction("GAME_FINISHED");
        registerReceiver(broadcastReceiver, filter);
    }

    private void movePlaneContinuously(){

        //Locate the airplane in sky height
        airPlaneView.setY(airPlaneView.getHeight() + 100f);
        airPlaneView.setX(screenWitdh + airPlaneView.getWidth());

        //Move the plane from right to left continuously
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!gameFinished){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(airPlaneView.getX() + airPlaneView.getWidth() < 0){
                        airPlaneView.setX(screenWitdh-(airPlaneView.getWidth()/3));
                    }
                    else{
                        airPlaneView.setX(airPlaneView.getX() - 10);
                    }
                }
            }
        }).start();
    }

    //Currently there is no use in ScheduledEvent.
    //Scheduled event could contains data(e.g. specifications for how to drop the parachutist)
    public void dropParachutist(ScheduledEvent event) {

        ImageView parachutistView = new ImageView(GameActivity.this);
        parachutistView.setBackgroundResource(R.drawable.parachutist);

        //Locate the parachutist as if the airplane drop him
        parachutistView.setX(airPlaneView.getX());
        parachutistView.setY(airPlaneView.getY() + 300f);

        layout.addView(parachutistView);

        ParachutistThread parachutistThread = new ParachutistThread(parachutistView);
        new Thread(parachutistThread).start();
    }


    //This method checks if the boat caught the parachutist
    private void parachutingCompleted(ImageView parachutistView){
        boolean catched;
        ImageView parachutist = (ImageView) parachutistView;
        float position = parachutist.getX();
        final String toToast;
        if((position > boatView.getX() - (parachutist.getWidth()/2))
                & (position < boatView.getX()+boatView.getWidth()-(parachutist.getWidth()/2))){

            catched = true;
            toToast = "Yay!";
        }
        else{
            catched = false;
            toToast = "Sigh!";
        }

        GameActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),toToast, Toast.LENGTH_SHORT).show();
            }
        });

        //Report to the controller
        reportPerformance(catched);
    }

    //Reports performances to controller
    private void reportPerformance(boolean catched){
        PerformanceEvent event = new PerformanceEvent(this, catched);
        Intent intent = new Intent();
        intent.setAction("ACTION_FINISHED");
        Bundle args = new Bundle();
        args.putSerializable("PERFORMANCE", (Serializable)event);
        intent.putExtra("DATA", args);

        //The BroadcastReceiver in GameController gets this message
        //Any other BroadcastReceiver which listen to ACTION_FINISHED action will receive this message.
        sendBroadcast(intent);
    }

    public void updateDataOnScreen(GameStatusEvent message){
        //extract scores from GameStatusEvent and update on screen
        int points = message.getStatus().getPoints();
        int livePoints = message.getStatus().getLivePoints();

        pointsView.setText(points + " points");
        livePointsView.setText("You have " + livePoints + " left");
    }

    public void finishGame(GameFinishedEvent message){
        Toast.makeText(getApplicationContext(),"Game Over", Toast.LENGTH_LONG).show();
        gameFinished = true;
        swithToMainScreen();
    }

    private void swithToMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void addBoatFunctionality(){

        boatView.setY(screenHeight-boatView.getHeight()-400f);

        layout.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float position = event.getX();
                if(position > boatView.getX()){
                    moveBoatToRight();
                }
                else{
                    moveBoatToLeft();
                }
                return false;
            }
        });
    }

    private void moveBoatToRight(){
        boatView.setX(boatView.getX() + 50);
    }

    private void moveBoatToLeft(){
        boatView.setX(boatView.getX() - 50);

    }

    private class ParachutistThread implements Runnable {
        private ImageView parachutistView;

        public ParachutistThread(ImageView parachutistView){
            this.parachutistView = parachutistView;
        }
        @Override
        public void run() {

            //Make the parachutist fall down
            while(parachutistView.getY() < screenHeight){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                parachutistView.setY(parachutistView.getY()+400);
            }
            //Prachutist finished dropping
            parachutingCompleted(parachutistView);
        }
    };




    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("I", "in onReceive in GameActivity, action is: " + intent.getAction());
            if(intent.getAction().equals("MAKE_SCHEDULED_ACTION")){
                //Received a request to drop a parachutist
                Bundle args = intent.getBundleExtra("DATA");
                ScheduledEvent scheduledParachutingEvent = (ScheduledEvent) args.getSerializable("SCHEDULED");
                dropParachutist(scheduledParachutingEvent);
            }
            else if(intent.getAction().equals("UPDATE_DATA")){
                Bundle args = intent.getBundleExtra("DATA");
                GameStatusEvent gameStatusEvent = (GameStatusEvent) args.getSerializable("GAME_STATUS");
                updateDataOnScreen(gameStatusEvent);
            }
            else if(intent.getAction().equals("GAME_FINISHED")){
                Bundle args = intent.getBundleExtra("DATA");
                GameFinishedEvent gameFinishedEvent = (GameFinishedEvent) args.getSerializable("FINISHED");
                finishGame(gameFinishedEvent);
            }

        }
    };
}
