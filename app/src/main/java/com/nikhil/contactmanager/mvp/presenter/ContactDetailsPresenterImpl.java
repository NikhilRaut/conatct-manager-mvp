package com.nikhil.contactmanager.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.nikhil.contactmanager.R;
import com.nikhil.contactmanager.api.RestApi;
import com.nikhil.contactmanager.mvp.models.Contact;
import com.nikhil.contactmanager.mvp.view.ContactDetailsView;
import com.nikhil.contactmanager.utility.Util;
import com.google.gson.JsonObject;
import com.orm.SugarRecord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Nikhil on 19-01-2017.
 */

public class ContactDetailsPresenterImpl implements ContactDetailsPresenter {

    private ContactDetailsView view;
    private Activity activity;
    private Retrofit retrofit;

    public ContactDetailsPresenterImpl(Activity activity, Retrofit retrofit) {
        this.activity = activity;
        this.view = (ContactDetailsView) activity;
        this.retrofit = retrofit;
    }

    @Override
    public void getContactDetailsFromServer(long contactID) {
        getContact(contactID);
    }

    @Override
    public void updateFavouriteToServer() {

    }

    @Override
    public void openPhoneDialer(String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber));
        activity.startActivity(intent);
    }

    @Override
    public void sentEmail(Contact contact) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("vnd.android.cursor.item/email");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{contact.getEmail()});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Contact");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hello sir\\Ma'am, \n\t");
        activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.sent_mail)));
    }

    @Override
    public void sentMessage(Contact contact) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.getPhoneNumber(), null)));
    }

    @Override
    public void shareContact(String contactDetails) {

    }

    @Override
    public void markAsFavourite(Contact contact) {
        updateContactToServer(contact);
    }

    private void getContact(long contactId) {

        if (Util.isInternetAvailable(activity)) {
            view.startProgressBar("Getting contact details..");
            Call<Contact> contacts = retrofit.create(RestApi.class).getContactDetails(String.valueOf(contactId));

            contacts.enqueue(new Callback<Contact>() {
                @Override
                public void onResponse(Call<Contact> call, final Response<Contact> response) {
                    updateContactDetails(response.body());
                    view.stopProgressBar();
                }

                @Override
                public void onFailure(Call<Contact> call, Throwable t) {
                    t.printStackTrace();
                    view.stopProgressBar();
                    view.onError(t.getMessage());
                }
            });
        } else {
            if (SugarRecord.listAll(Contact.class).size() < 0) {
                view.onError(activity.getString(R.string.error_internet));
            }
        }
    }

    private void updateContactDetails(Contact contact) {
        SugarRecord.save(contact);
        view.updateContactDetails(contact);
    }

    private void updateContactToServer(Contact contact) {
        if (Util.isInternetAvailable(activity)) {

            view.startProgressBar(activity.getString(R.string.prgs_update_contact));
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("id", contact.getContactId());
            jsonObject.addProperty("first_name", contact.getFirstName());
            jsonObject.addProperty("last_name", contact.getLastName());
            jsonObject.addProperty("phone_number", contact.getPhoneNumber());
            jsonObject.addProperty("profile_pic", contact.getProfilePic());
            jsonObject.addProperty("favorite", contact.getFavorite());
            jsonObject.addProperty("created_at", contact.getCreatedAt());
            jsonObject.addProperty("updated_at", contact.getUpdatedAt());

            Call<Contact> call = retrofit.create(RestApi.class).updateContact(jsonObject, String.valueOf(contact.getContactId()));
            call.enqueue(new Callback<Contact>() {
                @Override
                public void onResponse(Call<Contact> call, Response<Contact> response) {
                    SugarRecord.save(response.body());
                    view.stopProgressBar();
                    view.updateContactDetails(response.body());
                }

                @Override
                public void onFailure(Call<Contact> call, Throwable t) {
                    t.printStackTrace();
                    view.stopProgressBar();
                    view.onError(activity.getString(R.string.error_internet));
                }
            });
        } else {
            view.onError("Please connect to internet!");
        }

    }

    private void d() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                activity.getApplicationContext(),
                "",
                Regions.DEFAULT_REGION);

        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);

        TransferUtility transferUtility = new TransferUtility(s3Client, activity.getApplicationContext());

//        transferUtility.u
    }


}
