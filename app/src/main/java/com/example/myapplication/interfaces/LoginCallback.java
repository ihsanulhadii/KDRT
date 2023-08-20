package com.example.myapplication.interfaces;

import com.example.myapplication.model.User;

public interface LoginCallback {
   void onLoginSuccess(User user);
   void onLoginFailure(String errorMessage);
}

