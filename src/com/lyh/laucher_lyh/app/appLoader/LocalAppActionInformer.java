package com.lyh.laucher_lyh.app.appLoader;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.lyh.laucher_lyh.app.utils.SystemUtil;

@SuppressLint("UseValueOf")
public class LocalAppActionInformer {
	
	private static final String TAG = "LocalAppActionInformer";
	
	private static final String CHECK_TYPE = "type";
	private static final String PACKAGE_NAME = "package_name";
	
	public static final int CHECK_INSTALL = 0;
	public static final int CHECK_UNINSTALL = 1;
	
	private static final int TIME_CHECK_MAX = 3 * 60 * 1000;		// 每个应用检查的最长时间
	private static final int TIME_GAP_CHECK = 100;					// 检查的时间间隔
	
	private static LocalAppActionInformer mInstance = null;
	
	private Context mContext = null;
	private Thread mCheckThread = null;
	
	private InformHandler mHandler = null;
	private static final int MSG_INFORM = 0;
	
	private static Boolean mInstallLock = new Boolean(true);		// 用于监听安装包名队列的同步操作
	private static List<CheckPackageName> mInstallCheckPackageNames = null;		// 监听安装的包名队列
	
	private static List<CheckPackageName> mUnInstallCheckPackageNames = null;	// 监听卸载的包名队列
	private static Boolean mUnInstallLock = new Boolean(true);		// 用于监听卸载包名队列的同步操作
	
	public static LocalAppActionInformer getInstance(){
		if(null == mInstance) {
			synchronized (LocalAppActionInformer.class) {
				if(mInstance == null){
					mInstance = new LocalAppActionInformer();
				}
			}
		}
		return mInstance;
	}
	
	// 初始化
	public void init(Context context) {
		if(null == context) {
			return ;
		}
		
		mContext = context;
		if(null == mInstallCheckPackageNames) {
			mInstallCheckPackageNames = new LinkedList<CheckPackageName>();
		}
		
		if(null == mUnInstallCheckPackageNames) {
			mUnInstallCheckPackageNames = new LinkedList<CheckPackageName>();
		}
		
		if(null == mHandler) {
			mHandler = new InformHandler(this);
		}
	}
	
	// 添加监听包名
	public void addCheckPackageName(int type, String packageName) {
		
		if(null == packageName) {
			return ;
		}
		
		// 如果是安装监听，先判断是否为重新安装的情况，如果是重新安装则不能直接返回安装成功
		if(CHECK_INSTALL == type && SystemUtil.checkIfInstall(mContext, packageName)) {
			return ;
		}
		
		List<CheckPackageName> checkPackageNames = null;
		Boolean lock = null;
		
		if(CHECK_INSTALL == type) {
			lock = mInstallLock;
			checkPackageNames = mInstallCheckPackageNames;
			
		} else if(CHECK_UNINSTALL == type) {
			lock = mUnInstallLock;
			checkPackageNames = mUnInstallCheckPackageNames;
		}
		
		if(null != checkPackageNames) {
			synchronized (lock) {
				checkPackageNames.add(new CheckPackageName(packageName, TIME_CHECK_MAX));
				if(null == mCheckThread || !mCheckThread.isAlive()) {
					startChecking();
				}
			}
		}
	}
	
	// 开启监听线程
	private void startChecking() {
		if((null != mCheckThread && mCheckThread.isAlive())
				|| null == mInstallCheckPackageNames 
				|| null == mUnInstallCheckPackageNames) {
			return ;
		}
		
		mCheckThread = new Thread(new Runnable(){
			@Override
            public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				
				while (0 != mInstallCheckPackageNames.size() 
						|| 0 != mUnInstallCheckPackageNames.size()) {
					
					try {
						Thread.sleep(TIME_GAP_CHECK);
					} catch (InterruptedException e) {
					}
					
					// 更新监听时间
					refreshTimeLeft(TIME_GAP_CHECK);
					
					check(CHECK_INSTALL);
					check(CHECK_UNINSTALL);
				}
				
            }
		});
		
