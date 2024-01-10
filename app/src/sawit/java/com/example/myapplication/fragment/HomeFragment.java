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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.ActivityDetailArticle;
import com.example.myapplication.activity.ActivityListArticle;
import com.example.myapplication.adapter.ArticleHomeAdapter;
import com.example.myapplication.model.ArticleModel;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {


    EditText etSearch;

    TextView tvUsername, tvSeeAll ;
    private RecyclerView recyclerView;
    private ArticleHomeAdapter articleAdapter;

    private List<ArticleModel> articleModelList = new ArrayList<>();



    private List<User>userList = new ArrayList<>();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private CollectionReference artikelCollection = firestore.collection("articles");

    private DocumentSnapshot lastVisible;

    private boolean isScrolling = false;

    private int visibleThreshold = 5;

    private CircleImageView ivAvatar;

    private RelativeLayout rlEmpty,rlLoading;


    private SharedPreferences sharedPreferences;

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

        tvUsername = rootView.findViewById(R.id.tvUsername);
        tvSeeAll = rootView.findViewById(R.id.tvSeeAll);
        ivAvatar = rootView.findViewById(R.id.ivAvatar);
        etSearch = rootView.findViewById(R.id.etSearch);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        articleAdapter = new ArticleHomeAdapter(getActivity(),articleModelList);
        recyclerView.setAdapter(articleAdapter);





        tvUsername.setText(sharedPreferences.getString("username", ""));
        setShortenedUsername();



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



        tvSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityListArticle.class);
                startActivityForResult(intent,1414);
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

    private void setShortenedUsername() {
        String username = sharedPreferences.getString("username", ""); // Dapatkan username dari SharedPreferences

        if (username.length() > 15) {
            // Jika panjang username lebih dari 15 karakter, potong dan tambahkan titik-titik
            username = username.substring(0, 15) + "...";
        }

        tvUsername.setText(username);
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

                                articleAdapter.setOnItemClickListener(new ArticleHomeAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(ArticleModel articleModel) {
                                        Intent intent = new Intent(getActivity(), ActivityDetailArticle.class);
                                        intent.putExtra("title", articleModel.getTitle()); // Kirim data report ke aktivitas detail
                                        intent.putExtra("img",articleModel.getImg());
                                        intent.putExtra("content",articleModel.getContent());
                                        intent.putExtra("id",articleModel.getId());
                                        Date datePublish = articleModel.getDateValue("createdDate");
                                        String inputDateString = datePublish.toString();
                                        intent.putExtra("date",inputDateString);
                                        startActivityForResult(intent,1616);
                                    }
                                });


                                //rlEmpty.setVisibility(View.GONE);
                            } else {
                                //rlEmpty.setVisibility(View.VISIBLE);

                            }

                        }
                    }

                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1616){
            if(data.hasExtra("isRefresh")){
                Boolean isRefresh = data.getBooleanExtra("isRefresh",false);
                if(isRefresh){
                    getListArticles();
                }
            }

        }
    }




}
