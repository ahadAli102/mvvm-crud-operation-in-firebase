package com.example.mvvmfirebase.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mvvmfirebase.R;
import com.example.mvvmfirebase.model.SignInUser;
import com.example.mvvmfirebase.repository.SignInRepository;
import com.example.mvvmfirebase.viewmodel.SignInViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    public static final String TAG = "MyTag:SignInActivity: ";
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private Button mSignInButton;

    //private FirebaseAuth mAuth;
    private SignInViewModel mSignInViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);
        Log.d(TAG, "onCreate: called");
        init();
        intiSignInViewModel();
        signInMethod();

    }
    private void init(){
        mSignInButton= findViewById(R.id.signInButtonId);
        mSignInButton.setOnClickListener(v -> signIn());
    }

    private void intiSignInViewModel() {

        mSignInViewModel= new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.
                getInstance(this.getApplication())).get(SignInViewModel.class);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInMethod() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if(account != null){
                    getGoogleAuthCredential(account);
                }
            } catch (ApiException e) {
                Log.w(TAG, "onActivityResult: ", e);
                Toast.makeText(this,""+e,Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void getGoogleAuthCredential(GoogleSignInAccount account) {
        String googleTokenId= account.getIdToken();
        AuthCredential authCredential= GoogleAuthProvider.getCredential(googleTokenId,null);
        signInWithGoogle(authCredential);
    }

    private void signInWithGoogle(AuthCredential authCredential) {
        mSignInViewModel.signInWithGoogle(authCredential);
        mSignInViewModel.mAuthenticateUserLiveData.observe(this, s -> {
            if(!s.equals(SignInRepository.EXCEPTION_VALUE)){
                Log.d(TAG, "signInWithGoogle: going to main activity");
                goToMainActivity();
            }
            else{
                Log.d(TAG, "signInWithGoogle: there is sn exception in SingInRepository firebaseSignInWithGoogle");
            }
        });
    }
    private void goToMainActivity(){
        Intent intent= new Intent(SignInActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*private void goToMainActivity(SignInUser signInUser) {
        getIntent().putExtra("userDetails",signInUser);
        FragmentManager fragmentManager= getSupportFragmentManager();
        HomeFragment homeFragment= new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.homeFragment,homeFragment).commit();
    }*/
}