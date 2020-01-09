package org.gsm.rcsApp.misc;

public class RestJsonData {

	public static String getJsonForRegisterRequest(int expireTime,String userNameWithDomain,String domain,String privateIdentity,String passWord) {
		String jsonString = "{ \"type\":\"ims\",\"expire\":" + 
				expireTime +",\"attrs\":{\"aor\":\"" + userNameWithDomain + "\",\"realm\":\"" + domain + 
				"\",\"username\":\"" + privateIdentity + "\",\"passwd\":\"" +
				passWord +"\", \"long-polling\":\"events\" } }";
		System.out.println("Registration Json " + jsonString);
		return jsonString;
	}

	public static String getJsonForAddContactWithoutAttr(String userNameWithDomain, String displayName) {
		String jsonString = 
				"{\"attrs\": { \"contacts\":[{\"name\":\"personal\",\"displayname\":\"Personal Contacts\", \"contacts\":[" +
						"{\"uri\":\":" + userNameWithDomain + "\",\"displayname\":\"" +  displayName +"\"}]}]}}";
		System.out.println("AddContactWithoutAttr Json " + jsonString);
		return jsonString;

	}

///////// Chat Session //////
	public static String getJsonForSingleChatInvitation(String RecipientId, String chatSessionName) {
	    String jsonString = "{\"attrs\" : { \"invite/conf/" + RecipientId + "/" +  chatSessionName + "\" : \"attr:events\"}";
		System.out.println("SingleChatInvitation Json " + jsonString);
		return jsonString;
	}
	
	public static String getJsonForAcceptChatInvitation(String RecipientId, String SessionIDReceivedInEventForChatInvitation) {
		String jsonString = "{\"attrs\" : { \"invite/conf/"
				+ RecipientId + "/" + SessionIDReceivedInEventForChatInvitation+ "\" : \"accept\"}";
		System.out.println("AcceptChatInvitation Json " + jsonString);
		return jsonString;
	}
	
///////// File Transfer //////
	public static String getJsonForInitiateFileTransfer(String RecipientId, String FileTransSessionName) {
		String jsonString = "{\"attrs\" : { \"invite/file/" + RecipientId + "/" + FileTransSessionName + "\" : \"attr:events\"}";
		System.out.println("InitiateFileTransfer Json " + jsonString);
		return jsonString;
	}
	
	public static String getJsonForAcceptingFileTransfer(String RecipientId, String FileTransSessionName, String SessionIDReceivedInEventForFileTransfer) {
		String jsonString = "{\"attrs\" : { \"invite/file/" + RecipientId + "/" + SessionIDReceivedInEventForFileTransfer +"\" : \"accept\"}";
		System.out.println("InitiateFileTransfer Json " + jsonString);
		return jsonString;
	}
	
	public static String getJsonForSubscribeForPresence(String userIdToBeSubscribed) {
		String jsonString ="{\"attrs\" : { \"subscribe/presence/"+ userIdToBeSubscribed +"\" : \"attr:events\"}}"; 
		System.out.println("InitiateFileTransfer Json " + jsonString);
		return jsonString;
	}
}
