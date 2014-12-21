package com.lyh.laucher_lyh.app.appLoader;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.lyh.laucher_lyh.app.entity.AppEntity;
import com.lyh.laucher_lyh.app.listener.LocalAppActionListener;
import com.lyh.laucher_lyh.app.listener.LocalAppDisplayListener;
import com.lyh.laucher_lyh.app.utils.SystemUtil;

public class AppUpdateListController implements LocalAppActionListener {

	private static final String TAG = "AppUpdateListController";

	private static final String TAG_INIT = "init";
	private boolean mInit = false;

	// 监听者回调标志
	public static final String TAG_WAITTING = "waitting";
	public static final String TAG_FINE = "fine";

	private Context mApplication = null;
	private static AppUpdateListController mInstance = null;

	private List<AppEntity> mLocalAppList = null;
	private List<LocalAppDisplayListener> mDisplayListenerList = null;

	private AppUpdateListController() {
	}

	// 获取单实例
	public static AppUpdateListController getInstance() {
		if (null == mInstance) {
			synchronized (AppUpdateListController.class) {
				if (mInstance == null) {
					mInstance = new AppUpdateListController();
				}
			}
		}
		return mInstance;
	}

	// 初始化
	public void init(Context application) {
		if (null == application) {
			return;
		}
		if (null != mLocalAppList) {
			mLocalAppList.clear();
		} else {
			mLocalAppList = new LinkedList<AppEntity>();
		}

		mApplication = application;
		LocalAppActionController.getInstance().addListener(this);

		getLocalPackageInfos();
	}

	// 获取本地已安装应用包名列表
	public void getLocalPackageInfos() {

		if (null == mApplication
				|| (mInit && null != mLocalAppList && mLocalAppList.size() != 0)) {
			return;
		}

		// 重置应用列表
		reset();

		// 获取本地应用列表
		List<PackageInfo> localAppList = SystemUtil.getLocalPackageInfos(
				mApplication, null, true);

		// 过滤本地应用列表
		if (null == localAppList) {
			return;
		}
		
		mInit = false;
		for (PackageInfo pi : localAppList) {
			AppEntity app = new AppEntity();
			app.setPackageName(pi.packageName);
			app.setVersionCode(pi.versionCode);
			app.setVersionName(pi.versionName);
			mLocalAppList.add(app);
		}
		// 通知本地应用监听者
		notifyLocalAppList();
	}

	// 初始化本地应用列表
	private void initLocalAppList(final List<AppEntity> apps) {
		if (null == apps) {
			return;
		}

		// 设置本地安装应用列表初始化标志
		mInit = true;
		if (null == mLocalAppList) {
			mLocalAppList = apps;

		} else {
			List<AppEntity> tmpApps = new LinkedList<AppEntity>();
			for (AppEntity app : apps) {
				AppEntity tmpApp = new AppEntity();
				tmpApp.setId(app.getId());
				tmpApp.setPackageName(app.getPackageName());
				tmpApp.setClassify(app.getClassify());
				tmpApp.setVersionCode(app.getVersionCode());

				// 获取本地应用的版本号
				for (AppEntity app1 : mLocalAppList) {
					if (app1.getPackageName().equals(app.getPackageName())) {
						tmpApp.setVersionCode(app1.getVersionCode());
						break;
					}
				}

				tmpApps.add(tmpApp);
			}

			// 更新本地安装应用列表
			mLocalAppList = tmpApps;
		}
	}

	// 响应应用卸载
	@Override
	public void onDeleteApp(String packageName) {
		if (null == packageName) {
			return;
		}

		int i;

		if (null != mLocalAppList) {
			// 判断卸载的应用是否在本地已安装应用队列
			for (i = 0; i < mLocalAppList.size(); i++) {
				if (packageName.equals(mLocalAppList.get(i).getPackageName())) {
					break;
				}
			}

			if (i < mLocalAppList.size()) {
				mLocalAppList.remove(i);
				notifyLocalAppList();
			}
		}
	}

	// 响应应用安装
	@Override
	public void onAddApp(String packageName) {

		Log.i(TAG, " onAddApp package Name is " + packageName);

		if (null == packageName) {
			return;
		}

		for (AppEntity app : mLocalAppList) {
			if (null != app.getPackageName()
					&& app.getPackageName().equals(packageName)) {
				return;
			}
		}
		//重新加载应用
		getLocalPackageInfos();
		notifyLocalAppList();
	}

	// 检查安装
	public boolean checkInstalled(String packageName) {
		if (null == mLocalAppList) {
			return false;
		}

		for (AppEntity app : mLocalAppList) {
			if (app.getPackageName().equals(packageName)) {
				return true;
			}
		}

		return false;
	}
	// 清空数据
	public void reset() {
		mInit = false;

		if (null != mLocalAppList) {
			mLocalAppList.clear();
			notifyLocalAppList();
		}
	}
	// 通知本地已安装应用列表
	public void notifyLocalAppList() {

		if (!mInit || null == mDisplayListenerList) {
			return;
		}

		if (null != mLocalAppList) {
			AppEntityComparator comparator = new AppEntityComparator();
			Collections.sort(mLocalAppList, comparator);
			comparator = null;
		}

		// 通知显示本地应用列表的监听者
		for (LocalAppDisplayListener l : mDisplayListenerList) {
			l.onLocalAppsReturn(mLocalAppList,
					(null != mLocalAppList) ? TAG_FINE : TAG_WAITTING);
		}
	}

	// 添加显示本地应用列表的监听者
	public void addLocalAppDisplayListener(LocalAppDisplayListener listener) {
		if (null == listener) {
			return;
		}

		if (null == mDisplayListenerList) {
			mDisplayListenerList = new LinkedList<LocalAppDisplayListener>();
		}

		mDisplayListenerList.add(listener);

		if (mInit) {
			listener.onLocalAppsReturn(mLocalAppList,
					(null != mLocalAppList) ? TAG_FINE : TAG_WAITTING);
		}
	}

	// 移除显示本地应用列表的监听者
	public void removeLocalAppDisplayListener(LocalAppDisplayListener listener) {
		if (null == listener || null == mDisplayListenerList) {
			return;
		}
		mDisplayListenerList.remove(listener);
	}

	// app 按包名排序
	public static class AppEntityComparator implements Comparator<AppEntity> {
		public int compare(AppEntity arg0, AppEntity arg1) {
			if (null == arg0 || null == arg1) {
				return 1;
			}

			if (null != arg0.getName() && null != arg1.getName()
					&& !arg0.getName().equals(arg1.getName())) {
				return arg0.getName().compareTo(arg1.getName());
			}

			if (null != arg0.getPackageName() && null != arg1.getPackageName()) {
				return arg0.getPackageName().compareTo(arg1.getPackageName());
			}

			return 1;
		}
	}
}
