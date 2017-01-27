package com.nikhil.contactmanager.mvp.view;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nikhil.contactmanager.R;
import com.nikhil.contactmanager.mvp.presenter.ContactDetailsPresenter;
import com.nikhil.contactmanager.mvp.presenter.ContactDetailsPresenterImpl;
import com.nikhil.contactmanager.api.App;
import com.nikhil.contactmanager.api.Constant;
import com.nikhil.contactmanager.mvp.models.Contact;
import com.nikhil.contactmanager.utility.ProgressBarUtil;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Retrofit;

public class ContactDetailsActivity extends AppCompatActivity implements ContactDetailsView {

    @Inject
    Retrofit retrofit;

    @BindView(R.id.ivProfile)
    CircleImageView ivProfile;

    @BindView(R.id.ivFav)
    ImageView ivFav;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvPhone)
    TextView tvPhone;

    @BindView(R.id.tvEmail)
    TextView tvEmail;

    private ContactDetailsPresenter presenter;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        ButterKnife.bind(this);
        contact = (Contact) getIntent().getSerializableExtra("contact");
        Log.e("Contact", "Contact -------- " + contact.toString());
        ((App) getApplication()).getNetComponent().inject(this);

        presenter = new ContactDetailsPresenterImpl(this, retrofit);
        presenter.getContactDetailsFromServer(contact.getContactId());
        updateContactDetails(contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @OnClick(R.id.tvPhone)
    void onPhoneClick(View view) {
        presenter.openPhoneDialer(contact.getPhoneNumber());
    }


    @OnClick(R.id.tvEmail)
    void onEmailClick(View view) {
        presenter.sentEmail(contact);
    }

    @OnClick(R.id.ivFav)
    void onFavouriteClick(View view) {
        contact.setFavorite(!contact.getFavorite());
        presenter.markAsFavourite(contact);
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
    public void onError(String error) {
        Snackbar.make(ivProfile, error, Snackbar.LENGTH_LONG).setAction("Retry", null).show();
    }

    @Override
    public void updateContactDetails(Contact contact) {
        this.contact = contact;
        tvName.setText(contact.getFirstName() + " " + contact.getLastName());
        tvPhone.setText(contact.getPhoneNumber());
        tvEmail.setText(TextUtils.isEmpty(contact.getEmail()) ? "Not available" : contact.getEmail());
        String imgUrl = contact.getProfilePic().contains("http") ? contact.getProfilePic() : Constant.HOST_URL + contact.getProfilePic();
        Picasso.with(this).load(imgUrl).placeholder(R.drawable.default_profile)
                .resize(0, 500).error(R.drawable.default_profile).into(ivProfile);
        ivFav.setImageResource(contact.getFavorite() ? R.drawable.ic_favorite_select : R.drawable.ic_favorite_unselect);
    }
}
