package com.example.myapplication.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.devhoony.lottieproegressdialog.LottieProgressDialog;
import com.example.myapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ActivityEditProfile extends AppCompatActivity {

   private String fullName;

   private String phoneNumber;

   private String email;

   private String password;

   private String birthDate;

   private String gender;

   private SharedPreferences sharedPreferences;



   EditText etFullName, etPhoneNumber, etEmail, etBirthDate, etGender ;

   ImageView ivAddImage;

   AppCompatButton btnEditProfile;
   String userId;

   private LottieProgressDialog lottieLoading;

   String createdDate;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.fragment_profile);


      lottieLoading = new LottieProgressDialog(this, false, null, null, null, null, LottieProgressDialog.SAMPLE_1, null, null);

      sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);
       createdDate = sharedPreferences.getString("createdDate","");

      etFullName = findViewById(R.id.etFullname);
      etPhoneNumber = findViewById(R.id.etPhoneNumber);
      etEmail = findViewById(R.id.etEmail);
      etBirthDate = findViewById(R.id.etBirthDate);
      ivAddImage = findViewById(R.id.ivAddImage);
      btnEditProfile = findViewById(R.id.btnEditProfile);
      etGender = findViewById(R.id.etGender);
      etFullName.setText(sharedPreferences.getString("username",""));
      etPhoneNumber.setText(sharedPreferences.getString("phoneNumber",""));
      etEmail.setText(sharedPreferences.getString("email",""));
      etBirthDate.setText(sharedPreferences.getString("birthDate",""));
      etGender.setText(sharedPreferences.getString("gender", ""));




      etEmail.setClickable(false);
      etEmail.setFocusable(false);
      etBirthDate.setClickable(false);
      etBirthDate.setFocusable(false);


      etBirthDate.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              showDatePickerDialog();
          }
      });

      String avatar = (sharedPreferences.getString("avatar",""));
      userId = sharedPreferences.getString("userId","");


      if(!avatar.isEmpty()){
         Picasso.get()
                 .load(avatar)  // Assuming getImg() returns the image URL
                 /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
                 .error(R.drawable.profile1) // Error image if loading fails
                 .fit() // Resize the image to fit the ImageView dimensions
                 .centerCrop() // Crop the image to fill the ImageView
                 .into(ivAddImage); // ImageView to load the image into
      }

      btnEditProfile.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            fullName = etFullName.getText().toString();
            phoneNumber = etPhoneNumber.getText().toString();
            birthDate = etBirthDate.getText().toString();
            gender = etGender.getText().toString();


            if(fullName.isEmpty()){
               showToast("Nama Tidak Boleh Kosong");
            }else if(phoneNumber.isEmpty()){
               showToast("No Telp Tidak Boleh Kosong");
            }else if (gender.isEmpty()){
                showToast("gender tidak boleh kosong");
            }
            else {
               editProfile();
            }
         }
      });


   }


    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the date with two digits for month and day
                    String formattedMonth = String.format("%02d", selectedMonth + 1);
                    String formattedDay = String.format("%02d", selectedDay);
                    String selectedDate = formattedDay + "-" + formattedMonth + "-" + selectedYear;
                    etBirthDate.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

   private void hideLoading() {
      lottieLoading.dismiss();
   }
   //Fungsi memunculkan loading dialog
   private void showLoading() {
      lottieLoading.show();
   }

   private void showToast(String message){
      Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
   }


   private void editProfile(){
     showLoading();
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      DocumentReference userRef = db.collection("users").document(userId);

      Map<String, Object> updates = new HashMap<>();
      updates.put("name", fullName);
      updates.put("phoneNumber",phoneNumber);
      updates.put("birthDate",birthDate);
      updates.put("gender",gender);


      Map<String, Object> date = new HashMap<>();
      date.put("updatedDate", Timestamp.now());
      updates.put("date",date);

// Update the document with the new data
      userRef.update(updates)
              .addOnSuccessListener(aVoid -> {
                 showToast("Data berhasil dirubah");

                 SharedPreferences.Editor editor = sharedPreferences.edit();
                 editor.putString("phoneNumber",phoneNumber);
                 editor.putString("username",fullName);
                 editor.putString("birthDate",birthDate);
                 editor.putString("gender",gender);
                 editor.putString("createdDate",createdDate);
                 editor.apply();

                 hideLoading();
                 // Update successful
                 // Do something here if needed
              })
              .addOnFailureListener(e -> {
                 showToast("gagal");
                 hideLoading();
                 // Error occurred
                 // Handle the error here
              });

   }




}
