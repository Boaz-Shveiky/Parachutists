package com.example.savetheparachutists;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
/*
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
*/
public class MainActivity extends AppCompatActivity {

    Button startGameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startGameBtn = (Button)findViewById(R.id.start_game);
    }

    public void startGameButtonClicked(View view){
        switchToGameScreen();
        Game game = new Game(this);
        game.start();
    }

    private void switchToGameScreen(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

}
