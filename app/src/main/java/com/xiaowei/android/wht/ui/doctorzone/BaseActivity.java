package com.xiaowei.android.wht.ui.doctorzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * @Description: 基类
 */
public abstract class BaseActivity extends FragmentActivity {

    private final String TAG = "BaseActivity";
    public Activity activity;
    public BaseActivity baseActivity;

    public static ArrayList<BackPressHandler> mListeners = new ArrayList<BackPressHandler>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        activity = this;
        baseActivity = BaseActivity.this;
        init(savedInstanceState);
        setListener();
        setAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // if (mListeners.size() > 0)
        // for (BackPressHandler handler : mListeners) {
        // handler.activityOnResume();
        // }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if (mListeners.size() > 0)
        // for (BackPressHandler handler : mListeners) {
        // handler.activityOnPause();
        // }
    }

    protected abstract void setContentView();

    public abstract void init(Bundle savedInstanceState);

    public abstract void setListener();

    public void setAdapter() {

    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivityWithBundle(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityWithBundle(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 有回调的Activity跳转
     *
     * @param cls
     * @param requestCode
     */
    protected void startActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityBundleForResult(Class<?> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivityForResult(intent, requestCode, bundle);
    }


    // ==================================================================================================


    public interface BackPressHandler {

        void activityOnResume();

        void activityOnPause();

    }
}
