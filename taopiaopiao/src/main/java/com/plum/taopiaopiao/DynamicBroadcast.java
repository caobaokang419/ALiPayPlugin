package com.plum.taopiaopiao;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by mei on 2018/10/13.
 * Description:
 */

public class DynamicBroadcast extends BaseBroadcast {

    @Override
    public void attach(Context context) {
        super.attach(context);
        Toast.makeText(context, "-----绑定上下文成功---->", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Toast.makeText(context, "-----插件收到广播--->", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "-----插件收到广播1--->", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "-----插件收到广播2--->", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "-----插件收到广播3--->", Toast.LENGTH_SHORT).show();

    }
}
