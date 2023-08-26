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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ReportAdapter;
import com.example.myapplication.adapter.ThreadAdapter;
import com.example.myapplication.model.ReportModel;
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

public class ActivityListReport extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private List<ReportModel> reportList = new ArrayList<>();
    private List<User>userList = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference reportCollection = firestore.collection("report");
    private CollectionReference userCollection = firestore.collection("users");
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private int visibleThreshold = 5;

    private AppCompatButton btnAddReport;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout rlEmpty,rlLoading;

    private ImageView ivBack;
    private TextView tvTitleToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_report);



        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reportAdapter = new  ReportAdapter(reportList,userList);
        recyclerView.setAdapter(reportAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        ivBack = findViewById(R.id.ivBack);
        rlEmpty = findViewById(R.id.rlEmpty);
        rlLoading = findViewById(R.id.rlLoading);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);

        tvTitleToolbar.setText("List Report");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnAddReport = findViewById(R.id.btnAddReport);

        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityListReport.this,ActivityPostReport.class);
                startActivityForResult(intent,1312);
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
        // Clear the existing reportList before loading new data
        reportList.clear();

        reportCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("isPublish",true)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                ReportModel report = document.toObject(ReportModel.class);
                                reportList.add(report);


                                FirebaseFirestore.getInstance().collection("users")
                                        .document(report.getUserId()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        User user = document.toObject(User.class);
                                                        if (user != null) {
                                                            userList.add(user);
                                                        }
                                                    }
                                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if(!reportList.isEmpty()){
                                                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                                                reportAdapter.notifyDataSetChanged();
                                                                rlEmpty.setVisibility(View.GONE);
                                                                rlLoading.setVisibility(View.GONE);

                                                            }else {
                                                                rlEmpty.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    }, 200);
                                                }
                                            }
                                        });
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });
    }


    private void loadMoreReport() {
        reportCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
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
                                    ReportModel report = document.toObject(ReportModel.class);
                                    reportList.add(report);

                                }
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                reportAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });
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

