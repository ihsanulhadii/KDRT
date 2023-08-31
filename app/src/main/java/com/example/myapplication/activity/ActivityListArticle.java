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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ArtikelAdapter;
import com.example.myapplication.model.ArticleModel;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityListArticle extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArtikelAdapter artikelAdapter;
    private List<ArticleModel> articleModelList = new ArrayList<>();
    private List<User>userList = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference articelCollection = firestore.collection("articles");
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


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        artikelAdapter = new ArtikelAdapter(this,articleModelList);
        recyclerView.setAdapter(artikelAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        ivBack = findViewById(R.id.ivBack);
        rlEmpty = findViewById(R.id.rlEmpty);
        rlLoading = findViewById(R.id.rlLoading);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);

        tvTitleToolbar.setText("List Artikel");
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
                    loadMoreArticel();
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

        articelCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("isPublish", true)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    rlLoading.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            ArticleModel articleModel;
                            for (DocumentSnapshot document : querySnapshot) {
                                articleModel = document.toObject(ArticleModel.class);
                                articleModelList.add(articleModel);
                            }

                            if (!articleModelList.isEmpty()) {
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                artikelAdapter.notifyDataSetChanged();

                                artikelAdapter.setOnItemClickListener(new ArtikelAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(ArticleModel articleModel) {
                                        Intent intent = new Intent(ActivityListArticle.this, ActivityDetailArticle.class);
                                        intent.putExtra("title", articleModel.getTitle()); // Kirim data report ke aktivitas detail
                                        intent.putExtra("img",articleModel.getImg());
                                        intent.putExtra("content",articleModel.getContent());
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


    private void loadMoreArticel() {
        articelCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
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
                                    ArticleModel articleModel = document.toObject(ArticleModel.class);
                                    articleModelList.add(articleModel);

                                }
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                artikelAdapter.notifyDataSetChanged();
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
                    getListArticle();
                }
            }
        }

    }
}

