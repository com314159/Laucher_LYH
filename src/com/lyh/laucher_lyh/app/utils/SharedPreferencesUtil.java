package com.lyh.laucher_lyh.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {

	private static final String NAME_SHAREDPREFERENCES = "keyshare_SharedPreferencesUtil";

	private SharedPreferencesUtil() {

	}

	public static boolean getBoolean(Context context, String name,
			boolean defaultValue) {

		if (null == context || null == name) {
			return defaultValue;
		}

		SharedPreferences sp = context.getSharedPreferences(
				NAME_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		if (null == sp) {
			return defaultValue;
		}

		return sp.getBoolean(name, defaultValue);
	}

	public static void saveBoolean(Context context, String name, boolean value) {

		SharedPreferences sp = context.getSharedPreferences(
				NAME_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		if (null == sp) {
			return;
		}

		Editor edit = sp.edit();
		if (null == edit) {
			return;
		}

		edit.putBoolean(name, value);
		edit.commit();
	}

	public static String getString(Context context, String name,
			String defaultValue) {

		if (null == context || null == name) {
			return defaultValue;
		}

		SharedPreferences sp = context.getSharedPreferences(
				NAME_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		if (null == sp) {
			return defaultValue;
		}

		return sp.getString(name, defaultValue);
	}

	public static void saveString(Context context, String name, String value) {

		SharedPreferences sp = context.getSharedPreferences(
				NAME_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		if (null == sp) {
			return;
		}

		Editor edit = sp.edit();
		if (null == edit) {
			return;
		}

		edit.putString(name, value);
		edit.commit();
	}

	public static int getInt(Context context, String name, int defaultValue) {

		if (null == context || null == name) {
			return defaultValue;
		}

		SharedPreferences sp = context.getSharedPreferences(
				NAME_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		if (null == sp) {
			return defaultValue;
		}

		return sp.getInt(name, defaultValue);
	}

	public static void saveInt(Context context, String name, int value) {

		SharedPreferences sp = context.getSharedPreferences(
				NAME_SHAREDPREFERENCES, Context.MODE_PRIVATE);
		if (null == sp) {
			return;
		}

		Editor edit = sp.edit();
		if (null == edit) {
			return;
		}

		edit.putInt(name, value);
		edit.commit();
	}


	public static int getInt(Context context, String sharedPreferenceName,String name, int defaultValue) {

		if (null == context || null == name) {
			return defaultValue;
		}

		SharedPreferences sp = context.getSharedPreferences(
				sharedPreferenceName, Context.MODE_PRIVATE);
		if (null == sp) {
			return defaultValue;
		}

		return sp.getInt(name, defaultValue);
	}

	public static void saveInt(Context context, String sharedPreferenceName, String name, int value) {

		SharedPreferences sp = context.getSharedPreferences(
				sharedPreferenceName, Context.MODE_PRIVATE);
		if (null == sp) {
			return;
		}

		Editor edit = sp.edit();
		if (null == edit) {
			return;
		}

		edit.putInt(name, value);
		edit.commit();
	}

	
}
