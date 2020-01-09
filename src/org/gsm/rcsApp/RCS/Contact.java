package org.gsm.rcsApp.RCS;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
	
	public static final String CONTACT_ONLINE = "open";
	public static final String CONTACT_OFFLINE = "closed";

	/**
	 * @uml.property  name="contactId"
	 */
	String contactId=null;
	/**
	 * @uml.property  name="contactInfo"
	 */
	String contactInfo=null;
	/**
	 * @uml.property  name="displayName"
	 */
	String displayName=null;
	/**
	 * @uml.property  name="icon"
	 */
	int icon=0;
	/**
	 * @uml.property  name="status"
	 */
	String status=null;
	/**
	 * @uml.property  name="capabilities"
	 */
	String capabilities=null;
	/**
	 * @uml.property  name="hasNewMessage"
	 */
	boolean hasNewMessage=false;
	/**
	 * @uml.property  name="resourceURL"
	 */
	String resourceURL=null;
	/**
	 * @uml.property  name="accountype"
	 */
	String accountype=null;
	
	/**
	 * @return
	 * @uml.property  name="accountype"
	 */
	public String getAccountype() {
		return accountype;
	}
	/**
	 * @return
	 * @uml.property  name="contactId"
	 */
	public String getContactId() {
		return contactId;
	}
	/**
	 * @return
	 * @uml.property  name="contactInfo"
	 */
	public String getContactInfo() {
		return contactInfo;
	}
	/**
	 * @return
	 * @uml.property  name="displayName"
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @return
	 * @uml.property  name="icon"
	 */
	public int getIcon() {
		return icon;
	}
	/**
	 * @return
	 * @uml.property  name="status"
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @return
	 * @uml.property  name="capabilities"
	 */
	public String getCapabilities() {
		return capabilities;
	}
	/**
	 * @return
	 * @uml.property  name="resourceURL"
	 */
	public String getResourceURL() {
		return resourceURL;
	}
	public void setAccountType(String accountype) {
		this.accountype = accountype;
	}
	/**
	 * @param contactId
	 * @uml.property  name="contactId"
	 */
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	/**
	 * @param contactInfo
	 * @uml.property  name="contactInfo"
	 */
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}
	/**
	 * @param displayName
	 * @uml.property  name="displayName"
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @param icon
	 * @uml.property  name="icon"
	 */
	public void setIcon(int icon) {
		this.icon = icon;
	}
	/**
	 * @param status
	 * @uml.property  name="status"
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @param capabilities
	 * @uml.property  name="capabilities"
	 */
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	/**
	 * @return
	 * @uml.property  name="hasNewMessage"
	 */
	public boolean isHasNewMessage() {
		return hasNewMessage;
	}
	/**
	 * @param hasNewMessage
	 * @uml.property  name="hasNewMessage"
	 */
	public void setHasNewMessage(boolean hasNewMessage) {
		this.hasNewMessage = hasNewMessage;
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
	
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

	public void writeToParcel(Parcel out, int flags) {
        out.writeString(contactId);
        out.writeString(displayName);
        out.writeInt(icon);
        out.writeString(status);
        out.writeString(resourceURL);
        out.writeString(capabilities);
        out.writeString(accountype);
        out.writeInt(hasNewMessage?1:0);
    }

	public Contact() {
		
	}
	
	public Contact(Parcel in) {
        contactId = in.readString();
        displayName = in.readString();
        icon = in.readInt();
        status = in.readString();
        resourceURL = in.readString();
        capabilities = in.readString();
        accountype = in.readString();
        hasNewMessage=(in.readInt()>0);
    }
}
