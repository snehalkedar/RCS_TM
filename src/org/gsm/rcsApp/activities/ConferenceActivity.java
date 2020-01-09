package org.gsm.rcsApp.activities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.auth.AuthScope;
import org.apache.http.entity.StringEntity;
import org.gsm.RCSDemo.R;
import org.gsm.rcsApp.ServiceURL;
import org.gsm.rcsApp.RCS.ChatMessage;
import org.gsm.rcsApp.RCS.Contact;
import org.gsm.rcsApp.RCS.ContactExtended;
import org.gsm.rcsApp.RCS.ContactState;
import org.gsm.rcsApp.RCS.ContactStateManager;
import org.gsm.rcsApp.adapters.ContactRowAdapter;
import org.gsm.rcsApp.adapters.MessageRowAdapter;
import org.gsm.rcsApp.adapters.verySimpleAdapter;
import org.gsm.rcsApp.misc.RCSJsonHttpResponseHandler;
import org.gsm.rcsApp.misc.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Gallery;

public class ConferenceActivity extends Activity {
	static verySimpleAdapter Confcradapter=null;
	private static ArrayList<ContactExtended> retrievedConfContacts=new ArrayList<ContactExtended>();
	static Gallery confcontactListView=null;
	/**
	 * @uml.property  name="sessionId"
	 */
	String sessionId=null;
	private static ContactState contactState=null;
	static ListView messageListView=null;
	static MessageRowAdapter mradapter=null;
	private static final String RECEIVED_MESSAGE="receivedMessage";
	private static final String UPDATE_CONATACT="updateContact";
	private static Handler messageHandler = null;
	private static boolean viewIsVisible=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.conference);

		Intent intent = getIntent();
		sessionId = intent.getStringExtra("sessionId");
		//1. Retirve all participants and put in adapter
		//2. create a handler
		//3. send messages
		contactState = new ContactState(sessionId);
		messageListView=(ListView) findViewById(R.id.confmessageList);
		mradapter=new MessageRowAdapter(this, contactState.getMessageBuffer());
		messageListView.setAdapter(mradapter);


		//retrievedConfContacts =getConferenceParticipant();//intent.getParcelableArrayListExtra(MainActivity.ALL_CONTACTS);
		Confcradapter=new verySimpleAdapter(this, retrievedConfContacts);
		confcontactListView =(Gallery)findViewById(R.id.gallery12);
		confcontactListView.setAdapter(Confcradapter);
		confcontactListView.setFocusable(true);
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
	    				if (data.containsKey(UPDATE_CONATACT)) {
	    					ArrayList<ContactExtended> contacts=(ArrayList<ContactExtended>) data.get(UPDATE_CONATACT);
	    					if (contacts.size()!=0) {
	    						for(int i=0;i<contacts.size();i++)
	    							retrievedConfContacts.clear();
	    						retrievedConfContacts.addAll(contacts);
	    						Confcradapter.notifyDataSetChanged();
	    					}
	    				}
	    			}
					messageListView.smoothScrollToPosition(contactState.getMessageBuffer()!=null?(contactState.getMessageBuffer().size() - 1):0);
	    			mradapter.notifyDataSetChanged();
	    			messageListView.refreshDrawableState();
	    		}
	    	};

	} 

	private ArrayList<Contact> getConferenceParticipant(){
		ArrayList<Contact> retrievedContacts=new ArrayList<Contact>();

		
		/*AsyncHttpClient client = new AsyncHttpClient();
		AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
		client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);        
		String url =ServiceURL.getgroupChatParticipantURL(SplashActivity.userId,sessionId);
		if(SplashActivity.showLog)
        		Log.d("ConfActivity", "getConferenceParticipant = " +url);
		client.get(url, new RCSJsonHttpResponseHandler() {
		        	@Override
		            public void onSuccess(JSONObject response, int statusCode) {
		        		if(SplashActivity.showLog)
        		Log.d("ConfActivity", "participants = "+response.toString()+" statusCode="+statusCode);
		        		
		        		
		        	}
		        });*/


		return retrievedContacts;

	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	public void sendConfMessageClicked(View view) {
		EditText messageInputBox=(EditText) findViewById(R.id.confmessage_input_box);
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
					sent.setContactUri(sessionId);
					contactState.storeMessage(sent);
					/*if(skypeCheckbox.isChecked())
						trimmed="$skype"+trimmed;
					else if(gmailCheckBox.isChecked())
						trimmed="$gtalk"+trimmed;*/
					
						sendGroupChat(trimmed,sent);
					
					messageHandler.sendEmptyMessage(0);
				}
		    }
		};
		t.start();
		messageInputBox.setText("");
	}
private void sendGroupChat(String message, ChatMessage chatMessage) {
		
		try {
			ContactStateManager.registerOutgoingMessage(chatMessage);
		/*	AsyncHttpClient client = new AsyncHttpClient();
			AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
			client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();
			JSONObject chatMessageJSON=new JSONObject();
			
			chatMessageJSON.put("reportRequest", "Sent");
			chatMessageJSON.put("text", message);
			String jsonData="{\"chatMessage\":"+chatMessageJSON.toString()+"}";
			StringEntity requestData=new StringEntity(jsonData);
			String url= ServiceURL.getGropuChatURL(SplashActivity.userId, sessionId);
			
			
			 client.post(this.getApplication().getApplicationContext(),
		        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
		        	@Override
		            public void onSuccess(JSONObject response, int errorCode) {
		        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "sendGroupChat::success = "+response.toString()+" errorCode="+errorCode);

		        	
		        		
		        	}
		        });
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void refreshConfContactList(ArrayList<ContactExtended> contacts){

		Message newChatMessage=new Message();
			Bundle msgBundle=new Bundle();
			msgBundle.putParcelableArrayList(UPDATE_CONATACT, contacts);
			newChatMessage.setData(msgBundle);
			if(messageHandler!=null)
			messageHandler.sendMessage(newChatMessage);
			
		
		
	}
	public static boolean refreshGroupChatMessageList(String contactUri, String recipient, ChatMessage chatMessage) {
		boolean viewed=false;
		//	if (recipient!=null && recipient.equals(SplashActivity.userId)) {

		/*contactState.getMessageBuffer().add(chatMessage);
		messageListView.smoothScrollToPosition(contactState.getMessageBuffer()!=null?(contactState.getMessageBuffer().size() - 1):0);
		mradapter.notifyDataSetChanged();
		messageListView.refreshDrawableState();*/

		Message newChatMessage=new Message();
			Bundle msgBundle=new Bundle();
			msgBundle.putParcelable(RECEIVED_MESSAGE, chatMessage);
			newChatMessage.setData(msgBundle);
			messageHandler.sendMessage(newChatMessage);
			viewed=viewIsVisible;
		//	}
		return viewed;
	}

}
