package org.gsm.rcsApp.activities;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.MultihomePlainSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.gsm.rcsApp.ServiceURL;
import org.gsm.rcsApp.RCS.ChatMessage;
import org.gsm.rcsApp.RCS.Contact;
import org.gsm.rcsApp.RCS.ContactExtended;
import org.gsm.rcsApp.RCS.ContactState;
import org.gsm.rcsApp.RCS.ContactStateManager;
import org.gsm.rcsApp.adapters.ContactRowAdapter;
import org.gsm.rcsApp.adapters.MessageRowAdapter;
import org.gsm.rcsApp.adapters.SimpleContactAdapter;
import org.gsm.rcsApp.adapters.verySimpleAdapter;
import org.gsm.rcsApp.misc.FileDialog;
import org.gsm.rcsApp.misc.RCSJsonHttpResponseHandler;
import org.gsm.rcsApp.misc.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orangelabs.rcs.service.api.client.ClientApiException;
import com.orangelabs.rcs.service.api.client.messaging.MessagingApi;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.gsm.RCSDemo.R;

public class ChatSessionActivity extends Activity implements Runnable {
	private static String destinationUri=null;
	private static String displayName=null;
	public final static String ALL_CONTACTS = "com.mobilelife.rcsApp.activities.MainActivity.ALLCONTACTS";
	static boolean running=false;
	static Thread background=null;
	/**
	 * @uml.property  name="gruopchatinitiated"
	 */
	private boolean gruopchatinitiated=false;
	private static ContactState contactState=null;
	 /**
	 * @uml.property  name="retrievedContact"
	 * @uml.associationEnd  
	 */
	private ContactExtended retrievedContact = null;
	static MessageRowAdapter mradapter=null;
	
	static ListView messageListView=null;
	static TextView isComposingIndicator=null;
	static CheckBox skypeCheckbox=null;
	static CheckBox gmailCheckBox=null;
	static CheckBox joynCheckBox=null;
	private static Handler messageHandler = null;
	private static Handler composingIndicatorHandler = null;
	static boolean sentComposing=false;
	
	static ChatSessionActivity _instance=null;

	private static boolean viewIsVisible=false;
	
	private static final String RECEIVED_MESSAGE="receivedMessage";

	private static final int COMPOSINGUPDATEFREQUENCYSECONDS=60;
	static verySimpleAdapter Chatcradapter=null;
	private static ArrayList<ContactExtended> retrievedChatContacts=new ArrayList<ContactExtended>();
	private static ArrayList<ContactExtended> retrievedAllContacts=new ArrayList<ContactExtended>();
	static Gallery chatcontactListView=null;
	/**
	 * @uml.property  name="genericSkypeuser"
	 */
	private String genericSkypeuser="tel:+918410123414";//Skype generic user
	/**
	 * @uml.property  name="genericGtalkUser"
	 */
	private String genericGtalkUser="tel:+918410123415";//gtalk generic user
	
	private static String sessionId = null;
	/**
	 * @uml.property  name="messageApi"
	 * @uml.associationEnd  readOnly="true"
	 */
	private MessagingApi messageApi;
	private static Handler changeStateHandler = null;
	public static HashMap<String , String > statusMap = new HashMap<String, String>();
	/**
	 * @uml.property  name="grpChatended"
	 */
	private boolean grpChatended = false;
	@Override
    public void onCreate(Bundle savedInstanceState) {		
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatsession);
        
        _instance=this;

        Intent intent = getIntent();
        retrievedContact =intent.getParcelableExtra(MainActivity.SELECTED_CONTACT);
        retrievedAllContacts =intent.getParcelableArrayListExtra(MainActivity.ALL_CONTACTS);
        displayName=retrievedContact.getDisplayName();
        grpChatended =false;
        //messageApi = new MessagingApi(_instance);
      //  messageApi.connectApi();
      //  if (displayName==null) displayName=retrievedContact.getContactId();
     //   this.setTitle(displayName);
        retrievedChatContacts.add(retrievedContact);
        Chatcradapter=new verySimpleAdapter(this, retrievedChatContacts);
        chatcontactListView =(Gallery)findViewById(R.id.listview12);
        //chatcontactListView =(ListView)findViewById(R.id.chatcontactList);
        chatcontactListView.setAdapter(Chatcradapter);
        chatcontactListView.setFocusable(true);
        destinationUri=intent.getStringExtra("DesinationURI");//retrievedContact.getContactId();
        
