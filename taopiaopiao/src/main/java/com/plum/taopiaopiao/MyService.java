package com.plum.taopiaopiao;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends BaseService {

    private static final String TAG = "MyService";
    int i = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread() {
            @Override
            public void run() {
                while (i < 10) {
                    Log.i(TAG, "run: " + (i++));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
