package com.lyh.laucher_lyh;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.lyh.laucher_lyh.R;
import com.lyh.laucher_lyh.json.JsonParser;
import com.lyh.laucher_lyh.utils.KToast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private RecognizerDialog mIatDialog;
	// 语音听写对象
	private SpeechRecognizer mIat;
	Button mButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 初始化识别对象
		mIat = SpeechRecognizer.createRecognizer(this,null);
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");

	    mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
	    
		// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		mIatDialog = new RecognizerDialog(this,mInitListener);
		mButton = (Button) findViewById(R.id.button);
		
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startListenToUser();
			}
		});
	}
	
	private void startListenToUser() {
		mIatDialog.setListener(recognizerDialogListener);
		mIatDialog.show();
	}
	
	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		KToast.showToastLong(MainActivity.this, "初始化语音失败");
        	}
		}
	};
	
	private RecognizerListener mListener = new RecognizerListener() {
		
		@Override
		public void onVolumeChanged(int arg0) {
		}
		
		@Override
		public void onResult(RecognizerResult arg0, boolean arg1) {
			Log.d(TAG, arg0.getResultString());
		}
		
		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
		}
		
		@Override
		public void onError(SpeechError arg0) {
		}
		
		@Override
		public void onEndOfSpeech() {
		}
		
		@Override
		public void onBeginOfSpeech() {
		}
	};
	
	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			KToast.showCenter(MainActivity.this, text, Toast.LENGTH_LONG);
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			
		}

	};

	

	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	public void setParam(){
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
			// 设置语言
	    mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
	    mIat.setParameter(SpeechConstant.ACCENT,"mandarin");
	
	    mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/LYH/wavaudio.pcm");
	}

}
