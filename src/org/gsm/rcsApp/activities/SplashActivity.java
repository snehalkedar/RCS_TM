package org.gsm.rcsApp.activities;

import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.Header;
import org.gsm.RCSDemo.R;
import org.gsm.rcsApp.ServiceURL;
import org.gsm.rcsApp.misc.RCSJsonHttpResponseHandler;
import org.gsm.rcsApp.misc.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;

public class SplashActivity extends Activity {

	public static String userId=null;
	public static String tableName=null; 
//	public static String appCredentialUsername="NOUSER";
	//public static final String appCredentialPassword="3Kvm4\"DD";
	public static String defaultPassword=null;//"tY%u8%an";
	public static String PrjUsrName = null;
	
	public static String PrjUsrName1 = "5dda16af865448c9849c68c6bf41205c";
	public static String PrjUsrName2 = "ec65f8ac6ad20dbc13457df8356d12d3";
	public static String PrjUsrName3 = "b7bb5037c663fc0d896347e63be6bd2c";
	public static String PrjUsrName4 = "bdd1af6478b22dada943601b211e652f";
	public static String appCredentialPassword =null;// "tY%u8%an";
	public static boolean showLog = true; 
	static SplashActivity _instance=null;
	public Spinner projectSpinner = null;
	public static String notificationChannelURL=null;
	public static String notificationChannelResourceURL=null;
	
