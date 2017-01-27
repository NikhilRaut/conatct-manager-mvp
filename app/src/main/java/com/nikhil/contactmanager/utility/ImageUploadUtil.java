package com.nikhil.contactmanager.utility;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;

/**
 * Created by Nikhil on 21-01-2017.
 */

public class ImageUploadUtil {

    private static final String IDENTITY_POOL_ID = "";//Change id here..
    private static final String MY_BUCKET = "contacts-app";
    private String filePath;
    private File image;
    Context context;
    public static ImageUploadListener listener;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3Client s3Client;
    private TransferUtility transferUtility;
    private String imagePath = "";
    private String completeUrl = "https://" + MY_BUCKET + ".s3-ap-southeast-1.amazonaws.com/";

    //https://contacts-app.s3-ap-southeast-1.amazonaws.com/contacts/


    public void init(Context context) {
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),
                IDENTITY_POOL_ID,
                Regions.DEFAULT_REGION);
        s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, context.getApplicationContext());
        Log.e("init", "----------------------- imageUrl : " + completeUrl);
    }

    public void startImageUpload(String filePath) {
        try {
            image = new File(filePath);
            imagePath = "contacts/" + image.getName();
            Log.e("startImageUpload", "----------------------- image : " + image.getName());
            TransferObserver observer = transferUtility.upload(MY_BUCKET, completeUrl + imagePath, image, CannedAccessControlList.PublicRead);
            Log.e("startImageUpload", "----------------------- image : " + image.getName());
            observer.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (listener != null) {
                        listener.onStateChanged(id, state);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int percentage = (int) (bytesCurrent / bytesTotal * 100);
                    if (listener != null) {
                        if (percentage == 100) {
                            listener.onUploadComplete(completeUrl + imagePath);
                        }
                        listener.onProgressChanged(id, percentage);
                    }
                }

                @Override
                public void onError(int id, Exception ex) {
                    if (listener != null) {
                        listener.onError(id, ex);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(2, e);
            }
        }
    }

    public interface ImageUploadListener {

        public void onStateChanged(int id, TransferState state);

        public void onProgressChanged(int id, int percentage);

        public void onError(int id, Exception ex);

        public void onUploadComplete(String imageUrl);

    }

    public void SetImageUploadListener(ImageUploadListener uploadListener) {
        this.listener = uploadListener;
    }

}
