package com.example.mvvmfirebase.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mvvmfirebase.model.SignInUser;
import com.example.mvvmfirebase.repository.SignInRepository;
import com.google.firebase.auth.AuthCredential;

public class SignInViewModel extends AndroidViewModel {
    private SignInRepository mSignInRepository;
    public LiveData<SignInUser> mCheckAuthenticateLiveData;
    public LiveData<String> mAuthenticateUserLiveData;
    public LiveData<SignInUser> mCollectUserInfoLiveData;
    public static final String TAG = "MyTag:SignInViewModel: ";

    public SignInViewModel(@NonNull Application application) {
        super(application);
        if(mSignInRepository==null){
            Log.d(TAG, "SignInViewModel: called on "+Thread.currentThread().getName());
            mSignInRepository = new SignInRepository();
        }
        checkIsAuthenticate();
    }

    public void checkIsAuthenticate(){
        Log.d(TAG, "checkIsAuthenticate: called on "+Thread.currentThread().getName());
        mCheckAuthenticateLiveData = mSignInRepository.checkAuthenticationInFirebase();
    }
    public void signInWithGoogle(AuthCredential authCredential){
        mAuthenticateUserLiveData = mSignInRepository.firebaseSignInWithGoogle(authCredential);
    }
    public void collectUserInfo(){
        mCollectUserInfoLiveData = mSignInRepository.collectUserData();
    }
}
