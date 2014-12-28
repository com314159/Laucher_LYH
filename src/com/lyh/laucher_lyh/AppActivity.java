package com.lyh.laucher_lyh;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.lyh.laucher_lyh.app.DisplayLocalAppFragment;
import com.lyh.laucher_lyh.app.utils.AppManager;
import com.lyh.laucher_lyh.utils.KToast;

public class AppActivity extends Activity {

	private FragmentManager mFragmentManager;
	private DisplayLocalAppFragment mAppFragment;

	public static final int REQUEST_CODE_SELECT_FROM_GALERY = 1;
	
	public static final String SP_NAME = "sp_name";
	public static final String KEY_PATH = "path";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_layout);

		if (!AppManager.getInstance().isAppInstalled(this,
				"com.iflytek.speechcloud")) {
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
		
		if (getBackgroundPath()!=null) {
			mAppFragment.setBackground(getBackgroundPath());
		}
	}

	//
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_SELECT_FROM_GALERY && data != null) {
			Uri uri = data.getData();

			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(uri, filePathColumn,
					null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			
			SharedPreferences sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
			Editor et = sp.edit();
			et.putString(KEY_PATH, filePath);
			et.commit();
			
			if (mAppFragment != null) {
				mAppFragment.setBackground(filePath);
			}
		}
	}
	
	private String getBackgroundPath(){
		SharedPreferences sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		return sp.getString(KEY_PATH, null);
	}

}
