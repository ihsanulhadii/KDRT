package com.example.myapplication.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.devhoony.lottieproegressdialog.LottieProgressDialog;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityEditProfile extends AppCompatActivity {

   private String fullName;

   private String address;

   private String phoneNumber;

   private String email;

   private String password;

   private String birthDate;

   private String gender;

   private SharedPreferences sharedPreferences;




   EditText etFullName,etAddress, etPhoneNumber, etEmail, etBirthDate, etGender ;

   ImageView ivAddImage;

   AppCompatButton btnEditProfile;
   String userId;

   private LottieProgressDialog lottieLoading;

   String createdDate;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    Bitmap imageBitmap;
    private String urlImage;

    private Boolean updateAvatar = false;

    private String[] genderOptions = {"Laki-laki", "Perempuan"};



    @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.fragment_profile);


      lottieLoading = new LottieProgressDialog(this, false, null, null, null, null, LottieProgressDialog.SAMPLE_1, null, null);

      sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);
       createdDate = sharedPreferences.getString("createdDate","");

      etFullName = findViewById(R.id.etFullname);
      etAddress = findViewById(R.id.etAddress);
      etPhoneNumber = findViewById(R.id.etPhoneNumber);
      etEmail = findViewById(R.id.etEmail);
      etBirthDate = findViewById(R.id.etBirthDate);
      ivAddImage = findViewById(R.id.ivAddImage);
      btnEditProfile = findViewById(R.id.btnEditProfile);
      etGender = findViewById(R.id.etGender);
      etFullName.setText(sharedPreferences.getString("username",""));
      etAddress.setText(sharedPreferences.getString("address",""));
      etPhoneNumber.setText(sharedPreferences.getString("phoneNumber",""));
      etEmail.setText(sharedPreferences.getString("email",""));
      etBirthDate.setText(sharedPreferences.getString("birthDate",""));
      etGender.setText(sharedPreferences.getString("gender", ""));




      etEmail.setClickable(false);
      etEmail.setFocusable(false);
      etBirthDate.setClickable(false);
      etBirthDate.setFocusable(false);
      etGender.setClickable(false);
      etGender.setFocusable(false);

      etGender.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              showGenderSelectionDialog();


          }
      });

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

      ivAddImage.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              showDialogCameraGallery();
          }
      });

      btnEditProfile.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            fullName = etFullName.getText().toString();
            address = etAddress.getText().toString();
            phoneNumber = etPhoneNumber.getText().toString();
            birthDate = etBirthDate.getText().toString();
            gender = etGender.getText().toString();


            if(fullName.isEmpty()){
               showToast("Nama Tidak Boleh Kosong");
            } else if (address.isEmpty()) {
                showToast("Alamat Tidak Boleh Kosong");
            } else if(phoneNumber.isEmpty()){
               showToast("No Telp Tidak Boleh Kosong");
            }else if (gender.isEmpty()){
                showToast("gender tidak boleh kosong");
            }
            else {
                if(updateAvatar){
                    uploadImageToFirebaseStorage();
                }else {
                    editProfile();
                }
            }
         }
      });

   }



    private void uploadImageToFirebaseStorage(){
       showLoading();
        // Reference to your Firebase Storage location
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a child reference in the "images" folder
        StorageReference imageRef = storageRef.child("profileUser/" + UUID.randomUUID().toString());

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
                            editProfile();
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
                updateAvatar = true;
                //Jika Image Sudah tampil , munculkan tombol clear image
               // ivClearImage.setVisibility(View.VISIBLE);
               // setupClearImage();
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            ivAddImage.setImageURI(selectedImageUri);
            updateAvatar = true;
            //  Uri selectedImageUri = data.getData();

            try {
                // Convert the selected image URI to a Bitmap
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                // Now you can use 'imageBitmap' to upload to Firebase Storage
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Jika Image Sudah tampil , munculkan tombol clear image
           // ivClearImage.setVisibility(View.VISIBLE);
          //  setupClearImage();
        }
    }


    private void showGenderSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Gender")
                .setItems(genderOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedGender = genderOptions[which];
                        etGender.setText(selectedGender);
                        Toast.makeText(getApplicationContext(), "Gender yang dipilih: " + selectedGender, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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
      updates.put("address",address);
      updates.put("phoneNumber",phoneNumber);
      updates.put("birthDate",birthDate);
      updates.put("gender",gender);
      if(updateAvatar){
          updates.put("avatar",urlImage);
      }


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
                 editor.putString("address", address);
                 editor.putString("birthDate",birthDate);
                 editor.putString("gender",gender);
                 editor.putString("createdDate",createdDate);
                 if(updateAvatar){
                     editor.putString("avatar",urlImage);
                 }
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
