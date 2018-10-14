package com.plum.pluginstand;

import android.content.Context;
import android.content.Intent;

/**
 * Created by mei on 2018/10/13.
 * Description:插件需要实现的广播的接口
 */

public interface PayInterfaceBroadcast {
    void attach(Context context);

    void onReceive(Context context, Intent intent);
}
