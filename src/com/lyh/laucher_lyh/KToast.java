package com.lyh.laucher_lyh;

import android.content.Context;
import android.widget.Toast;

public class KToast {

	private static Toast mToast;
		
	public static void showCenter(Context context,String text,int duration){
		if (mToast == null) {
			mToast = Toast.makeText(context, text, duration);
		} else {
			mToast.cancel();
			mToast = Toast.makeText(context, text, duration);
		}
		mToast.show();
	}
	
	public static void showCenter(Context context,int textResId,int duration){
		if(context == null) {
			return ;
		}
		showCenter(context, context.getString(textResId), duration);
	}
	
	public static void showToastLong(Context context,String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		} else {
			mToast.cancel();
			mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		}
		mToast.show();
	}
	
	public static void showToastLong(Context context,int textResId) {
		if(context == null) {
			return ;
		}
		showToastLong(context, context.getString(textResId));
	}
	
}
