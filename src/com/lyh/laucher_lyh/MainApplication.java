package com.lyh.laucher_lyh;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;

public class MainApplication extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
	}
}
