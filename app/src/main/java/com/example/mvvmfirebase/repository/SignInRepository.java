package com.example.mvvmfirebase.repository;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvmfirebase.model.SignInUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignInRepository {
    public static final String TAG = "MyTag:SignInRepository:";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private SignInUser mUser = new SignInUser();
    private MutableLiveData<SignInUser> mIsAuthenticateMutableLiveData;
    private MutableLiveData<String> mAuthMutableLiveData;
    private MutableLiveData<SignInUser> mCollectUserMutableLiveData;
    public static String EXCEPTION_VALUE="";

    public MutableLiveData<SignInUser> checkAuthenticationInFirebase(){
        Log.d(TAG, "checkAuthenticationInFirebase: "+ Thread.currentThread().getName());
        mIsAuthenticateMutableLiveData = new MutableLiveData<>();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        if(currentUser==null){
            mUser.setLogIn(false);

        }
        else {
            mUser.setuId(currentUser.getUid());
            mUser.setLogIn(true);

        }
        mIsAuthenticateMutableLiveData.setValue(mUser);
        return mIsAuthenticateMutableLiveData;
    }

    public MutableLiveData<String> firebaseSignInWithGoogle(AuthCredential authCredential){
        Log.d(TAG, "firebaseSignInWithGoogle: "+ Thread.currentThread().getName());
        mAuthMutableLiveData = new MutableLiveData<>();
        mAuth.signInWithCredential(authCredential).addOnSuccessListener(authResult -> {
            Log.d(TAG, "firebaseSignInWithGoogle: addOnSuccessListener "+ Thread.currentThread().getName());
            FirebaseUser currentUser= mAuth.getCurrentUser();
            String uId= currentUser.getUid();
            mAuthMutableLiveData.setValue(uId);
        }).addOnFailureListener(e -> {
            Log.d(TAG, "firebaseSignInWithGoogle: addOnFailureListener"+Thread.currentThread().getName());
            EXCEPTION_VALUE = "error_on_firebaseSignInWithGoogle";
            mAuthMutableLiveData.setValue("error_on_firebaseSignInWithGoogle");
        });
        return mAuthMutableLiveData;
    }

    public MutableLiveData<SignInUser> collectUserData(){
        mCollectUserMutableLiveData = new MutableLiveData<>();
        FirebaseUser currentUser=  mAuth.getCurrentUser();
        if(currentUser !=null){
            String uId= currentUser.getUid();
            String name= currentUser.getDisplayName();
            String email= currentUser.getEmail();
            Uri getImageUrl= currentUser.getPhotoUrl();
            String imageUrl= getImageUrl.toString();
            SignInUser user= new SignInUser(uId,name,email,imageUrl);
            mCollectUserMutableLiveData.setValue(user);
        }
        return mCollectUserMutableLiveData;
    }

}
