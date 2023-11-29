package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatAdapter;
import com.example.myapplication.adapter.CommentAdapter;
import com.example.myapplication.model.ChatModel;
import com.example.myapplication.model.CommentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActivityDetailArticle extends AppCompatActivity {

    private String title,img,content;
    private TextView tvContent,tvTitle,tvTitleToolbar;
    private ImageView ivArticle,ivBack, ivSend;

    private EditText etMessage;

    private String message,userId;

    private SharedPreferences sharedPreferences;


    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private List<CommentModel> commentModelList = new ArrayList<>();
    private CollectionReference commentsCollection = firestore.collection("articles");

    private RecyclerView recyclerView;

    private CommentAdapter commentAdapter;


    private String commentRoomId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_a                    rticle);


        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");

        commentRoomId = getIntent().getStringExtra("commentRoomId");

        title = getIntent().getStringExtra("title");
        img = getIntent().getStringExtra("img");
        content = getIntent().getStringExtra("content");


        tvContent = findViewById(R.id.tvContent);
        ivArticle = findViewById(R.id.ivArticle);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText("Detail Artikel");
        ivBack = findViewById(R.id.ivBack);
        ivSend = findViewById(R.id.ivSend);
        etMessage = findViewById(R.id.etMessage);

        tvContent.setText(content);
        tvTitle.setText(title);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentAdapter = new CommentAdapter(ActivityDetailArticle.this,commentModelList);
        recyclerView.setAdapter(commentAdapter);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString();
                if(!message.isEmpty()){
                    sendMessage();
                }
            }
        });

        getListComment();
      //  startChatListener();




        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


      /* Picasso.get()
                .load(img)  // Assuming getImg() returns the image URL
                *//*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*//*
                .error(R.drawable.image_blank) // Error image if loading fails
                .fit() // Resize the image to fit the ImageView dimensions
                .centerCrop() // Crop the image to fill the ImageView
                .into(ivArticle); // ImageView to load the image into
*/



    @SuppressLint("NotifyDataSetChanged")
    private void getListComment() {
        // Clear the existing threadList before loading new data
        commentModelList.clear();

        commentsCollection.document(commentRoomId).collection("articles").orderBy("time", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (DocumentSnapshot document : task.getResult()) {
                            CommentModel commentModel = document.toObject(CommentModel.class);
                            commentModelList.add(commentModel);
                            commentAdapter.notifyDataSetChanged();
                        }
                    }

                });
    }

    private void sendMessage(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String idChat = UUID.randomUUID().toString();
        Timestamp timestamp = Timestamp.now();
        //untuk field dan value di database
        Map<String, Object> data = new HashMap<>();
        data.put("content",message);
        data.put("userId",userId);
        data.put("time",timestamp);

        //collection database
        db.collection("articles").document(commentRoomId).collection("comments").document(idChat).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        etMessage.setText("");
                        hideKeyboard();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // hideLoading();
                        showToast("Komen Gagal terkirim "+e.getMessage());
                    }
                });
    }

    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
