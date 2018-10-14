package com.plum.alipayplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by mei on 2018/10/10.
 * Description:
 */

public class PluginManager {


    private Context context;

    private DexClassLoader dexClassLoader;

    private static final PluginManager ourInstance = new PluginManager();
    private PackageInfo packageInfo;
    private Resources resources;

    public static PluginManager getInstance() {
        return ourInstance;
    }

    private PluginManager() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public Resources getResources() {
        return resources;
    }

    /**
     * 加载指定的插件，解析出插件的:
     * PackageInfo信息(类信息存储类)，
     * Resources信息（资源信息存储类）
     * 构造加载类的ClassLoader对象 ，
     * 解析出静态注册的广播
     *
     * @param context
     */
    public void loadPath(Context context) {
        // 插件存储的位置
        File filesDir = context.getDir("plugin", Context.MODE_PRIVATE);
        String name = "pluginb.apk";
        String path = new File(filesDir, name).getAbsolutePath();

        // 从插件中，取出所有的activity类，
        PackageManager packageManager = context.getPackageManager();
        packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);

        // 初始化ClassLoader
        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);
        dexClassLoader = new DexClassLoader(path, dexOutFile.getAbsolutePath(), null, context.getClassLoader());

        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, path);

            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(),
                    context.getResources().getConfiguration());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 解析插件静态注册的广播
        parseReceivers(context, path);

    }

    /**
     * 解析插件静态注册的广播
     *
     * @param context
     * @param path
     */
    private void parseReceivers(Context context, String path) {
        try {

            // 1.通过反射，拿到解析包信息的解析类
            Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
            Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
            Object packageParser = packageParserClass.newInstance();
            Object packageObj = parsePackageMethod.invoke(packageParser, new File(path),
                    PackageManager.GET_ACTIVITIES);

            // 2.拿到PackageParser类的receivers成员变量,并拿到静态注册的广播集合
            Field receiverField = packageObj.getClass().getDeclaredField("receivers");
            //拿到receivers  广播集合    app存在多个广播   集合  List<Activity>  name  ————》 ActivityInfo   className
            List receivers = (List) receiverField.get(packageObj);

            // 3.拿到存储Intent信息的字段
            Class<?> componentClass = Class.forName("android.content.pm.PackageParser$Component");
            Field intentsField = componentClass.getDeclaredField("intents");

            // 调用generateActivityInfo 方法, 把PackageParser.Activity 转换成
            Class<?> packageParser$ActivityClass = Class.forName("android.content.pm.PackageParser$Activity");
            // generateActivityInfo方法
            Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");

            Object defaultUserState = packageUserStateClass.newInstance();
            Method generateActivityInfoMethod = packageParserClass.getDeclaredMethod("generateActivityInfo", packageParser$ActivityClass,
                    int.class, packageUserStateClass, int.class);

            // 获取用户id
            Class<?> userHandler = Class.forName("android.os.UserHandle");
            Method getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId");
            int userId = (int) getCallingUserIdMethod.invoke(null);

            for (Object activity : receivers) {
                ActivityInfo info = (ActivityInfo) generateActivityInfoMethod.invoke(packageParser, activity, 0, defaultUserState, userId);
                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) dexClassLoader.loadClass(info.name).newInstance();
                List<? extends IntentFilter> intents = (List<? extends IntentFilter>) intentsField.get(activity);
                for (IntentFilter intentFilter : intents) {
                    context.registerReceiver(broadcastReceiver, intentFilter);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
