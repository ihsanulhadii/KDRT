package com.example.myapplication.fragment;

import android.content.Intent;
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

public class SettingFragment extends Fragment {


    TextView tvProfile ;
    ImageView ivBack;

    // Required empty constructor
    public SettingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        // You can initialize UI components and handle interactions here

        tvProfile = rootView.findViewById(R.id.tvProfile);
        ivBack = rootView.findViewById(R.id.ivBack);

        tvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pageprofile = new Intent(getActivity(),ProfileFragment.class);
                startActivity(pageprofile);
            }
        }
        );

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backpage = new Intent(getActivity(), HomeFragment.class);
                startActivity(backpage);
            }
        }
        );

        return rootView;
    }
}
