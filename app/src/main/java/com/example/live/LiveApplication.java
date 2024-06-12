package com.example.live;

import android.app.Application;
import android.content.Context;


import com.logrocket.core.SDK;

public class LiveApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        SDK.init(
                this,
                base,
                options -> options.setAppID("zerbys/live")
        );
    }
}