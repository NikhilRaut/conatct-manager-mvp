package com.nikhil.contactmanager.mvp.presenter;

import android.content.Intent;

import com.nikhil.contactmanager.mvp.models.Contact;

/**
 * Created by Nikhil on 19-01-2017.
 */

public interface NewContactPresenter {

    boolean validateAndSaveContact(Contact contact);

    void saveContact(Contact contact);

    void openImageSelector();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void uploadImage(String imagePath);

}
