package com.example.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityRegister extends AppCompatActivity {

    TextView tvAlready;
    EditText etFullName,etAddres, etPhoneNumber, etEmail, etPassword;
    AppCompatButton btnRegister;

    String email, fullName,addres, phoneNumber, password;


    private FirebaseAuth mAuth;

    private LottieProgressDialog lottieLoading;

    private FusedLocationProviderClient fusedLocationProviderClient;

    double latitude = 0.0;
    double longitude = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Request permission lokasi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
        }


        //initiate focusedLocation
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //fungsi untuk getLocation
        getLocation();


        //initiate/ kenalkan firebase authnya dulu
        mAuth = FirebaseAuth.getInstance();

        //initiate lottie
        lottieLoading = new LottieProgressDialog(this, false, null, null, null, null, LottieProgressDialog.SAMPLE_1, null, null);


        //ini untuk initiate/pengenalakan variabel
        tvAlready = findViewById(R.id.tvAlready);
        etFullName = findViewById(R.id.etFullname);
        etAddres = findViewById(R.id.etAddres);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);



        //ini untuk action klik
        tvAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Artinya pindah dari halaman register ke halaman login bisa pakai finish aja
                finish();
            }
        });

        //ini untuk action klik tombol daftar
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //munculkan loading dialog
                showLoading();

                //kita buat String dari semua edittext dulu untuk validasi
                email = etEmail.getText().toString();
                fullName = etFullName.getText().toString();
                addres = etAddres.getText().toString();
                phoneNumber = etPhoneNumber.getText().toString();
                password = etPassword.getText().toString();

                // dibawah merupakan validasi

                //jika email kosong
                if (email.isEmpty()) {
                    showToast("Email Harus diisi");
                }
                //jika  nama
                else if (fullName.isEmpty()) {
                    showToast("Nama Harus diisi");
                }
                //jika alamat kosong
                else if (addres.isEmpty()) {
                    showToast("Alamat harus diisi");
                }
                //jika telp kosong
                else if (phoneNumber.isEmpty()) {
                    showToast("No Telpon Harus diisi");
                }
                else if(!isValidEmail(email)){
                    showToast("Format emaill tidak valid");
                }
                //jika password kosong
                else if (password.isEmpty()) {
                    showToast("Password Harus diisi");
                } else {
                    //Jika semua validassi sudah terisi, jalankan perintah
                     registerToFireBaseAuth();
                }


            }
        });
    }


    public boolean isValidEmail(String email) {
        // Use Android's built-in Patterns class to validate email
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    //Ini merupakan fungsi untuk memunculkan Toast dialog
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    //Fungsi memunculkan loading dialog
    private void showLoading() {
        lottieLoading.show();
    }


    //fungsi menghilangan loading dialog
    private void hideLoading() {
        lottieLoading.dismiss();
    }


    //ini fungsi/function untuk mendaftarkan email ke firestore
    private void registerToFireBaseAuth() {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            String email = user.getEmail();
                            registerToFireStoreDataBase(userId);
                        } else {
                            //Hilang loading jika proses gagal
                            hideLoading();


                            //response failed Merupakan callback/response balik message  dari firebase
                            String responseFailed = task.getException().getMessage();
                            Log.d("message", responseFailed);

                            //Jika responseFailed isinya kek gini
                            if (responseFailed == "The email address is already in use by another account.") {
                                //Muncul kan error ini, ini merupakan erorr yang di mapping
                                showToast("Pendaftaran Gagal....Email telah terdaftar");
                            } else {
                                //Jika error nya selain message di atas maka tampilkan ini
                                showToast(responseFailed);
                            }
                        }
                    }
                });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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

    private void registerToFireStoreDataBase(String userId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("userId",userId);
        data.put("name", fullName);
        data.put("addres",addres);
        data.put("email", email);
        data.put("phoneNumber", phoneNumber);
        data.put("avatar","");
        data.put("gender","");
        data.put("birthDate","");
        data.put("type","user");
        data.put("password", Base64.encodeToString(password.getBytes(), Base64.DEFAULT));

        Map<String, Object> date = new HashMap<>();
        date.put("createdDate", Timestamp.now());
        date.put("updatedDate", Timestamp.now());
        data.put("date",date);

        Map<String, Object> location = new HashMap<>();
        location.put("latitude",latitude);
        location.put("longitude",longitude);
        data.put("location",location);


        db.collection("users").document(userId).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideLoading();
                        showToast("Pendaftaran Berhasil");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoading();
                        showToast("Pendaftaran Gagal Error "+e.getMessage());
                    }
                });;

    }

}