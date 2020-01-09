package org.gsm.rcsApp.RCS;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatSession implements Parcelable {

	/**
	 * @uml.property  name="destinationUri"
	 */
	String destinationUri=null;
	/**
	 * @uml.property  name="sessionId"
	 */
	String sessionId=null;
	/**
	 * @uml.property  name="messageId"
	 */
	String messageId=null;
	
	/**
	 * @return
	 * @uml.property  name="destinationUri"
	 */
	public String getDestinationUri() {
		return destinationUri;
	}

	/**
	 * @return
	 * @uml.property  name="sessionId"
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @return
	 * @uml.property  name="messageId"
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @param destinationUri
	 * @uml.property  name="destinationUri"
	 */
	public void setDestinationUri(String destinationUri) {
		this.destinationUri = destinationUri;
	}

	/**
	 * @param sessionId
	 * @uml.property  name="sessionId"
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @param messageId
	 * @uml.property  name="messageId"
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public int describeContents() {
        return 0;
    }
	
    public static final Parcelable.Creator<ChatSession> CREATOR = new Parcelable.Creator<ChatSession>() {
        public ChatSession createFromParcel(Parcel in) {
            return new ChatSession(in);
        }

        public ChatSession[] newArray(int size) {
            return new ChatSession[size];
        }
    };

	public void writeToParcel(Parcel out, int flags) {
        out.writeString(destinationUri);
        out.writeString(sessionId);
        out.writeString(messageId);
    }

	public ChatSession() {
		
	}
	
	public ChatSession(Parcel in) {
		destinationUri = in.readString();
		sessionId = in.readString();
		messageId = in.readString();
    }
}
