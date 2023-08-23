package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.devhoony.lottieproegressdialog.LottieProgressDialog;
import com.example.myapplication.R;
import com.example.myapplication.fragment.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityReport extends AppCompatActivity {

   ImageView ivBack, ivAddFoto;

   EditText etTitleReport, etPhoneNumber, etAddres,etDescription;

   AppCompatButton btnAddReport;

   String titleReport, phoneNumber, addres, description;

   private LottieProgressDialog lottieLoading;

   double latitude = 0.0;
   double longitude = 0.0;

   private SharedPreferences sharedPreferences;
   String userId;



   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_report);

      sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);

      userId = sharedPreferences.getString("userId","");


      ivBack = findViewById(R.id.ivBack);

      ivBack.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            finish();
         }
      }
      );



      //initiate lottie
      lottieLoading = new LottieProgressDialog(this, false, null, null, null, null, LottieProgressDialog.SAMPLE_1, null, null);

      //ini untuk initiate/pengenalakan variabel

      btnAddReport = findViewById(R.id.btnAddreport);
      etTitleReport = findViewById(R.id.etTitleReport);
      etPhoneNumber = findViewById(R.id.etPhoneNumber);
      etAddres = findViewById(R.id.etAddres);
      etDescription = findViewById(R.id.etDescription);

      btnAddReport.setOnClickListener((new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                    //kita buat String dari semua edittext dulu untuk validasi
                    titleReport = etTitleReport.getText().toString();
                    phoneNumber = etPhoneNumber.getText().toString();
                    addres = etAddres.getText().toString();
                    description = etDescription.getText().toString();

                    // dibawah merupakan validasi

                    //jika judul kosong
                    if (titleReport.isEmpty()) {
                       showToast("Judul Harus diisi");
                    }
                    //jika no hp kosong
                    else if (phoneNumber.isEmpty()) {
                       showToast("Nomor Harus diisi");
                    }
                    //jika alamat kosong
                    else if (addres.isEmpty()) {
                       showToast("Alamat Harus diisi");
                    }
                    //jika deskripsi kosong
                    else if (description.isEmpty()) {
                       showToast("Deskripsi Harus diisi");
                    }
                    //jika terpenuhi semua
                    showLoading();
                    postingReport();


                 }
              })
      );

   }



   //fungsi menghilangan loading dialog
   private void hideLoading() {
      lottieLoading.dismiss();
   }
   //Fungsi memunculkan loading dialog
   private void showLoading() {
      lottieLoading.show();
   }
   private void showToast(String message) {
      Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
   }


   private void postingReport(){
      FirebaseFirestore db = FirebaseFirestore.getInstance();

      String idReport = UUID.randomUUID().toString();
      //untuk field dan value di database
      Map<String, Object> data = new HashMap<>();
      data.put("title",titleReport);
      data.put("id",idReport);
      data.put("userId",userId);
      data.put("nohp",phoneNumber);
      data.put("alamat",addres);
      data.put("kronologikeseluruhan", description);

      Map<String, Object> date = new HashMap<>();
      date.put("createdDate", Timestamp.now());
      date.put("updatedDate", Timestamp.now());
      data.put("date",date);

      Map<String, Object> location = new HashMap<>();
      location.put("latitude",latitude);
      location.put("longitude",longitude);
      data.put("location",location);


      //collection database
      db.collection("Laporan").document(idReport).set(data)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                    hideLoading();
                    showToast("Posting Berhasil");
                 }
              })
              .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                    hideLoading();
                    showToast("Posting Gagal Error "+e.getMessage());
                 }
              });

   }

}
