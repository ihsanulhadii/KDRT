package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CounselorAdapter;
import com.example.myapplication.model.ChatRoom;
import com.example.myapplication.model.CounselorModel;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityListCounselor extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CounselorAdapter counselorAdapter;
    private List<CounselorModel> counselorModelList = new ArrayList<>();
    private List<User>userList = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference threadsCollection = firestore.collection("counselor");

    private CollectionReference chatRoomReference = firestore.collection("chats");

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
        setContentView(R.layout.activity_list_counselor);

        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","");


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        counselorAdapter = new CounselorAdapter(counselorModelList);
        recyclerView.setAdapter(counselorAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        ivBack = findViewById(R.id.ivBack);
        rlEmpty = findViewById(R.id.rlEmpty);
        rlLoading = findViewById(R.id.rlLoading);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);

        tvTitleToolbar.setText(Constants.titleToolbar);
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
                    //loadMoreReport();
                }
            }
        });

        loadReport();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadReport();
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadReport() {
        // Clear the existing threadList before loading new data
        counselorModelList.clear();

        threadsCollection
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    rlLoading.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            CounselorModel counselorModel;
                            for (DocumentSnapshot document : querySnapshot) {
                                counselorModel = document.toObject(CounselorModel.class);
                                counselorModelList.add(counselorModel);
                            }

                            if (!counselorModelList.isEmpty()) {
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                counselorAdapter.notifyDataSetChanged();

                                counselorAdapter.setOnItemClickListener(new CounselorAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(CounselorModel counselorModel) {
                                        checkChatRoom(counselorModel);
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


    private void checkChatRoom(CounselorModel counselorModel){

        // CollectionReference chatRoomReference = firestore.collection("chats");

        chatRoomReference
                .whereEqualTo("counselorId", counselorModel.getId())
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                // Parse the data into a model (e.g., UserModel)
                                ChatRoom chatRoomModel = document.toObject(ChatRoom.class);

                                if (chatRoomModel != null) {
                                    // Access data in the UserModel
                                    String id = chatRoomModel.getId();
                                    Intent intent = new Intent(ActivityListCounselor.this, ChatActivity.class);
                                    intent.putExtra("counselor",counselorModel);
                                    intent.putExtra("chatRoomId",id);
                                    startActivity(intent);
                                    // Do something with the parsed data
                                }
                            }
                        } else {
                            // Create ChatRoom
                            createChatRoom(counselorModel);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        showToast(e.getMessage().toString());
                    }
                });
    }

    private void createChatRoom(CounselorModel counselorModel){
        //String UUID = java.util.UUID.randomUUID().toString();
        DocumentReference documentReferences = chatRoomReference.document();
// Create a data map for the document
        Map<String, Object> data = new HashMap<>();
        data.put("counselorId", counselorModel.getId());
        data.put("userId", userId);
        data.put("id",documentReferences.getId().toString());
        data.put("updatedDate", Timestamp.now());

        chatRoomReference.document(documentReferences.getId().toString()).set(data)
                .addOnSuccessListener(aVoid -> {

                    Intent intent = new Intent(ActivityListCounselor.this, ChatActivity.class);
                    intent.putExtra("counselor",counselorModel);
                    intent.putExtra("chatRoomId",documentReferences.getId().toString());
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    showToast(e.getMessage().toString());
                });

    }

    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1313 && resultCode == RESULT_OK){
            if(data.hasExtra("isLoad")){
                Boolean isLoad = data.getBooleanExtra("isLoad",false);
                if(isLoad){
                    loadReport();
                }
            }
        }

    }
}

