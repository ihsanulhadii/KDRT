package com.example.myapplication.base;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);

    }


    public String getUserId(){
        String userId = sharedPreferences.getString("userId","" );
        return  userId;
    }
}
