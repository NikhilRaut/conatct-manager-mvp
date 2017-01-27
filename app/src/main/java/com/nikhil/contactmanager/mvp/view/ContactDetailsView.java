package com.nikhil.contactmanager.mvp.view;

import com.nikhil.contactmanager.mvp.models.Contact;

/**
 * Created by Nikhil on 20-01-2017.
 */

public interface ContactDetailsView {

    void startProgressBar(String message);

    void stopProgressBar();

    void onError(String error);

    void updateContactDetails(Contact contact);
}
