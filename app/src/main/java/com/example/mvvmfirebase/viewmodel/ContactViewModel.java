package com.example.mvvmfirebase.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mvvmfirebase.model.ContactUser;
import com.example.mvvmfirebase.model.UpdateUser;
import com.example.mvvmfirebase.repository.ContactRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ContactViewModel extends AndroidViewModel {
    private ContactRepository mContactRepository;
    public LiveData<String> mInsertResultLiveData;
    public LiveData<List<ContactUser>> mContactUserListLiveData;
    public LiveData<List> mDeleteContactLiveData;
    public LiveData<List> mUpdateContactLiveData;
    private Executor mInsertExecutor = Executors.newSingleThreadExecutor();
    private Executor mLoadDataExecutor = Executors.newSingleThreadExecutor();
    public static final String TAG="MyTag:ContactViewModel:";
    public ContactViewModel(@NonNull Application application) {
        super(application);
        mContactRepository = new ContactRepository();
    }
    public void insert(ContactUser user, Uri uri){
        Log.d(TAG, "insert: called");
        mInsertExecutor.execute(new Runnable() {
            @Override
            public void run() {
                insertMessage(user, uri);
            }
        });
    }
    public void getData(){
        Log.d(TAG, "getData: called on "+Thread.currentThread().getName());
        mContactUserListLiveData = mContactRepository.getContactList();
    }

    public void loadData() {
        Log.d(TAG, "loadData: called");
        mLoadDataExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        });
    }
    private void insertMessage(ContactUser user, Uri uri){
        Log.d(TAG, "insertMessage: called on : "+Thread.currentThread().getName());
        mInsertResultLiveData = mContactRepository.insertContactFirebase(user, uri);
    }
    public void delete(ContactUser user,final int position){
        mDeleteContactLiveData = mContactRepository.deleteData(user,position);

    }

    public void updateInfo(ContactUser newUser,ContactUser oldUser,final int position,final Uri updateImageUri) {
        Log.d(TAG, "updateInfo: called");
        mUpdateContactLiveData = mContactRepository.updateInfoFirebase(newUser,oldUser,position,updateImageUri);
    }
}
