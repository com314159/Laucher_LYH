package com.lyh.laucher_lyh.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.lyh.laucher_lyh.R;
import com.lyh.laucher_lyh.app.appLoader.AppUpdateListController;
import com.lyh.laucher_lyh.app.entity.AppEntity;
import com.lyh.laucher_lyh.app.listener.LocalAppDisplayListener;

public class DisplayLocalAppFragment extends Fragment implements LocalAppDisplayListener{
	private static final String TAG = "DisplayLocalAppFragment";
	private List<AppEntity> mLocalApps = null;
	private List<PackageInfo> mLocalPackageInfos;

	private GridView mAppGridView;
	private AppGridItemAdapter mGridItemAdapter;
	private UpdateHandler mHandler = null;
	private ViewGroup mRootViewGroup;
	private String mFilePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mHandler = new UpdateHandler(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		AppUpdateListController.getInstance().addLocalAppDisplayListener(this);

		View rootView = inflater.inflate(R.layout.local_app_layout, container, false);
		
		mRootViewGroup = (ViewGroup) rootView.findViewById(R.id.linearLayout);
		setBackground();
		mAppGridView = (GridView) rootView.findViewById(R.id.gridview);
		mGridItemAdapter = new AppGridItemAdapter(getActivity());
		mAppGridView.setAdapter(mGridItemAdapter);
		
		if (mLocalPackageInfos!=null) {
			mGridItemAdapter.setData(mLocalPackageInfos);
		}
		
		return rootView;
	}
	
	public void setBackground(String filePath){
		mFilePath = filePath;
		if (mRootViewGroup != null) {
			setBackground();
		}
	}
	
	private void setBackground() {
		Bitmap bmp= BitmapFactory.decodeFile(mFilePath);
		Drawable drawable = new BitmapDrawable(bmp);  
		mRootViewGroup.setBackgroundDrawable(drawable);
	}
	
	@Override
	public void onDestroyView() {
		AppUpdateListController.getInstance().removeLocalAppDisplayListener(this);
		super.onDestroyView();
	}

	@Override
	public void onLocalAppsReturn(List<AppEntity> apps, Object tag) {
		
		Log.i(TAG, " on Local appsReturn");
		
		if ((null == tag || null == apps || tag.equals(AppUpdateListController.TAG_WAITTING))) {
			return;
		}
		
		Log.i(TAG, " appsReturn size is " + apps.size());

		mLocalApps = apps;
		mLocalPackageInfos = toPackageInfo(apps);
		mHandler.obtainMessage().sendToTarget();
		Log.i(TAG, "have get apps " + mLocalApps.toString());
	}

	@Override
	public void onAppClassifySelectedReturn(String classify, Object tag) {
	}

	@Override
	public void onAppAllClassifiesReturn(List<String> classifies, Object tag) {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	

	private List<PackageInfo> toPackageInfo(List<AppEntity> appList) {
		List<PackageInfo> all = new ArrayList<PackageInfo>();
		
		PackageInfo wheather = new PackageInfo();
		wheather.packageName = AppGridItemAdapter.Wheather_PackageName;
		all.add(0,wheather);	

		PackageInfo changeBackground = new PackageInfo();
		changeBackground.packageName = AppGridItemAdapter.Change_Background_PackageName;
		all.add(0,changeBackground);
		
		PackageInfo searchInfo = new PackageInfo();
		searchInfo.packageName = AppGridItemAdapter.Search_PackageName;
		all.add(0,searchInfo);
		



		// 获取PackageInfo
		PackageManager pm = getActivity().getPackageManager();
		for (AppEntity app : appList) {
			try {
				PackageInfo pi = pm.getPackageInfo(app.getPackageName(),
						PackageManager.GET_ACTIVITIES);
				if (null == pi) {
					continue;
				}

				// 加入全部列表
				all.add(pi);

			} catch (NameNotFoundException e) {
			}
		}

		return all;
	}
	private static class UpdateHandler extends Handler {

		private WeakReference<DisplayLocalAppFragment> mThis = null;

		public UpdateHandler(DisplayLocalAppFragment activity) {
			mThis = new WeakReference<DisplayLocalAppFragment>(activity);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			DisplayLocalAppFragment activity = mThis.get();
			if (null == activity || activity.mGridItemAdapter == null) {
				return;
			}
			
			activity.mGridItemAdapter.setData(activity.mLocalPackageInfos);
		}
	}

}
