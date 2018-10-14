package com.plum.taopiaopiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.plum.pluginstand.PayInterfaceBroadcast;

/**
 * Created by mei on 2018/10/13.
 * Description:
 */

public class BaseBroadcast extends BroadcastReceiver implements PayInterfaceBroadcast {

    protected Context that;

    @Override
    public void attach(Context context) {
        this.that = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
