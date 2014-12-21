package com.lyh.laucher_lyh.app.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.lyh.laucher_lyh.R;

public class SystemUtil {
	
	private static final String TAG = "SystemUtil";
	
     
	public static int getVersionCode(Context context) {
	    int v = -1;
	    if(context == null){
	    	return v;
	    }
	    try {
	        v = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
	    } catch (NameNotFoundException e) {
	        // Huh? Really?
	    }
	    return v;
	}
    
    // �?查网络状�?
  	public static boolean isNetworkAvailable(Context c, boolean wifiOnly) { 
  		if(c == null) {
  			return false;
  		}
  		
  	    Context context = c.getApplicationContext();  
  	    ConnectivityManager connectivity = (ConnectivityManager)context
  	    		.getSystemService(Context.CONNECTIVITY_SERVICE);  
  	    
  	    if (connectivity != null) {
  	    	
  	        NetworkInfo[] info = connectivity.getAllNetworkInfo();  
  	        
  	        if (info != null) {
  	        	
  	            for (int i = 0; i < info.length; i++) {  
  	                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
  	                	if(!wifiOnly) {
  	                		info = null;
  		                    return true;
  		                    
  	                	} else if (info[i].getType() == ConnectivityManager
  	                			.TYPE_WIFI) {
  	                		info = null;
  	                		return true;
  	                	}
  	                }  
  	            }
  	            
  	        }   
  	    }
  	    return false;  
  	}
  	
  	// 判断是否为系统应�?
  	public static boolean isSystemApp(PackageInfo pInfo) {  
        return ((pInfo.applicationInfo.flags 
        		& ApplicationInfo.FLAG_SYSTEM) != 0);  
    }  
  	
  	// 判断是否为系统升级应�?
    public static boolean isSystemUpdateApp(PackageInfo pInfo) {  
        return ((pInfo.applicationInfo.flags 
        		& ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);  
    } 
    
    
  	// 获取本地已安装的应用信息
  	public static List<PackageInfo> getLocalPackageInfos(Context context, 
  			List<String> filters, boolean filterSystemApps) {
  		
  		// 获取�?有的应用信息
  		List<PackageInfo> allPackageInfos = context.getPackageManager()
  				.getInstalledPackages(0);
  		
  		// 返回的结�?
  		List<PackageInfo> packageInfos = new ArrayList<PackageInfo>();
  			
		for (int i = 0; i < allPackageInfos.size(); i++) {
			PackageInfo tmp = allPackageInfos.get(i);
			// 判断是否为系统应�?
			if (filterSystemApps && (isSystemUpdateApp(tmp) 
					|| isSystemApp(tmp))) {
				continue;
			}
			
			//用包名参数过�?
			boolean flag = true;
			for(int j = 0; null != filters && j < filters.size(); j++) {
				if(tmp.packageName.equals(filters.get(j))) {
					flag = false;
					break;
				}
			}
			if(flag) {
				packageInfos.add(tmp);
			}
		}
		
		allPackageInfos.clear();
		allPackageInfos = null;
		
		return packageInfos;
  	}
  	
  	// 判断是否已安装包名为packageName的应�?
  	public static boolean checkIfInstall(Context context, String packageName) { 
  		if(null == context || null == packageName) {
  			return false;
  		}
  		
  	    PackageManager packageManager = context.getPackageManager();  
  	    try {  
  	        PackageInfo pInfo = packageManager.getPackageInfo(packageName,  
  	                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT); 
  	        if(pInfo != null){  
  	            return true;
  	        }  
  	    } catch (NameNotFoundException e) {  
  	        return false;
  	    }  
  	    return false;
  	} 
  	
  	// 打开应用
  	public static boolean runApp(Context context, String packageName) {
  		if(null == context || null == packageName) {
  			return false;
  		}
  		
  		try {
			Intent intent = context.getPackageManager()
					.getLaunchIntentForPackage(packageName);
			if(null != intent) {
				context.startActivity(intent);
				return true;
			}
		} catch (Exception e) {
			Toast.makeText(context, context.getResources()
					.getString(R.string.error_app_run), 
					Toast.LENGTH_SHORT).show();
		}
  		return false;
  	}
  	
  }
