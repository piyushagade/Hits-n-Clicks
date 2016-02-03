package com.jp.cowsnbulls;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.util.Random;

public class LandingActivity extends Activity {
    String[] bg = {"#ef5350","#ba68c8","#29b6f6","#43a047","#9ccc65","#ffa000","#8d6e63","#ff6701","#546e7a"};
    int bg_index, first_time;
    RelativeLayout rl_body;
    TextView no_games, mpg, leastmoves;
    SharedPreferences sp;
    private  static final String PREFS_FILE = "com.jp.cowsnbulls.preferences";
    SharedPreferences.Editor ed;

    MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.content_landing);

//        music = MediaPlayer.create(LandingActivity.this, R.raw.b);
//        music.setLooping(true);
//        music.start();

        rl_body = (RelativeLayout) findViewById(R.id.rl_body);

        Button new_game = (Button) findViewById(R.id.new_game);

        no_games = (TextView) findViewById(R.id.no_games);
        mpg = (TextView) findViewById(R.id.mpg);
        leastmoves = (TextView) findViewById(R.id.leastmoves);

        sp = getSharedPreferences(PREFS_FILE, 0);
//        sp =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ed = sp.edit();

        int d1 = sp.getInt("highscore", 10000);
        float d2 = sp.getFloat("MPG", 0);
        int d3 = sp.getInt("GAMES", 0);

        if(d1 == 10000) d1 = 0;
        if(d2 == Float.POSITIVE_INFINITY) d2 = 0;
        if(d3 == 0) d3 = 0;

        int d4 = Math.round(d2);

        leastmoves.setText(Integer.toString(d1));
        mpg.setText(Integer.toString(d4));
        no_games.setText(Integer.toString(d3));


        // Random Digits Generator
        Random r = new Random();

        // Random Background Color
        bg_index=r.nextInt(8);
        rl_body.setBackgroundColor(Color.parseColor(bg[bg_index]));


        new_game.setTextColor(Color.parseColor(bg[bg_index]));
        new_game.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(LandingActivity.this,GameActivity.class);
                startActivity(i);
            }

        });
    }

    @Override
    protected void onResume() {
        // Set Background Music
        music = MediaPlayer.create(LandingActivity.this, R.raw.b);
        music.setLooping(true);
        music.start();
        super.onResume();

        // Geet Stats
        sp = getSharedPreferences(PREFS_FILE, 0);
        ed = sp.edit();

        int d1 = sp.getInt("highscore", 10000);
        float d2 = sp.getFloat("MPG", 0);
        int d3 = sp.getInt("GAMES", 0);

        if(d1 == 10000) d1 = 0;
        if(d2 == Float.POSITIVE_INFINITY) d2 = 0;
        if(d3 == 0) d3 = 0;

        int d4 = Math.round(d2);

        leastmoves.setText(Integer.toString(d1));
        leastmoves.setTextColor(Color.parseColor(bg[bg_index]));
        mpg.setText(Integer.toString(d4));
        mpg.setTextColor(Color.parseColor(bg[bg_index]));
        no_games.setText(Integer.toString(d3));
        no_games.setTextColor(Color.parseColor(bg[bg_index]));

    }

    @Override
    protected void onPause() {
        super.onPause();
        music.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        music.release();
    }
}
