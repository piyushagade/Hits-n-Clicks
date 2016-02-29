package com.jp.cowsnbulls;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jp.cowsnbulls.SimpleGestureFilter.SimpleGestureListener;

import java.util.Calendar;
import java.util.Random;

public class GameActivity extends Activity implements SimpleGestureListener{

	private static final CharSequence RESTART_TEXT = "INCOMING TRANSMISSION\n\nPlease reload to apply changes, press the 'reload' icon in the in-game menu." +
			"\nCAUTION: Reloading will restart the game." ;
	ScrollView sv;
	int tv_d = 1;
	TextView tv_d_1,tv_d_2,tv_d_3,tv_d_4;
	TextView p_1;

	int menu_ID;

	Typewriter tv_bulls, tw_bg;
	LinearLayout rl_feed;
	RelativeLayout rl_body, rl_bg;

	String temp_inputmethod="", temp_layout="";
	private String l_selected = "";
	boolean t_selected = false;

	ImageButton i_reload,i_scores, i_menu, i_mute, i_help;

	Random r;
	String[] rno=new String[4];
	String[] uip=new String[4];

	int bg_index;
	int feed_count=0;
	int bulls=0;
	int cows=0;

	String[] bg = {"#ef5350","#ba68c8","#29b6f6","#43a047","#9ccc65","#ffa000","#8d6e63","#ff6701","#546e7a"};

	boolean distinct=true, cont, notmute, restartPending = false;

	SharedPreferences sp_stats, sp_prefs;

	private  static final String STATS_FILE = "com.jp.cowsnbulls.stats";
	private  static final String PREFS_FILE = "com.jp.cowsnbulls.preferences";

	SharedPreferences.Editor ed, ed_pref;

	Button kp_0,kp_1,kp_2,kp_3,kp_4,kp_5,kp_6,kp_7,kp_8,kp_9,kp_decrypt;
	LinearLayout ll_kp;

	int accent;

	int leastMoves;
	TextView d_highscore;
	SeekBar sb;
	Dialog menu, report_dialog, training_dialog, rookie_training_dialog, review_dialog;
	Dialog brief_dialog;
	Button done;
	int no_games;
	float avg_mpg;
	MediaPlayer music;
	Animation blinkAnim;
	CardView console;

	//SharedPrefe Vars
	int layoutID, diffID;
	String inputMethod;
	boolean darkTheme = false;

	//Console inputs
	private RelativeLayout rl_input_4, rl_input_3, rl_input_5;

	String random_jargon[]= {"inject *ex.bch /hes","Access Denied","lst lan0 -verify",
			"remconn 13.48.33.75:3909", "netstat -t 00:24", "kill PID 2040",
			"brute force initiated", "Permission denied", "wait for callback", "security breach detected",
			"initiating security protocols", "system failure, rebooting.", "kill PID 4335 - force",
			"recieved beacon","warhead on track", "latitute:49.4412, longitude: 1.0740", "eta: 12 min",
			"system malfunction", "send beacon: 14.212.33.75:5509", "uplink status: [ok] [403kbps]"
	};

	RelativeLayout c1, c2, c3, c4;

	private SimpleGestureFilter detector;


	@Override
	public boolean dispatchTouchEvent(MotionEvent me){
		if(inputMethod.equals("slider"))
			detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}


	@Override
	public void onSwipe(int direction) {

		switch (direction) {

			case SimpleGestureFilter.SWIPE_RIGHT : if(tv_d == 4) tv_d = 0;
				activateTv(++tv_d, 1);
				break;
			case SimpleGestureFilter.SWIPE_LEFT : if(tv_d == 1) tv_d = 5;
				activateTv(--tv_d, 1);
				break;
			case SimpleGestureFilter.SWIPE_DOWN :
				break;
			case SimpleGestureFilter.SWIPE_UP :
				break;

		}
	}

	@Override
	public void onDoubleTap() {
		decrypt();
	}

