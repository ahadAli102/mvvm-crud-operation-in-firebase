package com.example.mvvmfirebase.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mvvmfirebase.R;
import com.example.mvvmfirebase.model.ContactUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    private List<ContactUser> mAdapterUserList;
    private List<ContactUser> mMainUserList;
    private ClickInterface clickInterface;

    public ContactAdapter(ClickInterface clickInterface) {
        this.clickInterface= clickInterface;
    }
    public void setUserList(List<ContactUser> mAdapterUserList){
        this.mAdapterUserList= mAdapterUserList;
        this.mMainUserList = new ArrayList<>(mAdapterUserList);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.single_contact,parent,false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext()).load(mAdapterUserList.get(position).getContactImage()).centerCrop()
                .placeholder(R.drawable.profile).into(holder.circleImageView);
        holder.contactId.setText(mAdapterUserList.get(position).getContactId());
        holder.contactName.setText(mAdapterUserList.get(position).getContactName());
    }

    @Override
    public int getItemCount() {
        return mAdapterUserList.size();
    }

    public ContactUser getPosition(int position){
        return mAdapterUserList.get(position);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private CircleImageView circleImageView;
        private TextView contactId;
        private TextView contactName;

        public ContactViewHolder(View itemView) {
            super(itemView);
            circleImageView= itemView.findViewById(R.id.singleImageId);
            contactId= itemView.findViewById(R.id.singleContactId);
            contactName= itemView.findViewById(R.id.singleNameId);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            clickInterface.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            clickInterface.onLongItemClick(getAdapterPosition());
            return false;
        }
    }
    public Filter getFilter(){
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence ch) {
            List<ContactUser> filterUser = new ArrayList<>();
            final String filterPattern = ch.toString().toLowerCase();
            if(ch==null || ch.length()==0){
                filterUser.addAll(mMainUserList);
            }
            else {
                for(ContactUser m : mMainUserList){
                    if(m.getContactName().toLowerCase().contains(filterPattern) || m.getContactId().toLowerCase().contains(filterPattern) ){
                        filterUser.add(m);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values=filterUser;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mAdapterUserList.clear();
            mAdapterUserList.addAll((List<ContactUser>)results.values);
            notifyDataSetChanged();
        }
    };
    public interface ClickInterface {
        // for on Click....
        void onItemClick(int position);
        void onLongItemClick(int position);
    }

}