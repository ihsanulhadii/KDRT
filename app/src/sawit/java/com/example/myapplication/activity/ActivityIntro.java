package com.example.myapplication.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;

public class ActivityIntro extends AppCompatActivity {

    AppCompatButton btnGet;

    Button btnOpenWebsite;

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
    public void openWebsite(View view) {
        // URL website yang ingin diarahkan
        String websiteUrl = getString(R.string.url_web);

        // Buat Intent untuk membuka browser dengan URL website
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));

        // Coba untuk menjalankan Intent
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle kesalahan jika tidak ada aplikasi browser yang tersedia
            e.printStackTrace();
        }
    }
}
