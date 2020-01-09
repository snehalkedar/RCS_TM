package org.gsm.rcsApp.RCS;

import java.util.ArrayList;

public class ContactState {

	/**
	 * @uml.property  name="contactUri"
	 */
	private String contactUri=null;
	/**
	 * @uml.property  name="chatIsVisible"
	 */
	private boolean chatIsVisible=false;
	/**
	 * @uml.property  name="newMessage"
	 */
	private boolean newMessage=false;
	/**
	 * @uml.property  name="composingState"
	 */
	private String composingState=null;
	
	/**
	 * @uml.property  name="messageBuffer"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="org.gsm.rcsApp.RCS.ChatMessage"
	 */
	private ArrayList<ChatMessage> messageBuffer=new ArrayList<ChatMessage>();
	
	public ContactState(String contactUri) {
		this.contactUri=contactUri;
	}
	
	/**
	 * @return
	 * @uml.property  name="contactUri"
	 */
	public String getContactUri() {
		return contactUri;
	}
	/**
	 * @return
	 * @uml.property  name="chatIsVisible"
	 */
	public boolean isChatIsVisible() {
		return chatIsVisible;
	}
	/**
	 * @param contactUri
	 * @uml.property  name="contactUri"
	 */
	public void setContactUri(String contactUri) {
		this.contactUri = contactUri;
	}
	/**
	 * @param chatIsVisible
	 * @uml.property  name="chatIsVisible"
	 */
	public void setChatIsVisible(boolean chatIsVisible) {
		this.chatIsVisible = chatIsVisible;
	}
	
	public void storeMessage(ChatMessage message) {
		messageBuffer.add(message);
	}
	
	public ArrayList<ChatMessage> getMessageBuffer() {
		return messageBuffer;
	}

	/**
	 * @return
	 * @uml.property  name="newMessage"
	 */
	public boolean isNewMessage() {
		return newMessage;
	}

	/**
	 * @param newMessage
	 * @uml.property  name="newMessage"
	 */
	public void setNewMessage(boolean newMessage) {
		this.newMessage = newMessage;
	}

	protected void setComposingIndicator(String composingState) {
		this.composingState=composingState;
	}
	
	public String getComposingIndicator() {
		return this.composingState; 
	}
	
}
