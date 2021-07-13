package com.example.mvvmfirebase.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mvvmfirebase.R;
import com.example.mvvmfirebase.model.ContactUser;
import com.example.mvvmfirebase.utils.Util;
import com.example.mvvmfirebase.viewmodel.ContactViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class InsertFragment extends Fragment {

    private CircleImageView insertImageView;
    private EditText insertNameEditText;
    private EditText insertPhoneEditText;
    private EditText insertEmailEditText;

    private Button saveButton;
    private Uri insertImageUri= null;
    private static final int CAPTURE_PIC_CODE = 989;
    public static final String TAG = "MyTag:InsertFragment: ";
    //view Model
    private ContactViewModel mContactViewModel;

    public InsertFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialViewModel();

        //find section...
        insertImageView= view.findViewById(R.id.insertImageId);
        insertNameEditText= view.findViewById(R.id.insertNameId);
        insertPhoneEditText= view.findViewById(R.id.insertPhoneId);
        insertEmailEditText= view.findViewById(R.id.insertEmailId);
        saveButton= view.findViewById(R.id.saveButtonId);
        insertImageView.setOnClickListener(v -> uploadImage());
        saveButton.setOnClickListener(v -> {

            String id= Util.randomDigit();
            String name= insertNameEditText.getText().toString();
            String phone= insertPhoneEditText.getText().toString();
            String email= insertEmailEditText.getText().toString();
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(email) && insertImageUri != null){
                //show spots dialogue here.....
                final AlertDialog dialogue= new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setCancelable(true).build();
                dialogue.show();

                ContactUser user= new ContactUser(id,name,"image_uri",phone,email);
                mContactViewModel.insert(user,insertImageUri);
                while(mContactViewModel.mInsertResultLiveData==null){
                    Log.d(TAG, "onViewCreated: mContactViewModel.mInsertResultLiveData==null");
                }
                Log.d(TAG, "onViewCreated: mContactViewModel.mInsertResultLiveData!=null");

                mContactViewModel.mInsertResultLiveData.observe(getActivity(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        dialogue.dismiss();
                        Toast.makeText(getActivity(), ""+s, Toast.LENGTH_SHORT).show();
                    }
                });

                insertImageView.setImageResource(R.drawable.profile);
                insertNameEditText.setText("");
                insertPhoneEditText.setText("");
                insertEmailEditText.setText("");


            }
            else {
                Toast.makeText(getActivity(), "Please Fill up all field", Toast.LENGTH_SHORT).show();
            }


        });
    }




    private void initialViewModel() {
        mContactViewModel= new ViewModelProvider(getActivity(),ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(ContactViewModel.class);
    }

    private void uploadImage() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

            }
            else {
                imagePick();
            }
        }
        else {
            imagePick();
        }
    }

    private void imagePick() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getContext(),this);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==getActivity().RESULT_OK){
                insertImageUri= result.getUri();
                insertImageView.setImageURI(insertImageUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.w(TAG, "onActivityResult: ",error );
            }
        }
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