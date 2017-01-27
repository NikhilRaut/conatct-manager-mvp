package com.nikhil.contactmanager.mvp.view;

import com.nikhil.contactmanager.mvp.models.Contact;

import java.util.List;

/**
 * Created by Nikhil on 18-01-2017.
 */

public interface ContactListView {

    void startProgressBar();

    void stopProgressBar();

    void updateContactToRecycleView(List<Contact> contacts);

    void onError(String error);

    void navigateToContactDetails(int position);

}

