package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class ActivityRegister extends AppCompatActivity {

    TextView tvAlready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvAlready = findViewById(R.id.tvAlready);

        tvAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pagelogin = new Intent(ActivityRegister.this,ActivityLogin.class);
                startActivity(pagelogin);
            }
        });
    }
}