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
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.lyh.laucher_lyh.json.JsonParser;
import com.lyh.laucher_lyh.utils.KToast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private RecognizerDialog mIatDialog;
	// ������д����
	private SpeechRecognizer mIat;
	Button mButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mIat = SpeechRecognizer.createRecognizer(this,null);
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");

	    mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
	    
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

	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		KToast.showToastLong(MainActivity.this, "初始化失败");
        	}
		}
	};
	
	/**
	 *
	 */
	private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			KToast.showCenter(MainActivity.this, text, Toast.LENGTH_LONG);
		}

		/**
		 * ʶ��ص�����.
		 */
		public void onError(SpeechError error) {
			
		}

	};

	

	/**
	 * ��������
	 * @param param
	 * @return 
	 */
	public void setParam(){
		// ��ղ���
		mIat.setParameter(SpeechConstant.PARAMS, null);
			// ��������
	    mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// ������������
	    mIat.setParameter(SpeechConstant.ACCENT,"mandarin");
	
	    mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/LYH/wavaudio.pcm");
	}

}
