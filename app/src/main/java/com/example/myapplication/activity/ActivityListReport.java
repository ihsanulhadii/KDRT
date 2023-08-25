package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference reportCollection = firestore.collection("report");
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private int visibleThreshold = 5;

    private AppCompatButton btnAddReport;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_report);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(reportList);
        recyclerView.setAdapter(reportAdapter);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);


        btnAddReport = findViewById(R.id.btnAddReport);

        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityListReport.this,ActivityPostReport.class);
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

    private void loadReport() {
        // Clear the existing ReportList before loading new data
        reportList.clear();

        reportCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                ReportModel report = document.toObject(ReportModel.class);
                                reportList.add(report);
                            }
                            lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                            reportAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Handle errors
                    }
                });
    }


    private void loadMoreReport() {
        reportCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                ReportModel report = document.toObject(ReportModel.class);
                                reportList.add(report);
                            }
                            if (!querySnapshot.isEmpty()) {
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                            }
                            reportAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Penanganan kesalahan
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

