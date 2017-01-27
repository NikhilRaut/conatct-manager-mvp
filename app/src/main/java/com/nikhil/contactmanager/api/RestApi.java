package com.nikhil.contactmanager.api;

import com.nikhil.contactmanager.mvp.models.Contact;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Nikhil on 18-01-2017.
 */

public interface RestApi {


    @GET("/contacts.json")
    Call<List<Contact>> getContacts();

    @GET("/contacts/{contact_id}.json")
    Call<Contact> getContactDetails(@Path("contact_id") String contactId);

    @PUT("/contacts/{contact_id}.json")
    Call<Contact> updateContact(@Body JsonObject jsonObject, @Path("contact_id") String contactId);

    @POST("/contacts.json")
    Call<Contact> saveContact(@Body JsonObject jsonObject);


}
