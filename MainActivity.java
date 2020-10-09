package com.kgbrussia7.a7exegerussian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textViewEge;
    private TextView textViewSolved;
    private TextView textViewTitle;
    private Button buttonStart;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        textViewEge = findViewById(R.id.textViewEge);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewSolved = findViewById(R.id.textViewSolved);
        buttonStart = findViewById(R.id.buttonStart);
        textViewEge.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
        textViewTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
        textViewSolved.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
        buttonStart.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        score = preferences.getInt("score", 0);
        textViewSolved.setText(String.format("Решено: %s", score));
    }
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }

    public void onClickStartGame(View view) {
        Intent intent = new Intent(this, com.kgbrussia7.a7exegerussian.GameActivity.class);
        startActivity(intent);
    }
}
