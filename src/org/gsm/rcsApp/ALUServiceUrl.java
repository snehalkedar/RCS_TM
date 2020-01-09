package org.gsm.rcsApp;

public class ALUServiceUrl {

	public static final String serverName="192.11.69.142";
	public static final int serverPort=80;
	public static final String ServerReAlm="skynet.com";
	
	private static String sitebase="http://"+serverName;
	public static String baseURLGW=sitebase+"/";
	
	private static String apiVersion="0.1";

	public static String getRegisterURL() {
		return baseURLGW+"root";				
	}

	public static String getUnregisterURL(String registrationopaqueurl) {
		return baseURLGW+"root/" + registrationopaqueurl;				
	}

	public static String getLongPollingURL(String registrationopaqueurl,int timeout) {
		return baseURLGW + "root/" + registrationopaqueurl +"?attr=events&long-polling=" + timeout;		
	}

	public static String getInitiateChatUrl(String registrationopaqueurl) {
		return baseURLGW+"root/" + registrationopaqueurl;				
	}

	public static String getSendChatUrl(String UniqueIDreceivedFromServerInEventForChat, String senderUser,String recipiantUser) {
		return baseURLGW + "msrp/out/"+ UniqueIDreceivedFromServerInEventForChat + "?from=" + senderUser + "&to=" + recipiantUser;				
	}

	public static String getInitiateFileTransferUrl(String registrationopaqueurl) {
		return baseURLGW+"root/" + registrationopaqueurl;				
	}
	
	public static String getSendFileTransferUrl(String UniqueIDreceivedFromServerInEventForFileTransfer) {
		return baseURLGW+ "/msrp/out/" + UniqueIDreceivedFromServerInEventForFileTransfer;			
	}
	
	public static String getFileDownloadUrl(String UniqueIDReceivedInFileTransferSessionSuccess,String FileNameReceivedInFileTransferSuccessEvent) {
		return baseURLGW+ "/msrp/in/" + UniqueIDReceivedInFileTransferSessionSuccess +"/" + FileNameReceivedInFileTransferSuccessEvent;			
	}
	
	public static String getUrlWithRegOpaque(String registrationopaqueurl) {
		return baseURLGW + "root/" + registrationopaqueurl;
	}
	
	public static String getGeneralUrl(String registrationopaqueurl) {
		return baseURLGW + "root/" + registrationopaqueurl;
	}
	
	public static String getLongPollingUrl(String registrationopaqueurl) {
		return getUrlWithRegOpaque(registrationopaqueurl) + "?attr=events&long-polling=20";
	}
	
}
