package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatRoomAdapter;
import com.example.myapplication.adapter.ThreadAdapter;
import com.example.myapplication.model.Admin;
import com.example.myapplication.model.ChatRoomModel;
import com.example.myapplication.model.ThreadModel;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ListRoom extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatRoomAdapter articleListAdapter;
    private List<ChatRoomModel> articleModelList = new ArrayList<>();
    private List<Admin>userList = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference articelCollection = firestore.collection("chats");
    private CollectionReference adminCollection = firestore.collection("admin");
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private int visibleThreshold = 5;


    private SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout rlEmpty,rlLoading;

    private ImageView ivBack;
    private TextView tvTitleToolbar;
    private String userId;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_article);

        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");
        Log.d("userId",userId);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        articleListAdapter = new ChatRoomAdapter(articleModelList,userList);
        recyclerView.setAdapter(articleListAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        ivBack = findViewById(R.id.ivBack);
        rlEmpty = findViewById(R.id.rlEmpty);
        rlLoading = findViewById(R.id.rlLoading);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);

        tvTitleToolbar.setText("List Room");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (isScrolling && lastVisibleItem + visibleThreshold >= totalItemCount) {
                    isScrolling = false;
                    //loadMoreArticel();
                }
            }
        });

        getListArticle();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getListArticle();
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getListArticle() {
        // Clear the existing threadList before loading new data
        articleModelList.clear();

        articelCollection.orderBy("updatedDate", Query.Direction.DESCENDING)
                .whereEqualTo("receiver",userId)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);

                    Handler mainHandler = new Handler(Looper.getMainLooper());

                    Thread adminThread = new Thread(() -> {
                        for (DocumentSnapshot document : task.getResult()) {
                            ChatRoomModel chatRoomModel = document.toObject(ChatRoomModel.class);
                            articleModelList.add(chatRoomModel);
                            Log.d("adminya1", chatRoomModel.getSender());
                        }

                        // When the for loop is done, post a Runnable to call getAdminList on the main thread
                        mainHandler.post(() -> {
                            getAdminList(); // Call your getAdminList function here
                        });
                    });

                    adminThread.start();

                });
    }

    private void getAdminList(){
        for (int i = 0; i < articleModelList.size(); i++) {
            String idAdmin = articleModelList.get(i).getSender();
            Log.d("adminya-"+articleModelList.size()+"-"+i, idAdmin);

            FirebaseFirestore.getInstance().collection("admin")
                    .whereEqualTo("id",idAdmin)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                if (document.exists()) {
                                    Admin admin = document.toObject(Admin.class);
                                    Log.d("adminya3", admin.getName());
                                    userList.add(admin);
                                } else {
                                    Log.d("adminya3", "not exist");
                                    // Handle the case where the document doesn't exist
                                    // You can log an error message or perform other actions here.
                                }
                            }
                            updateData();
                        } else {
                            // Handle the error
                            Exception exception = task.getException();
                            if (exception != null) {
                                Log.d("adminya3", exception.getMessage());
                                // Log or handle the exception
                            }
                        }
                    });

        }



    }

    private void updateData(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!articleModelList.isEmpty()){
                    articleListAdapter.notifyDataSetChanged();
                    articleListAdapter.setOnItemClickListener(new ChatRoomAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ChatRoomModel chatRoomModel, Admin admin) {
                            Intent intent = new Intent(ListRoom.this, ChatActivity.class);
                            intent.putExtra("chatroom",chatRoomModel);
                            intent.putExtra("admin",admin);

                            startActivity(intent);
                        }
                    });
                    rlEmpty.setVisibility(View.GONE);
                    rlLoading.setVisibility(View.GONE);

                }else {
                    rlEmpty.setVisibility(View.VISIBLE);
                }
            }
        }, 200);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1313 && resultCode == RESULT_OK){
            if(data.hasExtra("isLoad")){
                Boolean isLoad = data.getBooleanExtra("isLoad",false);
                if(isLoad){
                    getListArticle();
                }
            }
        }

    }
}

