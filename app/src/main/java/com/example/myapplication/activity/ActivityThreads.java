package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityThreads extends AppCompatActivity {

    ImageView ivBack, ivAddFoto;

    EditText etTitleReport,etShortDescription,etDescription;
    AppCompatButton btnAddThreads;

    String titleReport,shortDescription, description;

    private LottieProgressDialog lottieLoading;

    double latitude = 0.0;
    double longitude = 0.0;

    private SharedPreferences sharedPreferences;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threads);

        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);

        userId = sharedPreferences.getString("userId","");


        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        //initiate lottie
        lottieLoading = new LottieProgressDialog(this, false, null, null, null, null, LottieProgressDialog.SAMPLE_1, null, null);

        //ini untuk initiate/pengenalakan variabel
        btnAddThreads = findViewById(R.id.btnAddThreads);
        etTitleReport = findViewById(R.id.etTitleReport);
        etShortDescription = findViewById(R.id.etShortDescription);
        etDescription = findViewById(R.id.etDescription);


        btnAddThreads.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //kita buat String dari semua edittext dulu untuk validasi
                        titleReport = etTitleReport.getText().toString();
                        shortDescription = etShortDescription.getText().toString();
                        description = etDescription.getText().toString();

                        // dibawah merupakan validasi

                        //jika judul kosong
                        if (titleReport.isEmpty()) {
                            showToast("Judul Harus diisi");
                        }
                        //jika kronolgi singkat kosong
                        else if (shortDescription.isEmpty()) {
                            showToast("Kronologi SIngkat Harus diisi");
                        }
                        //jika deskripsi kosong
                        else if (description.isEmpty()) {
                            showToast("Deskripsi Harus diisi");
                        } else {
                            //jika terpenuhi semua
                            showLoading();
                            postingThreads();
                        }

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




    private void postingThreads(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String idReport = UUID.randomUUID().toString();
        //untuk field dan value di database
        Map<String, Object> data = new HashMap<>();
        data.put("title",titleReport);
        data.put("id",idReport);
        data.put("userId",userId);
        data.put("kronologisingkat",shortDescription);
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
        db.collection("threads").document(idReport).set(data)
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
                });;

    }

}
