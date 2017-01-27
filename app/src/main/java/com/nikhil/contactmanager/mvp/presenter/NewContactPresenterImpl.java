package com.nikhil.contactmanager.mvp.presenter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.nikhil.contactmanager.R;
import com.nikhil.contactmanager.api.Constant;
import com.nikhil.contactmanager.api.RestApi;
import com.nikhil.contactmanager.mvp.models.Contact;
import com.nikhil.contactmanager.mvp.view.NewContactView;
import com.nikhil.contactmanager.utility.ImageUploadUtil;
import com.nikhil.contactmanager.utility.Util;
import com.google.gson.JsonObject;
import com.orm.SugarRecord;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Nikhil on 19-01-2017.
 */

public class NewContactPresenterImpl implements NewContactPresenter {

    NewContactView view;
    Retrofit retrofit;
    Activity activity;
    ImageUploadUtil imageUploadUtil;

    public NewContactPresenterImpl(Activity activity, Retrofit retrofit) {
        this.activity = activity;
        this.retrofit = retrofit;
        view = (NewContactView) activity;
    }

    @Override
    public boolean validateAndSaveContact(Contact contact) {

        if (TextUtils.isEmpty(contact.getFirstName()) || (contact.getFirstName().length() < 2)) {
            view.onErrorFirstName(activity.getString(R.string.error_first_name));
            return false;
        } else if (TextUtils.isEmpty(contact.getPhoneNumber()) || (contact.getPhoneNumber().length() < 9)) {
            view.onErrorPhoneNumber(activity.getString(R.string.error_phone));
            return false;
        }
        return true;
    }

    @Override
    public void saveContact(Contact contact) {
        if (Util.isInternetAvailable(activity)) {

            view.startProgressBar("Saving contact...");
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("id", contact.getContactId());
            jsonObject.addProperty("first_name", contact.getFirstName());
            jsonObject.addProperty("last_name", contact.getLastName());
            jsonObject.addProperty("phone_number", contact.getPhoneNumber());
            jsonObject.addProperty("profile_pic", contact.getProfilePic());
            jsonObject.addProperty("email", contact.getEmail());
            jsonObject.addProperty("favorite", contact.getFavorite());
            jsonObject.addProperty("created_at", contact.getCreatedAt());
            jsonObject.addProperty("updated_at", contact.getUpdatedAt());

            Call<Contact> call = retrofit.create(RestApi.class).saveContact(jsonObject);
            call.enqueue(new Callback<Contact>() {
                @Override
                public void onResponse(Call<Contact> call, Response<Contact> response) {
                    SugarRecord.save(response.body());
                    view.stopProgressBar();
                    view.onSuccessMessage("Added contact successfully..");
                }

                @Override
                public void onFailure(Call<Contact> call, Throwable t) {
                    t.printStackTrace();
                    view.stopProgressBar();
                    view.onError("Not able to connect server");
                }
            });
        } else {
            view.onError("Please connect to internet!");
        }
    }

    @Override
    public void openImageSelector() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        CharSequence items[] = new CharSequence[]{"Camera", "Gallery"};
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int n) {
                d.dismiss();
                switch (n) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setTitle("Which one?");
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_CODE_CAMERA:
                    handleCameraImage(data);
                    break;
                case Constant.REQUEST_CODE_GALLERY:
                    handleGalleryImage(data);
                    break;
            }
        } else {
            view.onError("Please select image");
        }
    }

    @Override
    public void uploadImage(String imagePath) {
        view.startProgressBar("uploading image.. ");
        imageUploadUtil = new ImageUploadUtil();
        imageUploadUtil.init(activity);
        imageUploadUtil.startImageUpload(imagePath);
        imageUploadUtil.SetImageUploadListener(new ImageUploadUtil.ImageUploadListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, int percentage) {
                view.updateProgress(percentage);
            }

            @Override
            public void onError(int id, Exception ex) {
                view.stopProgressBar();
                view.onError("Image upload faild");
            }

            @Override
            public void onUploadComplete(String imageUrl) {
                view.stopProgressBar();
                view.setProfileImageUrl(imageUrl);
            }
        });
    }

    private void handleCameraImage(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        view.setProfileImage(thumbnail, destination.getPath());


    }

    private void handleGalleryImage(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        view.setProfileImage(bm, getRealPathFromURI(data.getData()));
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, Constant.REQUEST_CODE_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image "), Constant.REQUEST_CODE_GALLERY);
    }

    private String getRealPathFromURI(Uri contentURI) {
        try {
            String[] proj = {MediaStore.Audio.Media.DATA};
            Cursor cursor = activity.managedQuery(contentURI, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
