package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devhoony.lottieproegressdialog.LottieProgressDialog;
import com.example.myapplication.R;
import com.example.myapplication.activity.ActivityChat;
import com.example.myapplication.activity.ActivityDetailReport;
import com.example.myapplication.activity.ActivityListReport;
import com.example.myapplication.activity.ActivityListThreads;
import com.example.myapplication.activity.ActivityPostReport;
import com.example.myapplication.adapter.ArtikelAdapter;
import com.example.myapplication.adapter.ReportAdapter;
import com.example.myapplication.model.ArtikelModel;
import com.example.myapplication.model.ReportModel;
import com.example.myapplication.model.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeFragment extends Fragment {

    ImageView ivThreads;
    ImageView ivReport;
    ImageView ivChat;

    TextView tvUsername;
    private RecyclerView recyclerView;
    private ArtikelAdapter artikelAdapter;

    private List<ArtikelModel> artikelModelList = new ArrayList<>();

    private List<User>userList = new ArrayList<>();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private CollectionReference artikelCollection = firestore.collection("artikel");

    private DocumentSnapshot lastVisible;

    private boolean isScrolling = false;

    private int visibleThreshold = 5;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout rlEmpty,rlLoading;




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

        getListArtikel();


        return rootView;


    }
    private void getListArtikel() {
        // Clear the existing artikelList before loading new data
        artikelModelList.clear();

        artikelCollection.orderBy("date.createdDate", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    rlLoading.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            ArtikelModel artikelModel;
                            for (DocumentSnapshot document : querySnapshot) {
                                artikelModel = document.toObject(ArtikelModel.class);
                                artikelModelList.add(artikelModel);
                            }

                            if (!artikelModelList.isEmpty()) {
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                artikelAdapter.notifyDataSetChanged();

                                /*artikelAdapter.setOnItemClickListener(new ReportAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(ArtikelModel artikelModel) {
                                        Intent intent = new Intent(HomeFragment.this, ActivityDetailReport.class);
                                        intent.putExtra("title", artikelModel.getTitle()); // Kirim data artikel ke aktivitas detail
                                        intent.putExtra("img",artikelModel.getImg());
                                        intent.putExtra("description",artikelModel.getDescription());
                                        startActivity(intent);
                                    }
                                });*/

                                rlEmpty.setVisibility(View.GONE);
                            } else {
                                rlEmpty.setVisibility(View.VISIBLE);

                            }

                        }
                    }

                });


    }

}
