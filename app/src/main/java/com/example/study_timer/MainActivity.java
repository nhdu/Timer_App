package com.example.study_timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageButton play, pause, stop;
    private TextView view_time;
    private EditText input_task;
    private Chronometer stop_watch;
    private Boolean running = false;
    private long pauseOffset = 0;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = (ImageButton) findViewById(R.id.play_button);
        pause = (ImageButton) findViewById(R.id.pause_button);
        stop = (ImageButton) findViewById(R.id.stop_button);
        input_task = (EditText) findViewById(R.id.user_input);
        view_time = (TextView) findViewById(R.id.time_spent);
        stop_watch = (Chronometer) findViewById(R.id.timer_view);
        sharedPreferences = getSharedPreferences("com.example.study_timer", MODE_PRIVATE);
        if (savedInstanceState != null)
        {
            input_task.setText(savedInstanceState.getString("input"));
            pauseOffset = savedInstanceState.getLong("offset");
            running = savedInstanceState.getBoolean("timer_running");
            if (running)
            {
                stop_watch.setBase(savedInstanceState.getLong("time"));
                stop_watch.start();
            }
            else {
                stop_watch.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            }
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!running) {
                    stop_watch.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    stop_watch.start();
                    running = true;
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (running) {
                    stop_watch.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - stop_watch.getBase();
                    running = false;
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_task.getText().toString().trim().equals(""))
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter your task", Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("task_name", input_task.getText().toString());
                    editor.putString("time_tracked", stop_watch.getText().toString());
                    editor.apply();
                    stop_watch.setBase(SystemClock.elapsedRealtime());
                    pauseOffset = 0;
                    stop_watch.stop();
                    running = false;
                    view_time.setText("You spent " + sharedPreferences.getString("time_tracked", "") + " on " + sharedPreferences.getString("task_name", "") + " last time.");
                }


            }

        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("timer_running", running);
        outState.putLong("time", stop_watch.getBase());
        outState.putLong("offset", pauseOffset);
        outState.putString("input", input_task.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences("com.example.study_timer", MODE_PRIVATE);
        String user_task = pref.getString("task_name", "");
        String time_string = pref.getString("time_tracked", "");
        view_time.setText("You spent " + time_string + " on " + user_task + " last time.");
    }
}