	public static ArrayList<String> notificationSubscriptions=new ArrayList<String>();  
	/**
	 * @uml.property  name="splashUsernameInput"
	 * @uml.associationEnd  
	 */
	private EditText splashUsernameInput = null;
	/**
	 * @uml.property  name="splashPasswordInput"
	 * @uml.associationEnd  
	 */
	private EditText splashPasswordInput=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        _instance=this;
        splashUsernameInput=(EditText) findViewById(R.id.splashUsernameInput);
		splashPasswordInput=(EditText) findViewById(R.id.splashPasswordInput);
     }
    
    public void onStart() {
		super.onStart();
	
		final TextView splashStatusIndicator=(TextView) findViewById(R.id.splashStatusIndicator);
		splashStatusIndicator.setVisibility(View.INVISIBLE);
		splashStatusIndicator.setText("enter username / password");		
		
		 splashPasswordInput.setEnabled(true);
		 splashUsernameInput.setEnabled(true);
        /*AsyncHttpClient client = new AsyncHttpClient();
        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
        client.setBasicAuth(PrjUsrName, SplashActivity.appCredentialPassword, authscope);*/
		 
		/* projectSpinner = (Spinner) findViewById(R.id.projects_spinner);
		 ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.projects_array, android.R.layout.simple_spinner_item);
		 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 projectSpinner.setAdapter(adapter);*/
		
		AsyncHttpClient client = MainActivity.getAsyncHttpClient();

        
		splashPasswordInput.setText("");

		if (userId!=null) {
			/*
			 * De-register the previously logged in user
			 */

	      
	        final String url=ServiceURL.unregisterURL(userId);
	        
	        if(SplashActivity.showLog)
        		Log.d("RCSClient", "Unregistering user");
	        
	        client.delete(url, new RCSJsonHttpResponseHandler() {
	        	@Override
				public void onSuccess(String response, int statusCode) {
	        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "unregister::success status="+statusCode);
				}
	        });
		
			/*
			 * Clear previous notification subscriptions  
			 */
			if (notificationSubscriptions.size()>0) {
				for (final String durl:notificationSubscriptions) {
			        client.delete(durl, new RCSJsonHttpResponseHandler() {
			        	@Override
						public void onSuccess(String response, int statusCode) {
							if(SplashActivity.showLog)
        		Log.d("RCSClient", "deleted subscription status="+statusCode+" response="+response);
						}
			        });
				}
				notificationSubscriptions.clear();
			}
			if (notificationChannelResourceURL!=null) {
				final String durl=notificationChannelResourceURL;
		        client.delete(durl, new RCSJsonHttpResponseHandler() {
		        	@Override
					public void onSuccess(String response, int statusCode) {
						if(SplashActivity.showLog)
        		Log.d("RCSClient", "deleted notification channel status="+statusCode+" response="+response);
					}
		        });
		        notificationChannelResourceURL=null;
			}
	        userId=null;
		}
		
		MainActivity.stopMainActivity();
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(SplashActivity.showLog)
        		Log.d("RCSClient", "onDestroy splash");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(SplashActivity.showLog)
        		Log.d("RCSClient", "onStop splash");
	}

	public void proceedToMain(View view) {
	
		//final EditText splashUsernameInput=(EditText) findViewById(R.id.splashUsernameInput);
		//final EditText splashPasswordInput=(EditText) findViewById(R.id.splashPasswordInput);
		splashPasswordInput.setEnabled(false);
		splashUsernameInput.setEnabled(false);
		final String username=splashUsernameInput.getText().toString();
		
		
		if(username.contains("9923051"))
			PrjUsrName = PrjUsrName1;
		else if(username.contains("942202648"))
			PrjUsrName = PrjUsrName2;
		else if(username.contains("444440000"))
			PrjUsrName = PrjUsrName3;
		else if(username.contains("88776655"))
			PrjUsrName = PrjUsrName4;
			
			
		
		
	/*	switch(projectSpinner.getSelectedItemPosition()){
			case 0:
				PrjUsrName = PrjUsrName1;
				break;
			case 1 :
				PrjUsrName = PrjUsrName2;
				break;
			case 2 :
				PrjUsrName = PrjUsrName3;
				break;
		}*/
		
		Log.d("RCSClient", "PrjUsrName  :  "+PrjUsrName);
		final TextView splashStatusIndicator=(TextView) findViewById(R.id.splashStatusIndicator);
		
		if(!MainActivity.checkNetworkConnectivity(this)){
			splashStatusIndicator.setVisibility(View.VISIBLE);
			splashStatusIndicator.setText("No Network available");	
			return;
		}
    
		
	
 
	
		
		@SuppressWarnings("unused")
		final String password=(String)splashPasswordInput.getText().toString();
		if(TextUtils.isEmpty(password))
			SplashActivity.appCredentialPassword = SplashActivity.defaultPassword;
		else
			SplashActivity.appCredentialPassword = password;
		splashStatusIndicator.setVisibility(View.INVISIBLE);
		
		if (username!=null && username.trim().length()>0) {
  
	       /* AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(PrjUsrName, SplashActivity.appCredentialPassword, authscope);*/
			
			
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();
	        Log.d("RCSClient","password : "+SplashActivity.appCredentialPassword);
	        final String url=ServiceURL.registerURL(username);
	    
	     
	        client.post(url, new RCSJsonHttpResponseHandler() {
		        boolean successReceived=false;

	        	@Override
	            public void onSuccess(String response, int responseCode) {
	        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "proceedToMain::success status="+responseCode);
	                if (responseCode==204) {
		            	userId=username;
		            	tableName="tb"+username.substring(1);
		            	
		            /*	SQLiteDatabase db=null;
		            	db = openOrCreateDatabase( "RCSContacts.db"        , SQLiteDatabase.CREATE_IF_NECESSARY 
        			    		, null          );
        			    final String CREATE_TABLE_CONTAIN = "CREATE TABLE IF NOT EXISTS "+SplashActivity.tableName+" ("
        		                + "ID INTEGER primary key AUTOINCREMENT,"
        		             		               
        		                + "RCSContactID TEXT," + 
        		                "RCSContactInfo TEXT);";
        		        db.execSQL(CREATE_TABLE_CONTAIN);
        		        db.close();*/
        		        
        		  //      ContactHelper.CreateJSONFromFIle(_instance.getApplication().getApplicationContext());
		            	registerForNotifications();
		             
		            //	Intent intent = new Intent(_instance, ContactHelper.class);
		            	successReceived=true;
		             
	                } else if (responseCode==401) {
		    			splashStatusIndicator.setVisibility(View.VISIBLE);
		    			splashStatusIndicator.setText("invalid username / password");			            	
		            	successReceived=true;
	                }
	            }
	
				@Override
	            public void onStart() {
	                // Initiated the request
	    			splashStatusIndicator.setVisibility(View.VISIBLE);
	    			splashStatusIndicator.setText("sending login request");		
	            }
	        
	            @Override
	            public void onFailure(Throwable e, String response) {
	                // Response failed :(
	    			splashStatusIndicator.setVisibility(View.VISIBLE);
	    			splashStatusIndicator.setText("login request failed");
	    			Log.d("RCSClient","Response "+response);
	    			Log.d("RCSClient",e.toString());
	    			splashPasswordInput.setEnabled(true);
	    			splashUsernameInput.setEnabled(true);
	            }
	
	            @Override
	            public void onFinish() {
	                // Completed the request (either success or failure)
	            	if (!successReceived) {
		    			splashStatusIndicator.setVisibility(View.VISIBLE);
		    			splashStatusIndicator.setText("login request finished - unknown failure");
		    			splashUsernameInput.setEnabled(true);
		    			splashPasswordInput.setEnabled(true);
		    			
		    			
	            	}
	            }
	        });
		} else {
			splashStatusIndicator.setVisibility(View.VISIBLE);
			splashStatusIndicator.setText("enter username / password");		
		}

    }
    
    private void registerForNotifications() {
      /*  AsyncHttpClient client = new AsyncHttpClient();
        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
        client.setBasicAuth(SplashActivity.PrjUsrName, SplashActivity.appCredentialPassword, authscope);*/
    	//AsyncHttpClient client = MainActivity.getAsyncHttpClient();
    	 
        final String url=ServiceURL.createNotificationChannelURL(userId);
        
       /* String jsonData="{\"notificationChannel\": { \"channelData\": { \"maxNotifications\": 100 }, \"applicationTag\": \"GSMA RCS Demo\", "+
        				"\"channelLifetime\": 0, \"channelType\": \"LongPolling\"}}";*/
        String correlator=UUID.randomUUID().toString();
        
        String jsonData="{\"notificationChannel\": {\"applicationTag\": \"myApp\", \"channelData\": {\"maxNotifications\": \"20\", \"type\": \"LongPollingData\"}, \"channelLifetime\": \"20\", \"channelType\": \"LongPolling\", \"clientCorrelator\": \""+correlator+"\"}}";
        
        try {
        	 AsyncHttpClient client = new AsyncHttpClient();
             String auth = android.util.Base64.encodeToString((SplashActivity.PrjUsrName+":"+SplashActivity.appCredentialPassword).getBytes("UTF-8"), android.util.Base64.NO_WRAP);
             client.addHeader("Authorization", "Basic "+ auth);
 			client.addHeader("Accept", "application/json");
			StringEntity requestData=new StringEntity(jsonData);
	        
	        client.post(_instance.getApplication().getApplicationContext(),
	        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
	        	@Override
	            public void onSuccess(JSONObject response, int statusCode) {
	        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "registerForNotifications::success = "+response.toString()+" statusCode="+statusCode);
	        		
	        		if (statusCode==201) {
	        			JSONObject notificationChannel=Utils.getJSONObject(response, "notificationChannel");
	        			String callbackURL=Utils.getJSONStringElement(notificationChannel, "callbackURL");
	        			notificationChannelResourceURL=Utils.getJSONStringElement(notificationChannel, "resourceURL");
	        			JSONObject channelData=Utils.getJSONObject(notificationChannel, "channelData");
	        			notificationChannelURL=channelData!=null?Utils.getJSONStringElement(channelData, "channelURL"):null;
	        			if(SplashActivity.showLog)
        		Log.d("RCSClient", "callbackURL = "+callbackURL);
	        			if(SplashActivity.showLog)
        		Log.d("RCSClient", "resourceURL = "+notificationChannelResourceURL);
	        			if(SplashActivity.showLog)
        		Log.d("RCSClient", "channelURL = "+notificationChannelURL);
	        			
	        			subscribeToAddressBookNotifications(callbackURL);
	        			subscribeToSessionNotifications(callbackURL);
	        			subscribeToChatNotifications(callbackURL);
	        			subcribeToFileTransferNotifications(callbackURL);
	        		}
	        	}


	        });
		} catch (UnsupportedEncodingException e) { }

	}
    private void subcribeToFileTransferNotifications(String callbackURL){
	
    	try {
			JSONObject callbackReference=new JSONObject();
			JSONObject fileTransferSubscription = new JSONObject();
			callbackReference.put("callbackData", userId);
			callbackReference.put("notifyURL", callbackURL);
			fileTransferSubscription.put("callbackReference", callbackReference);
			String jsonData="{\"fileTransferSubscription\":"+fileTransferSubscription.toString()+"}";
			/*AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.PrjUsrName, SplashActivity.appCredentialPassword, authscope);*/
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();
			String url =ServiceURL.createFileTransferSubscriptionURL(userId);
			 try {
					StringEntity requestData=new StringEntity(jsonData);
			    
			        client.post(_instance.getApplication().getApplicationContext(),
			        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
			        	@Override
			            public void onSuccess(JSONObject response, int statusCode) {
			        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "File transfer subscription successfull");
			        	}
			        });
			 }catch (UnsupportedEncodingException e) { }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
	private void subscribeToAddressBookNotifications(String callbackURL) {
		try {
			JSONObject abChangesSubscription=new JSONObject();
			JSONObject callbackReference=new JSONObject();
			callbackReference.put("callbackData", userId);
			callbackReference.put("notifyURL", callbackURL);
			abChangesSubscription.put("callbackReference", callbackReference);
			abChangesSubscription.put("duration", (int) 0);
			String jsonData="{\"abChangesSubscription\":"+abChangesSubscription.toString()+"}";
			if(SplashActivity.showLog)
        		Log.d("RCSClient", "Subscription request data = "+jsonData);
			
	       /* AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.PrjUsrName, SplashActivity.appCredentialPassword, authscope);*/
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();
	        
	        final String url=ServiceURL.createAddressBookChangeSubscriptionURL(userId);
	        try {
				StringEntity requestData=new StringEntity(jsonData);
		        
		        client.post(_instance.getApplication().getApplicationContext(),
		        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
		        	@Override
		            public void onSuccess(JSONObject response, int statusCode) {
		        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "subscribeToAddressBookNotifications::success = "+response.toString()+" statusCode="+statusCode);
		        		if (statusCode==201) {
			        		String resourceURL=Utils.getResourceURL(Utils.getJSONObject(response, "abChangesSubscription"));
			        		if (resourceURL!=null) notificationSubscriptions.add(resourceURL);
		        		}
		        	}
		        });
			} catch (UnsupportedEncodingException e) { }

		} catch (JSONException e) {
		}
		
	}

	private void subscribeToSessionNotifications(String callbackURL) {
		try {
			JSONObject sessionSubscription=new JSONObject();
			JSONObject callbackReference=new JSONObject();
			callbackReference.put("callbackData", userId);
			callbackReference.put("notifyURL", callbackURL);
			sessionSubscription.put("callbackReference", callbackReference);
			sessionSubscription.put("duration", (int) 0);
			String jsonData="{\"sessionSubscription\":"+sessionSubscription.toString()+"}";
			if(SplashActivity.showLog)
        		Log.d("RCSClient", "Subscription request data = "+jsonData);
			
	   /*    AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.PrjUsrName, SplashActivity.appCredentialPassword, authscope);*/
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();
	        
	        final String url=ServiceURL.createSessionChangeSubscriptionURL(userId);
	        try {
				StringEntity requestData=new StringEntity(jsonData);
		        
		        client.post(_instance.getApplication().getApplicationContext(),
		        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
		        	@Override
		            public void onSuccess(JSONObject response, int statusCode) {
		        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "subscribeToSessionNotifications::success = "+response.toString()+" statusCode="+statusCode);
		        		if (statusCode==201) {
			        		String resourceURL=Utils.getResourceURL(Utils.getJSONObject(response, "sessionSubscription"));
			        		if (resourceURL!=null) notificationSubscriptions.add(resourceURL);
			        		
		        		}
		        	}
		        });
			} catch (UnsupportedEncodingException e) { }

		} catch (JSONException e) {
		}
		
	}

	private void subscribeToChatNotifications(String callbackURL) {
		try {
			JSONObject chatSubscription=new JSONObject();
			JSONObject callbackReference=new JSONObject();
			callbackReference.put("callbackData", userId);
			callbackReference.put("notifyURL", callbackURL);
			chatSubscription.put("callbackReference", callbackReference);
			chatSubscription.put("duration", (int) 0);
			String jsonData="{\"chatNotificationSubscription\":"+chatSubscription.toString()+"}";
			if(SplashActivity.showLog)
        		Log.d("RCSClient", "Subscription request data = "+jsonData);
			
	  /*      AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.PrjUsrName, SplashActivity.appCredentialPassword, authscope);*/
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();
	        
	        final String url=ServiceURL.createChatSubscriptionURL(userId);
	        try {
				StringEntity requestData=new StringEntity(jsonData);
		        
		        client.post(_instance.getApplication().getApplicationContext(),
		        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
		        	@Override
		            public void onSuccess(JSONObject response, int statusCode) {
		        		if(SplashActivity.showLog)
		        			Log.d("RCSClient", "subscribeToChatNotifications::success = "+response.toString()+" statusCode="+statusCode);
		        		if (statusCode==201) {
			        		String resourceURL=Utils.getResourceURL(Utils.getJSONObject(response, "chatNotificationSubscription"));
			        		if (resourceURL!=null) notificationSubscriptions.add(resourceURL);
			        		Intent intent = new Intent(_instance, MainActivity.class);
			            	startActivity(intent);
			        		
		        		}
		        	}
		        });
			} catch (UnsupportedEncodingException e) { }

		} catch (JSONException e) {
		}
		
	}
	
	
	
}
