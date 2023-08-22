package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

public class ProfileFragment extends Fragment {

    // Required empty constructor
    public ProfileFragment() {
    }

    EditText etName,etEmail,etPhoneNumber;

    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = getActivity().getSharedPreferences("kdrt", Context.MODE_PRIVATE);

        // You can initialize UI components and handle interactions here

        etName = rootView.findViewById(R.id.etFullname);
        etPhoneNumber = rootView.findViewById(R.id.etPhoneNumber);
        etEmail = rootView.findViewById(R.id.etEmail);
        etName.setText(sharedPreferences.getString("username",""));
        etEmail.setText(sharedPreferences.getString("email",""));
        etPhoneNumber.setText(sharedPreferences.getString("phoneNumber",""));



        return rootView;
    }
}
