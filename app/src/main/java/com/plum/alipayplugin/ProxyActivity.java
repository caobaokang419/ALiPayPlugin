package com.plum.alipayplugin;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.plum.pluginstand.PayInterfaceActivity;

import java.lang.reflect.Constructor;

public class ProxyActivity extends AppCompatActivity {

    private static final String TAG = "ProxyActivity";
    //    需要加载淘票票的  类名
    private String className;
    private PayInterfaceActivity payInterfaceActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 解析得到将要启动的插件的activity的全类名
        className = getIntent().getStringExtra("className");
        Log.i(TAG, "onCreate: className=" + className);
        try {
            // 通过DexClassLoader 加载未安装apk的java类，这里就是Activity类
            Class<?> activityClass = getClassLoader().loadClass(className);
            Constructor<?> constructor = activityClass.getConstructor(new Class[]{});

            // 实例化Activity对象
            Object instance = constructor.newInstance(new Object[]{});

            // PayInterfaceActivity是一个接口，是我们实现插件化的一个标准
            // 插件apk中的每个Activity都要实现该接口
            payInterfaceActivity = (PayInterfaceActivity) instance;

            // 为插件activity设置 Context对象
            payInterfaceActivity.attach(this);

            // 调用Activity的onCreate方法
            Bundle bundle = new Bundle();
            payInterfaceActivity.onCreate(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }

    /**
     * 该方法一定要重写，否则无法找到正确的资源文件（如布局文件，图片等）
     * <p>
     * 在调用插件Activity的onCreate方法的时候，会调用setContentView方法->that(ProxyActivity)#setContentView方法
     * ->PhoneWindow#setContentView方法->接着调用LayoutInflate#inflate方法，在该方法内部调用了Context
     * 的getResources()方法，LayoutInflate在PhoneWindow初始化的时候被实例化了，传递了Context对象，而PhoneWindow
     * 则是在Activity#attach方法中初始化的，所以LayoutInflate#inflate方法最终调用的是ProxyActivity的
     * getResources()方法，而我们要得到的是我们插件的Resources，而不是宿主app的Resources，所以我们要重写
     * ProxyActivity的getResources方法
     */
    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    @Override
    public void startActivity(Intent intent) {
        intent.setClass(this, ProxyActivity.class);
        super.startActivity(intent);
    }

    @Override
    public ComponentName startService(Intent service) {
        service.setClass(this, ProxyService.class);
        return super.startService(service);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(new ProxyBroadcast(this, receiver.getClass().getName()), filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        payInterfaceActivity.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payInterfaceActivity.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        payInterfaceActivity.onPause();
    }

}
