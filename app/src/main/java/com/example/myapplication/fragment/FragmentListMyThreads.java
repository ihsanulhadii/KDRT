package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.activity.ActivityDetailThreads;
import com.example.myapplication.activity.ActivityListThreads;
import com.example.myapplication.activity.ActivityPostThreads;
import com.example.myapplication.adapter.ThreadAdapter;
import com.example.myapplication.model.ThreadModel;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentListMyThreads extends Fragment {

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

    SharedPreferences sharedPreferences;

    String userId;
    Handler handler;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_list_mythreads, container, false);

        handler = new Handler(Looper.getMainLooper());
        sharedPreferences = getActivity().getSharedPreferences("kdrt", Context.MODE_PRIVATE);

        userId = sharedPreferences.getString("userId","");


        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        threadAdapter = new ThreadAdapter(userId,threadList);
        recyclerView.setAdapter(threadAdapter);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);
        ivBack = rootView.findViewById(R.id.ivBack);
        rlEmpty = rootView.findViewById(R.id.rlEmpty);
        rlLoading = rootView.findViewById(R.id.rlLoading);

        btnAddThreads = rootView.findViewById(R.id.btnAddThreads);



        btnAddThreads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityPostThreads.class);
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
        return rootView ;
    }


    private void loadThreads(){
        threadsCollection.orderBy("date.createdDate",Query.Direction.DESCENDING)
                .limit(10)
                .whereEqualTo("isPublish",true)
                .whereEqualTo("userId",userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getString("id");
                                String userId = document.getString("userId");
                                String description = document.getString("description");
                                String img = document.getString("img");
                                Timestamp timestamp = document.getTimestamp("date.createdDate");


                                if(task.getResult().size()>0){
                                    QuerySnapshot querySnapshot = task.getResult();

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

                                                            threadList.add(new ThreadModel(id,userId,description,img,timestamp,userName,avatar));
                                                            handler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    swipeRefreshLayout.setRefreshing(false);
                                                                    lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                                                    threadAdapter.notifyDataSetChanged();
                                                                    threadAdapter.setOnItemClickListener(new ThreadAdapter.OnItemClickListener() {
                                                                        @Override
                                                                        public void onItemClick(ThreadModel thread) {
                                                                            Intent intent = new Intent(getActivity(), ActivityDetailThreads.class);
                                                                            intent.putExtra("title", thread.getTitle()); // Kirim data thread ke aktivitas detail
                                                                            intent.putExtra("img",thread.getImg());
                                                                            intent.putExtra("description",thread.getDescription());
                                                                            startActivity(intent);
                                                                        }
                                                                    });

                                                                    if(!threadList.isEmpty()){
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
                                                        // Handle error
                                                    }
                                                }
                                            });
                                }else {
                                    rlEmpty.setVisibility(View.VISIBLE);
                                    rlLoading.setVisibility(View.GONE);
                                }

                            }
                        } else {
                            //Error Boskuh
                            showToast(task.getException().getMessage()+".");
                        }
                    }
                });

    }
    public void showEditDeleteDialog(ThreadModel thread,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Action");
        builder.setItems(new CharSequence[]{"Ubah", "Hapus"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        editThreads(thread);
                        break;
                    case 1:
                        deleteThreads(thread.getId(),position);
                        break;
                }
            }
        });
        builder.show();
    }

    private void editThreads(ThreadModel threadModel){
        Intent intent = new Intent(getActivity(), ActivityPostThreads.class);
        intent.putExtra("title",threadModel.getTitle());
        intent.putExtra("description",threadModel.getDescription());
        intent.putExtra("image",threadModel.getImg());
        intent.putExtra("id",threadModel.getId());
        startActivityForResult(intent,1313);
    }

    public void deleteThreads(String id,int position){
        threadsCollection.document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    showToast("Thread telah di hapus");
                    threadList.remove(position);
                    threadAdapter.notifyDataSetChanged();
                    // Handle successful deletion
                })
                .addOnFailureListener(e -> {
                    showToast("Thread gagal di hapus");
                    // Handle failure
                });

    }

    public void editThreads(String id, int position, String updatedTitle, String updatedImg, String updatedDescription) {
        threadsCollection.document(id)
                .update(
                        "title", updatedTitle,
                        "img", updatedImg,
                        "description", updatedDescription
                )
                .addOnSuccessListener(aVoid -> {
                    showToast("Threads telah di edit");
                    // Handle successful edit
                    // Update data di dalam list
                    ThreadModel editedThread = threadList.get(position);
                    editedThread.setTitle(updatedTitle);
                    editedThread.setImg(updatedImg);
                    editedThread.setDescription(updatedDescription);
                    threadAdapter.notifyItemChanged(position);
                })
                .addOnFailureListener(e -> {
                    showToast("Threads gagal di edit");
                    // Handle failure
                });
    }

    /*public void editThreads(String id,int position){
        threadsCollection.document(id)
                .update(
                        "title", updatedTitle,
                        "img", updatedImg,
                        "description", updatedDescription
                )


                .addOnSuccessListener(aVoid -> {
                    showToast("Threads telah di edit");
                    // Handle successful edit
                })
                .addOnFailureListener(e -> {
                    showToast("Threads gagal di edit");
                    // Handle failure
                });


    }
*/


    private void showToast(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    private void loadMoreThreads(){
        threadsCollection.orderBy("date.createdDate",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(10)
                .whereEqualTo("isPublish",true)
                .whereEqualTo("userId",userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getString("id");
                                String userId = document.getString("userId");
                                String description = document.getString("description");
                                String img = document.getString("img");
                                Timestamp timestamp = document.getTimestamp("date.createdDate");



                                QuerySnapshot querySnapshot = task.getResult();
                                userCollection
                                        .document(userId)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot userDocument = task1.getResult();
                                                if (userDocument.exists()) {
                                                    String userName = userDocument.getString("name");
                                                    String avatar = userDocument.getString("avatar");

                                                    threadList.add(new ThreadModel(id,userId,description,img,timestamp,userName,avatar));
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            threadAdapter.notifyDataSetChanged();
                                                            lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                                            threadAdapter.setOnItemClickListener(new ThreadAdapter.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(ThreadModel thread) {
                                                                    Intent intent = new Intent(getActivity(), ActivityDetailThreads.class);
                                                                    intent.putExtra("title", thread.getTitle()); // Kirim data thread ke aktivitas detail
                                                                    intent.putExtra("img",thread.getImg());
                                                                    intent.putExtra("description",thread.getDescription());
                                                                    startActivity(intent);
                                                                }
                                                            });

                                                        }
                                                    });
                                                }
                                            } else {
                                                Log.d("xxx", task1.getException().getMessage());
                                                // Handle error
                                            }
                                        });

                            }
                        } else {
                            //Error Boskuh
                            showToast(task.getException().getMessage()+".");
                        }
                    }
                });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1313 && resultCode == getActivity().RESULT_OK){
            if(data.hasExtra("isLoad")){
                Boolean isLoad = data.getBooleanExtra("isLoad",false);
                if(isLoad){
                    loadThreads();
                }
            }
        }

    }
}

