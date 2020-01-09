package org.gsm.rcsApp.RCS;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactExtended implements Parcelable {
	
	public static final String CONTACT_ONLINE = "online";
	public static final String CONTACT_OFFLINE = "offline";
	public static final String CONTACT_BUSY ="busy";
	public static final String CONATCT_AWAY = "away";
	
	/**
	 * @author   sk0099421
	 */
	public enum AccType{
		/**
		 * @uml.property  name="rCS"
		 * @uml.associationEnd  
		 */
		RCS,/**
		 * @uml.property  name="gtalk"
		 * @uml.associationEnd  
		 */
		gtalk,/**
		 * @uml.property  name="skype"
		 * @uml.associationEnd  
		 */
		skype,/**
		 * @uml.property  name="facebook"
		 * @uml.associationEnd  
		 */
		facebook,/**
		 * @uml.property  name="twitter"
		 * @uml.associationEnd  
		 */
		twitter
	}
	public static final String ACCOUNT_NOT_AVAILABLE ="ACC_NOT_PRESENT";
	
	
	/**
	 * @uml.property  name="displayName"
	 */
	String displayName=null;
	/**
	 * @uml.property  name="facebookID"
	 */
	String facebookID= ACCOUNT_NOT_AVAILABLE;
	/**
	 * @uml.property  name="rCSID"
	 */
	String RCSID = ACCOUNT_NOT_AVAILABLE;
	/**
	 * @uml.property  name="skypeID"
	 */
	String skypeID =ACCOUNT_NOT_AVAILABLE;
	/**
	 * @uml.property  name="gtalkID"
	 */
	String gtalkID=ACCOUNT_NOT_AVAILABLE;
	
	/**
	 * @uml.property  name="facebookStatus"
	 */
	String facebookStatus= CONTACT_OFFLINE;
	/**
	 * @uml.property  name="rCSStatus"
	 */
	String RCSStatus = CONTACT_OFFLINE;
	/**
	 * @uml.property  name="skypeStatus"
	 */
	String skypeStatus =CONTACT_OFFLINE;
	/**
	 * @uml.property  name="gtalkStatus"
	 */
	String gtalkStatus=CONTACT_OFFLINE;
	
	/**
	 * @uml.property  name="newmessagefrom"
	 */
	String newmessagefrom = null;
	
	/**
	 * @return
	 * @uml.property  name="facebookStatus"
	 */
	public String getFacebookStatus() {
		return facebookStatus;
	}

	/**
	 * @param facebookStatus
	 * @uml.property  name="facebookStatus"
	 */
	public void setFacebookStatus(String facebookStatus) {
		this.facebookStatus = facebookStatus;
	}

	/**
	 * @return
	 * @uml.property  name="rCSStatus"
	 */
	public String getRCSStatus() {
		return RCSStatus;
	}

	/**
	 * @param rCSStatus
	 * @uml.property  name="rCSStatus"
	 */
	public void setRCSStatus(String rCSStatus) {
		RCSStatus = rCSStatus;
	}

	/**
	 * @return
	 * @uml.property  name="skypeStatus"
	 */
	public String getSkypeStatus() {
		return skypeStatus;
	}

	/**
	 * @param skypeStatus
	 * @uml.property  name="skypeStatus"
	 */
	public void setSkypeStatus(String skypeStatus) {
		this.skypeStatus = skypeStatus;
	}

	/**
	 * @return
	 * @uml.property  name="gtalkStatus"
	 */
	public String getGtalkStatus() {
		return gtalkStatus;
	}

	/**
	 * @param gtalkStatus
	 * @uml.property  name="gtalkStatus"
	 */
	public void setGtalkStatus(String gtalkStatus) {
		this.gtalkStatus = gtalkStatus;
	}

	public static String getContactOffline() {
		return CONTACT_OFFLINE;
	}

	/**
	 * @uml.property  name="icon"
	 */
	int icon=0;
	
	
	/**
	 * @uml.property  name="hasNewMessage"
	 */
	boolean hasNewMessage=false;
	
	/**
	 * @return
	 * @uml.property  name="icon"
	 */
	public int getIcon() {
		return icon;
	}
	
	/**
	 * @return
	 * @uml.property  name="displayName"
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 * @uml.property  name="displayName"
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return
	 * @uml.property  name="facebookID"
	 */
	public String getFacebookID() {
		return facebookID;
	}

	/**
	 * @param facebookID
	 * @uml.property  name="facebookID"
	 */
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}

	/**
	 * @return
	 * @uml.property  name="rCSID"
	 */
	public String getRCSID() {
		return RCSID;
	}

	/**
	 * @param rCSID
	 * @uml.property  name="rCSID"
	 */
	public void setRCSID(String rCSID) {
		RCSID = rCSID;
	}
public String getnewmesgfrom(){
	return this.newmessagefrom;
}
	/**
	 * @return
	 * @uml.property  name="skypeID"
	 */
	public String getSkypeID() {
		return skypeID;
	}

	/**
	 * @param skypeID
	 * @uml.property  name="skypeID"
	 */
	public void setSkypeID(String skypeID) {
		this.skypeID = skypeID;
	}

	/**
	 * @return
	 * @uml.property  name="gtalkID"
	 */
	public String getGtalkID() {
		return gtalkID;
	}
	/**
	 * @param icon
	 * @uml.property  name="icon"
	 */
	public void setIcon(int icon) {
		this.icon = icon;
	}
	/**
	 * @param gtalkID
	 * @uml.property  name="gtalkID"
	 */
	public void setGtalkID(String gtalkID) {
		this.gtalkID = gtalkID;
	}

	/**
	 * @return
	 * @uml.property  name="hasNewMessage"
	 */
	public boolean isHasNewMessage() {
		return hasNewMessage;
	}

	public void setHasNewMessage(boolean hasNewMessage, String from) {
		this.hasNewMessage = hasNewMessage;
		newmessagefrom =from;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	 public static final Parcelable.Creator<ContactExtended> CREATOR = new Parcelable.Creator<ContactExtended>() {
	        public ContactExtended createFromParcel(Parcel in) {
	            return new ContactExtended(in);
	        }

	        public ContactExtended[] newArray(int size) {
	            return new ContactExtended[size];
	        }
	    };
	public void writeToParcel(Parcel out, int flags) {
       
        out.writeString(displayName);
       
        out.writeString(facebookID);
        out.writeString(RCSID);
        out.writeString(skypeID);
        out.writeString(gtalkID);
        out.writeString(facebookStatus);
        out.writeString(RCSStatus);
        out.writeString(skypeStatus);
        out.writeString(gtalkStatus);
        out.writeInt(icon);
        out.writeInt(hasNewMessage?1:0);
    }

	public ContactExtended() {
		
	}
	
	public ContactExtended(Parcel in) {
      
        displayName = in.readString();
       
        facebookID = in.readString();
        RCSID = in.readString();
        skypeID = in.readString();
        gtalkID = in.readString();
        facebookStatus = in.readString();
        RCSStatus = in.readString();
        skypeStatus = in.readString();
        gtalkStatus = in.readString();
        icon = in.readInt();
        hasNewMessage=(in.readInt()>0);
    }
	
	

}
