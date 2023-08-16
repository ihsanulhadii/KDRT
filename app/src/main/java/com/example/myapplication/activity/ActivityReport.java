package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.fragment.HomeFragment;

public class ActivityReport extends AppCompatActivity {

   ImageView ivBack;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_report);

      ivBack = findViewById(R.id.ivBack);

      ivBack.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent backpage = new Intent(ActivityReport.this, HomeFragment.class);
            startActivity(backpage);
         }
      }
      );
   }
}
