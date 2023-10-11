package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.ActivityDetailArticle;
import com.example.myapplication.activity.ActivityListArticle;
import com.example.myapplication.activity.ActivityListReport;
import com.example.myapplication.activity.ActivityListThreads;
import com.example.myapplication.activity.ActivityListChatRoom;
import com.example.myapplication.adapter.ArticleAdapter;
import com.example.myapplication.model.ArticleModel;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    ImageView ivThreads;
    ImageView ivReport;
    ImageView ivChat;

    EditText etSearch;

    TextView tvUsername, tvSeeAll;
    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;


    private List<ArticleModel> articleModelList = new ArrayList<>();

    private List<User>userList = new ArrayList<>();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private CollectionReference artikelCollection = firestore.collection("articles");

    private DocumentSnapshot lastVisible;

    private boolean isScrolling = false;

    private int visibleThreshold = 5;

    private CircleImageView ivAvatar;





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

        ivThreads = rootView.findViewById(R.id.ivThread);
        ivReport = rootView.findViewById(R.id.ivReport);
        ivChat = rootView.findViewById(R.id.ivChat);
        tvUsername = rootView.findViewById(R.id.tvUsername);
        tvSeeAll = rootView.findViewById(R.id.tvSeeAll);
        ivAvatar = rootView.findViewById(R.id.ivAvatar);
        etSearch = rootView.findViewById(R.id.etSearch);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        articleAdapter = new ArticleAdapter(getActivity(),articleModelList);
        recyclerView.setAdapter(articleAdapter);

        tvUsername.setText("Hallo " + sharedPreferences.getString("username", ""));



        String avatar = sharedPreferences.getString("avatar","");
        if(!avatar.isEmpty()){
            Picasso.get()
                    .load(avatar)  // Assuming getImg() returns the image URL
                    /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
                    .error(R.drawable.profile1) // Error image if loading fails
                    .fit() // Resize the image to fit the ImageView dimensions
                    .centerCrop() // Crop the image to fill the ImageView
                    .into(ivAvatar); // ImageView to load the image into
        }

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
                Intent pageChat = new Intent(getActivity(), ActivityListChatRoom.class);
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

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    actionSearch();
                    return true;
                }
                return false;
            } });

        getListArticles();


        return rootView;


    }

    private void actionSearch(){
        Intent intent = new Intent(getActivity(), ActivityListArticle.class);
        intent.putExtra("query",etSearch.getText().toString());
        startActivity(intent);
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
                                articleAdapter.notifyDataSetChanged();

                                articleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
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
