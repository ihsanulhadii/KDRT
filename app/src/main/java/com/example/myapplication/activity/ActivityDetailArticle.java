package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ActivityDetailArticle extends AppCompatActivity {

    private String title,img,content;
    private TextView tvContent,tvTitle,tvTitleToolbar, tvDate;
    private ImageView ivArticle,ivBack, ivSend;

    private EditText etMessage;

    private String message,userId;

    private SharedPreferences sharedPreferences;


    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private List<CommentModel> commentModelList = new ArrayList<>();
    private CollectionReference commentsCollection = firestore.collection("articles");

    private RecyclerView recyclerView;

    private CommentAdapter commentAdapter;


    private String id,date;

    private ListenerRegistration chatListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_article);


        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");

        id = getIntent().getStringExtra("id");
        date = getIntent().getStringExtra("date");

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
        tvDate = findViewById(R.id.tvDate);

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

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID")); // Indonesian locale

        try {
            Date date1 = inputFormat.parse(date);
            String outputDate = outputFormat.format(date1);
            tvDate.setText(outputDate);

            System.out.println(outputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(!img.isEmpty()){
            Picasso.get()
                    .load(img)
                    .error(R.drawable.image_blank) // Error image if loading fails
                    .fit() // Resize the image to fit the ImageView dimensions
                    .centerCrop() // Crop the image to fill the ImageView
                    .into(ivArticle); // ImageView to load the image into
        }


        getListComment();
        startCommentListener();

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


    @SuppressLint("NotifyDataSetChanged")
    private void getListComment() {
        // Clear the existing threadList before loading new data
        commentModelList.clear();

        commentsCollection.document(id).collection("comments").orderBy("time", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (DocumentSnapshot document : task.getResult()) {
                            CommentModel commentModel = document.toObject(CommentModel.class);
                            Log.d("coklat", new Gson().toJson(commentModel));
                            commentModelList.add(commentModel);
                        }
                    }

                });
    }


    private void startCommentListener() {
        chatListener = commentsCollection.document(id)
                .collection("comments")
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        // Clear the existing chatModelList before loading new data
                        commentModelList.clear();

                        for (DocumentSnapshot document : querySnapshot) {
                            CommentModel chatModel = document.toObject(CommentModel.class);
                            commentModelList.add(chatModel);
                        }

                        // Update the UI with the new data
                        commentAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void sendMessage(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String idComment = UUID.randomUUID().toString();
        Timestamp timestamp = Timestamp.now();
        //untuk field dan value di database
        Map<String, Object> data = new HashMap<>();
        data.put("content",message);
        data.put("userId",userId);
        data.put("time",timestamp);
        data.put("id",idComment);

        //collection database
        db.collection("articles").document(id).collection("comments").document(idComment).set(data)
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
