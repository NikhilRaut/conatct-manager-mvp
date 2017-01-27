package com.nikhil.contactmanager.api;

import com.nikhil.contactmanager.mvp.view.ContactDetailsActivity;
import com.nikhil.contactmanager.mvp.view.ContactListActivity;
import com.nikhil.contactmanager.mvp.view.NewContactActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Nikhil on 18-01-2017.
 */

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(ContactListActivity activity);

    void inject(ContactDetailsActivity activity);

    void inject(NewContactActivity activity);
}