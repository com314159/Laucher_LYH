package com.lyh.laucher_lyh.app;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.lyh.laucher_lyh.AppActivity;
import com.lyh.laucher_lyh.R;
import com.lyh.laucher_lyh.app.utils.AppIconCache;
import com.lyh.laucher_lyh.app.utils.AppIconCache.CacheEntry;
import com.lyh.laucher_lyh.app.utils.PackageUtil;
import com.lyh.laucher_lyh.app.utils.SystemUtil;
import com.lyh.laucher_lyh.app.utils.ToastUtil;
import com.lyh.laucher_lyh.json.JsonParser;
import com.lyh.laucher_lyh.utils.KToast;
import com.lyh.laucher_lyh.view.ConfirmDialog;
import com.lyh.laucher_lyh.view.ConfirmDialog.OnDialogButtonClickListener;


public class AppGridItemAdapter extends BaseAdapter{
	@SuppressWarnings("unused")
	private static final String TAG = AppGridItemAdapter.class.getSimpleName();

	private Context mContext;
	private List<PackageInfo> mDatas;
	private AppIconCache mAppIconCache = null;
	private RecognizerDialog mIatDialog;
	private SpeechRecognizer mIat;
	private boolean mIsFirstListener;
	
	//语音智能搜索
	public static final String Search_PackageName = "a";
	//更换壁纸
	public static final String Change_Background_PackageName = "b";
	//天气
	public static final String Wheather_PackageName = "c";
	
	public AppGridItemAdapter(Context context) {
		mContext = context;
		mAppIconCache = new AppIconCache(mContext, true);
		mIat = SpeechRecognizer.createRecognizer(context,null);
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");

	    mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
	    
		mIatDialog = new RecognizerDialog(context,mInitListener);
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
		
		if (packageInfo.packageName.equals(Search_PackageName)) {
			viewHolder.mImageView.setImageResource(R.drawable.icon_search);
			viewHolder.mTextView.setText("语音智能搜索");
			viewHolder.mImageView.setOnLongClickListener(null);
			viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					mIsFirstListener = true;
					KToast.showToastLong(mContext, "请说出你想要打开的应用");
					startListenToUser();
				}
			});
			return convertView;
		} else if (packageInfo.packageName.equals(Wheather_PackageName)) {
			viewHolder.mImageView.setImageResource(R.drawable.weather);
			viewHolder.mTextView.setText("天气");
			viewHolder.mImageView.setOnLongClickListener(null);
			viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					KToast.showToastLong(mContext, "天气预报在此");
				}
			});
			return convertView;
		} else if (packageInfo.packageName.equals(Change_Background_PackageName)) {
			viewHolder.mImageView.setImageResource(R.drawable.change_background);
			viewHolder.mTextView.setText("更换壁纸");
			viewHolder.mImageView.setOnLongClickListener(null);
			viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					((Activity)mContext).startActivityForResult(intent, AppActivity.REQUEST_CODE_SELECT_FROM_GALERY);
				}
			});
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
	
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		KToast.showToastLong(mContext, "初始化失败");
        	}
		}
	};
	
	private PackageInfo findApp(String word) {
		for (int i=0;i<mDatas.size(); ++i) {
			PackageInfo info = mDatas.get(i);
			if (info.packageName.equals(mContext.getPackageName())){
				continue;
			}
			CacheEntry entity = mAppIconCache.getAppIconAndLabel(info);
			if (entity.label.contains(word)) {
				return info;
			}
		}
		return null;
	}
	
	/**
	 *
	 */
	private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
		public void onResult(RecognizerResult results, boolean isLast) {
			if (mIsFirstListener == false) {
				return;
			}
			
			String text = JsonParser.parseIatResult(results.getResultString());
			KToast.showCenter(mContext,"识别语音为" + text, Toast.LENGTH_LONG);
			
			final PackageInfo info = findApp(text);
			
			if (info == null) {
				KToast.showCenter(mContext,"没有找到 应用" + text , Toast.LENGTH_LONG);	
			} else {
				final CacheEntry entity = mAppIconCache.getAppIconAndLabel(info);
				ConfirmDialog dialog = new ConfirmDialog(mContext, "是否打开", entity.label);
				dialog.setOnConfirmButtonClickListener(new OnDialogButtonClickListener() {
					
					@Override
					public void onClick(Dialog dialog, View v) {
						if(!SystemUtil.runApp(mContext,info.packageName)){
							ToastUtil.showToastCancelShowing(mContext, 
									mContext.getString(R.string.open_app_failed), 
									Toast.LENGTH_LONG);
						}						
					}
				});
				dialog.show();
			}
			
			mIsFirstListener = false;
		}


		public void onError(SpeechError error) {
			
		}

	};

	private void startListenToUser() {
		mIatDialog.setListener(recognizerDialogListener);
		mIatDialog.show();
	}
	

	/**
	 *
	 * @param param
	 * @return 
	 */
	public void setParam(){
		mIat.setParameter(SpeechConstant.PARAMS, null);
	    mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
	    mIat.setParameter(SpeechConstant.ACCENT,"mandarin");
	
	    mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/LYH/wavaudio.pcm");
	}

	
	private class ViewHolder {
		public TextView mTextView;
		public ImageView mImageView;
	}
}
