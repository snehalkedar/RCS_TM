package org.gsm.rcsApp.misc;

import org.gsm.rcsApp.RCS.ContactExtended;

import android.os.Parcel;
import android.os.Parcelable;

public class Notifications implements Parcelable{

	public static final String NOTIFICATION_TYPE_FAEBOOK_PHOTOTAG ="facebook.phototag";
	public static final String NOTIFICATION_TYPE_FACEBOOK_OTHERS ="facebook.others";
	public static final String NOTIFICATION_TWITTER ="tweets";
	public static final String NOTIFICATION_OTHERS ="others";
	
	/**
	 * @uml.property  name="filePath"
	 */
	private String filePath;
	/**
	 * @uml.property  name="text"
	 */
	private String text;
	/**
	 * @uml.property  name="messageType"
	 */
	private String messageType;
	
	
	public Notifications() {
		super();
		
	}
	
	public Notifications(String filePath, String text, String messageType) {
		super();
		this.filePath = filePath;
		this.text = text;
		this.messageType = messageType;
	}

	public String getfilePath() {
		return filePath;
	}
	public void setfilePath(String filePath) {
		this.filePath = filePath;
	}
	/**
	 * @return
	 * @uml.property  name="text"
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text
	 * @uml.property  name="text"
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return
	 * @uml.property  name="messageType"
	 */
	public String getMessageType() {
		return messageType;
	}
	/**
	 * @param messageType
	 * @uml.property  name="messageType"
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	 public static final Parcelable.Creator<Notifications> CREATOR = new Parcelable.Creator<Notifications>() {
	        public Notifications createFromParcel(Parcel in) {
	            return new Notifications(in);
	        }

	        public Notifications[] newArray(int size) {
	            return new Notifications[size];
	        }
	    };
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(filePath);
		dest.writeString(text);
		dest.writeString(messageType);
	}
	public Notifications(Parcel in) {
		filePath= in.readString();
		text =in.readString();
		messageType= in.readString();
	}
	
}
