package com.jp.cowsnbulls;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {

	private static final String HIGHSCORE = "highscore";
	String d_1,d_2,d_3,d_4;
	TextView tv_d_1,tv_d_2,tv_d_3,tv_d_4,tv_bulls;
	LinearLayout rl_feed;
	RelativeLayout rl_body;
	ImageButton i_reload,i_scores, i_menu;
	SQLiteDatabase db_scoreinfo=null;	
	Random r;
	String[] rno=new String[4];
	String[] uip=new String[4];
	int bg_index;
	int blank;
	int feed_count=0;
	int bulls=0;
	int cows=0;
	int least_score_yet;
	String[] bg = {"#ef5350","#ba68c8","#29b6f6","#43a047","#9ccc65","#ffa000","#8d6e63","#ff6701","#546e7a"};
	boolean distinct=true,cont;
	SharedPreferences sp;
	private  static final String PREFS_FILE = "com.jp.cowsnbulls.preferences";
	SharedPreferences.Editor ed;
	int leastMoves;
	TextView d_highscore;
	Button d_okay;
	int no_games;
	float avg_mpg;
	MediaPlayer music;
	String random_jargon[]= {"inject *ex.bch /hes","ACCESS DENIED","lst lan0 -verify",
			"remconn 13.48.33.75:3909", "netstat -t 00:24", "kill PID 2040",
			"brute thread initiated"};

	@Override
	protected void onResume() {
		// Set Background Music
		music = MediaPlayer.create(GameActivity.this, R.raw.a);
		music.setLooping(true);
		music.start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		music.release();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		tv_d_1 = (TextView) findViewById(R.id.d_1);
		tv_d_2 = (TextView) findViewById(R.id.d_2);
		tv_d_3 = (TextView) findViewById(R.id.d_3);
		tv_d_4 = (TextView) findViewById(R.id.d_4);
		tv_bulls = (TextView)findViewById(R.id.tv_bulls);

		sp = getSharedPreferences(PREFS_FILE, 0);
//		sp =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		ed = sp.edit();

		leastMoves = sp.getInt(HIGHSCORE, 10000);
		no_games = sp.getInt("GAMES", 0);
		avg_mpg = sp.getFloat("MPG", 0);

		rl_feed = (LinearLayout) findViewById(R.id.ll_feed);
		rl_body = (RelativeLayout) findViewById(R.id.rl_body);

		i_reload = (ImageButton) findViewById(R.id.i_reload);
		i_scores = (ImageButton) findViewById(R.id.i_scores);
		i_menu = (ImageButton) findViewById(R.id.i_menu);

		Button done = (Button) findViewById(R.id.new_game);



		// Lock Digits
		tv_d_2.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View arg0) {
				if(tv_d_2.isEnabled()==true)
				{
					tv_d_2.setBackgroundResource(R.drawable.bg_lock);
					tv_d_2.setEnabled(false);

				}
				else
				{
					tv_d_2.setBackgroundResource(R.drawable.bg);
					tv_d_2.setEnabled(true);
				}

				return false;
			}
		});

		tv_d_3.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View arg0) {
				if(tv_d_3.isEnabled())
				{
					tv_d_3.setBackgroundResource(R.drawable.bg_lock);
					tv_d_3.setEnabled(false);
				}
				else
				{
					tv_d_3.setBackgroundResource(R.drawable.bg);
					tv_d_3.setEnabled(true);
				}

				return true;
			}
		});

		tv_d_4.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View arg0) {
				if(tv_d_4.isEnabled())
				{
					tv_d_4.setBackgroundResource(R.drawable.bg_lock);
					tv_d_4.setEnabled(false);
				}
				else
				{
					tv_d_4.setBackgroundResource(R.drawable.bg);
					tv_d_4.setEnabled(true);
				}

				return true;
			}
		});

		tv_d_1.setOnLongClickListener(new View.OnLongClickListener(){
			public boolean onLongClick(View arg0) {
				if(tv_d_1.isEnabled())
				{
					tv_d_1.setBackgroundResource(R.drawable.bg_lock);
					tv_d_1.setEnabled(false);
				}
				else
				{
					tv_d_1.setBackgroundResource(R.drawable.bg);
					tv_d_1.setEnabled(true);
				}

				return false;
			}
		});

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

		tv_d_4.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if(tv_d_4.getText().length()!=0)
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



		initializeGame();

		i_reload.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				rl_feed.removeAllViews();
				rl_feed.setVisibility(View.INVISIBLE);
				initializeGame();
			}

		});

		i_scores.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				leastMoves = sp.getInt("highscore", 0);
				if(leastMoves == 10000){
					leastMoves = 0;
				}

				final Dialog dialog = new Dialog(GameActivity.this);
				dialog.setContentView(R.layout.highscore_dialog);
				Window window = dialog.getWindow();
				window.setBackgroundDrawableResource(R.drawable.blank);
				dialog.setCancelable(true);

				d_highscore = (TextView)dialog.findViewById(R.id.highscore);
				dialog.show();
				d_highscore.setText(Integer.toString(leastMoves));
				d_highscore.setTextColor(Color.parseColor(bg[bg_index]));
			}

		});

		i_menu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				finish();

			}

		});



		done.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cont=true;

				uip[0] = tv_d_1.getText().toString();
				uip[1] = tv_d_2.getText().toString();
				uip[2] = tv_d_3.getText().toString();
				uip[3] = tv_d_4.getText().toString();


				// If any field is empty
				if(uip[0].equals("")||uip[2].equals("")||uip[3].equals("")||uip[1].equals(""))
				{
					Snackbar.make(v, "Please do not leave any field empty.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
					tv_d_1.setBackgroundResource(R.drawable.bg_red);
					tv_d_2.setBackgroundResource(R.drawable.bg_red);
					tv_d_3.setBackgroundResource(R.drawable.bg_red);
					tv_d_4.setBackgroundResource(R.drawable.bg_red);
					cont=false;
				}
				else
				{	
					tv_d_1.setBackgroundResource(R.drawable.bg);
					tv_d_2.setBackgroundResource(R.drawable.bg);
					tv_d_3.setBackgroundResource(R.drawable.bg);
					tv_d_4.setBackgroundResource(R.drawable.bg);

					for(int i=0;i<3;i++)
						for(int k=i+1;k<4;k++)
							if (uip[i].equals(uip[k]))
							{
								Snackbar.make(v, "Enter distinct digits.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
								if(i==0)
									tv_d_1.setBackgroundResource(R.drawable.bg_red);
								else if (i==1)
									tv_d_2.setBackgroundResource(R.drawable.bg_red);
								else if (i==2)
									tv_d_3.setBackgroundResource(R.drawable.bg_red);
								else if (i==3)
									tv_d_4.setBackgroundResource(R.drawable.bg_red);

								if(k==0)
									tv_d_1.setBackgroundResource(R.drawable.bg_red);
								else if (k==1)
									tv_d_2.setBackgroundResource(R.drawable.bg_red);
								else if (k==2)
									tv_d_3.setBackgroundResource(R.drawable.bg_red);
								else if (k==3)
									tv_d_4.setBackgroundResource(R.drawable.bg_red);

								cont=false;
								break;
							}

					if (cont)
					{
						tv_d_1.setHint(tv_d_1.getText().toString());
						tv_d_2.setHint(tv_d_2.getText().toString());
						tv_d_3.setHint(tv_d_3.getText().toString());
						tv_d_4.setHint(tv_d_4.getText().toString());

						tv_d_1.setText("");
						tv_d_2.setText("");
						tv_d_3.setText("");
						tv_d_4.setText("");

						bulls=0;
						cows=0;

						//Calculate Bulls
						for(int i=0;i<4;i++)
							if(uip[i].equals(rno[i]))
								bulls+=1;						



						//Calculate Cows
						for(int i=0;i<4;i++)
							for(int k=0;k<4;k++)
								if (uip[i].equals(rno[k]))
								{
									cows+=1;
								}

						cows=cows-bulls;

						tv_bulls.setText("Hits: " + String.valueOf(bulls) + "         Clicks: " + String.valueOf(cows));
						Random r1 = new Random();
						int r_int = r1.nextInt(6);
						long l_int = r1.nextInt(1000000000);

						final TextView row1 = new TextView(getBaseContext());
						row1.generateViewId();
						row1.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
						row1.setText(">> " + random_jargon[r_int]);
						row1.setTextColor(Color.parseColor("#BBFFFFFF"));
						row1.setTypeface(Typeface.MONOSPACE);

						final TextView row2 = new TextView(getBaseContext());
						row2.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
						row2.setText(uip[0] + "" + uip[1] + "" + uip[2] + "" + uip[3] + "> " + "Hits:" + String.valueOf(bulls) + " && Clicks:" + String.valueOf(cows));
						row2.setTextColor(Color.parseColor("#FFFFFFFF"));
						row2.setTextSize(22);
						row2.setTypeface(Typeface.MONOSPACE);
						feed_count+=1;

						final TextView row3 = new TextView(getBaseContext());
						row3.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
						row3.setText(Long.toString(l_int)+"**********************");
						row3.setTextSize(10);
						row3.setTextColor(Color.parseColor("#AAFFFFFF"));
						row3.setTypeface(Typeface.MONOSPACE);

						rl_feed.addView(row1);
						rl_feed.addView(row2);
						rl_feed.addView(row3);
						rl_feed.setVisibility(View.VISIBLE);
						rl_feed.setBackgroundResource(R.drawable.card_background_layout);

						if(bulls==4&&cows==0)
						{
							tv_bulls.setText("Phew! That was close. Attempts: "+String.valueOf(feed_count)+" times.");

							//Update stats
							no_games++;
							avg_mpg = (avg_mpg*no_games + feed_count)/no_games;
							ed.putFloat("MPG", avg_mpg);
							ed.putInt("GAMES", no_games);
							ed.commit();

							if(leastMoves>=feed_count)
							{
								ed.putInt(HIGHSCORE, feed_count);
								ed.commit();
								tv_bulls.setText("You have outdone yourself Agent B!. New Record!");
							}
						}
					}
				}
			}

		});
		


	}

	@SuppressLint("ShowToast")
	void sendToast(Object charSequence){
		Toast.makeText(getBaseContext(), (String)charSequence, Toast.LENGTH_SHORT);

	}

	public void initializeGame(){
		tv_bulls.setText(">> initiating code breaker -d");
		
		tv_d_1.setHint("");
		tv_d_2.setHint("");
		tv_d_3.setHint("");
		tv_d_4.setHint("");
		
		// Random Digits Generator
		r = new Random();

		// Random Background Color
		bg_index=r.nextInt(8);
		rl_body.setBackgroundColor(Color.parseColor(bg[bg_index]));
		tv_bulls.setTextColor(Color.parseColor(bg[bg_index]));
		tv_d_1.setTextColor(Color.parseColor(bg[bg_index]));
		tv_d_2.setTextColor(Color.parseColor(bg[bg_index]));
		tv_d_3.setTextColor(Color.parseColor(bg[bg_index]));
		tv_d_4.setTextColor(Color.parseColor(bg[bg_index]));


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
	}
}