		mCheckThread.start();
	}
	
	// 检查是否已安装或卸载
	private void check(int type) {
		
		List<CheckPackageName> checkPackageNames = null;
		Boolean lock = null;
		
		if(CHECK_INSTALL == type) {
			lock = mInstallLock;
			checkPackageNames = mInstallCheckPackageNames;
			
		} else if(CHECK_UNINSTALL == type) {
			lock = mUnInstallLock;
			checkPackageNames = mUnInstallCheckPackageNames;
		}
		
		if(null == checkPackageNames) {
			return ;
		}
		
		List<CheckPackageName> tmpPackageNames = new LinkedList<CheckPackageName>();
		
		synchronized (lock) {
			for(CheckPackageName pn : checkPackageNames) {
				tmpPackageNames.add(pn);
			}
		}
		
		for(CheckPackageName pn : tmpPackageNames) {
			
			boolean tmpFlag = false;
			if(CHECK_INSTALL == type) {
				tmpFlag = SystemUtil.checkIfInstall(mContext, pn.mPackageName);
				
			} else if(CHECK_UNINSTALL == type) {
				tmpFlag = !SystemUtil.checkIfInstall(mContext, pn.mPackageName);
			}
			
			if(tmpFlag) {
				synchronized (lock) {
					checkPackageNames.remove(pn);
				}
				
				Bundle bundle = new Bundle();
				bundle.putString(PACKAGE_NAME, pn.mPackageName);
				
				if(CHECK_INSTALL == type) {
					bundle.putInt(CHECK_TYPE, CHECK_INSTALL);
					
				} else if(CHECK_UNINSTALL == type) {
					bundle.putInt(CHECK_TYPE, CHECK_UNINSTALL);
				}
				
				Message msg = new Message();
				msg.what = MSG_INFORM;
				msg.setData(bundle);
				mHandler.sendMessage(msg);
				
			} else if(pn.mTimeLeft <= 0) {
				synchronized (lock) {
					checkPackageNames.remove(pn);
				}
			}
		}
		
		tmpPackageNames.clear();
		tmpPackageNames = null;
	}
	
	// 更新最长检查时间
	private void refreshTimeLeft(int timeMinus) {
		
		// 更新监听卸载的检查时间
		if(null != mUnInstallCheckPackageNames) {
			synchronized (mUnInstallLock) {
				for(CheckPackageName pn : mUnInstallCheckPackageNames) {
					pn.mTimeLeft -= timeMinus;
//					LogUtil.i(TAG, "PackageName : " + pn.mPackageName + ", time left : " + pn.mTimeLeft);
				}
			}
		}
		
		// 更新监听安装的检查时间
		if(null != mInstallCheckPackageNames) {
			synchronized (mInstallLock) {
				for(CheckPackageName pn : mInstallCheckPackageNames) {
					pn.mTimeLeft -= timeMinus;
//					LogUtil.i(TAG, "PackageName : " + pn.mPackageName + ", time left : " + pn.mTimeLeft);
				}
			}
		}
	}
	
	// 监听的包名类
	private static class CheckPackageName {
		public String mPackageName = null;		// 包名
		public int mTimeLeft = 0;		// 生命周期
		
		public CheckPackageName(String packageName, int timeLeft) {
			mPackageName = packageName;
			mTimeLeft = timeLeft;
		}
	}
	
	// 通知监听者
	private void informListener(int type, String packageName) {
		if(null == packageName) {
			return ;
		}
		if(CHECK_INSTALL == type) {
			LocalAppActionController.getInstance().onAddApp(packageName);
			
		} else if(CHECK_UNINSTALL == type) {
			LocalAppActionController.getInstance().onDeleteApp(packageName);
		}
		
	}
	
	// 处理通知消息
	private static class InformHandler extends Handler {
		
		private WeakReference<LocalAppActionInformer> mInformer = null;
		
		public InformHandler(LocalAppActionInformer informer) {
			mInformer = new WeakReference<LocalAppActionInformer>(informer);
		}
		
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			LocalAppActionInformer informer = mInformer.get();
			if(null == informer) {
				return ;
			}
			
			switch (msg.what) {
				case MSG_INFORM:
					Bundle bundle = msg.getData();
					try {
						int type = bundle.getInt(CHECK_TYPE);
						String packageName = bundle.getString(PACKAGE_NAME);
						informer.informListener(type, packageName);
						
					} catch (Exception e) {
					}
					break;
					
				default:
			}
		}
	}
}
