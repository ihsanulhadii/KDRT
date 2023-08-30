package com.example.myapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.ThreadModel;
import com.squareup.picasso.Picasso;

public class ActivityDetailReport extends AppCompatActivity {

    private String title,img,description;
    private TextView tvDescription,tvTitle,tvTitleToolbar;
    private ImageView ivReport,ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_report);

        title = getIntent().getStringExtra("title");
        img = getIntent().getStringExtra("img");
        description = getIntent().getStringExtra("description");


        tvDescription = findViewById(R.id.tvDescription);
        ivReport = findViewById(R.id.ivReport);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText("Detail Report");
        ivBack = findViewById(R.id.ivBack);

        tvDescription.setText(description);
        tvTitle.setText(title);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Picasso.get()
                .load(img)  // Assuming getImg() returns the image URL
                /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
                .error(R.drawable.image_blank) // Error image if loading fails
                .fit() // Resize the image to fit the ImageView dimensions
                .centerCrop() // Crop the image to fill the ImageView
                .into(ivReport); // ImageView to load the image into


    }
}
