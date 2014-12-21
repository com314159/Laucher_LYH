package com.lyh.laucher_lyh;

import android.app.Application;
import cn.jpush.android.api.JPushInterface;

import com.iflytek.cloud.SpeechUtility;
import com.lyh.laucher_lyh.app.appLoader.AppUpdateListController;

public class MainApplication extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		SpeechUtility.createUtility(this, "appid="+ "54904469");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        AppUpdateListController.getInstance().init(this);
	}
}