	@Override
	protected void onResume() {
		// Set Background Music
		if(notmute) {
			music.start();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		music.pause();

		menu.show();
		defineMenu();
	}


	@Override
	public void onBackPressed() {
		menu.show();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		defineMenu();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);




		// Media Player Background Music
		music = MediaPlayer.create(GameActivity.this, R.raw.a);
		music.setLooping(true);

		//Set Accent Color
		// Random Digits Generator
		r = new Random();
		//Random bg_index
		bg_index=r.nextInt(8);
		accent = Color.parseColor(bg[bg_index]);

		//Read SharedPreferences
		sp_stats = getSharedPreferences(STATS_FILE, 0);
		sp_prefs = getSharedPreferences(PREFS_FILE, 0);

		ed = sp_stats.edit();
		ed_pref = sp_prefs.edit();

		// Get Shared Preferences information
		layoutID=sp_prefs.getInt("layoutID", 2);   // Default set to Bottom Console
		inputMethod=sp_prefs.getString("inputMethod", "keypad");  // Default set to Keypad
		darkTheme=sp_prefs.getBoolean("themeID", true);  // Default set to dark theme
		diffID=sp_prefs.getInt("diffID", 4);	//Difficulty level set to 3 by default

		// Set Layout
		int layoutResId = 0;
		if(layoutID==1) {
			if(inputMethod.equals("slider")) layoutResId = R.layout.game_layout_1_slider;
			if(inputMethod.equals("keyboard")) layoutResId = R.layout.game_layout_1_keyboard;
			if(inputMethod.equals("keypad")) layoutResId = R.layout.game_layout_1_keypad;
		} else if(layoutID==2) {
			if(inputMethod.equals("slider")) layoutResId = R.layout.game_layout_2_slider;
			if(inputMethod.equals("keyboard")) layoutResId = R.layout.game_layout_2_keyboard;
			if(inputMethod.equals("keypad")) layoutResId = R.layout.game_layout_2_keypad;
		}

		//Set Content Layout
		setContentView(layoutResId);

		// Set Difficulty
		switch (diffID){
//			case 3: rl_input_3.setVisibility(View.VISIBLE);
//				rl_input_4.setVisibility(View.GONE);
//				rl_input_5.setVisibility(View.GONE);
//				break;
//			case 4: rl_input_4.setVisibility(View.VISIBLE);
//				break;
//			case 5: rl_input_5.setVisibility(View.VISIBLE);
//				rl_input_4.setVisibility(View.GONE);
//				rl_input_3.setVisibility(View.GONE);
//				break;
		}


		//Cursors
		if(inputMethod.equals("slider")||inputMethod.equals("keypad")) {
			c1 = (RelativeLayout) findViewById(R.id.cur1);
			c2 = (RelativeLayout) findViewById(R.id.cur2);
			c3 = (RelativeLayout) findViewById(R.id.cur3);
			c4 = (RelativeLayout) findViewById(R.id.cur4);
		}

		sv = (ScrollView) findViewById(R.id.sv_feed);

		// Console text & Background animation
		tv_bulls = (Typewriter) findViewById(R.id.tv_bulls);
		tw_bg = (Typewriter) findViewById(R.id.tw_bg);
		tw_bg.setTextSize(8);
		tw_bg.setAlpha(0.2f);

		tw_bg.setCharacterDelay(1);
		String t = getResources().getString(R.string.tw_bg);
		tw_bg.animateText(t);

		if(inputMethod.equals("slider")) {
			tv_d_1 = (TextView) findViewById(R.id.d_1);
			tv_d_2 = (TextView) findViewById(R.id.d_2);
			tv_d_3 = (TextView) findViewById(R.id.d_3);
			tv_d_4 = (TextView) findViewById(R.id.d_4);
			sb = (SeekBar) findViewById(R.id.slider);
		}

		if(inputMethod.equals("keypad")) {
			tv_d_1 = (TextView) findViewById(R.id.d_1);
			tv_d_2 = (TextView) findViewById(R.id.d_2);
			tv_d_3 = (TextView) findViewById(R.id.d_3);
			tv_d_4 = (TextView) findViewById(R.id.d_4);
			kp_0 = (Button) findViewById(R.id.kp_0);
			kp_1 = (Button) findViewById(R.id.kp_1);
			kp_2 = (Button) findViewById(R.id.kp_2);
			kp_3 = (Button) findViewById(R.id.kp_3);
			kp_4 = (Button) findViewById(R.id.kp_4);
			kp_5 = (Button) findViewById(R.id.kp_5);
			kp_6 = (Button) findViewById(R.id.kp_6);
			kp_7 = (Button) findViewById(R.id.kp_7);
			kp_8 = (Button) findViewById(R.id.kp_8);
			kp_9 = (Button) findViewById(R.id.kp_9);
			kp_decrypt = (Button) findViewById(R.id.kp_decrypt);
			ll_kp = (LinearLayout) findViewById(R.id.ll_kp);
		}

		else if(inputMethod.equals("keyboard")){
			tv_d_1 = (EditText) findViewById(R.id.d_1);
			tv_d_2 = (EditText) findViewById(R.id.d_2);
			tv_d_3 = (EditText) findViewById(R.id.d_3);
			tv_d_4 = (EditText) findViewById(R.id.d_4);
			done = (Button) findViewById(R.id.decrypt);
		}

		p_1 = (TextView) findViewById(R.id.pointer1);

		//Slider Listener
		if(inputMethod.equals("slider"))
			sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
											  boolean fromUser) {
					// TODO Auto-generated method stub
					switch (tv_d){
						case 1: tv_d_1.setText(Integer.toString(progress));
							break;
						case 2: tv_d_2.setText(Integer.toString(progress));
							break;
						case 3: tv_d_3.setText(Integer.toString(progress));
							break;
						case 4: tv_d_4.setText(Integer.toString(progress));
							break;
					}

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					int progress = seekBar.getProgress();

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					switch (tv_d){
						case 1: activateTv(2, 1);
							break;
						case 2: activateTv(3, 1);
							break;
						case 3: activateTv(4, 1);
							break;
						case 4: activateTv(1, 1);
							break;
					}
				}

			});

		if(inputMethod.equals("keyboard"))
			done.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					decrypt();
				}
			});

		// Read Shared Preferences Statistics
		leastMoves = sp_stats.getInt("highscore", 10000);
		no_games = sp_stats.getInt("GAMES", 0);
		avg_mpg = sp_stats.getFloat("MPG", 0);

		rl_feed = (LinearLayout) findViewById(R.id.ll_feed);
		rl_body = (RelativeLayout) findViewById(R.id.rl_body);
		rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
		console = (CardView) findViewById(R.id.console);

		i_reload = (ImageButton) findViewById(R.id.i_reload);
		i_scores = (ImageButton) findViewById(R.id.i_scores);
		i_menu = (ImageButton) findViewById(R.id.i_menu);
		i_mute = (ImageButton) findViewById(R.id.i_mute);
		i_help = (ImageButton) findViewById(R.id.i_help);


		// Define in-game menu
		menu = new Dialog(GameActivity.this);
		defineMenu();

		// Set Mute or Unmute
		boolean mute_notmute = (sp_prefs.getBoolean("MUTE", false));
		// Start or pause accordingly
		if (!mute_notmute) {
			music.start();
			i_mute.setBackgroundResource(R.drawable.mute);
			notmute = true;
		}else{
			i_mute.setBackgroundResource(R.drawable.unmute);
			music.pause();
			notmute = false;
		}

		//Rookie Training Dialog
		Boolean never_show_rookie_dialog = false;
		never_show_rookie_dialog = sp_prefs.getBoolean("ROOKIENEVERSHOW", false);
		if(!never_show_rookie_dialog) {
			rookie_training_dialog = new Dialog(GameActivity.this);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(rookie_training_dialog.getWindow().getAttributes());

			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.MATCH_PARENT;
			rookie_training_dialog.setContentView(R.layout.rookie_training_dialog);
			rookie_training_dialog.show();
			menu_ID = 4;

			Window window = rookie_training_dialog.getWindow();
			window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
			rookie_training_dialog.setCancelable(false);
			rookie_training_dialog.getWindow().setAttributes(lp);

			TextView rookie_training_title = (TextView) rookie_training_dialog.findViewById(R.id.review_title);
			TextView rookie_training_skip = (TextView) rookie_training_dialog.findViewById(R.id.rookie_training_skip);
			rookie_training_title.setTextColor(Color.parseColor(bg[bg_index]));
			Typewriter rookie_training_text = (Typewriter) rookie_training_dialog.findViewById(R.id.training_text);
			final CheckBox never_show = (CheckBox) rookie_training_dialog.findViewById(R.id.never_show);

			never_show.setBackgroundColor(Color.parseColor(bg[bg_index]));
			rookie_training_skip.setBackgroundColor(Color.parseColor(bg[bg_index]));
			rookie_training_skip.setAlpha(0.7f);
			String st = getResources().getString(R.string.training);

			rookie_training_text.setCharacterDelay(1);
			rookie_training_text.setText(st);

			rookie_training_skip.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (never_show.isChecked()) {
						ed_pref.putBoolean("ROOKIENEVERSHOW", true);
						ed_pref.commit();
					}
					rookie_training_dialog.cancel();
					menu_ID = 0;
				}

			});
		}


