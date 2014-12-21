package com.lyh.laucher_lyh.app;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseEntity implements Parcelable {
	
	protected String mId = null;
	protected String mName = null;		// 唯一标识应用的名称
	
	public BaseEntity() {
		
	}
	
	public BaseEntity(String id, String name) {
		mId = id;
		mName = name;
	}
	
	public void setId(String id) {
		if(null != id) {
			mId = id;
		}
	}
	public String getId() {
		return mId;
	}
	
	public void setName(String name) {
		if(null != name) {
			mName = name;
		}
	}
	public String getName() {
		return mName;
	}

	
	public static final Parcelable.Creator<BaseEntity>	CREATOR = 
			new Creator<BaseEntity>() {

		@Override
		public BaseEntity createFromParcel(Parcel source) {
			return new BaseEntity(source);
		}

		@Override
		public BaseEntity[] newArray(int size) {
			return new BaseEntity[size];
		}
	};
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	protected BaseEntity(Parcel in) {
		mId = in.readString();
		mName = in.readString();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mId);
		dest.writeString(mName);
	}
}
