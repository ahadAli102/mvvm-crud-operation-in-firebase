package com.example.mvvmfirebase.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvmfirebase.model.ContactUser;
import com.example.mvvmfirebase.model.UpdateUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactRepository {
    public static final String TAG = "MyTag:ContactRepository";
    public static final String STORAGE_CHILD="profile_image";
    public static final String UPDATE_SUCCESS = "update success123";
    public static final String UPDATE_FAILED = "update success123";
    public static String STORAGE_INSERT_ERROR="";
    public static String FIRE_STORE_SUCCESS="FIRE_STORE_SUCCESS";
    public static final String FIRE_STORE_CONTACT="contact_list";
    public static final String FIRE_STORE_USER_INFO="user_info";
    public static final String DELETE_SUCCESS="delete success123";
    public static final String DELETE_FAILED="delete failed123";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mStorage.getReference();
    private FirebaseFirestore mFirebaseFireStore = FirebaseFirestore.getInstance();
    public MutableLiveData<String> mInsertCompletionMessageMutableLiveData;
    private MutableLiveData<List<ContactUser>> mContactMutableLiveData;
    private MutableLiveData<List> mDeleteContactMutableLiveData;
    private MutableLiveData<List> mUpdateContactMutableLiveData;

    public MutableLiveData<String> insertContactFirebase(final ContactUser user, Uri uri){

        final String currentUser= mAuth.getCurrentUser().getUid();
        mInsertCompletionMessageMutableLiveData = new MutableLiveData<>();
        final StorageReference image_path= mStorageReference.child("profile_image").child(currentUser).child(user.contactId+".jpg");
        image_path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: called");

                        Map<String,String> contactMap= new HashMap<>();
                        contactMap.put("contact_Id",user.contactId);
                        contactMap.put("contact_Name",user.contactName);
                        contactMap.put("contact_Image",uri.toString());
                        contactMap.put("contact_Phone",user.contactPhone);
                        contactMap.put("contact_Email",user.contactEmail);
                        contactMap.put("contact_Search",user.contactId);

                        //now put this data in firebase....
                        mFirebaseFireStore.collection(FIRE_STORE_CONTACT).document(currentUser).collection(FIRE_STORE_USER_INFO)
                                .document(user.contactId).set(contactMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: called");
                                        mInsertCompletionMessageMutableLiveData.setValue(FIRE_STORE_SUCCESS);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: called");
                                        mInsertCompletionMessageMutableLiveData.setValue(e.toString());
                                        reLode();
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: called");
                mInsertCompletionMessageMutableLiveData.setValue(e.toString());
            }
        });

        return  mInsertCompletionMessageMutableLiveData;
    }

    public MutableLiveData<List<ContactUser>> getContactList(){
        Log.d(TAG, "getContactList: called");
        Log.d(TAG, "getContactList: running on : "+Thread.currentThread().getName());
        String currentUser= mAuth.getCurrentUser().getUid();
        final List<ContactUser> contactList = new ArrayList<>();
        mContactMutableLiveData= new MutableLiveData<>();
        mFirebaseFireStore.collection(FIRE_STORE_CONTACT).document(currentUser).collection(FIRE_STORE_USER_INFO).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                contactList.clear();
                for (DocumentSnapshot documentSnapshot: task.getResult()){
                    String id= documentSnapshot.getString("contact_Id");
                    String name= documentSnapshot.getString("contact_Name");
                    String image= documentSnapshot.getString("contact_Image");
                    String phone= documentSnapshot.getString("contact_Phone");
                    String email= documentSnapshot.getString("contact_Email");
                    ContactUser user= new ContactUser(id,name,image,phone,email);
                    contactList.add(user);
                    Log.d(TAG, "onComplete: "+user.getContactName());
                }
                Log.d(TAG, "onComplete: "+contactList.size());
                mContactMutableLiveData.setValue(contactList);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: ",e);
            }
        });
        return mContactMutableLiveData;
    }
    private void reLode(){
        Log.d(TAG, "reLode: called on "+Thread.currentThread().getName());
        getContactList();
    }

    public MutableLiveData<List> deleteData(ContactUser user,final int position){
        List message = new ArrayList();
        mDeleteContactMutableLiveData = new MutableLiveData<>();
        final String currentUser= mAuth.getCurrentUser().getUid();
        StorageReference deleteRef = mStorage.getReferenceFromUrl(user.getContactImage());
        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirebaseFireStore.collection(FIRE_STORE_CONTACT).document(currentUser).collection(FIRE_STORE_USER_INFO)
                        .document(user.getContactId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        message.add(0,position);
                        message.add(1,DELETE_SUCCESS);
                        mDeleteContactMutableLiveData.setValue(message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        message.add(0,position);
                        message.add(1,DELETE_FAILED);
                        mDeleteContactMutableLiveData.setValue(message);
                    }
                });
            }
        }).addOnFailureListener(e -> {
            message.add(0,position);
            message.add(1,DELETE_FAILED);
            mDeleteContactMutableLiveData.setValue(message);
        });
        return mDeleteContactMutableLiveData;
    }

    public MutableLiveData<List> updateInfoFirebase(ContactUser newUser,ContactUser oldUser, int position, Uri updateImageUri){
        List updateMessage = new ArrayList(4);
        final String currentUser= mAuth.getCurrentUser().getUid();
        mUpdateContactMutableLiveData = new MutableLiveData<>();
        /*mFirebaseFireStore.collection(FIRE_STORE_CONTACT).document(currentUser)
                .collection(FIRE_STORE_USER_INFO).document(newUser.contactId)
                .update("contact_Name",newUser.contactName,
                        "contact_Phone",newUser.contactPhone,"contact_Email",newUser.contactEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                newUser.setContactImage(oldUser.getContactImage());
                updateMessage.add(0,position);
                updateMessage.add(1,UPDATE_SUCCESS);
                updateMessage.add(2,newUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateMessage.add(0,position);
                updateMessage.add(1,UPDATE_FAILED);
                updateMessage.add(2,oldUser);

            }
        });*/
        StorageReference deleteRef = mStorage.getReferenceFromUrl(oldUser.getContactImage());
        Log.d(TAG, "updateInfoFirebase: "+oldUser.getContactImage());
        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final StorageReference image_path= mStorageReference.child("profile_image")
                        .child(currentUser).child(newUser.contactId+".jpg");
                //delete old image
                image_path.putFile(updateImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //insert new image
                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String updateImageLink = uri.toString();
                                newUser.setContactImage(updateImageLink);
                                //update fire_store
                                mFirebaseFireStore.collection(FIRE_STORE_CONTACT).document(currentUser)
                                        .collection(FIRE_STORE_USER_INFO).document(newUser.contactId)
                                        .update("contact_Name",newUser.contactName,
                                                "contact_Phone",newUser.contactPhone,"contact_Email",newUser.contactEmail,
                                                "contact_Image",newUser.contactImage)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                updateMessage.add(0,position);
                                                updateMessage.add(1,UPDATE_SUCCESS);
                                                updateMessage.add(2,newUser);
                                                mUpdateContactMutableLiveData.setValue(updateMessage);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        updateMessage.add(0,position);
                                        updateMessage.add(1,UPDATE_FAILED);
                                        updateMessage.add(2,oldUser);
                                        Log.w(TAG, "onFailure: update fire_store",e );
                                        mUpdateContactMutableLiveData.setValue(updateMessage);
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "onFailure: insert new image ",e );
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: delete image ",e );
            }
        });
        return mUpdateContactMutableLiveData;
    }

}
