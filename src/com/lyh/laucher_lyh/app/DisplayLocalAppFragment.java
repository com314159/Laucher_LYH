package com.lyh.laucher_lyh.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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

/**
 * @author ZhiCheng Guo
 * @version 2014�?11�?25�? 上午10:23:50
 */
public class DisplayLocalAppFragment extends Fragment implements LocalAppDisplayListener{
	private static final String TAG = "DisplayLocalAppFragment";
	private List<AppEntity> mLocalApps = null;
	private List<PackageInfo> mLocalPackageInfos;

	private GridView mAppGridView;
	private AppGridItemAdapter mGridItemAdapter;
	private UpdateHandler mHandler = null;

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
		mAppGridView = (GridView) rootView.findViewById(R.id.gridview);
		mGridItemAdapter = new AppGridItemAdapter(getActivity());
		mAppGridView.setAdapter(mGridItemAdapter);
		
		if (mLocalPackageInfos!=null) {
			mGridItemAdapter.setData(mLocalPackageInfos);
		}
		
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		AppUpdateListController.getInstance().removeLocalAppDisplayListener(this);
		super.onDestroyView();
	}

	@Override
	public void onLocalAppsReturn(List<AppEntity> apps, Object tag) {
		if ((null == tag || null == apps || tag.equals(AppUpdateListController.TAG_WAITTING))) {
			return;
		}

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
