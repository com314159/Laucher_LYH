package com.lyh.laucher_lyh.app.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class AppManager {
	static private AppManager mAppManager = null;
	private AppManager() {
	
	}
	
	static public AppManager  getInstance(){
		if(mAppManager==null){
			synchronized (AppManager.class) {
				if(mAppManager==null){
					mAppManager = new AppManager();
				}
			}
		}
		return mAppManager;
	}
	
	public boolean startAppByAppNameORPkg(Context context, String appName,String packageName){
		if(packageName!=null){
			if(startAppByPackageName(context, packageName))
			{
				return true;
			}
		}
		
		if(appName != null){
			if(startAppByAppName(context, appName))
				return true;
		}
			
			return false;
	}
	
	public boolean isAppInstalled(Context context, String packageName){
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
//		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageName);
		
		List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
		ResolveInfo ri = null;
		if(apps!=null&&apps.iterator().hasNext())
			ri = apps.iterator().next();
		if (ri != null ) {
			return true;
		}
		return false;
	}
	
	public boolean startAppByPackageName(Context context, String packageName){
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
//		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageName);
		
		List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
		ResolveInfo ri = null;
		if(apps!=null&&apps.iterator().hasNext())
			ri = apps.iterator().next();
		if (ri != null ) {
			String packageName1 = ri.activityInfo.packageName;
			String className = ri.activityInfo.name;
			 
			Intent intent = new Intent(Intent.ACTION_MAIN);
//			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			 
			ComponentName cn = new ComponentName(packageName1, className);
			 
			intent.setComponent(cn);
			context.startActivity(intent);
			return true;
		}
		return false;
	}
	
	public boolean startAppByAppName(Context context, String appName){
        PackageManager pm = context.getApplicationContext().getPackageManager();


        List<PackageInfo> allApps = pm.getInstalledPackages(0); // 获取本地所有已经安装的应用


        Intent intent = null;
        if (null != allApps && null != appName) {
                for (PackageInfo pi : allApps) {
                        // 在本地已经安装应用中比较应用名称
                        if (appName.equals(pi.applicationInfo.loadLabel(pm))) {
                                intent = getOpenAppIntent((Activity) context, pi.packageName);
                                break;
                        }
                }
        }
        if(intent!=null){
        	context.startActivity(intent);
        	return true;
        }
        	return false;
	}
	
	private Intent getOpenAppIntent(Context context, String packageName) {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		for (ResolveInfo info : context.getPackageManager().queryIntentActivities(mainIntent, 0)) {
			if (packageName.equals(info.activityInfo.packageName)) {
				ComponentName component = new ComponentName(packageName, info.activityInfo.name);
				Intent intent = new Intent();
				intent.setComponent(component);
				return intent;
			}
		}
		return null;
	}
	
	public static PackageInfo getThisPackageInfo(Context context) {

		String packageName = context.getPackageName();
		return getPackageInfo(context, packageName);

	}

	public static PackageInfo getPackageInfo(Context context,
			String packageName) {
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo pInfo = packageManager.getPackageInfo(packageName,
					PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
			if (pInfo != null) {
				return pInfo;
			}
		} catch (NameNotFoundException e) {
			Log.w("gzc", e.getMessage());
		}
		return null;
	}
	
	public static void InstallAssetApk(Context context, String apkName) {
		AssetManager assetManager = context.getAssets();

		InputStream in = null;
		OutputStream out = null;

		File OutDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		
		String OutPath = OutDir.toString();

		try {
			if (!OutDir.exists()) {
				OutDir.mkdirs();
			}
			in = assetManager.open(apkName);
			
			String filePath = OutPath + File.separator + apkName;
			
			out = new FileOutputStream(filePath);

			byte[] buffer = new byte[1024];

			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;

			out.flush();
			out.close();
			out = null;

			
			File apk = new File(filePath);
			
			Intent intent = new Intent(Intent.ACTION_VIEW);

			intent.setDataAndType(Uri.fromFile(apk),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
			
			apk.deleteOnExit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isSystemApp(Context context, String pname) {
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(pname, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}

	public boolean isSystemUpdateApp(Context context, String pname) {
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(pname, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}
}
