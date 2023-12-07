package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ThreadAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.model.CommentModel;
import com.example.myapplication.model.ThreadModel;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ActivityListThreads extends BaseActivity {

    private RecyclerView recyclerView;
    private ThreadAdapter threadAdapter;
    private List<ThreadModel> threadList = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference threadsCollection = firestore.collection("threads");
    private CollectionReference userCollection = firestore.collection("users");
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private int visibleThreshold = 5;

    private AppCompatButton btnAddThreads;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout rlEmpty,rlLoading;

    private ImageView ivBack;
    private TextView tvTitleToolbar;

    String userId;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_threads);

        handler = new Handler(Looper.getMainLooper());

        recyclerView  = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        threadAdapter = new ThreadAdapter(getUserId(),threadList);
        recyclerView.setAdapter(threadAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        ivBack             = findViewById(R.id.ivBack);
        rlEmpty            = findViewById(R.id.rlEmpty);
        rlLoading          = findViewById(R.id.rlLoading);
        tvTitleToolbar     = findViewById(R.id.tvTitleToolbar);

        tvTitleToolbar.setText("List Threads");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnAddThreads = findViewById(R.id.btnAddThreads);

        btnAddThreads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityListThreads.this,ActivityPostThreads.class);
                startActivityForResult(intent,1313);
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
                    loadMoreThreads();
                }
            }
        });

        loadThreads();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadThreads();
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadThreads() {
        // Clear the existing threadList before loading new data
        threadList.clear();

        threadsCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("isPublish",true)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && querySnapshot.size()>0) {
                            for (DocumentSnapshot document : querySnapshot) {
                                ThreadModel thread = document.toObject(ThreadModel.class);
                                threadList.add(thread);
                                if(threadList.isEmpty()){
                                    rlEmpty.setVisibility(View.VISIBLE);
                                    rlLoading.setVisibility(View.GONE);
                                }else {
                                    userCollection
                                            .document(userId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot userDocument = task.getResult();
                                                        if (userDocument.exists()) {
                                                            String userName = userDocument.getString("name");
                                                            String avatar = userDocument.getString("avatar");

                                                            Log.d("xxx11",userName +" "+ avatar);
                                                          // threadList.add(new ThreadModel(thread.getId(),userId,thread.getDescription(),"" ,userName,avatar));
                                                            handler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if(!threadList.isEmpty()){
                                                                        lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                                                        threadAdapter.notifyDataSetChanged();
                                                                        threadAdapter.setOnItemClickListener(new ThreadAdapter.OnItemClickListener() {
                                                                            @Override
                                                                            public void onItemClick(ThreadModel thread) {
                                                                                Intent intent = new Intent(ActivityListThreads.this, ActivityDetailThreads.class);
                                                                                intent.putExtra("title", thread.getTitle()); // Kirim data thread ke aktivitas detail
                                                                                intent.putExtra("img",thread.getImg());
                                                                                intent.putExtra("description",thread.getDescription());
                                                                                startActivity(intent);
                                                                            }
                                                                        });
                                                                        rlEmpty.setVisibility(View.GONE);
                                                                        rlLoading.setVisibility(View.GONE);

                                                                    }else {
                                                                        rlEmpty.setVisibility(View.VISIBLE);
                                                                    }
                                                                }
                                                            });


                                                        }
                                                    } else {
                                                        Log.d("xxx",task.getException().getMessage());
                                                    }
                                                }
                                            });

                                }
                            }
                        }else {
                            rlEmpty.setVisibility(View.VISIBLE);
                            rlLoading.setVisibility(View.GONE);
                        }






                    } else {
                        showToast(String.valueOf(threadList.size()));
                        // Handle errors
                    }
                });
    }


    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }



    private void loadMoreThreads() {
        /*threadsCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .whereEqualTo("isPublish",true)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            if (!querySnapshot.isEmpty()) { // Check if there are documents in the query result
                                for (DocumentSnapshot document : querySnapshot) {
                                    ThreadModel thread = document.toObject(ThreadModel.class);
                                    threadList.add(thread);

                                }
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                threadAdapter.notifyDataSetChanged();
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
                    loadThreads();
                }
            }
        }

    }
}

