package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;

public class ActivityIntro extends AppCompatActivity {

    AppCompatButton btnGet;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);
        Boolean isLogin = sharedPreferences.getBoolean("isLogin",false);
        if(isLogin){
            Intent nextPage  = new Intent( ActivityIntro.this, MainActivity.class);
            startActivity(nextPage);
            finish();
        }else {
            btnGet= findViewById(R.id.btnGet);
            btnGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent nextPage  = new Intent( ActivityIntro.this, ActivityLogin.class);
                    startActivity(nextPage);
                    finish();

                }
            });


        }


    }
}
