package org.gsm.rcsApp;

import org.gsm.rcsApp.misc.Utils;

public class ServiceURL {

	public static final String serverName="rcs.oneapi-gw.gsma.com";
	public static final int serverPort=80;
	
	private static String sitebase="https://"+serverName;
	private static String baseURLGW=sitebase+"/";
	
	private static String apiVersion="0.1";

	public static String registerURL(String username) {
		return baseURLGW+"register/"+apiVersion+"/"+Utils.URLEncode(username)+"/sessions";				
	}

	public static String unregisterURL(String username) {
		return baseURLGW+"register/"+apiVersion+"/"+Utils.URLEncode(username)+"/sessions";				
	}

	public static String createNotificationChannelURL(String username) {
		return baseURLGW+"notificationchannel/"+apiVersion+"/"+Utils.URLEncode(username)+"/channels";				
	}

	public static String deleteNotificationChannelURL(String username, String channelId) {
		return baseURLGW+"notificationchannel/"+apiVersion+"/"+Utils.URLEncode(username)+"/channels/"+Utils.URLEncode(channelId);				
	}

	public static String createAddressBookChangeSubscriptionURL(String username) {
		return baseURLGW+"addressbook/"+apiVersion+"/"+Utils.URLEncode(username)+"/subscriptions/abChanges";				
	}
	public static String createFileTransferSubscriptionURL(String username) {
		return baseURLGW+"filetransfer/"+apiVersion+"/"+Utils.URLEncode(username)+"/subscriptions";
	}

	public static String createSessionChangeSubscriptionURL(String username) {
		return baseURLGW+"register/"+apiVersion+"/"+Utils.URLEncode(username)+"/subscriptions";				
	}

	public static String createChatSubscriptionURL(String username) {
		return baseURLGW+"chat/"+apiVersion+"/"+Utils.URLEncode(username)+"/subscriptions";				
	}

	public static String sendAdhocIMMessageURL(String username, String contactId) {
		return baseURLGW+"chat/"+apiVersion+"/"+Utils.URLEncode(username)+"/oneToOne/"+Utils.URLEncode(contactId)+"/adhoc/messages";				
	}

	public static String getContactListURL(String username) {
		return baseURLGW+"addressbook/"+apiVersion+"/"+Utils.URLEncode(username)+"/contacts";				
	}

	public static String getSendIsComposingAutomaticContactURL(String username, String contactId) {
		return baseURLGW+"chat/"+apiVersion+"/"+Utils.URLEncode(username)+"/oneToOne/"+Utils.URLEncode(contactId)+"/adhoc/messages";				
	}

	public static String getSendMessageStatusReportURL(String userId, String otherUserId, String sessionId, String messageId) {
		return baseURLGW+"chat/"+apiVersion+"/"+Utils.URLEncode(userId)+"/oneToOne/"+
				Utils.URLEncode(otherUserId)+"/"+Utils.URLEncode(sessionId)+"/messages/"+Utils.URLEncode(messageId)+"/status";
	}

	public static String getAddContactURL(String username, String contactId) {
		return baseURLGW+"addressbook/"+apiVersion+"/"+Utils.URLEncode(username)+"/contacts/"+Utils.URLEncode(contactId);				
	}
	
	public static String getDeleteContactURL(String username, String contactId) {
		return baseURLGW+"addressbook/"+apiVersion+"/"+Utils.URLEncode(username)+"/contacts/"+Utils.URLEncode(contactId);				
	}

	public static String getEditContactURL(String username, String contactId) {
		return baseURLGW+"addressbook/"+apiVersion+"/"+Utils.URLEncode(username)+"/contacts/"+Utils.URLEncode(contactId);				
	}
	public static String createGroupChatURL(String username) {
		return baseURLGW+"chat/"+apiVersion+"/"+Utils.URLEncode(username)+"/group";
	}
	public static String createGroupChatAddContactURL(String userId,String sessionId) {
		return baseURLGW+"chat/"+apiVersion+"/"+Utils.URLEncode(userId)+"/group/"+sessionId+"/participants";
	}
	public static String createGroupChatExitURL(String userId,String sessionId) {
		return baseURLGW+"chat/"+apiVersion+"/"+Utils.URLEncode(userId)+"/group/"+sessionId+"/participants/"+Utils.URLEncode(userId);
	}
	public static String getGropuChatURL(String userId,String sessionId){
		return baseURLGW+"chat/"+apiVersion+"/"+Utils.URLEncode(userId)+"/group/"+sessionId+"/messages";
		
	}
	public static String getAcceptGroupChat(String userId,String sessionId,String participantId){
		return baseURLGW+"chat/"+apiVersion+"/"+Utils.URLEncode(userId)+"/group/"+sessionId+"/participants/"+participantId+"/status";
	}
	public static String getgroupChatParticipantURL(String userId,String sessionId){
		return createGroupChatAddContactURL(userId,sessionId);
	}
	public static String getfileTransferURL(String userId){
		return baseURLGW+"filetransfer/"+apiVersion+"/"+Utils.URLEncode(userId)+"/sessions";
	}
	
}
