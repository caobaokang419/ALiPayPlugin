package com.plum.alipayplugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.plum.pluginstand.PayInterfaceService;

import java.lang.reflect.Constructor;

import dalvik.system.DexClassLoader;

public class ProxyService extends Service {

    private String serviceName;
    private PayInterfaceService mPayInterfaceService;

    public ProxyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mPayInterfaceService == null) {
            init(intent);
        }
        return mPayInterfaceService.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (mPayInterfaceService == null) {
            init(intent);
        }
        mPayInterfaceService.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        init(intent);
        return null;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        if (mPayInterfaceService != null) {
            mPayInterfaceService.onBind(intent);
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        if (mPayInterfaceService != null) {
            mPayInterfaceService.onRebind(intent);
        }
        super.onRebind(intent);
    }

    @Override
    public void onLowMemory() {
        if (mPayInterfaceService != null) {
            mPayInterfaceService.onLowMemory();
        }
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        if (mPayInterfaceService != null) {
            mPayInterfaceService.onDestroy();
        }
        super.onDestroy();
    }

    private void init(Intent intent) {
        serviceName = intent.getStringExtra("serviceName");

        DexClassLoader dexClassLoader = PluginManager.getInstance().getDexClassLoader();
        try {
            Class<?> serviceClass = dexClassLoader.loadClass(serviceName);

            Constructor<?> serviceConstructor = serviceClass.getConstructor(new Class[]{});
            Object obj = serviceConstructor.newInstance(new Object[]{});
            mPayInterfaceService = (PayInterfaceService) obj;

            mPayInterfaceService.attach(this);
            mPayInterfaceService.onCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