		messageListView=(ListView) findViewById(R.id.messageList);
		isComposingIndicator=(TextView) findViewById(R.id.isComposingIndicator);

		mradapter=new MessageRowAdapter(this, contactState.getMessageBuffer());
        messageListView.setAdapter(mradapter);
        messageHandler = new Handler() {
    		public void handleMessage(Message msg) {
    			super.handleMessage(msg);
    			if (msg.getData()!=null) {
    				Bundle data=msg.getData();
    				if (data.containsKey(RECEIVED_MESSAGE)) {
    					ChatMessage receivedMessage=(ChatMessage) data.get(RECEIVED_MESSAGE);
    					if (receivedMessage!=null) {
    						contactState.getMessageBuffer().add(receivedMessage);
    					}
    				}
    			}
				messageListView.smoothScrollToPosition(contactState.getMessageBuffer()!=null?(contactState.getMessageBuffer().size() - 1):0);
    			mradapter.notifyDataSetChanged();
    			messageListView.refreshDrawableState();
    		}
    	};
    	
    	composingIndicatorHandler = new Handler() {
    		public void handleMessage(Message msg) {
    			if (msg.what==1) {
					isComposingIndicator.setVisibility(View.VISIBLE);
					isComposingIndicator.setText(displayName+" is writing");
				} else {
					isComposingIndicator.setVisibility(View.INVISIBLE);
				}
    		}
    	};
    	
