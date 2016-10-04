//Vollyの通信クラスです。
package com.example.hiroshima.myapplication;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.kii.cloud.storage.Kii;

public class VolleyApplication extends Application {
    private static VolleyApplication sInstance;

    private RequestQueue mRequestQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;
        Kii.initialize(getApplicationContext(), "c590209a", "28c840e70b92e2553960ecd7b05a95b3", Kii.Site.JP);
    }
    //インスタンスを返す関数
    public synchronized static VolleyApplication getInstance() {
        return sInstance;
    }

    //通信クラスを返す関数
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
