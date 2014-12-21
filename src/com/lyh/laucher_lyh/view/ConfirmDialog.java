package com.lyh.laucher_lyh.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lyh.laucher_lyh.R;

/**
 */
public class ConfirmDialog extends Dialog{
	@SuppressWarnings("unused")
	private static final String TAG = ConfirmDialog.class.getSimpleName();	
	
	
	
	private CharSequence mTitle,
						 mMessage;
	@SuppressWarnings("unused")
	private Context mContext;
	
//	private View.OnClickListener mConfirmListener,mCancelListener;
	
	private Button mConfirmButton, mCancelButton;
	
	private TextView mTitleTV, mContentTV;
	
	private boolean mNeedNegativeButton = true;
	
	public interface OnDialogButtonClickListener{
		void onClick(Dialog dialog, View v);
	}
	
	private OnDialogButtonClickListener mConfirmButtonClickListener;
	private OnDialogButtonClickListener mCancelButtonClickListener;
	
	private View.OnClickListener mConfirmOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ConfirmDialog.this.dismiss();
			
			if(mConfirmButtonClickListener!=null) {
				mConfirmButtonClickListener.onClick(ConfirmDialog.this, v);
			}
		}
	};
	
	
	
	private View.OnClickListener mCancelOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ConfirmDialog.this.dismiss();
			
			if (mCancelButtonClickListener != null) {
				mCancelButtonClickListener.onClick(ConfirmDialog.this, v);
			}
		}
	};
	
	public ConfirmDialog(Context context,CharSequence title, CharSequence message) {
		super(context,R.style.Dialog_customStyle);
		
		mContext = context;
		mTitle = title;
		mMessage = message;
		mNeedNegativeButton = true;
	}
	
	public ConfirmDialog(Context context, int titleId, int mesageId) {
		this(context, context.getString(titleId), context.getString(mesageId));
	}
	
	public ConfirmDialog(Context context,CharSequence title, CharSequence message, boolean needNegativeButton) {
		super(context,R.style.Dialog_customStyle);
		
		mContext = context;
		mTitle = title;
		mMessage = message;
		mNeedNegativeButton = needNegativeButton;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_dialog);
		
		mTitleTV = (TextView)findViewById(R.id.confirm_title);
		mContentTV = (TextView) findViewById(R.id.confirm_content);
		
		mCancelButton = (Button) findViewById(R.id.confirm_cancel);
		mConfirmButton = (Button) findViewById(R.id.confirm_confirm);
		
		mCancelButton.setOnClickListener(mCancelOnClickListener);
		mConfirmButton.setOnClickListener(mConfirmOnClickListener);
		
		mTitleTV.setText(mTitle);
		
		mContentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		mContentTV.setText(mMessage);
		
		if (!mNeedNegativeButton) {
			mCancelButton.setVisibility(View.INVISIBLE);
		}
	}

	public void setOnConfirmButtonClickListener(
			OnDialogButtonClickListener confirmButtonClickListener) {
		mConfirmButtonClickListener = confirmButtonClickListener;
	}
	
	public void setOnCancelButtonClickListener(OnDialogButtonClickListener l) {
		mCancelButtonClickListener = l;
	}
	
	public Button getCancelButton() {
		return mCancelButton;
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		mTitleTV.setText(mTitle);
	}
	
	public void setMessage(CharSequence message) {
		mMessage = message;
		mContentTV.setText(message);
	}
	
}
