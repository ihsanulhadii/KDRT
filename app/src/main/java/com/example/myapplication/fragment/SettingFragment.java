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
import com.example.myapplication.activity.ActivityLogin;

public class SettingFragment extends Fragment {


    TextView tvProfile,tvLogOut ;


    SharedPreferences sharedPreferences;

    // Required empty constructor
    public SettingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        // You can initialize UI components and handle interactions here

        sharedPreferences = getActivity().getSharedPreferences("kdrt", Context.MODE_PRIVATE);

        tvLogOut = rootView.findViewById(R.id.tvLogOut);




        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        }
        );


        return rootView;
    }

    private void logOut(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",false);
        editor.apply();

        Intent intent = new Intent(getActivity(), ActivityLogin.class);
        startActivity(intent);
        getActivity().finish();
    }
}