    	changeStateHandler = new Handler() {
    		public void handleMessage(Message msg) {
    			super.handleMessage(msg);
    			 Chatcradapter.notifyDataSetChanged();
    			chatcontactListView.refreshDrawableState();
    			
    			//chatcontactListView.invalidate();
    		}
    	};
		if (!running) {
	        running=true;
	        background=new Thread(this);
	        background.start();			
		}

	}
	
	public void onStart() {
		super.onStart();
		
		if (!running) {
	        running=true;
	        background=new Thread(this);
	        background.start();			
		}
		viewIsVisible=true;
		isComposingIndicator.setVisibility(View.INVISIBLE);
		
		/*skypeCheckbox =(CheckBox)findViewById(R.id.checkbox_skype);
		gmailCheckBox =(CheckBox)findViewById(R.id.checkbox_gtalk);
		joynCheckBox=(CheckBox)findViewById(R.id.checkbox_joyn);*/
	}
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		  if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","onPause");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(retrievedChatContacts.size()>1 && !grpChatended)
			exitGroupChat();
		
		retrievedChatContacts.clear();
		statusMap.clear();
		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","onDestroy");
		
	
		ChatSessionActivity.sessionId = null;
		gruopchatinitiated =false;
	}

	public void onStop() {
		super.onStop();
		 if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","onStop");
		running=false;
		if (background!=null) {
			background.interrupt();
			background=null;
		}
		viewIsVisible=false;
		if (sentComposing) {
			clearComposingIndicator();
		}
		
		MainActivity.chatSessionClosed(destinationUri);
	}
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.messaging_options, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.exitChat:
	    	exitGroupChat();
	    	return true;
	        case R.id.startGroupChat:
	        	startGroupChat();
	        	return true;
	        case R.id.sendFile:
	        	chooseFile();
	        	    	//	sendFile("/mnt/sdcard/taj.jpg","dummy from facebook");
	    		    return true;
	        case R.id.deleteUser:
	       deleteContact(destinationUri);
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void chooseFile(){
		Intent fileIntent = new Intent(_instance, FileDialog.class);
		startActivityForResult(fileIntent, 0);
	}
	
	public void sendFile(String FilePath,String fileDesc){
		if(FilePath==null)
			return;
		 if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","SedFile File path : "+FilePath);
		 new uploadFilesTask().execute(FilePath,fileDesc);
		/*DefaultHttpClient mHttpClient= new DefaultHttpClient();
		HttpParams params1 = new BasicHttpParams();
     //   params1.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpConnectionParams
        .setConnectionTimeout(mHttpClient.getParams(), 10000);
   //     mHttpClient = new DefaultHttpClient(params1);
       
      
		
		AsyncHttpClient client = new AsyncHttpClient();
	
        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
        
        client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope); 
        
        mHttpClient.getCredentialsProvider().setCredentials(authscope, new UsernamePasswordCredentials(SplashActivity.userId, SplashActivity.appCredentialPassword));
       
        File newFile= new File(FilePath);
        JSONObject FileTransferSessionInformationobj = new JSONObject();
        JSONObject FileInformation = new JSONObject();
        JSONObject FileSelector = new JSONObject();
        try {
			FileSelector.put("name",newFile.getName());
			FileSelector.put("type","image/jpeg");
			FileInformation.put("fileSelector", FileSelector);
			FileInformation.put("fileDescription", fileDesc);
			//send either file content or fileURL
		//	FileInformation.put("fileURL","http://fbcdn-sphotos-h-a.akamaihd.net/hphotos-ak-prn1/s720x720/555928_584450548249983_203037803_n.jpg");
			FileTransferSessionInformationobj.put("originatorAddress", "tel:"+SplashActivity.userId);
			FileTransferSessionInformationobj.put("receiverAddress", destinationUri);
			FileTransferSessionInformationobj.put("fileInformation",FileInformation);
			
			
      
			
        //	FileEntity file = new FileEntity(newFile, "image/jpeg");
        	String FileTransferSessionInformation="{\"fileTransferSessionInformation\":"+FileTransferSessionInformationobj.toString()+"}";
        	StringEntity requestData=new StringEntity(FileTransferSessionInformation);
        	String url = ServiceURL.getfileTransferURL(SplashActivity.userId);
        //	ByteArrayInputStream bytes= new ByteArrayInputStream(FileUtils.readFileToByteArray(newFile));
        	
        	FileInputStream fileStream = new FileInputStream(newFile);
        	
        	BufferedInputStream bytes = new BufferedInputStream(fileStream);
        
       
        	MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        	multipartEntity.addPart("root-fields", new StringBody(FileTransferSessionInformation));
        	multipartEntity.addPart("attachments", new FileBody(newFile));
     
        	
        	  HttpPost httppost = new HttpPost(url);
        	  httppost.setEntity(multipartEntity);
        	  HttpResponse response = mHttpClient.execute(httppost);
        	  if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","response : "+response.toString());
        	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    // File newFile = new File()
        
        					
	
		
		
	}
	private class uploadFilesTask extends AsyncTask<String, Integer, Long> {
	  
		protected Long doInBackground(String ...post) {
	        String FilePath = post[0];
	        String fileDesc =post[1];
	        DefaultHttpClient mHttpClient= new DefaultHttpClient();
			HttpParams params1 = new BasicHttpParams();
	     //   params1.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	        HttpConnectionParams
	        .setConnectionTimeout(mHttpClient.getParams(), 10000);
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        mHttpClient.getCredentialsProvider().setCredentials(authscope, new UsernamePasswordCredentials(SplashActivity.userId, SplashActivity.appCredentialPassword));
	        File newFile= new File(/*Environment.getExternalStorageDirectory().getAbsolutePath()+"/redflower.jpg"*/FilePath);
	        JSONObject FileTransferSessionInformationobj = new JSONObject();
	        JSONObject FileInformation = new JSONObject();
	        JSONObject FileSelector = new JSONObject();
	        try {
				FileSelector.put("name",newFile.getName());
				FileSelector.put("type","image/jpeg");
				FileInformation.put("fileSelector", FileSelector);
				FileInformation.put("fileDescription", "Dummy descrption");
				//send either file content or fileURL
				//FileInformation.put("fileURL","http://fbcdn-sphotos-h-a.akamaihd.net/hphotos-ak-prn1/s720x720/555928_584450548249983_203037803_n.jpg");
				FileTransferSessionInformationobj.put("originatorAddress", "tel:"+SplashActivity.userId);
				FileTransferSessionInformationobj.put("receiverAddress", destinationUri);
				FileTransferSessionInformationobj.put("fileInformation",FileInformation);
				
				
	      
				
	        //	FileEntity file = new FileEntity(newFile, "image/jpeg");
	        	String FileTransferSessionInformation="{\"fileTransferSessionInformation\":"+FileTransferSessionInformationobj.toString()+"}";
	        	if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient" ,"JSON DATA   "+ FileTransferSessionInformation);
	        	StringEntity requestData=new StringEntity(FileTransferSessionInformation);
	        	String url = ServiceURL.getfileTransferURL(SplashActivity.userId);
	        //	ByteArrayInputStream bytes= new ByteArrayInputStream(FileUtils.readFileToByteArray(newFile));
	        	
	        	FileInputStream fileStream = new FileInputStream(newFile);
	        	
	        	BufferedInputStream bytes = new BufferedInputStream(fileStream);
	        
	        /*	RequestParams params = new RequestParams();
	        	
	        	params.put("root-fields", FileTransferSessionInformation);
	        	params.put("attachments", bytes,"test.jpg");*/
	        	//params.put("attachments",newFile);
	        	MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	        	multipartEntity.addPart("root-fields", new StringBody(FileTransferSessionInformation));
	        	multipartEntity.addPart("attachments", new FileBody(newFile));
	     
	        	
	        	  HttpPost httppost = new HttpPost(url);
	        	  httppost.setEntity(multipartEntity);
	        	  HttpResponse response = mHttpClient.execute(httppost);
	        	  if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","response : "+response.toString());
	        	/*client.post(this.getApplication().getApplicationContext(),
		        		url, multipartEntity,"application/json",new RCSJsonHttpResponseHandler() {
	        		
	        		  public void onSuccess(JSONObject response, int statusCode) {
	        			  if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("SplashActivity", "fileTransfer::success = "+response.toString()+" statusCode="+statusCode);
	        			  
	        		  }
	        	});*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         long totalSize = 0;
	         if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","In Do in BG");
	         return totalSize;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	       
	     }

	     protected void onPostExecute(Long result) {
	         
	     }
	 }

	public static void refreshContactStatus(String str, String value) {
		
		
			statusMap.put(str, value);
		
		if(chatcontactListView!=null){
			changeStateHandler.sendEmptyMessage(0);
			
		}
	}
	
	public void startGroupChat(){
		if(!gruopchatinitiated){
			
		gruopchatinitiated =true;
		JSONObject groupchatSubscription=new JSONObject();
		JSONObject participants=new JSONObject();
		JSONArray participantList=new JSONArray();
		try {
			
			
			participants.put("address", destinationUri);
			participantList.put(0, participants);
			groupchatSubscription.put("participant", participantList);
			groupchatSubscription.put("subject", "Lets Chat");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String jsonData="{\"groupChatSessionInformation\":"+groupchatSubscription.toString()+"}";
		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","JSON DATA :"+jsonData);
		 /* AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
		AsyncHttpClient client = MainActivity.getAsyncHttpClient();
	        
	        final String url=ServiceURL.createGroupChatURL(SplashActivity.userId);
	        try {
				StringEntity requestData=new StringEntity(jsonData);
		        
		        client.post(this.getApplication().getApplicationContext(),
		        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
		        	@Override
		            public void onSuccess(JSONObject response, int statusCode) {
		        		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "startGroupChat::success = "+response.toString()+" statusCode="+statusCode);
		        		
		        	}
		        });
			} catch (UnsupportedEncodingException e) { }
	        
		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","startGroupChat initiated");
		}
		MainActivity.chatCordinator = true;
		Intent intent = new Intent(ChatSessionActivity.this, ListNetworkContacts.class);
		intent.putParcelableArrayListExtra(ALL_CONTACTS, retrievedAllContacts);
        startActivityForResult(intent, 0);
       
	}
	private void sendExitMessage(){
		sendGroupChat("Group chat terminated".trim(),null);
	}
	public void exitGroupChat() {
	
	//	sendExitMessage();
		
		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","exitGroupChat Session id "+sessionId);
		grpChatended = true;
		final String deleteurl=ServiceURL.createGroupChatExitURL(SplashActivity.userId, sessionId);
        
     /*   AsyncHttpClient client = new AsyncHttpClient();
        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
        client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
		AsyncHttpClient client = MainActivity.getAsyncHttpClient();
        
        RCSJsonHttpResponseHandler deleterequestHandler=new RCSJsonHttpResponseHandler() {
        	public void onSuccess(String response, int statusCode) {
        		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "end grp chat "+statusCode+" "+response);
        		if (statusCode==204) {
        			Toast.makeText(_instance, "Chat session ended", Toast.LENGTH_SHORT).show();
        			
					finish();
        		}
        	}
        	public void onFailure(Throwable error, JSONObject response, int statusCode) {
        		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "Failure response for deleteing grp chat "+statusCode+" "+(response!=null?response.toString():null));
        		Toast.makeText(_instance, "Chat session ended", Toast.LENGTH_SHORT).show();
				finish();
				
        	}
        };
        client.delete(deleteurl, deleterequestHandler);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if(data != null && data.hasExtra(FileDialog.RESULT_PATH)){
			String filePath= data.getStringExtra(FileDialog.RESULT_PATH);
		//	String FileDesc = data.getStringExtra(FileDialog.FILE_DESCRIPTOR);
			sendFile(filePath,"new desc");
		 }
	
		 
		 if(data != null && data.hasExtra("selectedContact"))
		 {
			  final ContactExtended selectedContact=data.getParcelableExtra("selectedContact");
			 final String ContactId = data.getStringExtra("ContactURI");
			 
			  JSONObject participant=new JSONObject();
			  try {
				participant.put("address", ContactId);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  String jsonData="{\"participantInformation\":"+participant.toString()+"}";
			  String url = ServiceURL.createGroupChatAddContactURL(SplashActivity.userId,ChatSessionActivity.sessionId);
			  if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","session id : "+ChatSessionActivity.sessionId);
		/*	  AsyncHttpClient client = new AsyncHttpClient();
			    AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
			    client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
				AsyncHttpClient client = MainActivity.getAsyncHttpClient();
			    
			    try {
					StringEntity requestData=new StringEntity(jsonData);
			        
			        client.post(this.getApplication().getApplicationContext(),
			        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
			        	@Override
			            public void onSuccess(JSONObject response, int statusCode) {
			        		retrievedChatContacts.add(selectedContact);
							  Chatcradapter.notifyDataSetChanged();
							 
			        	}
			        });
				} catch (UnsupportedEncodingException e) { }
			  if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","onActivityResult"+selectedContact.getDisplayName());
		 }
	 }
	 
	 
			/*if(data != null && data.hasExtra("Order"))
			  //  Toast.makeText(this, data.getStringExtra("Order") + " ordered.", Toast.LENGTH_LONG).show();
			else
			  //  Toast.makeText(this, "Nothing ordered!", Toast.LENGTH_LONG).show();
		    } */
	 
	/*
	 * invoked when the user presses the button to send a message
	 */
	public void sendMessageClicked(View view) {
		EditText messageInputBox=(EditText) findViewById(R.id.message_input_box);
		final Editable text=messageInputBox.getText();
		Thread t = new Thread(){
		    public void run(){
				String trimmed=text.toString().trim();
				
				if (trimmed.length()>0) {
					ChatMessage sent=new ChatMessage();
					sent.setMessageText(trimmed);
					sent.setMessageDirection(ChatMessage.MESSAGE_SENT);
					sent.setMessageTime(Utils.getNowAsDisplayString());	
					sent.setViewed(true);
					sent.setStatus(ChatMessage.MESSAGE_STATUS_PENDING);
					sent.setContactUri(destinationUri);
					contactState.storeMessage(sent);
					/*if(skypeCheckbox.isChecked())
						trimmed="$skype"+trimmed;
					else if(gmailCheckBox.isChecked())
						trimmed="$gtalk"+trimmed;*/
				//	if(sessionId==null ||sessionId.equalsIgnoreCase("adhoc"))
					if(retrievedChatContacts.size()==1)
						sendAdhocMessage(trimmed, sent);
					else
						sendGroupChat(trimmed,sent);
					
					messageHandler.sendEmptyMessage(0);
				}
		    }
		};
		t.start();
		messageInputBox.setText("");
	}
	
	public static void setSessionId(String sessionid){
		ChatSessionActivity.sessionId = sessionid;
	}
	public static boolean refreshGroupChatMessageList(String contactUri, String recipient, ChatMessage chatMessage) {
		boolean viewed=false;
	//	if (recipient!=null && recipient.equals(SplashActivity.userId)) {
			Message newChatMessage=new Message();
			Bundle msgBundle=new Bundle();
			msgBundle.putParcelable(RECEIVED_MESSAGE, chatMessage);
			newChatMessage.setData(msgBundle);
			messageHandler.sendMessage(newChatMessage);
			viewed=viewIsVisible;
	//	}
	return viewed;
	}
	/*
	 * invoked when the list of messages needs to be restored from saved state
	 */
	public static boolean refreshMessageList(String contactUri, String recipient, ChatMessage chatMessage) {
		boolean viewed=false;
		if (recipient!=null && recipient.equals(SplashActivity.userId)) {
			Message newChatMessage=new Message();
			Bundle msgBundle=new Bundle();
			msgBundle.putParcelable(RECEIVED_MESSAGE, chatMessage);
			newChatMessage.setData(msgBundle);
			messageHandler.sendMessage(newChatMessage);
			viewed=viewIsVisible;
		}
		return viewed;
	}
	
	private void sendGroupChat(String message, ChatMessage chatMessage) {
		Log.d("RCSClient", "sendGroupChat enter : "+message);
		try {
		//	ContactStateManager.registerOutgoingMessage(chatMessage);
		/*	AsyncHttpClient client = new AsyncHttpClient();
			AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
			client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();
			JSONObject chatMessageJSON=new JSONObject();
			
			chatMessageJSON.put("reportRequest", "Sent");
			chatMessageJSON.put("text", message);
			String jsonData="{\"chatMessage\":"+chatMessageJSON.toString()+"}";
			StringEntity requestData=new StringEntity(jsonData);
			String url= ServiceURL.getGropuChatURL(SplashActivity.userId, ChatSessionActivity.sessionId);
			
			
			 client.post(_instance.getApplication().getApplicationContext(),
		        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
		        	@Override
		            public void onSuccess(JSONObject response, int errorCode) {
		        		if(SplashActivity.showLog)
		        			if(SplashActivity.showLog)
		        			Log.d("RCSClient", "sendGroupChat::success = "+response.toString()+" errorCode="+errorCode);
		        			
		        			
		        		
		        	}
		        });
			
		} catch (Exception e) {
			Log.d("RCSClient", "sendGroupChat failue ");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * sends a message in ad-hoc mode
	 */
	private void sendAdhocMessage(String message, ChatMessage chatMessage) {
		try {
			
			
			final String messageInternalId=chatMessage.getMessageInternalId();
			
	        ContactStateManager.registerOutgoingMessage(chatMessage);

			JSONObject chatMessageJSON=new JSONObject();
			
			chatMessageJSON.put("reportRequest", "Sent"); // possible status values are "Sent,Delivered,Displayed,Failed"
			
			
	    /*    AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();
	        
	        try {
				
				
				
				
				
					
					chatMessageJSON.put("text", message);
					
					String jsonData="{\"chatMessage\":"+chatMessageJSON.toString()+"}";
					StringEntity requestData=new StringEntity(jsonData);
					
					
					String url=null;
					
					
					
						url=ServiceURL.sendAdhocIMMessageURL(SplashActivity.userId,destinationUri);
					
					
					if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient","URL : "+url);
		        client.post(_instance.getApplication().getApplicationContext(),
		        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
		        	@Override
		            public void onSuccess(JSONObject response, int errorCode) {
		        		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "sendAdhocMessage::success = "+response.toString()+" errorCode="+errorCode);

		        		JSONObject resourceReference=Utils.getJSONObject(response, "resourceReference");
		        		String resourceURL=Utils.getJSONStringElement(resourceReference, "resourceURL");
	        			String messageId=Utils.getMessageIdFromResourceURL(resourceURL, destinationUri);
	        			if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "messageId="+messageId+" resourceURL="+resourceURL);
		        		
		        		if (errorCode==201) {
							ContactStateManager.setMessageIdForSentMessage(messageInternalId, messageId, resourceURL);
							ContactStateManager.updateStatusFor(messageId, ChatMessage.MESSAGE_STATUS_SENT);
							messageHandler.sendEmptyMessage(0);
		        		}
		        	}
		        });
			 
		}catch (UnsupportedEncodingException e) { }

		} catch (JSONException e1) {
		}
		
	}
	

	/*
	 * background thread to deal with 'isComposing' indicator
	 */
	public void run() {
		long lastSent=0;
		
		while (running) {
			try {
				Thread.sleep (1000);
								
				if (ContactStateManager.haveSentMessageTo(destinationUri) && running) {
					EditText messageInputBox=(EditText) findViewById(R.id.message_input_box);
					Editable text=messageInputBox.getText();
					String trimmed=text.toString().trim();
					if (trimmed.length()>0) {
						long now=System.currentTimeMillis();
						if ((now-lastSent)>=(COMPOSINGUPDATEFREQUENCYSECONDS*1000)) {
					        sendComposingIndicator(SplashActivity.userId, destinationUri,"active",COMPOSINGUPDATEFREQUENCYSECONDS,new java.util.Date(), "text/plain");
					        lastSent=now;
						}
					    sentComposing=true;
					} else if (sentComposing) {
						clearComposingIndicator();
						lastSent=0;
					}
				}	
			} catch (InterruptedException ie) {}
		}
	}
	
	/*
	 * send the isComposing indicator (generic)
	 */
	private void sendComposingIndicator(String userId, String contactId, String state, int refresh, java.util.Date lastActive, String contentType) {
		final String pingUrl=ServiceURL.getSendIsComposingAutomaticContactURL(SplashActivity.userId, contactId);
		JSONObject isComposing=new JSONObject();
		try {
	     /*   AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();

	        isComposing.put("state", state);
			isComposing.put("refresh", refresh);
			isComposing.put("contentType", contentType);
			//isComposing.put("lastActive", lastActive);
			String jsonData="{\"isComposing\":"+isComposing.toString()+"}";
	        try {
				StringEntity requestData=new StringEntity(jsonData);
		        
		        client.post(_instance.getApplication().getApplicationContext(),
		        		pingUrl, requestData, "application/json", new RCSJsonHttpResponseHandler() {
		        	@Override
		            public void onSuccess(JSONObject response, int statusCode) {
		        		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "sendComposingIndicator::response = "+(response!=null?response.toString():null)+" statusCode="+statusCode);
		        	}
		        });
	        } catch (UnsupportedEncodingException e) { }
		} catch (JSONException e) { }
	}

	/*
	 * clear the isComposing indicator
	 */
	private void clearComposingIndicator() {
        sendComposingIndicator(SplashActivity.userId, destinationUri,"idle",15,new java.util.Date(), "text/plain");
        sentComposing=false;
	}

	/*
	 * called on startup of the activity to set the state of the contact
	 */
	public static void setContactState(ContactState contactState) {
		ChatSessionActivity.contactState=contactState;
	}
	
	/*
	 * called from the MainActivity when a message (send) status has been updated
	 */
	public static void updateStatus(String messageId, String status) {
		ContactStateManager.updateStatusFor(messageId, status);
		messageHandler.sendEmptyMessage(0);
	}

	/* 
	 * called from the MainActivity when the isComposing indicator has been received
	 */
	public static void updateComposingIndicator(String state) {
		int code=(state!=null && state.equalsIgnoreCase("active"))?1:0;
		composingIndicatorHandler.sendEmptyMessage(code);
	}
	
	  public void deleteContact(String contactUri) {
	        final String deleteurl=ServiceURL.getDeleteContactURL(SplashActivity.userId, contactUri);
	        
	      /*  AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
	    	AsyncHttpClient client = MainActivity.getAsyncHttpClient();
	        
	        RCSJsonHttpResponseHandler deleterequestHandler=new RCSJsonHttpResponseHandler() {
	        	public void onSuccess(String response, int statusCode) {
	        		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "Delete response is "+statusCode+" "+response);
	        		if (statusCode==204) {
	        			Toast.makeText(_instance, "Account deleted successfully", Toast.LENGTH_SHORT).show();
						finish();
	        		}
	        	}
	        	public void onFailure(Throwable error, JSONObject response, int statusCode) {
	        		if(SplashActivity.showLog)
        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "Failure response is "+statusCode+" "+(response!=null?response.toString():null));
					Message msg=new Message();
					msg.what=statusCode;
					msg.obj="contact could not be deleted";
					
	        	}
	        };
	        client.delete(deleteurl, deleterequestHandler);
	    }
}
