package com.example.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.devhoony.lottieproegressdialog.LottieProgressDialog;
import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityPostReport extends AppCompatActivity {

   ImageView ivBack, ivAddImage,ivClearImage;


   EditText etTitleReport, etPhoneNumber,etAddres, etDescription;

   AppCompatButton btnAddReport;

   String titleReport, phoneNumber, addres, description;

   private LottieProgressDialog lottieLoading;

   double latitude = 0.0;
   double longitude = 0.0;

   private SharedPreferences sharedPreferences;
   String userId;

   private static final int REQUEST_IMAGE_CAPTURE = 1;
   private static final int REQUEST_IMAGE_PICK = 2;

   Bitmap imageBitmap;
   private String urlImage;

   private FusedLocationProviderClient fusedLocationProviderClient;

   private Boolean isPostSuccess = false;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_report);

      sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);

      userId = sharedPreferences.getString("userId","");

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
      }


      //initiate focusedLocation
      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
      //fungsi untuk getLocation
      getLocation();


      ivBack = findViewById(R.id.ivBack);

      ivBack.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("isLoad",isPostSuccess);
            setResult(RESULT_OK,intent);
            finish();
         }
      });



      //initiate lottie
      lottieLoading = new LottieProgressDialog(this, false, null, null, null, null, LottieProgressDialog.SAMPLE_1, null, null);

      //ini untuk initiate/pengenalakan variabel

      btnAddReport = findViewById(R.id.btnAddreport);
      etTitleReport = findViewById(R.id.etTitleReport);
      etPhoneNumber = findViewById(R.id.etPhoneNumber);
      etAddres = findViewById(R.id.etAddres);
      etDescription = findViewById(R.id.etDescription);
      ivAddImage = findViewById(R.id.ivAddImage);
      ivClearImage = findViewById(R.id.ivClearImage);


      ivAddImage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            showDialogCameraGallery();
         }
      });


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
                    } else {
                       //jika terpenuhi semua
                       showLoading();
                       uploadImageToFirebaseStorage();
                    }


                 }
              })
      );

   }

   private void getLocation() {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         return;
      }
      fusedLocationProviderClient.getLastLocation()
              .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                 @Override
                 public void onSuccess(Location location) {
                    if (location != null) {
                       latitude = location.getLatitude();
                       longitude = location.getLongitude();
                    }
                 }
              });
   }


   private void uploadImageToFirebaseStorage(){
      // Reference to your Firebase Storage location
      StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a child reference in the "images" folder
      StorageReference imageRef = storageRef.child("report/" + UUID.randomUUID().toString());

// Get the image data (assuming you have a Bitmap imageBitmap)
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      byte[] imageData = baos.toByteArray();

// Upload the image to Firebase Storage
      UploadTask uploadTask = imageRef.putBytes(imageData);

// Monitor the upload task
      uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
         @Override
         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // Image upload successful, get the download URL
            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
               @Override
               public void onComplete(@NonNull Task<Uri> task) {
                  if (task.isSuccessful()) {
                     Uri downloadUri = task.getResult();
                     urlImage = downloadUri.toString();
                     postingReport();
                     // Handle the image URL here (e.g., save it to a database)
                  } else {
                     hideLoading();
                     showToast("Failed Upload Image");
                     // Handle the error
                  }
               }
            });
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            // Handle the error
         }
      });

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
            imageBitmap = (Bitmap) extras.get("data");
            ivAddImage.setImageBitmap(imageBitmap);
            //Jika Image Sudah tampil , munculkan tombol clear image
            ivClearImage.setVisibility(View.VISIBLE);
            setupClearImage();
         }
      } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
         Uri selectedImageUri = data.getData();
         ivAddImage.setImageURI(selectedImageUri);
         //  Uri selectedImageUri = data.getData();

         try {
            // Convert the selected image URI to a Bitmap
            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

            // Now you can use 'imageBitmap' to upload to Firebase Storage
         } catch (IOException e) {
            e.printStackTrace();
         }
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
      data.put("img", urlImage);

      Map<String, Object> date = new HashMap<>();
      date.put("createdDate", Timestamp.now());
      date.put("updatedDate", Timestamp.now());
      data.put("date",date);

      Map<String, Object> location = new HashMap<>();
      location.put("latitude",latitude);
      location.put("longitude",longitude);
      data.put("location",location);


      //collection database
      db.collection("report").document(idReport).set(data)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                    hideLoading();
                    clearAllData();
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

   public void clearAllData(){
      etDescription.setText("");
      etTitleReport.setText("");
      etAddres.setText("");
      etPhoneNumber.setText("");
      ivClearImage.setVisibility(View.GONE);
      ivAddImage.setImageDrawable(getDrawable(R.drawable.image_blank));

   }

   @Override
   public void onBackPressed() {
      Intent intent = new Intent();
      intent.putExtra("isLoad",isPostSuccess);
      setResult(RESULT_OK,intent);
      finish();
   }

}
