package com.nikhil.contactmanager.api;

import android.app.Application;

import com.nikhil.contactmanager.api.DaggerNetComponent;

/**
 * Created by Nikhil on 18-01-2017.
 */

public class App extends Application {
    private NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(Constant.HOST_URL))
                .build();
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }
}
