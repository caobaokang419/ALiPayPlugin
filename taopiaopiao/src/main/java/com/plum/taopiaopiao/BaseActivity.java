package com.plum.taopiaopiao;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.plum.pluginstand.PayInterfaceActivity;

/**
 * Created by mei on 2018/10/10.
 * Description:
 */

public class BaseActivity extends AppCompatActivity implements PayInterfaceActivity {

    protected Activity that;

    @Override
    public void attach(Activity proxyActivity) {
        this.that = proxyActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (that != null) {
            that.setContentView(layoutResID);
        } else {
            super.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View view) {
        if (that != null) {
            that.setContentView(view);
        } else {
            super.setContentView(view);
        }
    }

    @Override
    public View findViewById(@IdRes int id) {
        if (that != null) {
            return that.findViewById(id);
        }
        return super.findViewById(id);
    }

    @Override
    public Intent getIntent() {
        if (that != null) {
            return that.getIntent();
        }
        return super.getIntent();
    }

    @Override
    public ClassLoader getClassLoader() {
        if (that != null) {
            return that.getClassLoader();
        }
        return super.getClassLoader();
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        if (that != null) {
            return that.getLayoutInflater();
        }
        return super.getLayoutInflater();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        if (that != null) {
            return that.getApplicationInfo();
        }
        return super.getApplicationInfo();
    }

    @Override
    public Window getWindow() {
        if (that != null) {
            return that.getWindow();
        }
        return super.getWindow();
    }

    @Override
    public WindowManager getWindowManager() {
        if (that != null) {
            return that.getWindowManager();
        }
        return super.getWindowManager();
    }



    @Override
    public void startActivity(Intent intent) {
        String activityName = intent.getComponent().getClassName();
        // 启动的目标Activity由ProxyActivity的startActivity方法指定
        Intent p = new Intent();
        p.putExtra("className", activityName);
        that.startActivity(p);
    }

    @Override
    public ComponentName startService(Intent service) {
        Intent p = new Intent();
        p.putExtra("serviceName", service.getComponent().getClassName());
        return that.startService(p);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        that.sendBroadcast(intent);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return that.registerReceiver(receiver, filter);
    }
}
