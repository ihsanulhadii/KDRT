package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.ActivityDetailArticle;
import com.example.myapplication.activity.ActivityDetailEvent;
import com.example.myapplication.activity.ActivityListArticle;
import com.example.myapplication.activity.ActivityListCounselor;
import com.example.myapplication.activity.ActivityListEvent;
import com.example.myapplication.activity.ActivityListReport;
import com.example.myapplication.activity.ActivityThreadsNew;
import com.example.myapplication.adapter.ArticleExploreAdapter;
import com.example.myapplication.adapter.EventExploreAdapter;
import com.example.myapplication.model.ArticleModel;
import com.example.myapplication.model.EventModel;
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

public class ExploreFragment extends Fragment {

    ImageView ivThreads;
    ImageView ivReport;
    ImageView ivChat;

    EditText etSearch;

    TextView tvUsername, tvSeeAll, tvPopular,tvEvent;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2 ;

    private RecyclerView recyclerView3;
    private ArticleExploreAdapter articleAdapter;

    private ArticleExploreAdapter articlePopularAdapter;

    private EventExploreAdapter eventExploreAdapter;

    private List<ArticleModel> articleModelList = new ArrayList<>();

    private List<ArticleModel> articleModelList2 = new ArrayList<>();

    private List<EventModel> eventModelList3 = new ArrayList<>();

    private List<User>userList = new ArrayList<>();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private CollectionReference artikelCollection = firestore.collection("articles");

    private CollectionReference eventCollection = firestore.collection("Event");

    private DocumentSnapshot lastVisible;

    private boolean isScrolling = false;

    private int visibleThreshold = 5;

    private CircleImageView ivAvatar;

    private RelativeLayout rlEmpty,rlLoading;


    private SharedPreferences sharedPreferences;

    // Required empty constructor
    public ExploreFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);


        sharedPreferences = getActivity().getSharedPreferences("kdrt", Context.MODE_PRIVATE);

        // You can initialize UI components and handle interactions here

        ivThreads = rootView.findViewById(R.id.ivThread);
        ivReport = rootView.findViewById(R.id.ivReport);
        ivChat = rootView.findViewById(R.id.ivChat);
        tvUsername = rootView.findViewById(R.id.tvUsername);
        tvSeeAll = rootView.findViewById(R.id.tvSeeAll);
        ivAvatar = rootView.findViewById(R.id.ivAvatar);
        etSearch = rootView.findViewById(R.id.etSearch);
        tvPopular = rootView.findViewById(R.id.tvPopular);
        tvEvent = rootView.findViewById(R.id.tvEvent);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        articleAdapter = new ArticleExploreAdapter(getActivity(),articleModelList);
        recyclerView.setAdapter(articleAdapter);

        recyclerView2 = rootView.findViewById(R.id.recyclerView2);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager2);


        articlePopularAdapter = new ArticleExploreAdapter(getActivity(),articleModelList2);
        recyclerView2.setAdapter(articlePopularAdapter);

        recyclerView3 = rootView.findViewById(R.id.recyclerView3);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView3.setLayoutManager(layoutManager3);

        eventExploreAdapter = new EventExploreAdapter(getActivity(),eventModelList3);
        recyclerView3.setAdapter(eventExploreAdapter);

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

        ivThreads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextPage = new Intent(getActivity(), ActivityThreadsNew.class);
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
                Intent pageChat = new Intent(getActivity(), ActivityListCounselor.class);
                startActivity(pageChat);
            }
        }
        );

        tvSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityListArticle.class);
                startActivityForResult(intent,1414);
            }
        });

        tvPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityListArticle.class);
                intent.putExtra("popular","popular");
                startActivityForResult(intent,1414);

            }
        });

        tvEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityListEvent.class);
                startActivityForResult(intent, 311);
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

        getListArticlesPopular();

            getListEvent();


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

                                articleAdapter.setOnItemClickListener(new ArticleExploreAdapter.OnItemClickListener() {
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
        if (requestCode == 1616) {
            if (data.hasExtra("isRefresh")) {
                boolean isRefresh = data.getBooleanExtra("isRefresh", false);
                if (isRefresh) {
                    getListArticles();
                }
            }
        } else if (requestCode == 1617) {
            if (data.hasExtra("isRefresh")) {
                boolean isRefresh = data.getBooleanExtra("isRefresh", false);
                if (isRefresh) {
                    getListArticlesPopular();
                }
            }
        } else if (requestCode == 1618) {
            if (data.hasExtra("isRefresh")) {
                boolean isRefresh = data.getBooleanExtra("isRefresh", false);
                if (isRefresh) {
                    getListEvent();
                }
            }
        }

    }

    private void getListArticlesPopular() {
        // Clear the existing artikelList before loading new data
        articleModelList2.clear();
        artikelCollection.orderBy("commentCount", Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("isPublish",true)
                .get()
                .addOnCompleteListener(task -> {
                    Log.d("xxx11",task.isSuccessful()+" pesann");
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            Log.d("xxx11","pesann 2");
                            ArticleModel articleModel2;
                            for (DocumentSnapshot document : querySnapshot) {
                                articleModel2 = document.toObject(ArticleModel.class);
                                articleModelList2.add(articleModel2);
                            }

                            if (!articleModelList2.isEmpty()) {
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                articlePopularAdapter.notifyDataSetChanged();

                                articlePopularAdapter.setOnItemClickListener(new ArticleExploreAdapter.OnItemClickListener() {
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
                                        startActivityForResult(intent,1617);
                                    }
                                });

                                 //rlEmpty.setVisibility(View.GONE);
                            } else {
                                  //rlEmpty.setVisibility(View.VISIBLE);

                            }

                        }
                    }
                    else {
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("FirestoreQuery", "Error: " + e.getMessage());
                        }
                    }


                });


    }
    private void getListEvent() {
        // Clear the existing artikelList before loading new data
        eventModelList3.clear();

        eventCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("isPublish",true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            EventModel eventModel;
                            for (DocumentSnapshot document : querySnapshot) {
                                eventModel = document.toObject(EventModel.class);
                                eventModelList3.add(eventModel);
                            }

                            if (!eventModelList3.isEmpty()) {
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                eventExploreAdapter.notifyDataSetChanged();

                                eventExploreAdapter.setOnItemClickListener(new EventExploreAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(EventModel eventModel) {
                                        Intent intent = new Intent(getActivity(), ActivityDetailEvent.class);
                                        intent.putExtra("title", eventModel.getTitle()); // Kirim data report ke aktivitas detail
                                        intent.putExtra("img",eventModel.getImg());
                                        intent.putExtra("content",eventModel.getContent());
                                        intent.putExtra("id",eventModel.getId());
                                        intent.putExtra("link",eventModel.getLink());
                                        intent.putExtra("tanggal",eventModel.getTanggal());

                                        Date datePublish = eventModel.getDateValue("createdDate");
                                        String inputDateString = datePublish.toString();
                                        intent.putExtra("date",inputDateString);
                                        startActivity(intent);
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




}
