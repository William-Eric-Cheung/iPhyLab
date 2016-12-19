package com.wec_tech.iphylab;

import android.app.Application;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

/**
 * Created by William Eric Cheung on 11/27/2016.
 */

public class FeedbackApplication extends Application {
    public final static String DEFAULT_APPKEY = "23547982";
    @Override
    public void onCreate() {
        super.onCreate();
        //建议放在此处做初始化
        FeedbackAPI.init(this, DEFAULT_APPKEY);
    }
}
