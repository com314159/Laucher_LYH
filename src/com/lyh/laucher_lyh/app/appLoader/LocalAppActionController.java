package com.lyh.laucher_lyh.app.appLoader;

import java.util.LinkedList;
import java.util.List;

import com.lyh.laucher_lyh.app.listener.LocalAppActionListener;
import com.lyh.laucher_lyh.config.Constant;


public class LocalAppActionController implements LocalAppActionListener {

	// 监听者
	private List<LocalAppActionListener> mListenerList = null;
	
	// 单实例
	private static LocalAppActionController mInstance = new LocalAppActionController();
	
	private LocalAppActionController() {
		mListenerList = new LinkedList<LocalAppActionListener>();
	}
	
	public static LocalAppActionController getInstance(){
		if(null == mInstance) {
			synchronized (LocalAppActionController.class) {
				if(mInstance == null){
					mInstance = new LocalAppActionController();
				}
			}
		}
		return mInstance;
	}
	
	public void addListener(LocalAppActionListener listener) {
		if(null != listener) {
			mListenerList.add(listener);
		}
	}
	
	public void removeListener(LocalAppActionListener listener) {
		if(mListenerList.contains(listener)) {
			mListenerList.remove(listener);
		}
	}

	@Override
	public void onDeleteApp(String packageName) {
		
		// 去掉安装监听返回的包名前缀 
		String pn = packageName.replaceAll(Constant.PACKAGE_NAME_PREFIX, "");
		
		for(int i = 0; i < mListenerList.size(); i++) {
			if(null != mListenerList.get(i)) {
				mListenerList.get(i).onDeleteApp(pn);
			}
        }
	}

	@Override
	public void onAddApp(String packageName) {
		
		// 去掉安装监听返回的包名前缀 
		String pn = packageName.replaceAll(Constant.PACKAGE_NAME_PREFIX, "");
		
		for(int i = 0; i < mListenerList.size(); i++) {
			if(null != mListenerList.get(i)) {
				mListenerList.get(i).onAddApp(pn);
			}
        }
	}
}
