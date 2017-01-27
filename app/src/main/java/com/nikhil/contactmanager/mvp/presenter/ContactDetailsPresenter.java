package com.nikhil.contactmanager.mvp.presenter;

import com.nikhil.contactmanager.mvp.models.Contact;

/**
 * Created by Nikhil on 19-01-2017.
 */

public interface ContactDetailsPresenter {

    public void getContactDetailsFromServer(long contactID);

    public void updateFavouriteToServer();

    public void openPhoneDialer(String mobileNumber);

    public void sentEmail(Contact contact);

    public void sentMessage(Contact contact);

    public void shareContact(String contactDetails);

    public void markAsFavourite(Contact contact);

}
