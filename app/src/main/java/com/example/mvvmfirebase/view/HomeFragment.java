package com.example.mvvmfirebase.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mvvmfirebase.R;
import com.example.mvvmfirebase.model.SignInUser;
import com.example.mvvmfirebase.viewmodel.SignInViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private static final String TAG = "MyTag:HomeFragment: ";
    private Button mSignOutButton;
    private CircleImageView mProfileImageView;
    private TextView mNameTextView,mEmailTextView;
    // private SignInUser signInUser;
    private SignInViewModel mSignInViewModel;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getUserInfo();
        initGoogleSignInClient();
        init(view);
        mSignOutButton.setOnClickListener(v -> signOut());
    }
    private void init(View view){
        //find section
        mSignOutButton= view.findViewById(R.id.singOutButtonId);
        mProfileImageView= view.findViewById(R.id.profileImageId);
        mNameTextView= view.findViewById(R.id.profileNameId);
        mEmailTextView= view.findViewById(R.id.profileEmailId);
    }

    private void initGoogleSignInClient() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private void getUserInfo() {
        mSignInViewModel= new ViewModelProvider(getActivity(),ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(SignInViewModel.class);
        mSignInViewModel.collectUserInfo();
        mSignInViewModel.mCollectUserInfoLiveData.observe(getViewLifecycleOwner(), new Observer<SignInUser>() {
            @Override
            public void onChanged(SignInUser signInUser) {
                Log.d(TAG, "onChanged: "+signInUser.getEmail());
                setProfile(signInUser);
            }
        });
    }

    private void setProfile(SignInUser signInUser) {
        if(signInUser != null){
            Glide.with(getActivity()).load(signInUser.getImageUrl())
                    .centerCrop().placeholder(R.drawable.profile).into(mProfileImageView);
            mNameTextView.setText(signInUser.getName());
            mEmailTextView.setText(signInUser.getEmail());
        }
    }

    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        Intent intent= new Intent(getActivity(),SignInActivity.class);
        startActivity(intent);
        getActivity().onBackPressed();// after going to SignInActivity.class if we back pressed then we won't come in this activity
        //this is like finish()
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: called");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");
    }

}