package com.example.myapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

public class ActivityDetailArticle extends AppCompatActivity {

    private String title,img,content;
    private TextView tvContent,tvTitle,tvTitleToolbar;
    private ImageView ivArticle,ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_article);

        title = getIntent().getStringExtra("title");
        img = getIntent().getStringExtra("img");
        content = getIntent().getStringExtra("content");


        tvContent = findViewById(R.id.tvContent);
        ivArticle = findViewById(R.id.ivArticle);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText("Detail Artikel");
        ivBack = findViewById(R.id.ivBack);

        tvContent.setText(content);
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
                .into(ivArticle); // ImageView to load the image into


    }
}
