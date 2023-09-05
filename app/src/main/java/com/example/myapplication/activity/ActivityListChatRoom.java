package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatRoomAdapter;
import com.example.myapplication.adapter.ReportAdapter;
import com.example.myapplication.model.ChatRoomModel;
import com.example.myapplication.model.ReportModel;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityListChatRoom extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatRoomAdapter rchatRoomAdapter;
    private List<ChatRoomModel> chatRoomModelList = new ArrayList<>();
    private List<User>userList = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference threadsCollection = firestore.collection("chat_room");
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
        setContentView(R.layout.activity_list_chatroom);

        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rchatRoomAdapter = new ChatRoomAdapter(chatRoomModelList);
        recyclerView.setAdapter(rchatRoomAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        ivBack = findViewById(R.id.ivBack);
        rlEmpty = findViewById(R.id.rlEmpty);
        rlLoading = findViewById(R.id.rlLoading);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);

        tvTitleToolbar.setText("Chat Konselor");
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
                    loadMoreReport();
                }
            }
        });

        loadChatRoom();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadChatRoom();
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadChatRoom() {
        // Clear the existing threadList before loading new data
        chatRoomModelList.clear();

        threadsCollection.orderBy("updatedDate", Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("receiver",userId)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    rlLoading.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            ChatRoomModel chatRoomModel;
                            for (DocumentSnapshot document : querySnapshot) {
                                chatRoomModel = document.toObject(ChatRoomModel.class);
                                chatRoomModelList.add(chatRoomModel);
                            }

                            if (!chatRoomModelList.isEmpty()) {
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                rchatRoomAdapter.notifyDataSetChanged();

                                rchatRoomAdapter.setOnItemClickListener(new ChatRoomAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(ChatRoomModel reportModel) {
                                        Intent intent = new Intent(ActivityListChatRoom.this, ChatActivity.class);
                                       /* intent.putExtra("title", reportModel.getTitle()); // Kirim data report ke aktivitas detail
                                        intent.putExtra("img",reportModel.getImg());
                                        intent.putExtra("description",reportModel.getDescription());*/
                                        startActivity(intent);
                                    }
                                });

                                rlEmpty.setVisibility(View.GONE);
                            } else {
                                rlEmpty.setVisibility(View.VISIBLE);

                            }

                        }
                    }
                });
    }


    private void loadMoreReport() {
       /* threadsCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(10)
                .whereEqualTo("userId",userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            if (!querySnapshot.isEmpty()) { // Check if there are documents in the query result
                                for (DocumentSnapshot document : querySnapshot) {
                                    ReportModel reportModel = document.toObject(ReportModel.class);
                                    chatRoomModelList.add(reportModel);

                                }
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                rchatRoomAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1313 && resultCode == RESULT_OK){
            if(data.hasExtra("isLoad")){
                Boolean isLoad = data.getBooleanExtra("isLoad",false);
                if(isLoad){
                    loadChatRoom();
                }
            }
        }

    }
}

