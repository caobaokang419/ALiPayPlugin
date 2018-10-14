package com.plum.taopiaopiao;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 该方法会调用BaseActivity->setContentView方法->that(ProxyActivity)#setContentView方法
         * ->PhoneWindow#setContentView方法->接着调用LayoutInflate#inflate方法，在该方法内部调用了Context
         * 的getResources()方法，LayoutInflate在PhoneWindow初始化的时候被实例化了，传递了Context对象，而PhoneWindow
         * 则是在Activity#attach方法中初始化的，所以LayoutInflate#inflate方法最终调用的是ProxyActivity的
         * getResources()方法，而我们要得到的是我们插件的Resources，而不是宿主app的Resources，所以我们要重写
         * ProxyActivity的getResources方法
         *
         */
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start_activity).setOnClickListener(this);
        findViewById(R.id.btn_start_service).setOnClickListener(this);
        findViewById(R.id.btn_send_dynamic_broadcast).setOnClickListener(this);
        findViewById(R.id.btn_send_static_broadcast).setOnClickListener(this);

        // 给插件注册一个动态广播
        IntentFilter intentFilter = new IntentFilter("com.plum.taopiaopiao.MainActivity");
        registerReceiver(new DynamicBroadcast(), intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_activity:
                startActivity(new Intent(that, SecondActivity.class));
                break;
            case R.id.btn_start_service:
                startService(new Intent(that, MyService.class));
                break;
            case R.id.btn_send_dynamic_broadcast:
                Intent intent = new Intent();
                intent.setAction("com.plum.taopiaopiao.MainActivity");
                sendBroadcast(intent);
                break;
            case R.id.btn_send_static_broadcast:

                break;
        }
    }
}
