package com.lyh.laucher_lyh.app.listener;

public interface LocalAppActionListener {
	
	// 卸载应用事件
	public void onDeleteApp(final String packageName);
	
	// 安装应用事件
	public void onAddApp(final String packageName);
	
}
