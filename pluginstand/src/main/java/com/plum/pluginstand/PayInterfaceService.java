package com.plum.pluginstand;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * Created by mei on 2018/10/13.
 * Description:定义插件Service需要实现的接口
 */

public interface PayInterfaceService {
    void onCreate();

    void onStart(Intent intent, int startId);

    int onStartCommand(Intent intent, int flags, int startId);

    void onDestroy();

    void onConfigurationChanged(Configuration newConfig);

    void onLowMemory();

    void onTrimMemory(int level);

    IBinder onBind(Intent intent);

    boolean onUnbind(Intent intent);

    void onRebind(Intent intent);

    void onTaskRemoved(Intent rootIntent);

    void attach(Service proxyService);
}
