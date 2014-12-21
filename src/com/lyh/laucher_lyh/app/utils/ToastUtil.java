package com.lyh.laucher_lyh.app.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	private static Toast mToast;
		
	public static void showToastCancelShowing(Context context,String text,int duration){
		if(mToast == null){
			mToast = Toast.makeText(context, text, duration);
		}else{
			mToast.cancel();
			mToast = Toast.makeText(context, text, duration);
		}
		mToast.show();
	}

}
