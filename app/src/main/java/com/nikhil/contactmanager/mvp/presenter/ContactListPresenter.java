package com.nikhil.contactmanager.mvp.presenter;

/**
 * Created by Nikhil on 19-01-2017.
 */

public interface ContactListPresenter {

    public void getContactListFromServer();

    public void checkForFavouriteUpdate();

    public void onItemClick(int position);

}
