package com.nikhil.contactmanager.mvp.presenter;

import android.app.Activity;
import android.util.Log;

import com.nikhil.contactmanager.R;
import com.nikhil.contactmanager.api.RestApi;
import com.nikhil.contactmanager.mvp.models.Contact;
import com.nikhil.contactmanager.mvp.view.ContactListView;
import com.nikhil.contactmanager.utility.Util;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Nikhil on 18-01-2017.
 */

public class ContactListPresenterImpl implements ContactListPresenter {

    private Retrofit retrofit;
    private ContactListView contactListView;
    private Activity activity;

    public ContactListPresenterImpl(Activity activity, Retrofit retrofit) {
        this.activity = activity;
        this.contactListView = (ContactListView) activity;
        this.retrofit = retrofit;
    }

    private void updateContactToRecycleView() {
        List<Contact> contacts = new ArrayList<>();
        contacts.addAll(SugarRecord.findWithQuery(Contact.class, "select * from Contact where favorite = ?" +
                " order by first_name COLLATE NOCASE ;", "1"));
        contacts.addAll(SugarRecord.findWithQuery(Contact.class, "select * from Contact where favorite = ? " +
                "order by first_name COLLATE NOCASE ;", "0"));
        contactListView.updateContactToRecycleView(contacts);
    }

    @Override
    public void getContactListFromServer() {

        getContact();
    }

    @Override
    public void checkForFavouriteUpdate() {
        List<Contact> contacts = new ArrayList<>();
        contacts.addAll(SugarRecord.findWithQuery(Contact.class, "select * from Contact where favorite = ? " +
                "order by first_name COLLATE NOCASE ;", "1"));
        contacts.addAll(SugarRecord.findWithQuery(Contact.class, "select * from Contact where favorite = ? " +
                "order by first_name COLLATE NOCASE ;", "0"));
        contactListView.updateContactToRecycleView(contacts);
    }

    @Override
    public void onItemClick(int position) {
        contactListView.navigateToContactDetails(position);
    }

    private void getContact() {
        if (SugarRecord.listAll(Contact.class).size() > 0) {
            updateContactToRecycleView();
            return;
        }

        if (Util.isInternetAvailable(activity)) {
            contactListView.startProgressBar();
            Call<List<Contact>> contacts = retrofit.create(RestApi.class).getContacts();

            contacts.enqueue(new Callback<List<Contact>>() {
                @Override
                public void onResponse(Call<List<Contact>> call, final Response<List<Contact>> response) {
                    SugarRecord.saveInTx(response.body());
                    updateContactToRecycleView();
                    contactListView.stopProgressBar();
                }

                @Override
                public void onFailure(Call<List<Contact>> call, Throwable t) {
                    t.printStackTrace();
                    contactListView.stopProgressBar();
                    contactListView.onError(t.getMessage());
                }
            });
        } else {
            if (SugarRecord.listAll(Contact.class).size() < 0) {
                contactListView.onError(activity.getString(R.string.error_internet));
            } else {
                updateContactToRecycleView();
            }
        }
    }
}
