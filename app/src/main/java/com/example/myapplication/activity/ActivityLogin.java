package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;

public class ActivityLogin extends AppCompatActivity {
 TextView tvCreateAccount;
 AppCompatButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        btnLogin = findViewById(R.id.btnLogin);

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextPage = new Intent( ActivityLogin.this, ActivityRegister.class);
                startActivity(nextPage);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextPage = new Intent( ActivityLogin.this, MainActivity.class);
                startActivity(nextPage);
            }
        });
    }
}