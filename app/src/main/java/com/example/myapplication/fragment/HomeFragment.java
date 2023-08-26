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

import com.example.myapplication.R;
import com.example.myapplication.activity.ActivityChat;
import com.example.myapplication.activity.ActivityListReport;
import com.example.myapplication.activity.ActivityListThreads;
import com.example.myapplication.activity.ActivityPostReport;

public class HomeFragment extends Fragment {

    ImageView ivThreads;
    ImageView ivReport;
    ImageView ivChat;

    TextView tvUsername;

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

        tvUsername.setText("Hallo "+sharedPreferences.getString("username",""));

        ivThreads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextPage = new Intent(getActivity(), ActivityListThreads.class );
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
        ivChat.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent pageChat = new Intent(getActivity(), ActivityChat.class);
                startActivity(pageChat);
            }
        }
        );


        return rootView;


    }
}
