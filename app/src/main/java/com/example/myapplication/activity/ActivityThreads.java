package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityThreads extends AppCompatActivity {

    ImageView ivBack, ivAddImage,ivClearImage;

    EditText etTitleReport,etShortDescription,etDescription;
    AppCompatButton btnAddThreads;

    String titleReport,shortDescription, description;

    private LottieProgressDialog lottieLoading;

    double latitude = 0.0;
    double longitude = 0.0;

    private SharedPreferences sharedPreferences;
    String userId;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;



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
        ivAddImage = findViewById(R.id.ivAddImage);
        ivClearImage = findViewById(R.id.ivClearImage);


        //klik button add image
        ivAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogCameraGallery();
            }
        });


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

    private void uploadImageToFirebaseStorage(){

    }

    private void showDialogCameraGallery(){

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_pick_image, null);

        Button btnCamera = dialogView.findViewById(R.id.btnCamera);
        Button btnGallery = dialogView.findViewById(R.id.btnGallery);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                dialog.dismiss();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                dialog.dismiss();
            }
        });
    }


    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ivAddImage.setImageBitmap(imageBitmap);
                //Jika Image Sudah tampil , munculkan tombol clear image
                ivClearImage.setVisibility(View.VISIBLE);
                setupClearImage();
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            ivAddImage.setImageURI(selectedImageUri);

            //Jika Image Sudah tampil , munculkan tombol clear image
            ivClearImage.setVisibility(View.VISIBLE);
            setupClearImage();
        }
    }

    private void setupClearImage(){
        ivClearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivClearImage.setVisibility(View.GONE);
                ivAddImage.setImageDrawable(getDrawable(R.drawable.image_blank));

            }
        });

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
