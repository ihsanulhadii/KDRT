package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.devhoony.lottieproegressdialog.LottieProgressDialog;
import com.example.myapplication.R;
import com.example.myapplication.interfaces.LoginCallback;
import com.example.myapplication.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity implements LoginCallback {
    TextView tvCreateAccount;
    AppCompatButton btnLogin;

    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = "ActivityLogin";
    String email,password;

    EditText etEmail,etPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //untuk menyimpan data ke local
    private SharedPreferences sharedPreferences;

    private LottieProgressDialog lottieLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initiate/ kenalkan firebase authnya dulu
        mAuth = FirebaseAuth.getInstance();

        //initiate lottie
        lottieLoading = new LottieProgressDialog(this, false, null, null, null, null, LottieProgressDialog.SAMPLE_1, null, null);


        sharedPreferences = getSharedPreferences("kdrt",MODE_PRIVATE);


        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        btnLogin = findViewById(R.id.btnLogin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextPage = new Intent( ActivityLogin.this, ActivityRegister.class);
                startActivity(nextPage);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                if(email.isEmpty()){
                    showToast("Email tidak boleh kosong");
                }else if(!isValidEmail(email)){
                    showToast("Email tidak valid");
                }else if(password.isEmpty()){
                   showToast("Password Tidak Boleh Kosong");
                }else {
                    login();
                }
            }
        });
    }

    public boolean isValidEmail(String email) {
        // Use Android's built-in Patterns class to validate email
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void showLoading() {
        lottieLoading.show();
    }


    //fungsi menghilangan loading dialog
    private void hideLoading() {
        lottieLoading.dismiss();
    }

    private void login(){
        showLoading();
        checkRequestLogin(this);
    }

    private void checkRequestLogin(LoginCallback callback){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();


                            /////////////////////////////
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email);
                            db.collection("users").document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        hideLoading();
                                        if (documentSnapshot.exists()) {
                                            // Retrieve user data and pass it to the callback
                                            callback.onLoginSuccess(documentSnapshot.toObject(User.class));
                                        } else {
                                            callback.onLoginFailure("User data not found");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        hideLoading();
                                        callback.onLoginFailure(e.getMessage());
                                    });
                        } else {
                            hideLoading();
                            String response = task.getException().getMessage();
                            if(response.equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                showToast("Email Belum Terdaftar");
                            }else if(response.equals("The password is invalid or the user does not have a password.")){
                                showToast("Kata Sandi Anda Salah");
                            }else {
                                showToast("Gagal Login, Coba Lagi nanti");
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle other failure scenarios, such as network issues
                        showToast(e.getMessage());
                    }
                });;

    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.d(TAG, "signInResult:success");
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failure, code=" + e.getStatusCode());
        }
    }


    @Override
    public void onLoginSuccess(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId",user.getUserId());
        editor.putString("email",user.getEmail());
        editor.putString("phoneNumber",user.getPhoneNumber());
        editor.putString("username",user.getName());
        editor.putString("avatar", user.getAvatar());
        editor.putString("birthDate",user.getBirthDate());
        editor.putString("gender",user.getGender());
        editor.putBoolean("isLogin",true);
        editor.apply();

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();


    }

    @Override
    public void onLoginFailure(String errorMessage) {
        showToast(errorMessage);
    }
}