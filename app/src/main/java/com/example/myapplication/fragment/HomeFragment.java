package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activity.ActivityChat;
import com.example.myapplication.activity.ActivityReport;
import com.example.myapplication.activity.ActivityThreads;

public class HomeFragment extends Fragment {

    ImageView ivThreads;
    ImageView ivReport;
    ImageView ivChat;

    // Required empty constructor
    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // You can initialize UI components and handle interactions here

        ivThreads = rootView.findViewById(R.id.ivThreads);
        ivReport = rootView.findViewById(R.id.ivReport);
        ivChat = rootView.findViewById(R.id.ivChat);

        ivThreads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextpage = new Intent(getActivity(), ActivityThreads.class );
                startActivity(nextpage);
            }
        }
        );

        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pagereport = new Intent(getActivity(), ActivityReport.class);
                startActivity(pagereport);
            }
        }
        );
        ivChat.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent pagechat = new Intent(getActivity(), ActivityChat.class);
                startActivity(pagechat);
            }
        }
        );


        return rootView;


    }
}
