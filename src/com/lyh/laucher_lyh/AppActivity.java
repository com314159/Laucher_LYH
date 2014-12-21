package com.lyh.laucher_lyh;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.lyh.laucher_lyh.app.DisplayLocalAppFragment;

public class AppActivity extends Activity{
	
	private FragmentManager mFragmentManager;
	private DisplayLocalAppFragment mAppFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_layout);
		mFragmentManager = getFragmentManager();
		mAppFragment = (DisplayLocalAppFragment) mFragmentManager.findFragmentById(R.id.fragment);
		if (mAppFragment == null) {
			mAppFragment = new DisplayLocalAppFragment();
		}
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.add(R.id.fragment, mAppFragment);
		transaction.show(mAppFragment);
		transaction.commit();
	}
}
