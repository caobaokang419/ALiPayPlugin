package com.plum.alipayplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.plum.pluginstand.PayInterfaceBroadcast;

import java.lang.reflect.Constructor;

import dalvik.system.DexClassLoader;

/**
 * Created by mei on 2018/10/13.
 * Description:
 */

public class ProxyBroadcast extends BroadcastReceiver {

    private String className;
    private PayInterfaceBroadcast mPayInterfaceBroadcast;

    public ProxyBroadcast(Context context, String className) {
        this.className = className;

        DexClassLoader dexClassLoader = PluginManager.getInstance().getDexClassLoader();
        try {
            Class<?> broadcastClass = dexClassLoader.loadClass(className);
            Constructor<?> constructor = broadcastClass.getConstructor(new Class[]{});
            Object instance = constructor.newInstance(new Object[]{});
            mPayInterfaceBroadcast = (PayInterfaceBroadcast) instance;
            mPayInterfaceBroadcast.attach(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mPayInterfaceBroadcast.onReceive(context, intent);
    }
}