//		// Lock Digits
//		tv_d_2.setOnLongClickListener(new View.OnLongClickListener(){
//			public boolean onLongClick(View arg0) {
//				if(tv_d_2.isEnabled()==true)
//				{
//					tv_d_2.setBackgroundResource(R.drawable.bg_lock);
//					tv_d_2.setEnabled(false);
//
//				}
//				else
//				{
//					tv_d_2.setBackgroundResource(R.drawable.bg);
//					tv_d_2.setEnabled(true);
//				}
//
//				return false;
//			}
//		});
//
//		tv_d_3.setOnLongClickListener(new View.OnLongClickListener() {
//			public boolean onLongClick(View arg0) {
//				if (tv_d_3.isEnabled()) {
//					tv_d_3.setBackgroundResource(R.drawable.bg_lock);
//					tv_d_3.setEnabled(false);
//				} else {
//					tv_d_3.setBackgroundResource(R.drawable.bg);
//					tv_d_3.setEnabled(true);
//				}
//
//				return true;
//			}
//		});
//
//		tv_d_4.setOnLongClickListener(new View.OnLongClickListener() {
//			public boolean onLongClick(View arg0) {
//				if (tv_d_4.isEnabled()) {
//					tv_d_4.setBackgroundResource(R.drawable.bg_lock);
//					tv_d_4.setEnabled(false);
//				} else {
//					tv_d_4.setBackgroundResource(R.drawable.bg);
//					tv_d_4.setEnabled(true);
//				}
//
//				return true;
//			}
//		});
//
//		tv_d_1.setOnLongClickListener(new View.OnLongClickListener() {
//			public boolean onLongClick(View arg0) {github
//				if (tv_d_1.isEnabled()) {
//					tv_d_1.setBackgroundResource(R.drawable.bg_lock);
//					tv_d_1.setEnabled(false);
//				} else {
//					tv_d_1.setBackgroundResource(R.drawable.bg);
//					tv_d_1.setEnabled(true);
//				}
//
//				return false;
//			}
//		});

		if(inputMethod.equals("keypad")) {
			Button[] kps = {kp_0,kp_1,kp_2,kp_3,kp_4,kp_5,kp_6,kp_7,kp_8,kp_9};
			for(Button b:kps) {

				final Button button = b;
				b.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						switch (tv_d) {
							case 1:
								tv_d_1.setText(String.valueOf(button.getText()));
								activateTv(2,1);
								break;
							case 2:
								tv_d_2.setText(String.valueOf(button.getText()));
								activateTv(3,1);;
								break;
							case 3:
								tv_d_3.setText(String.valueOf(button.getText()));
								activateTv(4,1);
								break;
							case 4:
								tv_d_4.setText(String.valueOf(button.getText()));
								activateTv(1,1);
								break;
						}
					}
				});
			}

			kp_decrypt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					decrypt();

				}
			});

		}


		if(inputMethod.equals("keyboard")||inputMethod.equals("keypad"))
			// Change Focus
			tv_d_1.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before,
										  int count) {
					// TODO Auto-generated method stub
					if(tv_d_1.getText().length()!=0)
					{
						tv_d_2.requestFocus();
					}

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
											  int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub

				}
			});

		if(inputMethod.equals("keyboard"))
			tv_d_2.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before,
										  int count) {
					// TODO Auto-generated method stub
					if(tv_d_2.getText().length()!=0)
						tv_d_3.requestFocus();


				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
											  int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub

				}
			});

		if(inputMethod.equals("keyboard"))
			tv_d_3.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before,
										  int count) {
					// TODO Auto-generated method stub
					if(tv_d_3.getText().length()!=0)
						tv_d_4.requestFocus();


				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
											  int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub

				}
			});

		if(inputMethod.equals("keyboard"))
			tv_d_4.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before,
										  int count) {
					// TODO Auto-generated method stub
					if (tv_d_4.getText().length() != 0)
						tv_d_1.requestFocus();


				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
											  int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub

				}
			});

		if(inputMethod.equals("slider")||inputMethod.equals("keypad"))
			tv_d_1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					activateTv(1, 0);
				}

			});

		if(inputMethod.equals("slider")||inputMethod.equals("keypad"))
			tv_d_2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					activateTv(2, 0);
				}

			});

		if(inputMethod.equals("slider")||inputMethod.equals("keypad"))
			tv_d_3.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					activateTv(3, 0);
				}

			});

		if(inputMethod.equals("slider")||inputMethod.equals("keypad"))
			tv_d_4.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					activateTv(4, 0);
				}

			});




		i_reload.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!restartPending) {
					rl_feed.removeAllViews();
					rl_feed.setVisibility(View.INVISIBLE);
					reInitializeGame();
				} else {
					Intent i = new Intent(GameActivity.this, GameActivity.class);
					startActivity(i);
					finish();
				}
			}

		});

		i_scores.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				report_dialog = new Dialog(GameActivity.this);
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(report_dialog.getWindow().getAttributes());

				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.MATCH_PARENT;
				report_dialog.setContentView(R.layout.report_dialog);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				report_dialog.show();
				menu_ID=2;

				Window window = report_dialog.getWindow();
				window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
				report_dialog.setCancelable(true);
				report_dialog.getWindow().setAttributes(lp);

				TextView report_title = (TextView)report_dialog.findViewById(R.id.report_title);
				TextView report_back = (TextView)report_dialog.findViewById(R.id.report_back);
				report_back.setText("Back to Mission");
				report_title.setTextColor(accent);
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
						"\nBest mission: " + Integer.toString(d1) + " moves"+history + "\n\n";

				report_text.setCharacterDelay(2);
				report_text.animateText(st);
				report_back.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						report_dialog.cancel();
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
						menu_ID=0;
					}

				});
			}

		});

		i_menu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				menu.show();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				defineMenu();
			}

		});


		i_mute.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (notmute) {
					music.pause();
					notmute = false;
					i_mute.setBackgroundResource(R.drawable.unmute);
					ed_pref.putBoolean("MUTE", true);
				}else{
					i_mute.setBackgroundResource(R.drawable.mute);
					music.start();
					notmute=true;
					ed_pref.putBoolean("MUTE", false);
				}
				ed_pref.commit();

			}

		});

		i_help.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				training_dialog = new Dialog(GameActivity.this);
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(training_dialog.getWindow().getAttributes());

				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.MATCH_PARENT;
				training_dialog.setContentView(R.layout.training_dialog);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				training_dialog.show();
				menu_ID=3;

				Window window = training_dialog.getWindow();
				window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
				training_dialog.setCancelable(true);
				training_dialog.getWindow().setAttributes(lp);

				TextView training_title = (TextView)training_dialog.findViewById(R.id.training_title);
				TextView training_back = (TextView)training_dialog.findViewById(R.id.training_back);
				training_title.setTextColor(Color.parseColor(bg[bg_index]));
				Typewriter training_text = (Typewriter)training_dialog.findViewById(R.id.training_text);
				training_back.setText("Back to Mission");

				String st = getResources().getString(R.string.training);

				training_text.setCharacterDelay(1);
				training_text.setText(st);

				training_back.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						training_dialog.cancel();
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
						menu_ID=0;
					}

				});
			}

		});


		//Initialize game
		initializeGame();


	}

	private void reInitializeGame() {
		//Set Accent Color
		// Random Digits Generator
		r = new Random();
		//Random bg_index
		bg_index=r.nextInt(8);
		accent = Color.parseColor(bg[bg_index]);

		initializeGame();
	}

	private void defineMenu() {

		final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(menu.getWindow().getAttributes());

		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		menu.setContentView(R.layout.menu_dialog);

		Window window = menu.getWindow();
		window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
		menu.setCancelable(true);

		menu.getWindow().setAttributes(lp);

		TextView d_title = (TextView)menu.findViewById(R.id.menu_title);
		final Typewriter d_not = (Typewriter)menu.findViewById(R.id.menu_notification);
		final TextView resume = (TextView)menu.findViewById(R.id.resume);
		TextView objective = (TextView)menu.findViewById(R.id.objective);
		TextView layout = (TextView)menu.findViewById(R.id.layout);
		TextView quit = (TextView)menu.findViewById(R.id.quit);
		TextView input = (TextView)menu.findViewById(R.id.input);
		TextView theme = (TextView)menu.findViewById(R.id.theme);
		final TextView input_selected = (TextView)menu.findViewById(R.id.input_selected);
		final TextView layout_selected = (TextView)menu.findViewById(R.id.layout_selected);
		final TextView theme_selected = (TextView)menu.findViewById(R.id.theme_selected);

		//Colorify Menu
		d_title.setTextColor(accent);
		d_not.setTextColor(accent);
		layout_selected.setTextColor(accent);
		input_selected.setTextColor(accent);
		theme_selected.setTextColor(accent);

		//Read SharedPreferences File
		input_selected.setText(sp_prefs.getString("inputMethod", "keypad"));

		String layouts[]={"top console", "bottom console"};
		layout_selected.setText(layouts[layoutID-1]);

		String themes[]={"light theme", "dark theme"};
		int themeID = 0;
		if(!darkTheme) themeID = 0;
		else themeID = 1;
		theme_selected.setText(themes[themeID]);



		// OnClick Listeners
		quit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}

		});

		TextView[] ia = {input,input_selected};

		for(TextView i: ia)
			i.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					inputMethod = sp_prefs.getString("inputMethod", "keypad");
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
					if(!restartPending)d_not.animateText(RESTART_TEXT);
					restartPending = true;
					animateTv(resume, 100);
					animateIcon(i_reload);
				}

			});

		TextView[] ta = {theme,theme_selected};
		for(TextView n: ta)
			n.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					darkTheme = sp_prefs.getBoolean("themeID", true);
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
					if(!restartPending)d_not.animateText(RESTART_TEXT);
					restartPending = true;
					animateTv(resume, 100);
					animateIcon(i_reload);
				}

			});

		resume.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				menu.cancel();
				if(restartPending) {
					consoleAlert("Restart required!");
				}
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}

		});

		objective.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				brief_dialog = new Dialog(GameActivity.this);
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(brief_dialog.getWindow().getAttributes());

				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.MATCH_PARENT;
				brief_dialog.setContentView(R.layout.brief_dialog);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				brief_dialog.show();

				Window window = brief_dialog.getWindow();
				window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
				brief_dialog.setCancelable(true);
				brief_dialog.getWindow().setAttributes(lp);

				TextView brief_title = (TextView)brief_dialog.findViewById(R.id.brief_title);
				TextView brief_back = (TextView)brief_dialog.findViewById(R.id.brief_back);
				brief_title.setTextColor(accent);
				Typewriter brief_text = (Typewriter)brief_dialog.findViewById(R.id.brief_text);

				String st = getResources().getString(R.string.mission_brief);
				brief_text.setCharacterDelay(20);
				brief_text.animateText(st);
				brief_back.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						brief_dialog.cancel();
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
					}

				});
			}

		});

		TextView[] la = {layout,layout_selected};
		for(TextView p: la)
			p.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					layoutID = sp_prefs.getInt("layoutID", 2);
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
					if(!restartPending)d_not.animateText(RESTART_TEXT);
					restartPending = true;
					animateTv(resume, 100);
					animateIcon(i_reload);
				}

			});
	}


	private void activateTv(int i, int mode){
		// mode = 0, activated from decrypt() on empty fields, onClick
		// mode = 1, is right swipe gesture
		// mode = 2, is left swipe gesture
		tv_d = i;
		if(i==1){
			//tv_d_1 active
			if(inputMethod.equals("slider"))
				if(!(tv_d_1.getText().toString().equals("")))
					sb.setProgress(Integer.valueOf(tv_d_1.getText().toString()));
				else if(!(tv_d_1.getHint().toString().equals(""))&&mode==1){
					sb.setProgress(Integer.valueOf(tv_d_1.getHint().toString()));
				}
			stopOtherAnim(1);
			animateCur(c1);
			c1.setVisibility(View.VISIBLE);
			c2.setVisibility(View.INVISIBLE);
			c3.setVisibility(View.INVISIBLE);
			c4.setVisibility(View.INVISIBLE);
		}
		else if(i==2){
			//tv_d_2 active
			if(inputMethod.equals("slider"))
				if(!(tv_d_2.getText().toString().equals("")))
					sb.setProgress(Integer.valueOf(tv_d_2.getText().toString()));
				else if(!(tv_d_2.getHint().toString().equals(""))&&mode==1){
					sb.setProgress(Integer.valueOf(tv_d_2.getHint().toString()));
				}

			stopOtherAnim(2);
			animateCur(c2);
			c2.setVisibility(View.VISIBLE);
			c1.setVisibility(View.INVISIBLE);
			c3.setVisibility(View.INVISIBLE);
			c4.setVisibility(View.INVISIBLE);
		}
		else if(i==3){
			//tv_d_3 active
			if(inputMethod.equals("slider"))
				if(!(tv_d_3.getText().toString().equals("")))
					sb.setProgress(Integer.valueOf(tv_d_3.getText().toString()));
				else if(!(tv_d_3.getHint().toString().equals(""))&&mode==1){
					sb.setProgress(Integer.valueOf(tv_d_3.getHint().toString()));
				}

			stopOtherAnim(3);
			animateCur(c3);
			c3.setVisibility(View.VISIBLE);
			c2.setVisibility(View.INVISIBLE);
			c1.setVisibility(View.INVISIBLE);
			c4.setVisibility(View.INVISIBLE);
		}
		else if(i==4){
			//tv_d_4 active
			if(inputMethod.equals("slider"))
				if(!(tv_d_4.getText().toString().equals("")))
					sb.setProgress(Integer.valueOf(tv_d_4.getText().toString()));
				else if(!(tv_d_4.getHint().toString().equals(""))&&mode==1){
					sb.setProgress(Integer.valueOf(tv_d_4.getHint().toString()));
				}

			stopOtherAnim(4);
			animateCur(c4);
			c4.setVisibility(View.VISIBLE);
			c2.setVisibility(View.INVISIBLE);
			c3.setVisibility(View.INVISIBLE);
			c1.setVisibility(View.INVISIBLE);

		}
	}

	private void stopOtherAnim(int c) {
		int cur = c;
		RelativeLayout[] rl = {c1,c2,c3,c4};
		switch (cur){
			case 1: for(int i = 1; i <=3; i++) rl[i].clearAnimation();
				break;
			case 2: for(int i = 0; i <=3; i++) if(i != 1) rl[i].clearAnimation();
				break;
			case 3: for(int i = 0; i <=3; i++) if(i != 2) rl[i].clearAnimation();
				break;
			case 4: for(int i = 0; i <=2; i++) rl[i].clearAnimation();
				break;

		}
	}


	private void animateCur(RelativeLayout c) {
		final RelativeLayout cur = c;

		blinkAnim = new AlphaAnimation(1, 0);
		blinkAnim.setDuration(200);
		blinkAnim.setInterpolator(new LinearInterpolator());
		blinkAnim.setRepeatCount(Animation.INFINITE);
		blinkAnim.setRepeatMode(Animation.REVERSE);
		c.startAnimation(blinkAnim);
	}

	private void animateIcon(ImageButton b) {
		final ImageButton cur = b;

		blinkAnim = new AlphaAnimation(1, 0);
		blinkAnim.setDuration(500);
		blinkAnim.setInterpolator(new LinearInterpolator());
		blinkAnim.setRepeatCount(Animation.INFINITE);
		blinkAnim.setRepeatMode(Animation.REVERSE);
		b.startAnimation(blinkAnim);
	}

	private void animateTv(TextView t, int repeatCount) {
		blinkAnim = new AlphaAnimation(1, 0);
		blinkAnim.setDuration(800);
		blinkAnim.setInterpolator(new LinearInterpolator());
		blinkAnim.setRepeatCount(repeatCount);
		blinkAnim.setRepeatMode(Animation.REVERSE);
		t.startAnimation(blinkAnim);
	}

	private void consoleAlert(String t){
		cont = true;
		consoleTextAnim(t, 100, 40);
		cont = false;
	}

	private void consoleTextAnim(final String text, int time, int charDelay){
		final int cd = charDelay;
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// After time ms

				tv_bulls.setCharacterDelay(cd);
				tv_bulls.animateText(text);
			}
		}, time);
	}

	public void sendToast(Object charSequence){
		Toast.makeText(getBaseContext(), (String) charSequence, Toast.LENGTH_SHORT);

	}

	public void makeSnack(String t){
		View v = findViewById(R.id.rl_body);
		Snackbar.make(v, t, Snackbar.LENGTH_LONG).setAction("Action", null).show();


	}

	//Initialize game
	private void initializeGame(){

		// Enable Input methods
		if(inputMethod.equals("keyboard")) done.setEnabled(true);
		if(inputMethod.equals("keypad")) kp_decrypt.setEnabled(true);
		if(inputMethod.equals("slider")) sb.setEnabled(true);

		//Reset Score counter
		feed_count = 0;

		//Clear animations
		tv_bulls.clearAnimation();
		i_reload.clearAnimation();
		i_scores.clearAnimation();

		// Reset digit textviews
		tv_d_1.setHint("");
		tv_d_2.setHint("");
		tv_d_3.setHint("");
		tv_d_4.setHint("");

		tv_d_1.setText("");
		tv_d_2.setText("");
		tv_d_3.setText("");
		tv_d_4.setText("");

		//Request focus on tv_d_1
		if(inputMethod.equals("slider")||inputMethod.equals("keypad")) {
			tv_d = 1;
			activateTv(1, 0);
			if(inputMethod.equals("slider"))sb.setProgress(0);
		}


		// Theme/Background Color
		if(darkTheme) {
			rl_body.setBackgroundColor(Color.parseColor("#FE000000"));
			rl_bg.setBackgroundColor(Color.parseColor("#FE000000"));
			console.setCardBackgroundColor(Color.parseColor("#33FFFFFF"));
			tv_d_1.setHintTextColor(Color.parseColor("#21FFFFFF"));
			tv_d_2.setHintTextColor(Color.parseColor("#21FFFFFF"));
			tv_d_3.setHintTextColor(Color.parseColor("#21FFFFFF"));
			tv_d_4.setHintTextColor(Color.parseColor("#21FFFFFF"));
			tv_d_1.setBackgroundResource(R.drawable.bg_dark);
			tv_d_2.setBackgroundResource(R.drawable.bg_dark);
			tv_d_3.setBackgroundResource(R.drawable.bg_dark);
			tv_d_4.setBackgroundResource(R.drawable.bg_dark);
			tw_bg.setTextColor(accent);
		}
		else {
			rl_body.setBackgroundColor(accent);
			tw_bg.setTextColor(Color.parseColor("#AAFFFFFF"));
			console.setCardBackgroundColor(Color.parseColor("#DDFFFFFF"));
			tv_d_1.setHintTextColor(Color.parseColor("#AAFFFFFF"));
			tv_d_2.setHintTextColor(Color.parseColor("#AAFFFFFF"));
			tv_d_3.setHintTextColor(Color.parseColor("#AAFFFFFF"));
			tv_d_4.setHintTextColor(Color.parseColor("#AAFFFFFF"));
			tv_d_1.setBackgroundResource(R.drawable.bg);
			tv_d_2.setBackgroundResource(R.drawable.bg);
			tv_d_3.setBackgroundResource(R.drawable.bg);
			tv_d_4.setBackgroundResource(R.drawable.bg);
		}

		tv_bulls.setTextColor(accent);
		tv_d_1.setTextColor(accent);
		tv_d_2.setTextColor(accent);
		tv_d_3.setTextColor(accent);
		tv_d_4.setTextColor(accent);
		p_1.setTextColor(accent);

		//Setup cursors
		if(inputMethod.equals("slider")||inputMethod.equals("keypad")) {
			c1.setBackgroundColor(accent);
			c1.setVisibility(View.VISIBLE);
			c2.setBackgroundColor(accent);
			c2.setVisibility(View.INVISIBLE);
			c3.setBackgroundColor(accent);
			c3.setVisibility(View.INVISIBLE);
			c4.setBackgroundColor(accent);
			c4.setVisibility(View.INVISIBLE);
		}

		// Distinct digits generator
		do{
			for(int i = 0;i<4;i++)
				rno[i]=String.valueOf(r.nextInt(10));

			distinct=true;

			for(int i=0;i<3;i++)
				for(int k=i+1;k<4;k++)
					if (rno[i].equals(rno[k]))
					{
						distinct=false;
						break;
					}


		}while(distinct==false);

		// Detect touched area
		detector = new SimpleGestureFilter(this,this);

		// Typewriter animation
		consoleTextAnim("booting code breaker -d", 2000, 40);
		consoleTextAnim("user: nira_ag_b", 6000, 25);
		consoleTextAnim("pass: ********", 8000, 40);
		consoleTextAnim("code breaker active", 10000, 40);
		consoleTextAnim("", 12800, 40);
	}

	//Decrypt function
	private void decrypt(){

		switch(diffID) {
			case 4:

				if (inputMethod.equals("slider")) {
					// activateTv(4,0);
				}

				sv.setSmoothScrollingEnabled(true);
				sv.postDelayed(new Runnable() {
					public void run() {
						sv.fullScroll(ScrollView.FOCUS_DOWN);
					}
				}, 30);

				cont = true;

				uip[0] = tv_d_1.getText().toString();
				uip[1] = tv_d_2.getText().toString();
				uip[2] = tv_d_3.getText().toString();
				uip[3] = tv_d_4.getText().toString();


				// If any field is empty
				if (uip[0].equals("") || uip[2].equals("") || uip[3].equals("") || uip[1].equals("")) {
					consoleAlert("Do not leave any field empty.");
					if (uip[0].equals("")) {
						tv_d_1.setBackgroundResource(R.drawable.bg_red);
						if (inputMethod.equals("slider") || inputMethod.equals("keypad"))
							activateTv(1, 0);
					}
					if (uip[1].equals("")) {
						tv_d_2.setBackgroundResource(R.drawable.bg_red);
						if (inputMethod.equals("slider") || inputMethod.equals("keypad"))
							activateTv(2, 0);
					}
					if (uip[2].equals("")) {
						tv_d_3.setBackgroundResource(R.drawable.bg_red);
						if (inputMethod.equals("slider") || inputMethod.equals("keypad"))
							activateTv(3, 0);
					}
					if (uip[3].equals("")) {
						tv_d_4.setBackgroundResource(R.drawable.bg_red);
						if (inputMethod.equals("slider") || inputMethod.equals("keypad"))
							activateTv(4, 0);
					}
					cont = false;
				} else {
					tv_d_1.setBackgroundResource(R.drawable.bg);
					tv_d_2.setBackgroundResource(R.drawable.bg);
					tv_d_3.setBackgroundResource(R.drawable.bg);
					tv_d_4.setBackgroundResource(R.drawable.bg);

					for (int i = 0; i <= 3; i++)
						for (int k = i + 1; k < 4; k++)
							if (uip[i].equals(uip[k])) {
								consoleAlert("Enter distinct digits.");
								if (i == 0)
									tv_d_1.setBackgroundResource(R.drawable.bg_red);
								else if (i == 1)
									tv_d_2.setBackgroundResource(R.drawable.bg_red);
								else if (i == 2)
									tv_d_3.setBackgroundResource(R.drawable.bg_red);
								else if (i == 3)
									tv_d_4.setBackgroundResource(R.drawable.bg_red);

								if (k == 0)
									tv_d_1.setBackgroundResource(R.drawable.bg_red);
								else if (k == 1)
									tv_d_2.setBackgroundResource(R.drawable.bg_red);
								else if (k == 2)
									tv_d_3.setBackgroundResource(R.drawable.bg_red);
								else if (k == 3)
									tv_d_4.setBackgroundResource(R.drawable.bg_red);

								cont = false;
								break;
							}

					if (cont) {
						tv_d_1.setHint(tv_d_1.getText().toString());
						tv_d_2.setHint(tv_d_2.getText().toString());
						tv_d_3.setHint(tv_d_3.getText().toString());
						tv_d_4.setHint(tv_d_4.getText().toString());

						tv_d_1.setText("");
						tv_d_2.setText("");
						tv_d_3.setText("");
						tv_d_4.setText("");

						gameLogic(4);


//						bulls = 0;
//						cows = 0;

//						//Calculate Bulls
//						for (int i = 0; i < 4; i++)
//							if (uip[i].equals(rno[i]))
//								bulls += 1;
//
//
//						//Calculate Cows
//						for (int i = 0; i < 4; i++)
//							for (int k = 0; k < 4; k++)
//								if (uip[i].equals(rno[k])) {
//									cows += 1;
//								}
//
//						cows = cows - bulls;


//						if (bulls != 4)
//							consoleTextAnim("[Hits]: " + String.valueOf(bulls) + "  &  [Clicks]: " + String.valueOf(cows), 30, 20);
//
//						//Random jargon & long integer generator
//						Random p = new Random();
//						int r_int = p.nextInt(20);
//						long l_int = p.nextInt(1000000000);
//
//						//Jargon
//						final Typewriter row1 = new Typewriter(getBaseContext());
//						row1.generateViewId();
//						row1.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
//						row1.setCharacterDelay(140);
//						row1.animateText(">> " + random_jargon[r_int]);
//						if (!darkTheme)
//							row1.setTextColor(Color.parseColor("#BBFFFFFF"));
//						else {
//							row1.setTextColor(accent);
//							row1.setAlpha(0.5f);
//						}
//						row1.setTypeface(Typeface.MONOSPACE);
//
//						//Result
//						final TextView row2 = new TextView(getBaseContext());
//						row2.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
//						row2.setText(uip[0] + "" + uip[1] + "" + uip[2] + "" + uip[3] + "> " + "Hits:" + String.valueOf(bulls) + " && Clicks:" + String.valueOf(cows));
//						if (!darkTheme)
//							row2.setTextColor(Color.parseColor("#BBFFFFFF"));
//						else {
//							row2.setTextColor(accent);
//							row2.setAlpha(0.9f);
//						}
//						row2.setTextSize(22);
//						row2.setTypeface(Typeface.MONOSPACE);
//						feed_count += 1;
//
//						//Random long number
//						final TextView row3 = new TextView(getBaseContext());
//						row3.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
//						row3.setText(Long.toString(l_int) + "**********************");
//						row3.setTextSize(10);
//						if (!darkTheme)
//							row3.setTextColor(Color.parseColor("#BBFFFFFF"));
//						else {
//							row3.setTextColor(accent);
//							row3.setAlpha(0.5f);
//						}
//						row3.setTypeface(Typeface.MONOSPACE);
//
//						//Add a reult to console output
//						rl_feed.addView(row1);
//						rl_feed.addView(row2);
//						rl_feed.addView(row3);
//						rl_feed.setVisibility(View.VISIBLE);
//						if (darkTheme)
//							rl_feed.setBackgroundResource(R.drawable.console_output_darktheme);
//						else
//							rl_feed.setBackgroundResource(R.drawable.console_output);

						//On game complete
						if (bulls == 4 && cows == 0) {
							tv_d_1.setText("O");
							tv_d_2.setText("P");
							tv_d_3.setText("E");
							tv_d_4.setText("N");

//							// Disable input methods
//							if (inputMethod.equals("keyboard")) done.setEnabled(false);
//							if (inputMethod.equals("keypad")) kp_decrypt.setEnabled(false);
//							if (inputMethod.equals("slider")) sb.setEnabled(false);
//
//
//							//Update highscore if valid
//							if (leastMoves > feed_count) {
//								ed.putInt("highscore", feed_count);
//								consoleTextAnim("This is your best yet!", 100, 20);
//								ed.commit();
//							} else {
//								consoleTextAnim("You did it!", 100, 20);
//							}
//
//							//Display messages & animations
//							String st = "Warhead disarmed in " + String.valueOf(feed_count + " moves.");
//							consoleTextAnim(st, 3500, 40);
//							consoleTextAnim("Good job Agent B!", 6000, 40);
//							consoleTextAnim("Sending extraction.", 8000, 40);
//							consoleTextAnim("", 16000, 40);
//							animateIcon(i_scores);
//
//							// Get date and time
//							Calendar c = Calendar.getInstance();
//							String instant = String.valueOf(c.get(Calendar.MONTH)) + "-" +
//									String.valueOf(c.get(Calendar.DATE)) + "-" +
//									String.valueOf(c.get(Calendar.YEAR));
//
//							//Update stats
//							no_games++;
//							avg_mpg = (avg_mpg * (no_games - 1) + feed_count) / no_games;
//							ed.putFloat("MPG", avg_mpg);
//							ed.putInt("GAMES", no_games);
//							ed.putString("HISTORY", sp_stats.getString("HISTORY", "") + "\n" + instant + "  ::  " + String.valueOf(feed_count) + " Moves");
//							ed.commit();
//
//							if (no_games == 3 || no_games == 8 || no_games == 18 || no_games == 32 || no_games == 50 || no_games == 70) {
//								//Review dialog
//								review_dialog = new Dialog(GameActivity.this);
//								WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//								lp.copyFrom(review_dialog.getWindow().getAttributes());
//
//								lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//								lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//								review_dialog.setContentView(R.layout.review_dialog);
//								review_dialog.show();
//								menu_ID = 5;
//
//								Window window = review_dialog.getWindow();
//								window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
//								review_dialog.setCancelable(false);
//								review_dialog.getWindow().setAttributes(lp);
//
//								TextView review_title = (TextView) review_dialog.findViewById(R.id.review_title);
//								TextView review_skip = (TextView) review_dialog.findViewById(R.id.review_skip);
//								TextView review_now = (TextView) review_dialog.findViewById(R.id.review_now);
//								review_skip.setTextColor(Color.parseColor(bg[bg_index]));
//								Typewriter review_text = (Typewriter) review_dialog.findViewById(R.id.review_text);
//
//								review_title.setTextColor(Color.parseColor(bg[bg_index]));
//								review_now.setBackgroundColor(Color.parseColor(bg[bg_index]));
//								review_skip.setAlpha(0.7f);
//
//								String rv = getResources().getString(R.string.review);
//
//								review_text.setCharacterDelay(1);
//								review_text.setText(rv);
//
//								review_skip.setOnClickListener(new View.OnClickListener() {
//									public void onClick(View v) {
//										review_dialog.cancel();
//										menu_ID = 0;
//									}
//
//								});
//
//								review_now.setOnClickListener(new View.OnClickListener() {
//									public void onClick(View v) {
//										final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//										try {
//											startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//										} catch (android.content.ActivityNotFoundException anfe) {
//											startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//										}
//
//										review_dialog.cancel();
//										menu_ID = 0;
//									}
//
//								});
//							}
							onGameComplete();
						}
					}
				}
				break;
		}	//Switch (diffID)
	}


	//Game Logic
	private void gameLogic(int diffID) {

		bulls = 0;
		cows = 0;

		//Calculate Bulls
		for (int i = 0; i < diffID; i++)
			if (uip[i].equals(rno[i]))
				bulls += 1;


		//Calculate Cows
		for (int i = 0; i < diffID; i++)
			for (int k = 0; k < diffID; k++)
				if (uip[i].equals(rno[k])) {
					cows += 1;
				}

		cows = cows - bulls;

		if (bulls != diffID)
			consoleTextAnim("[Hits]: " + String.valueOf(bulls) + "  &  [Clicks]: " + String.valueOf(cows), 30, 20);

		//Random jargon & long integer generator
		Random p = new Random();
		int r_int = p.nextInt(20);
		long l_int = p.nextInt(1000000000);

		//Jargon
		final Typewriter row1 = new Typewriter(getBaseContext());
		row1.generateViewId();
		row1.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
		row1.setCharacterDelay(140);
		row1.animateText(">> " + random_jargon[r_int]);
		if (!darkTheme)
			row1.setTextColor(Color.parseColor("#BBFFFFFF"));
		else {
			row1.setTextColor(accent);
			row1.setAlpha(0.5f);
		}
		row1.setTypeface(Typeface.MONOSPACE);

		//Result
		final TextView row2 = new TextView(getBaseContext());
		row2.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
		switch (diffID){
			case 3: row2.setText(uip[0] + "" + uip[1] + "" + uip[2] + "" + "> " + "Hits:" + String.valueOf(bulls) + " && Clicks:" + String.valueOf(cows));
				break;
			case 4: row2.setText(uip[0] + "" + uip[1] + "" + uip[2] + "" + uip[3] + "> " + "Hits:" + String.valueOf(bulls) + " && Clicks:" + String.valueOf(cows));
				break;
			case 5: row2.setText(uip[0] + "" + uip[1] + "" + uip[2] + "" + uip[3] + "" + uip[4] + "> " + "Hits:" + String.valueOf(bulls) + " && Clicks:" + String.valueOf(cows));
				break;
		}
		if (!darkTheme)
			row2.setTextColor(Color.parseColor("#BBFFFFFF"));
		else {
			row2.setTextColor(accent);
			row2.setAlpha(0.9f);
		}
		row2.setTextSize(22);
		row2.setTypeface(Typeface.MONOSPACE);
		feed_count += 1;

		//Random long number
		final TextView row3 = new TextView(getBaseContext());
		row3.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
		row3.setText(Long.toString(l_int) + "**********************");
		row3.setTextSize(10);
		if (!darkTheme)
			row3.setTextColor(Color.parseColor("#BBFFFFFF"));
		else {
			row3.setTextColor(accent);
			row3.setAlpha(0.5f);
		}
		row3.setTypeface(Typeface.MONOSPACE);

		//Add a reult to console output
		rl_feed.addView(row1);
		rl_feed.addView(row2);
		rl_feed.addView(row3);
		rl_feed.setVisibility(View.VISIBLE);
		if (darkTheme)
			rl_feed.setBackgroundResource(R.drawable.console_output_darktheme);
		else
			rl_feed.setBackgroundResource(R.drawable.console_output);
	}


	//On Game Complete
	private void onGameComplete() {
		// Disable input methods
		if (inputMethod.equals("keyboard")) done.setEnabled(false);
		if (inputMethod.equals("keypad")) kp_decrypt.setEnabled(false);
		if (inputMethod.equals("slider")) sb.setEnabled(false);


		//Update highscore if valid
		if (leastMoves > feed_count) {
			ed.putInt("highscore", feed_count);
			consoleTextAnim("This is your best yet!", 100, 20);
			ed.commit();
		} else {
			consoleTextAnim("Warhead disarmed!", 100, 20);
		}

		//Display messages & animations
		String st = "You made " + String.valueOf(feed_count + " moves.");
		consoleTextAnim(st, 3500, 40);
		consoleTextAnim("Good job Agent B!", 6000, 40);
		consoleTextAnim("Sending extraction.", 8000, 40);
		consoleTextAnim("", 16000, 40);
		animateIcon(i_scores);

		// Get date and time
		Calendar c = Calendar.getInstance();
		String instant = String.valueOf(c.get(Calendar.MONTH)) + "-" +
				String.valueOf(c.get(Calendar.DATE)) + "-" +
				String.valueOf(c.get(Calendar.YEAR));

		//Update stats
		no_games++;
		avg_mpg = (avg_mpg * (no_games - 1) + feed_count) / no_games;
		ed.putFloat("MPG", avg_mpg);
		ed.putInt("GAMES", no_games);
		ed.putString("HISTORY", sp_stats.getString("HISTORY", "") + "\n" + instant + "  ::  " + String.valueOf(feed_count) + " Moves");
		ed.commit();

		if (no_games == 3 || no_games == 8 || no_games == 18 || no_games == 32 || no_games == 50 || no_games == 70) {
			//Review dialog
			review_dialog = new Dialog(GameActivity.this);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(review_dialog.getWindow().getAttributes());

			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.MATCH_PARENT;
			review_dialog.setContentView(R.layout.review_dialog);
			review_dialog.show();
			menu_ID = 5;

			Window window = review_dialog.getWindow();
			window.setBackgroundDrawableResource(R.drawable.menu_dialog_back);
			review_dialog.setCancelable(false);
			review_dialog.getWindow().setAttributes(lp);

			TextView review_title = (TextView) review_dialog.findViewById(R.id.review_title);
			TextView review_skip = (TextView) review_dialog.findViewById(R.id.review_skip);
			TextView review_now = (TextView) review_dialog.findViewById(R.id.review_now);
			review_skip.setTextColor(Color.parseColor(bg[bg_index]));
			Typewriter review_text = (Typewriter) review_dialog.findViewById(R.id.review_text);

			review_title.setTextColor(Color.parseColor(bg[bg_index]));
			review_now.setBackgroundColor(Color.parseColor(bg[bg_index]));
			review_skip.setAlpha(0.7f);

			String rv = getResources().getString(R.string.review);

			review_text.setCharacterDelay(1);
			review_text.setText(rv);

			review_skip.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					review_dialog.cancel();
					menu_ID = 0;
				}

			});

			review_now.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
					try {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
					} catch (android.content.ActivityNotFoundException anfe) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
					}

					review_dialog.cancel();
					menu_ID = 0;
				}

			});
		}
	}
}
