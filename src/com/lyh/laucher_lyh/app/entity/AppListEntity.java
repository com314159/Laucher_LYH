package com.lyh.laucher_lyh.app.entity;

import java.util.ArrayList;
import java.util.List;

public class AppListEntity {
	
	private List<AppEntity> mItems = null;
	
	public AppListEntity() {
		mItems = new ArrayList<AppEntity>();
	}
	
	public AppListEntity(List<AppEntity> items) {
		mItems = items;
	}
	
	public List<AppEntity> getItems() {
		return mItems;
	}

	public void setItems(List<AppEntity> items) {
		if(null != items) {
			mItems = items;
		}
	}
	
	public void addItem(AppEntity item) {
		if(null == mItems) {
			return ;
		}
		mItems.add(item);
	}
	
	// 转成应用列表条目
	public static List<AppListEntity> toAppListItems(
			List<AppEntity> appItems, int itemsPerline) {
		
		if(null == appItems) {
			return null;
		}
		
		List<AppListEntity> appListItems = new ArrayList<AppListEntity>();
		
		for(int i = 0 ; i < appItems.size(); ) {
			
			// 列表条目
			AppListEntity appListItem = new AppListEntity();
			
			// 获取列表条目中的每个应用
			for(int j = 0; j < itemsPerline && i < appItems.size(); j++, i++) {
				appListItem.addItem(appItems.get(i));
			}
				appListItems.add(appListItem);
		}
		
		return appListItems;
	}
}
