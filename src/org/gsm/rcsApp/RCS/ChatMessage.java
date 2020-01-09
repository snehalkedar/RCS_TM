package org.gsm.rcsApp.RCS;

import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatMessage implements Parcelable {
	
	public static final String MESSAGE_RECEIVED = "rx";
	public static final String MESSAGE_SENT = "tx";
	
	public static final String MESSAGE_STATUS_PENDING="pending";
	public static final String MESSAGE_STATUS_SENT="sent";
	public static final String MESSAGE_STATUS_DELIVERED="delivered";
	public static final String MESSAGE_STATUS_VIEWED="viewed";
	public static final String MESSAGE_STATUS_RECEIVED = "received";

	/**
	 * @uml.property  name="contactUri"
	 */
	String contactUri=null;
	/**
	 * @uml.property  name="messageText"
	 */
	String messageText=null;
	/**
	 * @uml.property  name="messageTime"
	 */
	String messageTime=null;
	/**
	 * @uml.property  name="messageDirection"
	 */
	String messageDirection=null;
	/**
	 * @uml.property  name="messageId"
	 */
	String messageId=null;
	/**
	 * @uml.property  name="messageInternalId"
	 */
	private String messageInternalId=null;
	/**
	 * @uml.property  name="status"
	 */
	String status=null;
	/**
	 * @uml.property  name="viewed"
	 */
	boolean viewed=false;
	/**
	 * @uml.property  name="resourceURL"
	 */
	String resourceURL=null;
	
	/**
	 * @return
	 * @uml.property  name="contactUri"
	 */
	public String getContactUri() {
		return contactUri;
	}

	/**
	 * @param contactUri
	 * @uml.property  name="contactUri"
	 */
	public void setContactUri(String contactUri) {
		this.contactUri = contactUri;
	}

	/**
	 * @return
	 * @uml.property  name="messageText"
	 */
	public String getMessageText() {
		return messageText;
	}

	/**
	 * @return
	 * @uml.property  name="messageTime"
	 */
	public String getMessageTime() {
		return messageTime;
	}

	/**
	 * @param messageText
	 * @uml.property  name="messageText"
	 */
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	/**
	 * @param messageTime
	 * @uml.property  name="messageTime"
	 */
	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	/**
	 * @return
	 * @uml.property  name="messageDirection"
	 */
	public String getMessageDirection() {
		return messageDirection;
	}

	/**
	 * @param messageDirection
	 * @uml.property  name="messageDirection"
	 */
	public void setMessageDirection(String messageDirection) {
		this.messageDirection = messageDirection;
	}

	/**
	 * @return
	 * @uml.property  name="messageId"
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @return
	 * @uml.property  name="viewed"
	 */
	public boolean isViewed() {
		return viewed;
	}

	/**
	 * @param messageId
	 * @uml.property  name="messageId"
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @param viewed
	 * @uml.property  name="viewed"
	 */
	public void setViewed(boolean viewed) {
		this.viewed = viewed;
	}

	/**
	 * @return
	 * @uml.property  name="messageInternalId"
	 */
	public String getMessageInternalId() {
		return messageInternalId;
	}

	/**
	 * @param messageInternalId
	 * @uml.property  name="messageInternalId"
	 */
	protected void setMessageInternalId(String messageInternalId) {
		this.messageInternalId = messageInternalId;
	}

	/**
	 * @return
	 * @uml.property  name="status"
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 * @uml.property  name="status"
	 */
	public void setStatus(String status) {
		if (this.status==null && status!=null) {
			this.status=status;
		} else if (status!=null) {
			// It is possible notifications are received out of order - therefore if a message is 'viewed' it should stay that way
			if (!MESSAGE_STATUS_VIEWED.equalsIgnoreCase(this.status) && MESSAGE_STATUS_VIEWED.equalsIgnoreCase(status)) {
				this.status=MESSAGE_STATUS_VIEWED;
			} else if (MESSAGE_STATUS_PENDING.equalsIgnoreCase(this.status)) {
				this.status=status;
			}
		}
	}

	/**
	 * @return
	 * @uml.property  name="resourceURL"
	 */
	public String getResourceURL() {
		return resourceURL;
	}
	
	/**
	 * @param resourceURL
	 * @uml.property  name="resourceURL"
	 */
	public void setResourceURL(String resourceURL) {
		this.resourceURL=resourceURL;
	}

	public int describeContents() {
        return 0;
    }
	
    public static final Parcelable.Creator<ChatMessage> CREATOR = new Parcelable.Creator<ChatMessage>() {
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

	public void writeToParcel(Parcel out, int flags) {
        out.writeString(contactUri);
        out.writeString(messageText);
        out.writeString(messageTime);
        out.writeString(messageDirection);
        out.writeString(messageId);
        out.writeString(messageInternalId);
        out.writeString(status);
        out.writeString(resourceURL);
        out.writeInt(viewed?1:0);
    }

	public ChatMessage() {
		messageInternalId=UUID.randomUUID().toString();
	}
	
	public ChatMessage(Parcel in) {
		contactUri = in.readString();
		messageText = in.readString();
		messageTime = in.readString();
		messageDirection = in.readString();
		messageId = in.readString();
		messageInternalId = in.readString();
		status = in.readString();
		resourceURL = in.readString();
		viewed = in.readInt()>0;
    }

}
