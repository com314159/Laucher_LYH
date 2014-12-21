package com.lyh.laucher_lyh.app.entity;


public class AppEntity extends com.lyh.laucher_lyh.app.BaseEntity{
	
	public static final int MAX_GRADE = 5;
	
	private String mClassify = null;
	
	private double mPrice = 0;
	
	private String mChannelId;						// 开发者的ID
	private String mChannelKey = null;				// 开发者的密钥
	
	private String mPackageName = null;
	private long mVersionCode = 0;
	private String mVersionName = null;
	
	private String mApkUrl = null;

	private double mApkSizeMB = 0;
	
	private int mDownload = 0;
	private double mGrade = 0;
	
	
	public AppEntity() {
		
	}
	
	public AppEntity(String id, String name) {
		super(id, name);
	}

	public void setClassify(String classify) {
		if(null != classify) {
			mClassify = classify;
		}
	}
	public String getClassify() {
		return mClassify;
	}
	
	public void setDownload(int download) {
		mDownload = download;
	}
	public int getDownload() {
		return mDownload;
	}
	
	public void setGrade(double grade) {
		mGrade = grade;
	}
	public double getGrade() {
		if(mGrade < 0) {
			return 0;
		} else if(mGrade > MAX_GRADE) {
			return MAX_GRADE;
		}
		return mGrade;
	}
	
	public void setPrice(double price) {
		mPrice = price;
	}
	public double getPrice() {
		return mPrice;
	}
	
	public void setChannelId(String channelId) {
		mChannelId = channelId;
	}
	public String getChannelId() {
		return mChannelId;
	}
	
	public void setChannelSecretKey(String channelKey) {
		if(null != channelKey) {
			mChannelKey = channelKey;
		}
	}
	public String getChannelKey() {
		return mChannelKey;
	}
	
	public void setName(String name) {
		if(null != name) {
			mName = name;
		}
	}
	public String getName() {
		return mName;
	}
	
	public void setPackageName(String packageName) {
		if(null != packageName) {
			mPackageName = packageName;
		}
	}
	public String getPackageName() {
		return mPackageName;
	}
	
	public void setVersionCode(long versionCode) {
		mVersionCode = versionCode;
	}
	public long getVersionCode() {
		return mVersionCode;
	}
	
	public void setVersionName(String versionName) {
		if(null != versionName) {
			mVersionName = versionName;
		}
	}
	public String getVersionName() {
		return mVersionName;
	}
	
	public void setApkUrl(String apkUrl) {
		if(null != apkUrl) {
			mApkUrl = apkUrl;
		}
	}
	public String getApkUrl() {
		return mApkUrl;
	}
	
	public void setApkSizeMB(double apkSize) {
		mApkSizeMB = apkSize;
	}
	public double getApkSizeMB() {
		return mApkSizeMB;
	}
	
	
	//得到比如 2.52MB;
	public String getApkSizeMBString(){
		return String.valueOf(mApkSizeMB)+"MB";
	}
	
	public int getApkSizeKB() {
		return (int) mApkSizeMB * 1024;
	}
	
	public String[] getClassifys(){
		if(mClassify!=null){
			return mClassify.split("#");
		}
		return null;
	}
	
}
