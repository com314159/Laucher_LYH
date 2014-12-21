package com.lyh.laucher_lyh.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lyh.laucher_lyh.app.appLoader.LocalAppActionController;

public class LocalAppActionReceiver extends BroadcastReceiver {
	private static final String TAG = "LocalAppActionReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(TAG, " onReceive ");
		// 卸载应用
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) { 
            String packageName = intent.getDataString();
            Log.i(TAG, "package remove " + packageName);
            LocalAppActionController.getInstance().onDeleteApp(packageName); 
        }
        // 安装应用
        else if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) { 
            String packageName = intent.getDataString();
            Log.i(TAG, "package add " + packageName);
            LocalAppActionController.getInstance().onAddApp(packageName); 
        }
        
	}
	
}
