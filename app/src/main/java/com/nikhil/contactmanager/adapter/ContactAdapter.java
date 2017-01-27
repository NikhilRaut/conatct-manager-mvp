package com.nikhil.contactmanager.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nikhil.contactmanager.R;
import com.nikhil.contactmanager.api.Constant;
import com.nikhil.contactmanager.mvp.models.Contact;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nikhil on 18-01-2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Activity activity;
    private List<Contact> arrayList;
    public static OnItemClickListener mItemClickListener;

    public ContactAdapter(Activity activity, List<Contact> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = arrayList.get(position);
        if (contact.getFavorite()) {
            holder.ivFavourite.setVisibility((position == 0) ? View.VISIBLE : View.GONE);
            holder.tvTitle.setText("");
        } else {
            holder.ivFavourite.setVisibility(View.GONE);
            String str1 = String.valueOf(contact.getFirstName().charAt(0)).toUpperCase();
            if (position != 0) {
                if (contact.isFirstLatter()) {
                    holder.tvTitle.setText(str1);
                } else {
                    String str2 = String.valueOf(arrayList.get(position - 1).getFirstName().charAt(0)).toUpperCase();
                    if (!str1.equalsIgnoreCase(str2)) {
                        holder.tvTitle.setText(str1);
                        contact.setFirstLatter(true);
                        arrayList.set(position, contact);
                    } else {
                        holder.tvTitle.setText("");
                    }
                }
            } else {
                holder.tvTitle.setText(str1);
            }
        }

        String imgUrl = contact.getProfilePic().contains("http") ? contact.getProfilePic() : Constant.HOST_URL + contact.getProfilePic();
        Picasso.with(activity).load(imgUrl).into(holder.ivProfile);
        holder.tvName.setText(contact.getFirstName() + " " + contact.getLastName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvT)
        TextView tvTitle;

        @BindView(R.id.ivProfile)
        CircleImageView ivProfile;

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.ivFavourite)
        ImageView ivFavourite;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
