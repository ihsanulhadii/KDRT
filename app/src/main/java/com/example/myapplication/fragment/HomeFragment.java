package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.ActivityChat;
import com.example.myapplication.activity.ActivityDetailArticle;
import com.example.myapplication.activity.ActivityListArticle;
import com.example.myapplication.activity.ActivityListReport;
import com.example.myapplication.activity.ActivityListThreads;
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

public class HomeFragment extends Fragment {

    ImageView ivThreads,ivArticle;
    ImageView ivReport;
    ImageView ivChat;

    TextView tvUsername, tvTitle, tvDate, tvContent, tvSeeAll;
    private RecyclerView recyclerView;
    private ArtikelAdapter artikelAdapter;


    private List<ArticleModel> articleModelList = new ArrayList<>();

    private List<User>userList = new ArrayList<>();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private CollectionReference artikelCollection = firestore.collection("articles");

    private DocumentSnapshot lastVisible;

    private boolean isScrolling = false;

    private int visibleThreshold = 5;





    SharedPreferences sharedPreferences;

    // Required empty constructor
    public HomeFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        sharedPreferences = getActivity().getSharedPreferences("kdrt", Context.MODE_PRIVATE);

        // You can initialize UI components and handle interactions here

        ivThreads = rootView.findViewById(R.id.ivThreads);
        ivReport = rootView.findViewById(R.id.ivReport);
        ivChat = rootView.findViewById(R.id.ivChat);
        tvUsername = rootView.findViewById(R.id.tvUsername);
        ivArticle = rootView.findViewById(R.id.ivArticle);
        tvTitle = rootView.findViewById(R.id.tvTitle);
        tvDate = rootView.findViewById(R.id.tvDate);
        tvContent = rootView.findViewById(R.id.tvContent);
        tvSeeAll = rootView.findViewById(R.id.tvSeeAll);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        artikelAdapter = new ArtikelAdapter(getActivity(),articleModelList);
        recyclerView.setAdapter(artikelAdapter);

        tvUsername.setText("Hallo " + sharedPreferences.getString("username", ""));



        ivThreads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextPage = new Intent(getActivity(), ActivityListThreads.class);
                startActivity(nextPage);
            }
        }
        );

        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pageReport = new Intent(getActivity(), ActivityListReport.class);
                startActivity(pageReport);
            }
        }
        );
        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pageChat = new Intent(getActivity(), ActivityChat.class);
                startActivity(pageChat);
            }
        }
        );

        tvSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityListArticle.class);
                startActivity(intent);
            }
        });

        getListArticles();


        return rootView;


    }



    private void getListArticles() {
        // Clear the existing artikelList before loading new data
        articleModelList.clear();

        artikelCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("isPublish",true)
                .get()
                .addOnCompleteListener(task -> {
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
                                        Intent intent = new Intent(getActivity(), ActivityDetailArticle.class);
                                        intent.putExtra("title", articleModel.getTitle()); // Kirim data report ke aktivitas detail
                                        intent.putExtra("img",articleModel.getImg());
                                        intent.putExtra("content",articleModel.getContent());
                                        startActivity(intent);
                                    }
                                });

                               // rlEmpty.setVisibility(View.GONE);
                            } else {
                              //  rlEmpty.setVisibility(View.VISIBLE);

                            }

                        }
                    }

                });


    }



}
