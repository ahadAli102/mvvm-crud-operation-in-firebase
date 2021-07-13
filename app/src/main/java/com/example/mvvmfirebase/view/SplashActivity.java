package com.example.mvvmfirebase.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mvvmfirebase.model.SignInUser;
import com.example.mvvmfirebase.viewmodel.SignInViewModel;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "MyTag :SplashActivity: ";
    private SignInViewModel mSignInViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        checkIfUserAuthenticate();
    }
    private void init(){
        mSignInViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(SignInViewModel.class);
    }
    private void checkIfUserAuthenticate(){
        mSignInViewModel.mCheckAuthenticateLiveData.observe(this, new Observer<SignInUser>() {
            @Override
            public void onChanged(SignInUser signInUser) {
                Log.d(TAG, "onChanged: called on "+Thread.currentThread().getName());
                if(!signInUser.isLogIn()){
                    //if user is not sign in
                    goToSignInActivity();
                }
                else{
                    //if user is sign in
                    goToMainActivity();
                }
            }
        });
    }

    private void goToSignInActivity() {
        startActivity(new Intent(SplashActivity.this,SignInActivity.class));
        finish();
    }
    private void goToMainActivity(){
        startActivity(new Intent(SplashActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }
}