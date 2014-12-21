package com.lyh.laucher_lyh;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.lyh.laucher_lyh.app.DisplayLocalAppFragment;
import com.lyh.laucher_lyh.app.utils.AppManager;
import com.lyh.laucher_lyh.utils.KToast;

public class AppActivity extends Activity{
	
	private FragmentManager mFragmentManager;
	private DisplayLocalAppFragment mAppFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_layout);
		
		if (!AppManager.getInstance().isAppInstalled(this, "com.iflytek.speechcloud")){
			KToast.showToastLong(this, "请先安装语音组件");
			AppManager.getInstance().InstallAssetApk(this, "SpeechService.apk");
		}
		
		
		mFragmentManager = getFragmentManager();
		if (mAppFragment == null) {
			mAppFragment = new DisplayLocalAppFragment();
		}
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.add(R.id.fragment, mAppFragment);
		transaction.show(mAppFragment);
		transaction.commit();
	}
	
	
}
