package com.lyh.laucher_lyh.app.listener;

import java.util.List;

import com.lyh.laucher_lyh.app.entity.AppEntity;


public interface LocalAppDisplayListener {
	
	// 响应返回的本地已安装应用列表
	public void onLocalAppsReturn(final List<AppEntity> apps, Object tag);
	
	// 响应返回的选择的应用类别
	public void onAppClassifySelectedReturn(final String classify, Object tag);
	
	// 响应返回的所有的应用类别
	public void onAppAllClassifiesReturn(final List<String> classifies, Object tag);
}
