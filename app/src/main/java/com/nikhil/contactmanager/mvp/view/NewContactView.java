package com.nikhil.contactmanager.mvp.view;

import android.graphics.Bitmap;

/**
 * Created by Nikhil on 21-01-2017.
 */

public interface NewContactView {

    void startProgressBar(String message);

    void stopProgressBar();

    void updateProgress(int progress);

    void onError(String error);

    void onErrorFirstName(String error);

    void onErrorPhoneNumber(String error);

    void onErrorEmail(String error);

    void onSuccessMessage(String message);

    void setProfileImage(Bitmap bitmap, String filePath);

    void setProfileImageUrl(String imageUrl);
}
