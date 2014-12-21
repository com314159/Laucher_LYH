package com.lyh.laucher_lyh.app.utils;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.lyh.laucher_lyh.R;


@SuppressLint("NewApi") 
public class AppIconCache {
	
	private static final int INITIAL_ICON_CACHE_CAPACITY = 50;
	
	public static class CacheEntry {
        public Drawable icon;
        public String label;
    }
	
    private final PackageManager mPackageManager;
    private final CacheEntry mDefaultEntity;
    private final HashMap<String, CacheEntry> mCache =
            new HashMap<String, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);
    private boolean mProIconFlag = false;		// 处理图标标志
    private int mIconDpi;
    
    private int mDefaultIconWidth = 0;
    
    public AppIconCache(Context context, boolean proIconFlag) {
    	
    	ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mPackageManager = context.getPackageManager();
        mProIconFlag = proIconFlag;
        
        mIconDpi = activityManager.getLauncherLargeIconDensity();
        
        mDefaultEntity = new CacheEntry();
        mDefaultEntity.icon = getFullResDefaultActivityIcon();
        mDefaultEntity.label = "";
        
        mDefaultIconWidth = (int) context.getResources().getDimension(
				R.dimen.applocal_icon_width);
    }
    
    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(),
                android.R.mipmap.sym_def_app_icon);
    }
    
    @SuppressLint("NewApi")
	public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }
        return (d != null) ? d : getFullResDefaultActivityIcon();
    }
    
    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }
    
    public Drawable getFullResIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }
    
    public Drawable getFullResIcon(ActivityInfo info) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(
                    info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }
    
    /**
     * Remove any records for the supplied ComponentName.
     */
    public void remove(PackageInfo packageInfo) {
    	if(null == packageInfo || null == packageInfo.packageName) {
    		return ;
    	}
    	
        synchronized (mCache) {
            mCache.remove(packageInfo.packageName);
        }
    }
    
    /**
     * Empty out the cache.
     */
    public void flush() {
        synchronized (mCache) {
            mCache.clear();
        }
    }
    
    public CacheEntry getAppIconAndLabel(PackageInfo packageInfo) {
    	if(null == packageInfo) {
    		return mDefaultEntity;
    	}
        synchronized (mCache) {
            return cacheLocked(packageInfo);
        }
    }
    
    @SuppressWarnings("deprecation")
	private CacheEntry cacheLocked(PackageInfo packageInfo) {
    	if(null == packageInfo) {
    		return mDefaultEntity;
    	}
    	
    	CacheEntry entity = mCache.get(packageInfo.packageName);
        if (entity == null) {
			entity = new CacheEntry();
			entity.label = (String) mPackageManager
					.getApplicationLabel(packageInfo.applicationInfo);
			
			int iconId = packageInfo.applicationInfo.icon;
			entity.icon = getFullResIcon(packageInfo.packageName, iconId);
			if(mProIconFlag) {
				Bitmap bitmap = IconViewEffect.getInstance()
						.addBorderToDrawable(entity.icon, mDefaultIconWidth, mDefaultIconWidth);
				entity.icon = new BitmapDrawable(bitmap);
			}
			
			mCache.put(packageInfo.packageName, entity);
        }
        return entity;
    }
}
