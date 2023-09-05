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
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activity.ActivityEditProfile;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    // Required empty constructor
    public ProfileFragment() {
    }

    EditText etName,etEmail,etPhoneNumber,etBirthDate, etGender;

    CircleImageView ivAddImage;

    AppCompatButton btnEditProfile;

    SharedPreferences sharedPreferences;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

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
        etBirthDate = rootView.findViewById(R.id.etBirthDate);
        etGender = rootView.findViewById(R.id.etGender);

        btnEditProfile = rootView.findViewById(R.id.btnEditProfile);
        ivAddImage = rootView.findViewById(R.id.ivAddImage);

        etName.setClickable(false);
        etName.setFocusable(false);

        etPhoneNumber.setClickable(false);
        etPhoneNumber.setFocusable(false);

        etEmail.setClickable(false);
        etEmail.setFocusable(false);

        etBirthDate.setClickable(false);
        etBirthDate.setFocusable(false);

        etGender.setClickable(false);
        etGender.setFocusable(false);


        btnEditProfile.setText("Ubah Profil");
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityEditProfile.class);
                startActivityForResult(intent,1111);

            }
        });

        updateView();




        return rootView;
    }

    private void updateView(){

        String username = sharedPreferences.getString("username","");
        etName.setText(username);

        etEmail.setText(sharedPreferences.getString("email",""));
        etPhoneNumber.setText(sharedPreferences.getString("phoneNumber",""));
        etBirthDate.setText(sharedPreferences.getString("birthDate",""));
        etGender.setText(sharedPreferences.getString("gender",""));
        String avatar = sharedPreferences.getString("avatar","");

        if(!avatar.isEmpty()){
            Picasso.get()
                    .load(avatar)  // Assuming getImg() returns the image URL
                    /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
                    .error(R.drawable.profile1) // Error image if loading fails
                    .fit() // Resize the image to fit the ImageView dimensions
                    .centerCrop() // Crop the image to fill the ImageView
                    .into(ivAddImage); // ImageView to load the image into
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1111){
            sharedPreferences = getActivity().getSharedPreferences("kdrt",Context.MODE_PRIVATE);
            updateView();
        }
    }
}
