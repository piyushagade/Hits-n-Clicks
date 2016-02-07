package com.jp.cowsnbulls;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Random;

public class LandingActivity extends Activity {
    String[] bg = {"#ef5350","#ba68c8","#29b6f6","#43a047","#9ccc65","#ffa000","#8d6e63","#ff6701","#546e7a"};
    int bg_index;
    RelativeLayout rl_body, rl_splash;
    ImageView rl_logo;
    Boolean first_time;

    TextView no_games, mpg, leastmoves;
    TextView title, new_game, objective, layout, input, layout_selected, input_selected, theme_selected, quit;

    int width, height;

    Typewriter tw_bg, tw_stats, load_progress;

    SharedPreferences sp_stats, sp_prefs;
    private  static final String STATS_FILE = "com.jp.cowsnbulls.stats";
    private  static final String PREFS_FILE = "com.jp.cowsnbulls.preferences";
    SharedPreferences.Editor ed, ed_pref;

    String temp_inputmethod="";
    int temp_layout;
    int layoutID;
    String inputMethod;
    boolean darkTheme = false;

    ImageView splash_logo;
    RelativeLayout splash_rl;

    int accent = Color.parseColor(bg[bg_index]);

    MediaPlayer music;
    private boolean restartPending = false;
    private String l_selected = "";
    boolean t_selected = false;

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.content_landing);




        final HorizontalScrollView title_sv = (HorizontalScrollView) findViewById(R.id.title_sv);
        TextView title = (TextView) findViewById(R.id.title);
        TextView new_game = (TextView) findViewById(R.id.new_game);
        TextView objective = (TextView) findViewById(R.id.objective);
        TextView report = (TextView) findViewById(R.id.report);
        TextView settings = (TextView) findViewById(R.id.settings);
        TextView training = (TextView) findViewById(R.id.training);
        TextView about = (TextView) findViewById(R.id.about);
        TextView quit = (TextView) findViewById(R.id.quit);

        //Get screen Dimensions
        width = this.getResources().getDisplayMetrics().widthPixels;
        height = this.getResources().getDisplayMetrics().heightPixels;

        // Random Digits Generator
        Random r = new Random();

        // Random Background Color
        bg_index=r.nextInt(8);

        //Animations
        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        //Setup Background
        rl_body = (RelativeLayout) findViewById(R.id.rl_body);
        rl_body.setBackgroundColor(Color.parseColor("#FE000000"));

        // Get Shared Preferences Preferences
        sp_stats = getSharedPreferences(STATS_FILE, 0);
        sp_prefs = getSharedPreferences(PREFS_FILE, 0);

        ed = sp_stats.edit();
        ed_pref = sp_prefs.edit();

        layoutID=sp_prefs.getInt("layoutID", 2);   // Default set to Bottom Console
        inputMethod=sp_prefs.getString("inputMethod", "keypad");  // Default set to Keypad
        darkTheme=sp_prefs.getBoolean("themeID", true);  // Default set to dark theme

        //Splash screen
        rl_splash = (RelativeLayout)findViewById(R.id.rl_splash);
        splash_logo = (ImageView) findViewById(R.id.splash_logo);

        splash_logo.startAnimation(animationFadeIn);
        rl_splash.startAnimation(animationFadeOut);
        animationFadeOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splash_logo.setVisibility(View.GONE);
                rl_splash.setVisibility(View.GONE);
            }
        });


        //Title Scrollview Animate
        title_sv.postDelayed(new Runnable() {
            public void run() {
                title_sv.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            }
        }, 7400);
        title_sv.postDelayed(new Runnable() {
            public void run() {
                title_sv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 9000);

        //Load Progress Animation
        load_progress = (Typewriter) findViewById(R.id.load_progress);
        load_progress.setCharacterDelay(440);
        load_progress.animateText(".....");

        //Typewriter animations
        tw_bg = (Typewriter) findViewById(R.id.tw_bg);
        tw_bg.setTextSize(8);
        tw_bg.setAlpha(0.4f);
        tw_bg.setTextColor(Color.parseColor(bg[bg_index]));

        tw_bg.setCharacterDelay(1);
        String t = getResources().getString(R.string.tw_bg);
        tw_bg.animateText(t);


        // OnClick Listeners

        // Quit
        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }

        });

        // Mission Briefing
        objective.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Dialog brief_dialog = new Dialog(LandingActivity.this);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(brief_dialog.getWindow().getAttributes());

                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                brief_dialog.setContentView(R.layout.brief_dialog);
                brief_dialog.show();

                Window window = brief_dialog.getWindow();
                window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
                brief_dialog.setCancelable(true);
                brief_dialog.getWindow().setAttributes(lp);

                TextView brief_title = (TextView)brief_dialog.findViewById(R.id.brief_title);
                TextView brief_back = (TextView)brief_dialog.findViewById(R.id.brief_back);
                brief_title.setTextColor(Color.parseColor(bg[bg_index]));
                final Typewriter brief_text = (Typewriter)brief_dialog.findViewById(R.id.brief_text);
                final ScrollView brief_sv = (ScrollView) brief_dialog.findViewById(R.id.brief_sv);


                String st = getResources().getString(R.string.mission_brief);
                brief_text.setCharacterDelay(20);
                brief_text.animateText(st);
                brief_back.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        brief_dialog.cancel();
                    }

                });
            }

        });

        //Report
        report.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Dialog report_dialog = new Dialog(LandingActivity.this);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(report_dialog.getWindow().getAttributes());

                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                report_dialog.setContentView(R.layout.report_dialog);
                report_dialog.show();

                Window window = report_dialog.getWindow();
                window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
                report_dialog.setCancelable(true);
                report_dialog.getWindow().setAttributes(lp);

                TextView report_title = (TextView)report_dialog.findViewById(R.id.report_title);
                TextView report_back = (TextView)report_dialog.findViewById(R.id.report_back);
                report_title.setTextColor(Color.parseColor(bg[bg_index]));
                Typewriter report_text = (Typewriter)report_dialog.findViewById(R.id.report_text);

                // Get Stats Preferences
                int d1 = sp_stats.getInt("highscore", 10000);
                float d2 = sp_stats.getFloat("MPG", 0);
                int d3 = sp_stats.getInt("GAMES", 0);

                String history = sp_stats.getString("HISTORY", "");
                if(!history.equals(""))
                    history = "\n\nHistory:\n" + history;

                if(d1 == 10000) d1 = 0;
                if(d2 == Float.POSITIVE_INFINITY) d2 = 0;
                if(d3 == 0) d3 = 0;

                int d4 = Math.round(d2);

                String st = "File #: 0310848664523\nAlias: Agent B\nReal name: CLASSIFIED\nAge: 26\n\nHandler: Agent O\n" +
                        "Nationality: CLASSIFIED\n\n# of Missions: "+ Integer.toString(d3)+ "\n# of Moves per Mission: " + Integer.toString(d4) +
                        "\nBest mission: " + Integer.toString(d1) + " moves"+history;

                report_text.setCharacterDelay(5);
                report_text.animateText(st);
                report_back.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        report_dialog.cancel();
                    }

                });
            }

        });

        //Training
        training.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Dialog training_dialog = new Dialog(LandingActivity.this);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(training_dialog.getWindow().getAttributes());

                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                training_dialog.setContentView(R.layout.training_dialog);
                training_dialog.show();

                Window window = training_dialog.getWindow();
                window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
                training_dialog.setCancelable(true);
                training_dialog.getWindow().setAttributes(lp);

                TextView training_title = (TextView)training_dialog.findViewById(R.id.training_title);
                TextView training_back = (TextView)training_dialog.findViewById(R.id.training_back);
                training_title.setTextColor(Color.parseColor(bg[bg_index]));
                final Typewriter training_text = (Typewriter)training_dialog.findViewById(R.id.training_text);
                final ScrollView training_sv = (ScrollView) training_dialog.findViewById(R.id.training_sv);


                String st = getResources().getString(R.string.training);

                training_text.setCharacterDelay(5);
                training_text.animateText(st);

                training_back.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        training_dialog.cancel();
                    }

                });
            }

        });

        //About
        about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Dialog about_dialog = new Dialog(LandingActivity.this);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(about_dialog.getWindow().getAttributes());

                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                about_dialog.setContentView(R.layout.about_dialog);
                about_dialog.show();

                Window window = about_dialog.getWindow();
                window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
                about_dialog.setCancelable(true);
                about_dialog.getWindow().setAttributes(lp);

                TextView about_title = (TextView)about_dialog.findViewById(R.id.about_title);
                TextView about_back = (TextView)about_dialog.findViewById(R.id.about_back);
                about_title.setTextColor(Color.parseColor(bg[bg_index]));
                Typewriter about_text = (Typewriter)about_dialog.findViewById(R.id.about_text);
                final ScrollView about_sv = (ScrollView) about_dialog.findViewById(R.id.about_sv);


                String st = getResources().getString(R.string.about);

                about_text.setCharacterDelay(20);
                about_text.animateText(st);


                about_back.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        about_dialog.cancel();
                    }

                });
            }

        });

        //Settings
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Dialog settings_dialog = new Dialog(LandingActivity.this);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(settings_dialog.getWindow().getAttributes());

                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                settings_dialog.setContentView(R.layout.settings_dialog);
                settings_dialog.show();

                Window window = settings_dialog.getWindow();
                window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
                settings_dialog.setCancelable(true);
                settings_dialog.getWindow().setAttributes(lp);

                TextView settings_title = (TextView)settings_dialog.findViewById(R.id.settings_title);
                TextView settings_back = (TextView)settings_dialog.findViewById(R.id.settings_back);


                final TextView layout = (TextView) settings_dialog.findViewById(R.id.layout);
                final TextView input = (TextView) settings_dialog.findViewById(R.id.input);
                final TextView theme = (TextView) settings_dialog.findViewById(R.id.theme);

                final TextView layout_selected = (TextView) settings_dialog.findViewById(R.id.layout_selected);
                final TextView input_selected = (TextView) settings_dialog.findViewById(R.id.input_selected);
                final TextView theme_selected = (TextView) settings_dialog.findViewById(R.id.theme_selected);

                //Read SharedPreferences File
                sp_stats = getSharedPreferences(STATS_FILE, 0);
                sp_prefs = getSharedPreferences(PREFS_FILE, 0);
                ed = sp_stats.edit();
                ed_pref = sp_prefs.edit();

                String layouts[]={"top console", "bottom console"};
                String themes[]={"light theme", "dark theme"};
                int themeID = 0;
                if(!sp_prefs.getBoolean("themeID", true)) themeID = 0;
                else themeID = 1;

                //Set current options to settings texts
                input_selected.setText(sp_prefs.getString("inputMethod", "keypad"));    // Default set to Keypad
                layout_selected.setText(layouts[sp_prefs.getInt("layoutID", 2)-1]);
                theme_selected.setText(themes[themeID]);

                //Colorify Menu
                settings_title.setTextColor(Color.parseColor(bg[bg_index]));
                theme_selected.setTextColor(Color.parseColor(bg[bg_index]));
                layout_selected.setTextColor(Color.parseColor(bg[bg_index]));
                input_selected.setTextColor(Color.parseColor(bg[bg_index]));


                settings_back.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        settings_dialog.cancel();
                    }

                });

                TextView[] la = {layout,layout_selected};
                for(TextView p: la)
                    p.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            layoutID = sp_prefs.getInt("layoutID", 2);  // Default set to Bottom Console
                            String sel = "";
                            String layouts[]={"top console", "bottom console"};

                            if(layoutID == 2){
                                ed_pref.putInt("layoutID", 1);
                                sel = "top console";

                            }
                            else if(layoutID==1){
                                ed_pref.putInt("layoutID", 2);
                                sel = "bottom console";
                            }
                            layout_selected.setText(sel);
                            ed_pref.commit();
                            restartPending = true;

                        }

                    });

                TextView[] ia = {input,input_selected};

                for(TextView i: ia)
                    i.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            inputMethod = sp_prefs.getString("inputMethod", "keypad"); // Default set to keypad
                            String sel = "";
                            if(inputMethod.equals("keyboard")){
                                ed_pref.putString("inputMethod", "slider");
                                sel = "slider";
                            }
                            else  if(inputMethod.equals("slider")){
                                ed_pref.putString("inputMethod", "keypad");
                                sel = "keypad";
                            }
                            else  if(inputMethod.equals("keypad")){
                                ed_pref.putString("inputMethod", "keyboard");
                                sel = "keyboard";
                            }

                            input_selected.setText(sel);
                            ed_pref.commit();

                            restartPending = true;
                        }

                    });

                TextView[] ta = {theme,theme_selected};
                for(TextView n: ta)
                    n.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            darkTheme = sp_prefs.getBoolean("themeID", true); // Default set to dark theme
                            String sel = "";
                            if(!darkTheme){
                                ed_pref.putBoolean("themeID", true);
                                sel = "dark theme";
                            }
                            else  if(darkTheme){
                                ed_pref.putBoolean("themeID", false);
                                sel = "light theme";
                            }

                            theme_selected.setText(sel);

                            ed_pref.commit();
                            restartPending = true;
                        }

                    });

            }

        });

        //Typewriter Animation
        tw_stats = (Typewriter) findViewById(R.id.tw_stats);
        tw_stats.setTextColor(Color.parseColor(bg[bg_index]));

        tw_stats.setCharacterDelay(1);

        notifTextAnim("INCOMING TRANSMISSION", 0, 40, 0);
        notifTextAnim("Welcome Agent B!", 2500, 40, 0);
        notifTextAnim("The world is on the brink of another world war.", 5500, 40, 0);
        notifTextAnim("We need you in Prague ASAP!", 10500, 40, 0);
        notifTextAnim("While you get there, check the Mission Briefing.", 17500, 50, 0);
        notifTextAnim("Good Luck, Agent B!", 24000, 40, 0);
        notifTextAnim("NIRA HQ out.", 26000, 40, 0);
        notifTextAnim("CONNECTION TERMINATED", 29000, 40, 1);




        //Colorify Menu
        title.setTextColor(Color.parseColor(bg[bg_index]));

        new_game.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(LandingActivity.this,GameActivity.class);
                startActivity(i);
            }

        });
    }


    private void animateTv(TextView t, int repeatCount) {
        Animation blinkAnim = new AlphaAnimation(1, 0);
        blinkAnim.setDuration(800);
        blinkAnim.setInterpolator(new LinearInterpolator());
        blinkAnim.setRepeatCount(repeatCount);
        blinkAnim.setRepeatMode(Animation.REVERSE);
        t.startAnimation(blinkAnim);
    }

    private void notifTextAnim(final String text, int time, int charDelay, int mode){
        final int m = mode;
        final int cd = charDelay;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // After time ms
                tw_stats.setCharacterDelay(cd);
                tw_stats.animateText(text);
                if(m==1)animateTv(tw_stats, 500);
                else tw_stats.clearAnimation();
            }
        }, time);
    }

    public void makeSnack(String t){
        View v = findViewById(R.id.rl_body);
        Snackbar.make(v, t, Snackbar.LENGTH_LONG).setAction("Action", null).show();


    }

    @Override
    protected void onResume() {
        // Set Background Music
        music = MediaPlayer.create(LandingActivity.this, R.raw.b);
        music.setLooping(true);
        music.start();
        super.onResume();

        // Get Stats
        sp_stats = getSharedPreferences(STATS_FILE, 0);
        ed = sp_stats.edit();

        int d1 = sp_stats.getInt("highscore", 10000);
        float d2 = sp_stats.getFloat("MPG", 0);
        int d3 = sp_stats.getInt("GAMES", 0);

        if(d1 == 10000) d1 = 0;
        if(d2 == Float.POSITIVE_INFINITY) d2 = 0;
        if(d3 == 0) d3 = 0;

        int d4 = Math.round(d2);

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
