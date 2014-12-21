package com.lyh.laucher_lyh.app;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyh.laucher_lyh.R;
import com.lyh.laucher_lyh.app.utils.AppIconCache;
import com.lyh.laucher_lyh.app.utils.AppIconCache.CacheEntry;
import com.lyh.laucher_lyh.app.utils.PackageUtil;
import com.lyh.laucher_lyh.app.utils.SystemUtil;
import com.lyh.laucher_lyh.app.utils.ToastUtil;


/**
 * @author ZhiCheng Guo
 * @version 2014�?11�?25�? 下午3:20:51
 */
public class AppGridItemAdapter extends BaseAdapter{
	@SuppressWarnings("unused")
	private static final String TAG = AppGridItemAdapter.class.getSimpleName();

	private Context mContext;
	private List<PackageInfo> mDatas;
	private AppIconCache mAppIconCache = null;
	public AppGridItemAdapter(Context context) {
		mContext = context;
		mAppIconCache = new AppIconCache(mContext, true);
	}
	
	public void setData(List<PackageInfo> data) {
		mDatas = data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if (mDatas == null) {
			return 0;
		}
		
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.local_app_item, parent, false);
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.imageview);
			viewHolder.mTextView = (TextView)convertView.findViewById(R.id.textview);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final PackageInfo packageInfo = mDatas.get(position);
		
		viewHolder.mImageView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility") @Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()){
					
		        	case MotionEvent.ACTION_DOWN: {
		        		ImageView view = (ImageView) v;
		        		view.getDrawable().setColorFilter(0x77000000, 
		        				PorterDuff.Mode.SRC_ATOP);
		        		v.invalidate();
		        		break;
		        	}
		          
		        	case MotionEvent.ACTION_UP:
		        	case MotionEvent.ACTION_CANCEL: {
		        		ImageView view = (ImageView) v;
		        		view.getDrawable().clearColorFilter();
		        		view.invalidate();
		        		break;
		          	}
		        }
				return false;
			}
		});
		
		if (packageInfo.packageName.equals(mContext.getPackageName())) {
			viewHolder.mImageView.setImageResource(R.drawable.icon_search);
			viewHolder.mTextView.setText("语音智能搜索");
			viewHolder.mImageView.setOnLongClickListener(null);
			viewHolder.mImageView.setOnClickListener(null);
			return convertView;
		}
		
		CacheEntry entity = mAppIconCache.getAppIconAndLabel(packageInfo);
		
		viewHolder.mTextView.setText(entity.label);
		viewHolder.mImageView.setImageDrawable(entity.icon);
		
		viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!SystemUtil.runApp(mContext, packageInfo.packageName)){
					ToastUtil.showToastCancelShowing(mContext, 
							mContext.getString(R.string.open_app_failed), 
							Toast.LENGTH_LONG);
				}
			}
		});
		
		
		viewHolder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				PackageUtil.uninstallNormal(mContext, packageInfo.packageName);
				return false;
			}
		});
		
		
		return convertView;
	}
	
	private class ViewHolder {
		public TextView mTextView;
		public ImageView mImageView;
	}
}
