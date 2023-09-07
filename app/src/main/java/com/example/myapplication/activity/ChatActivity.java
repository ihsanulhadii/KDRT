package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatAdapter;
import com.example.myapplication.adapter.ChatRoomAdapter;
import com.example.myapplication.model.Admin;
import com.example.myapplication.model.ChatModel;
import com.example.myapplication.model.ChatRoomModel;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String title,img,content;
    private TextView tvContent,tvTitle,tvTitleToolbar,tvName;
    private ImageView ivArticle,ivBack;
    private CircleImageView ivAvatar;

    private ChatRoomModel chatRoomModel;
    private Admin admin;
    private EditText etMessage;
    private ImageView ivSend;

    private String message,userId;

    private SharedPreferences sharedPreferences;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private List<ChatModel> chatModelList = new ArrayList<>();
    private CollectionReference chatCollection = firestore.collection("chats");

    private RecyclerView recyclerView;

    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");

        chatRoomModel = (ChatRoomModel) getIntent().getSerializableExtra("chatroom");
        admin = (Admin) getIntent().getSerializableExtra("admin");


        ivAvatar = findViewById(R.id.ivAvatar);
        tvName = findViewById(R.id.tvName);
        ivSend = findViewById(R.id.ivSend);
        etMessage = findViewById(R.id.etMessage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatAdapter = new ChatAdapter(ChatActivity.this,chatModelList);
        recyclerView.setAdapter(chatAdapter);

        tvName.setText(admin.getName());

        Picasso.get()
                .load(admin.getImg())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .fit() // Resize the image to fit the ImageView dimensions
                .centerCrop() // Crop the image to fill the ImageView
                .into(ivAvatar); // ImageView to load the image into*/



        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString();
                if(!message.isEmpty()){
                    sendMessage();
                }
            }
        });

        getListChat();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListChat() {
        // Clear the existing threadList before loading new data
        chatModelList.clear();

        chatCollection.document(chatRoomModel.getId()).collection("chat").orderBy("time", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       for (DocumentSnapshot document : task.getResult()) {
                           ChatModel chatModel = document.toObject(ChatModel.class);
                           chatModelList.add(chatModel);
                           chatAdapter.notifyDataSetChanged();
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
        data.put("id",idChat);
        data.put("sender",userId);
        data.put("receiver",admin.getId());
        data.put("time",timestamp);

        //collection database
        db.collection("chats").document(chatRoomModel.getId()).collection("chat").document(idChat).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        etMessage.setText("");
                        ChatModel chatModel = new ChatModel();
                        chatModel.setId(idChat);
                        chatModel.setContent(message);
                        chatModel.setReceiver(admin.getId());
                        chatModel.setSender(userId);
                        chatModel.setTimestamp(timestamp);
                        chatModelList.add(chatModel);
                        chatAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // hideLoading();
                        showToast("Pesan Gagal terkirim "+e.getMessage());
                    }
                });
    }




    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
