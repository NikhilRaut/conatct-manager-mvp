package com.nikhil.contactmanager.mvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.nikhil.contactmanager.R;
import com.nikhil.contactmanager.mvp.presenter.ContactListPresenter;
import com.nikhil.contactmanager.mvp.presenter.ContactListPresenterImpl;
import com.nikhil.contactmanager.adapter.ContactAdapter;
import com.nikhil.contactmanager.api.App;
import com.nikhil.contactmanager.mvp.models.Contact;
import com.nikhil.contactmanager.utility.ProgressBarUtil;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class ContactListActivity extends AppCompatActivity implements ContactListView {


    @BindView(R.id.recycler_view_contact)
    RecyclerView recyclerView;

    @BindView(R.id.fast_scroller)
    VerticalRecyclerViewFastScroller fastScroller;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Inject
    Retrofit retrofit;

    private ContactAdapter adapter;
    private List<Contact> contactArrayList;
    private ContactListPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        ButterKnife.bind(this);
        SugarContext.init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((App) getApplication()).getNetComponent().inject(this);

        presenter = new ContactListPresenterImpl(this, retrofit);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactArrayList = new ArrayList<>();
        adapter = new ContactAdapter(this, contactArrayList);
        recyclerView.setAdapter(adapter);
        fastScroller.setRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(fastScroller.getOnScrollListener());

        adapter.SetOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                presenter.onItemClick(position);
            }
        });

        presenter.getContactListFromServer();

    }


    @Override
    protected void onResume() {
        super.onResume();
        presenter.checkForFavouriteUpdate();
    }


    @Override
    public void startProgressBar() {
        ProgressBarUtil.startProgressDialog(this, "Getting contact list...");
    }

    @Override
    public void stopProgressBar() {
        ProgressBarUtil.stopProgressDialog(this);
    }

    @Override
    public void updateContactToRecycleView(List<Contact> contacts) {
        contactArrayList.clear();
        contactArrayList.addAll(contacts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String error) {
        Snackbar.make(fab, error, Snackbar.LENGTH_LONG).setAction("Retry", null).show();
    }

    @Override
    public void navigateToContactDetails(int position) {
        Log.e("Act", "navigateToContactDetails ------------ position : " + position);
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        intent.putExtra("contact", contactArrayList.get(position));
        startActivity(intent);
    }


    @OnClick(R.id.fab)
    public void navigateToAddContact() {
        startActivity(new Intent(this, NewContactActivity.class));
//
    }


}
