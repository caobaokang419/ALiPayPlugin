package com.plum.alipayplugin;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    static final String ACTION = "com.dongnao.receivebrod.Receive1.PLUGIN_ACTION";

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PluginManager.getInstance().setContext(this);
        requestPermission();

        // 注册广播，用户接收插件发送来的广播
        registerReceiver(mReceiver, new IntentFilter(ACTION));
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, " 我是宿主，收到你的消息,握手完成!", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 加载插件
     *
     * @param v
     */
    public void load(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadPlugin();
            }
        }).start();
    }

    public void click(View view) {
        // 启动代理Activity，通过代理Activity启动插件的MainActivity
        Intent intent = new Intent(this, ProxyActivity.class);
        String activityName = PluginManager.getInstance().getPackageInfo().activities[0].name;
        Log.i(TAG, "click: activityName=" + activityName);
        intent.putExtra("className", activityName);
        startActivity(intent);
    }

    // 给插件发送一条广播，插件的广播是静态注册的
    public void sendBroadCast(View view) {
        Toast.makeText(getApplicationContext(), "我是宿主  插件插件!收到请回答!!  1", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction("com.plum.taopiaopiao.MainActivity");
        sendBroadcast(intent);
    }

    /**
     * 加载插件
     */
    private void loadPlugin() {
        File filesDir = getDir("plugin", Context.MODE_PRIVATE);
        String name = "pluginb.apk";

        // 1.插件将要复制到的位置
        String filePath = new File(filesDir, name).getAbsolutePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        InputStream is = null;
        FileOutputStream os = null;

        try {
            // 2.插件下载存储的位置
            File pluginDownloadPath = new File(Environment.getExternalStorageDirectory(), name);
//            File pluginDownloadPath = new File(name);
            Log.i(TAG, "加载插件 " + pluginDownloadPath.getAbsolutePath());
            is = new FileInputStream(pluginDownloadPath);
            os = new FileOutputStream(filePath);

            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }

            // 复制成功
            File f = new File(filePath);
            if (f.exists()) {
                getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "dex overwrite", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // 把插件资源加入内存当中
            PluginManager.getInstance().loadPath(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 0);
        }

    }
}
