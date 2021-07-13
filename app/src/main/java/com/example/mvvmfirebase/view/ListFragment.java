package com.example.mvvmfirebase.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mvvmfirebase.R;
import com.example.mvvmfirebase.adapter.ContactAdapter;
import com.example.mvvmfirebase.dialog.DetailsDialog;
import com.example.mvvmfirebase.model.ContactUser;
import com.example.mvvmfirebase.repository.ContactRepository;
import com.example.mvvmfirebase.viewmodel.ContactViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ListFragment extends Fragment implements ContactAdapter.ClickInterface{
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ContactAdapter mAdapter;
    private List<ContactUser> mUserList= new ArrayList<>();
    public static final String TAG="MyTag:ListFragment:";
    private Button mUpdateImageButton,mUpdateInfoButton;
    private CircleImageView mUpdateImage;
    private TextView mIdTextView;
    private EditText mNameEditText,mPhoneEditText,mEmailEditText;
    private SearchView mSearchView;
    private Uri mUpdateUri= null;
    int mUserPosition;

    //view Model
    private ContactViewModel mContactViewModel;


    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: called");
        initViewModel();
        searchView= view.findViewById(R.id.searchViewId);
        recyclerView= view.findViewById(R.id.recycleViewId);
        mAdapter= new ContactAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setUpRecycle();
        setSearchProperty(view);
    }

    private void setSearchProperty(@NonNull View view){
        mSearchView = view.findViewById(R.id.searchViewId);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    private void setUpRecycle() {
        Log.d(TAG, "setUpRecycle: called");
        final AlertDialog dialogue= new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setCancelable(true).build();
        dialogue.show();
        mContactViewModel.loadData();
        while ( mContactViewModel.mContactUserListLiveData==null){
        }
        if(mAdapter == null){
            Log.d(TAG, "setUpRecycle: mAdapter == null");
            mAdapter = new ContactAdapter(this);
        }
        Log.d(TAG, "setUpRecycle: mAdapter != null");
        Log.d(TAG, "setUpRecycle: mContactViewModel.mContactUserListLiveData!=null");
        mContactViewModel.mContactUserListLiveData.observe(getActivity(), new Observer<List<ContactUser>>() {
            @Override
            public void onChanged(List<ContactUser> contactUsers) {
                dialogue.dismiss();
                mUserList= contactUsers;
                //mUserList.clear();
                mAdapter.setUserList(mUserList);
                recyclerView.setAdapter(mAdapter);
                //Toast.makeText(getActivity(), ""+contactUsers.get(0).getContactName(), Toast.LENGTH_SHORT).show(); //check it before set recycle

            }
        });
    }

    private void initViewModel() {
        Log.d(TAG, "initViewModel: called");
        mContactViewModel= new ViewModelProvider(getActivity(),ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(ContactViewModel.class);
    }


    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), ""+position, Toast.LENGTH_SHORT).show();
        openDetailsDialogue(position);
    }
    @Override
    public void onLongItemClick(final int position) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        String[] option= {"Update","Delete"};
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    update(position);
                }
                if(which==1){
                    mContactViewModel.delete(mUserList.get(position),position);
                    mContactViewModel.mDeleteContactLiveData.observe(getActivity(), new Observer<List>() {
                        @Override
                        public void onChanged(List list) {
                            Log.d(TAG, "onChanged: "+list.get(1)+" "+list.get(0));
                            if(list.get(1).toString().equals(ContactRepository.DELETE_SUCCESS)){
                                mUserList.remove(position);
                                mAdapter.notifyItemRemoved(position);
                            }
                            else{
                                Toast.makeText(getActivity(), "Delete failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).create().show();

    }

    private void update(final int position) {
        Log.d(TAG, "update: called");
        if (mUpdateUri!=null){
            mUpdateUri = null;
        }
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        final View view= inflater.inflate(R.layout.update_dialog,null);
        builder.setView(view).setTitle("Update Contact").setIcon(R.drawable.ic_update).setCancelable(true)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateInfo(builder,position);
            }
        });
        mUpdateInfoButton= view.findViewById(R.id.updateInfoButtonId);
        mIdTextView= view.findViewById(R.id.updateId);
        mNameEditText= view.findViewById(R.id.updateNameId);
        mPhoneEditText= view.findViewById(R.id.updatePhoneId);
        mEmailEditText= view.findViewById(R.id.updateEmailId);
        mUpdateImage = view.findViewById(R.id.updateImageViewId);
        mUpdateInfoButton.setOnClickListener(v -> {


        });
        mUpdateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
                if(mUpdateUri!=null){
                    Glide.with(view.getContext()).load(mUserList.get(position).getContactImage()).into(mUpdateImage);
                }
            }
        });
        mIdTextView.setText("ID: "+mUserList.get(position).getContactId());
        mNameEditText.setText(mUserList.get(position).getContactName());
        mPhoneEditText.setText(mUserList.get(position).getContactPhone());
        mEmailEditText.setText(mUserList.get(position).getContactEmail());
        Glide.with(getActivity()).load(mUserList.get(position).getContactImage()).into(mUpdateImage);
        builder.create().show();
    }
    

    private void pickImage() {
        Log.d(TAG, "pickImage: called");
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getContext(),this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: called");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==getActivity().RESULT_OK){
                mUpdateUri= result.getUri();
                mUpdateImage.setImageURI(mUpdateUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.w(TAG, "onActivityResult: ",error );
            }
        }
    }
    private void openDetailsDialogue(int position) {
        DetailsDialog dialogue= new DetailsDialog(mUserList.get(position));
        dialogue.show(getChildFragmentManager(),"Details Dialogue");

    }
    private void updateInfo(AlertDialog.Builder builder,final int position){
        Toast.makeText(getActivity(), "will be updated", Toast.LENGTH_SHORT).show();
        String id= mUserList.get(position).getContactId();
        String name= mNameEditText.getText().toString();
        String phone= mPhoneEditText.getText().toString();
        String email= mEmailEditText.getText().toString();
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(email) && mUpdateUri != null){
            Log.d(TAG, "updateInfo: all are good");
            builder.setCancelable(true);
            ContactUser newUser= new ContactUser(id,name,"image_uri",phone,email);
            mContactViewModel.updateInfo(newUser,mUserList.get(position),position,mUpdateUri);

            if(mContactViewModel.mUpdateContactLiveData==null){
                Log.d(TAG, "update: mContactViewModel.mUpdateContactLiveData==null");

            }
            while(mContactViewModel.mUpdateContactLiveData==null){

            }
            if(mContactViewModel.mUpdateContactLiveData!=null){
                Log.d(TAG, "update: mContactViewModel.mUpdateContactLiveData!=null");
            }
            mContactViewModel.mUpdateContactLiveData.observe(getActivity(), new Observer<List>() {
                @Override
                public void onChanged(List updateMessage) {
                    Log.d(TAG, "onChanged: "+updateMessage.get(1)+" "+updateMessage.get(0));
                    Toast.makeText(getActivity(), ""+updateMessage.get(1), Toast.LENGTH_SHORT).show();
                    if(updateMessage.get(1).toString().equals(ContactRepository.UPDATE_SUCCESS)){
                        int position = Integer.parseInt(""+updateMessage.get(0));
                        ContactUser updateUser = (ContactUser) updateMessage.get(2);
                        mUserList.set(position,updateUser);
                        mAdapter.setUserList(mUserList);
                        mAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onChanged: all done");
                    }
                }
            });
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