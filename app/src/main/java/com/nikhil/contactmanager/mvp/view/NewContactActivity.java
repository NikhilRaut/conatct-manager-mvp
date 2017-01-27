package com.nikhil.contactmanager.mvp.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.nikhil.contactmanager.R;
import com.nikhil.contactmanager.mvp.presenter.NewContactPresenter;
import com.nikhil.contactmanager.mvp.presenter.NewContactPresenterImpl;
import com.nikhil.contactmanager.api.App;
import com.nikhil.contactmanager.mvp.models.Contact;
import com.nikhil.contactmanager.utility.ProgressBarUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Retrofit;

public class NewContactActivity extends AppCompatActivity implements NewContactView {

    @Inject
    Retrofit retrofit;

    @BindView(R.id.ivProfile)
    CircleImageView ivProfile;

    @BindView(R.id.etFirstName)
    EditText etFirstName;

    @BindView(R.id.etLastName)
    EditText etLastName;

    @BindView(R.id.etPhone)
    EditText etPhone;

    @BindView(R.id.etEmail)
    EditText etEmail;

    NewContactPresenter presenter;

    String imageUrl;

    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((App) getApplication()).getNetComponent().inject(this);
        ButterKnife.bind(this);

        presenter = new NewContactPresenterImpl(this, retrofit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.btnSave)
    public void saveContact() {
        Contact contact = new Contact();
        contact.setFirstName(etFirstName.getText().toString().trim());
        contact.setLastName(etLastName.getText().toString().trim());
        contact.setPhoneNumber(etPhone.getText().toString().trim());
        contact.setEmail(etEmail.getText().toString());
        contact.setProfilePic(imageUrl);
        this.contact = contact;
        if (presenter.validateAndSaveContact(contact)) {
            presenter.saveContact(contact);
        }
    }

    @Override
    public void startProgressBar(String message) {
        ProgressBarUtil.startProgressDialog(this, message);
    }

    @Override
    public void stopProgressBar() {
        ProgressBarUtil.stopProgressDialog(this);
    }


    @Override
    public void updateProgress(int progress) {
        Log.e("URL", "----------------------- Progress : " + progress);
        ProgressBarUtil.updateProgress(progress);
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorFirstName(String error) {
        etFirstName.setError(error);
    }

    @Override
    public void onErrorPhoneNumber(String error) {
        etPhone.setError(error);
    }

    @Override
    public void onErrorEmail(String error) {

    }

    @Override
    public void onSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void setProfileImage(Bitmap bitmap, String imageUrl) {
        ivProfile.setImageBitmap(bitmap);
        Log.e("URL", "----------------------- imageUrl : " + imageUrl);
        this.imageUrl = imageUrl;
        presenter.uploadImage(imageUrl);
    }

    @Override
    public void setProfileImageUrl(String imageUrl) {
        Log.e("URL", "----------------------- imageUrl : " + imageUrl);
        this.imageUrl = imageUrl;
//        presenter.uploadImage(imageUrl);
    }

    @OnClick(R.id.ivProfile)
    public void takePhoto() {
        presenter.openImageSelector();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }


}
