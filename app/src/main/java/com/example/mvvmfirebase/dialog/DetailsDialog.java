package com.example.mvvmfirebase.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.mvvmfirebase.R;
import com.example.mvvmfirebase.model.ContactUser;


import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsDialog extends DialogFragment {
    private CircleImageView mCircleImageView;
    private TextView mIdTextView,mNameTextView,mPhoneTextView,mEmailTextView;
    private ContactUser mUser;


    public DetailsDialog(ContactUser user) {
        this.mUser = user;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.details_dialog,null);
        builder.setView(view).setTitle("Contact Details").setIcon(R.drawable.ic_view).setCancelable(true)
                .setNegativeButton("Close", (dialog, which) -> {

                });

        mCircleImageView= view.findViewById(R.id.detailsImageId);
        mIdTextView= view.findViewById(R.id.detailsId);
        mNameTextView= view.findViewById(R.id.detailsNameId);
        mPhoneTextView= view.findViewById(R.id.detailsPhoneId);
        mEmailTextView= view.findViewById(R.id.detailsEmailId);
        Glide.with(view.getContext()).load(mUser.getContactImage()).centerCrop()
                .placeholder(R.drawable.profile).into(mCircleImageView);
        mIdTextView.setText("ID: "+mUser.getContactId());
        mNameTextView.setText("Name: "+mUser.getContactName());
        mPhoneTextView.setText("Phone: "+mUser.getContactPhone());
        mEmailTextView.setText("Email: "+mUser.getContactEmail());
        return builder.create();

    }
}
