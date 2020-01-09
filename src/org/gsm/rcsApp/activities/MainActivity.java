package org.gsm.rcsApp.activities;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.jar.JarOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MinimalField;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.gsm.rcsApp.ServiceURL;
import org.gsm.rcsApp.RCS.ChatMessage;
import org.gsm.rcsApp.RCS.ChatSessionManager;
import org.gsm.rcsApp.RCS.Contact;
import org.gsm.rcsApp.RCS.ContactExtended;
import org.gsm.rcsApp.RCS.ContactState;
import org.gsm.rcsApp.RCS.ContactStateManager;
import org.gsm.rcsApp.adapters.ContactRowAdapter;
import org.gsm.rcsApp.misc.RCSJsonHttpResponseHandler;
import org.gsm.rcsApp.misc.Utils;
import org.gsm.rcsApp.misc.Notifications;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceActivity.Header;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.gsm.RCSDemo.R;

public class MainActivity extends Activity implements Runnable {
	public final static String SELECTED_CONTACT = "com.techm.rcsApp.activities.MainActivity.SELECTEDCONTACT";
	public final static String ALL_CONTACTS = "com.techm.rcsApp.activities.MainActivity.ALLCONTACTS";
	public final static String ALL_NOTIFICATION = "com.techm.rcsApp.activities.MainActivity.NOTIFICATIONS";
	
	public static ContactStateManager contactStateManager=new ContactStateManager();
	
    private static ArrayList<Contact> retrievedContacts=new ArrayList<Contact>();
    private static ArrayList<ContactExtended> retrivedExtendedContacts = new ArrayList<ContactExtended>();
    
    private static HashMap<String,Contact> contactMap=new HashMap<String,Contact>();
    private static HashMap<String,ContactExtended> contactExtendedMap = new HashMap<String, ContactExtended>();
    
    public static ChatSessionManager chatSessionCache=new ChatSessionManager();

	static Thread background=null;
	static boolean running=false;
	
	private static HashMap<String, ArrayList<ChatMessage>> messageCache=new HashMap<String, ArrayList<ChatMessage>>();

	static MainActivity _instance=null;
	static ContactRowAdapter cradapter=null;
	static ListView contactListView=null;
    public static boolean chatCordinator=false;
	private static Handler changeStateHandler = null;
	private static Handler contactChangeHandler = null;
	private static Handler showNotificationHandler = null;
	static String currentChatSessionContactUri=null;
	/**
	 * @uml.property  name="currentfocused"
	 * @uml.associationEnd  
	 */
	private ContactExtended currentfocused =null;
	private static final int MAIN_LOOP_DELAY=5000;
	private static final int LONGPOLL_TIMEOUT=30000;
	private final static int FILE_TRANSFER_ACCEPT            = 1;
	private final static int SHOW_NOTIFICATION            = 2;
	private final static int NETWORK_FAILURE = 3;
	/**
	 * @uml.property  name="notificationView"
	 * @uml.associationEnd  
	 */
	private TextView NotificationView = null;
	private static Vibrator v = null;
	  /**
	 * @uml.property  name="myImageList" multiplicity="(0 -1)" dimension="1"
	 */
	int[] myImageList;
	/**
	 * @uml.property  name="notificationObj"
	 * @uml.associationEnd  
	 */
	private JSONObject NotificationObj = null;
	/**
	 * @uml.property  name="gotStatus"
	 */
	private boolean gotStatus =false;
	/**
	 * @uml.property  name="fileSenderName"
	 */
	private String fileSenderName;
	/**
	 * @uml.property  name="sentFile"
	 */
	private String sentFile;
    /**
	 * @uml.property  name="phtoTagMsg"
	 */
    private String phtoTagMsg;
	private static ArrayList<Notifications> Inbox = new ArrayList<Notifications>();
	private static String botContactId = null;
	/**
	 * @uml.property  name="currentNotification"
	 * @uml.associationEnd  
	 */
	private Notifications currentNotification = null;
	/**
	 * @uml.property  name="moreButton"
	 * @uml.associationEnd  
	 */
	private ImageButton moreButton =null;
	 public static Contact getContactFromConatctURI(String URI){
		
		 for(Contact contact:retrievedContacts){
			 if(URI.contains(contact.getContactId()))
				 return contact;
		 }
		 return null;
		 
	 }
	 public static AsyncHttpClient getAsyncHttpClient(){
		/* AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(AuthScope.ANY_HOST, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.PrjUsrName, SplashActivity.appCredentialPassword, authscope);   
	        client.addHeader("Content-Type","application/json");
	        client.addHeader("Accept","application/json");*/
		 AsyncHttpClient client = new AsyncHttpClient();
	        String auth = null;
			try {
				auth = android.util.Base64.encodeToString((SplashActivity.PrjUsrName+":"+SplashActivity.appCredentialPassword).getBytes("UTF-8"), android.util.Base64.NO_WRAP);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     
	       
	        
	        client.addHeader("Authorization", "Basic "+ auth);
			client.addHeader("Accept", "application/json");
			client.addHeader("Content-Type", "application/json");
	        return client;
	 }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
       
        if ( customTitleSupported ) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
            }
		_instance=this;	
		
		  myImageList = new int[]{R.drawable.face, R.drawable.face1,R.drawable.face2,R.drawable.face3,R.drawable.face4,R.drawable.face5,R.drawable.face6,R.drawable.face7,R.drawable.face8};
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    	ContactStateManager.reset();
    	contactStateManager.clearCache();

		contactListView=(ListView) findViewById(R.id.contactList);
		NotificationView=(TextView)findViewById(R.id.notifiationtextView);
		moreButton = (ImageButton)findViewById(R.id.MoreButton);
		moreButton.setVisibility(View.INVISIBLE);
		cradapter=new ContactRowAdapter(this, retrivedExtendedContacts,true);
		contactListView.setAdapter(cradapter);
		contactListView.setFocusable(true);
		
		contactListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub
				if(SplashActivity.showLog)
        		Log.d("RCSClient","onItemSelected"+paramInt);
				currentfocused = retrivedExtendedContacts.get(paramInt);
			}

			public void onNothingSelected(AdapterView<?> paramAdapterView) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	    contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	@SuppressWarnings("rawtypes")
			public void onItemClick(AdapterView parent, View view, int position, long id) {
	    		if(SplashActivity.showLog)
        		Log.d("RCSClient","setOnItemClickListener");
	    		Contact selectedRecord=retrievedContacts.get(position);
	    		
	    		currentChatSessionContactUri=selectedRecord.getContactId();
	    		selectedRecord.setHasNewMessage(false);
	    		
	    		ContactState contactState=contactStateManager.getOrCreateContactState(currentChatSessionContactUri);
				ChatSessionActivity.setContactState(contactState);
				contactState.setNewMessage(false);
	    		
	    		contactStateManager.setChatVisible(currentChatSessionContactUri, true);
				
	    		Intent intent = new Intent(_instance, ChatSessionActivity.class);
	    		intent.putExtra(SELECTED_CONTACT, selectedRecord);
	    		intent.putParcelableArrayListExtra(ALL_CONTACTS, retrievedContacts);
	    		startActivity(intent);
	    	}
	    });
	    
	    contactListView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
	    	@SuppressWarnings("rawtypes")
			public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
	    		Contact selectedRecord=retrievedContacts.get(position);
	    		Intent intent = new Intent(_instance, EditContactActivity.class);
	    		intent.putExtra(SELECTED_CONTACT, selectedRecord);
	    		startActivity(intent);
				return true;
			}
	    	
	    });
	    
	    contactChangeHandler = new Handler() {
    		public void handleMessage(Message msg) {
    			super.handleMessage(msg);
    			refreshContacts();
    		}	    	
	    };

		changeStateHandler = new Handler() {
    		public void handleMessage(Message msg) {
    			super.handleMessage(msg);
    			cradapter.notifyDataSetChanged();
    			contactListView.refreshDrawableState();
    		}
    	};
    	showNotificationHandler = new Handler() {
    			public void handleMessage(Message msg) {
    				super.handleMessage(msg);
    				
    				   
    					   switch (msg.what) {
    					   
    			     case NETWORK_FAILURE:
    						   //Toast.makeText(_instance, "There seems to be problem with the network", Toast.LENGTH_SHORT).show();
    			    	 
    						   break;
    				 case SHOW_NOTIFICATION:
    					 currentNotification = (Notifications) msg.obj;
    					 
    					 NotificationView.setText(currentNotification.getText());
    					 moreButton.setVisibility(View.VISIBLE);
    					
    					 Log.d("RCSClient","current file is : "+currentNotification.getfilePath());
    					 break;
    				 case  FILE_TRANSFER_ACCEPT:
    				
    					 JSONObject notification = (JSONObject)msg.obj;
    					
    					 	
    	    				final JSONObject ftSessionInvitationNotification=Utils.getJSONObject(notification, "ftSessionInvitationNotification");
    						
    	    				fileSenderName = Utils.getJSONStringElement(ftSessionInvitationNotification, "originatorAddress");
    	    				Contact ctemp = getContactFromConatctURI(fileSenderName);
    	    				   if(ctemp!=null)
    	    					   fileSenderName = ctemp.getContactInfo();
    						     
    						      
    						            //Yes button clicked
    						        	 try {
											JSONArray link=Utils.getJSONArray(ftSessionInvitationNotification, "link");
											JSONObject fileinf = Utils.getJSONObject(ftSessionInvitationNotification, "fileInformation");
											JSONObject fileSelector = Utils.getJSONObject(fileinf,"fileSelector");
											if (link!=null && link.length()>0) {
												for (int li=0; li<link.length(); li++) {
													JSONObject litem=link.getJSONObject(li);
													String rel=Utils.getJSONStringElement(litem, "rel");
													String href=Utils.getJSONStringElement(litem, "href");
													String fileName = Utils.getJSONStringElement(fileSelector, "name");
													 sentFile =  Environment.getExternalStorageDirectory()+"/"+fileName;
													if ("ReceiverSessionStatus".equals(rel) && href!=null){
													/*	AsyncHttpClient acceptClient = new AsyncHttpClient();	

														AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
														acceptClient.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
														AsyncHttpClient acceptClient = MainActivity.getAsyncHttpClient();

														try {
															StringEntity requestData = new StringEntity("{\"receiverSessionStatus\":{\"status\":\"Connected\"}}");
															acceptClient.post(_instance.getApplication().getApplicationContext(),
																	href, requestData, "application/json", new RCSJsonHttpResponseHandler() {
																@Override
																public void onSuccess(String response, int errorCode) {
																	if(SplashActivity.showLog)
        		Log.d("RCSClient", "accept ftSessionInvitationNotification::success = "+response+" errorCode="+errorCode);
																	
																}
																@Override
																public void onFailure(Throwable e, JSONObject response, int errorCode) {
																	if(SplashActivity.showLog)
        		Log.d("RCSClient", "accept ftSessionInvitationNotification::failure = "+response.toString()+" errorCode="+errorCode);
																}
															});
														} catch (UnsupportedEncodingException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}

													}
												}
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
    						            break;

    						   
    						        
    						    
    						

    						
    					 
    				 }
    			}
    	    };
    	    refreshContacts();
    	    IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
          
            registerReceiver(mSystemManagerReceiver, filter);
    }
    
    /**
	 * @uml.property  name="mSystemManagerReceiver"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private final BroadcastReceiver mSystemManagerReceiver = 
            new BroadcastReceiver() {

				@Override
				public void onReceive(Context paramContext, Intent paramIntent) {
					  String action = paramIntent.getAction();
					  if(action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)){
						  NetworkInfo networkInfo = paramIntent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
						  boolean isConnected = networkInfo.isConnectedOrConnecting();
						  if(isConnected){
							  Log.d("RCSClient","Network connected , resuming main thread");
							  if (!running) {
							        running=true;
							        background=new Thread(_instance);
							        background.start();
						        }
						  }
							  
						  else{
							  Log.d("RCSClient","Network disconnected , stoping main thread");
							  if (background!=null) {
						    		running=false;
						    		if (background!=null) {
						    			background.interrupt();
						    			background=null;
						    		}
						    	}
						  }
							  
					  }
					  
					
				}
    	
    };
    
    
   public void startChat(ContactExtended selectedRecord){
  	    ContactState contactState=contactStateManager.getOrCreateContactState(currentChatSessionContactUri);
		ChatSessionActivity.setContactState(contactState);
		contactState.setNewMessage(false);
		
		contactStateManager.setChatVisible(currentChatSessionContactUri, true);
		
		Intent intent = new Intent(_instance, ChatSessionActivity.class);
		//Contact c= getContactFromConatctURI(currentChatSessionContactUri);
		intent.putExtra(SELECTED_CONTACT, selectedRecord);
		intent.putExtra("DesinationURI",currentChatSessionContactUri);
		intent.putParcelableArrayListExtra(ALL_CONTACTS, retrivedExtendedContacts);
		startActivity(intent);
   }
   
    public static void setcurrentChatSessionContactUri(String currentURI)
    {
    	currentChatSessionContactUri = currentURI;
    }
    public void onStart() {
		super.onStart();
		
		if (currentChatSessionContactUri!=null) {
			contactStateManager.setChatVisible(currentChatSessionContactUri, false);
		}
        if(checkNetworkConnectivity(_instance))
        if (!running) {
	        running=true;
	        background=new Thread(this);
	        background.start();
        }
        if(SplashActivity.showLog)
        		Log.d("RCSClient","MainActivity onstart");
        changeStateHandler.sendEmptyMessage(0);
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
	}

	public static void stopMainActivity() {
    	running=false;
    	if (background!=null) {
    		running=false;
    		if (background!=null) {
    			background.interrupt();
    			background=null;
    		}
    	}
    	contactStateManager.clearCache();
    	ContactStateManager.reset();
    }
    
    public void refreshContacts() {
    	final String url=ServiceURL.getContactListURL(SplashActivity.userId);

    	if (SplashActivity.userId!=null) {
	     /*   AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
    		AsyncHttpClient client = MainActivity.getAsyncHttpClient();
	
	    	client.get(url, new RCSJsonHttpResponseHandler() {
	            @Override
	            public void onSuccess(JSONObject response, int errorCode) {
	            	if(SplashActivity.showLog)
        		Log.d("RCSClient", "refreshContacts errorCode="+errorCode+" response="+(response!=null?response.toString():null));
	            	if (response!=null) {
	            		try {
	            			
							if (errorCode==200) {
								JSONObject contactCollection=Utils.getJSONObject(response, "contactCollection");
								if (contactCollection!=null) {
									JSONArray contactList=Utils.getJSONArray(contactCollection, "contact");
									retrievedContacts.clear();
									retrivedExtendedContacts.clear();
									if (contactList!=null) {
										for (int i=0; i<contactList.length(); i++) {
											JSONObject contact=(JSONObject) contactList.get(i);
											
											if(SplashActivity.showLog)
        		Log.d("RCSClient", "MainActivity ["+i+"] = "+contact.toString());
											
											String contactId=Utils.getJSONStringElement(contact, "contactId");
											String contactInfo=contactId;
											String resourceURL=null;
											String displayName=null;
											String capabilities=null;
											String accountType=null;
											JSONObject attributeList=Utils.getJSONObject(contact, "attributeList");
											if (attributeList!=null) {
												resourceURL=Utils.getJSONStringElement(attributeList, "resourceURL");
												JSONArray attributes=Utils.getJSONArray(attributeList, "attribute");
												if (attributes!=null) {
													for (int a=0; a<attributes.length();a++) {
														JSONObject attribute=attributes.getJSONObject(a);
														String name=Utils.getJSONStringElement(attribute, "name");
														String value=Utils.getJSONStringElement(attribute, "value");
														if (name!=null && name.equals("display-name")) {
															String[] str=value.split("\\s");
															if(str.length == 1){
																displayName =str[0];
																contactInfo = str[0];
															}
															accountType = str[str.length-1];
															if(accountType.contains(";")){
																String str1[]=accountType.split(";");
																accountType=str1[0];
																if(str1.length>1)
																	contactInfo=str1[1];
															}
															for(int j=0;j<str.length-1;j++)
															{
																if(displayName!=null)
																displayName=displayName+" "+str[j];
																else
																	displayName=str[j];
															}
														}
														if (name!=null && name.equals("capabilities")) {
															capabilities=value;
														}
													}
												}
													
											}
											if(displayName.equalsIgnoreCase("bot")){
												botContactId = contactId;
												if(SplashActivity.showLog)
        		Log.d("RCSClient","BOT FOUND "+botContactId);
												continue;
											}
											if(displayName.equalsIgnoreCase(accountType))
												accountType= "joyn";
	//										String icon=contact.getString("icon");
											int icon = R.drawable.no_thumbnail;
											if(i< myImageList.length)
												icon = (myImageList[i]);
	
		
											ContactState cs=contactStateManager.getOrCreateContactState(contactId);
		
											Contact contactRecord=new Contact();
											 ContactExtended contactEx = new ContactExtended();
											 
											
											
								
											SQLiteDatabase db=null;
											Cursor cursor = null;
											String contactInfoDB = "";
										/*	try{
											
											db= openOrCreateDatabase( "RCSContacts.db"        , SQLiteDatabase.CREATE_IF_NECESSARY 
					        			    		, null          );
											String newcontact= contactId.substring(5);
											if(newcontact.contains("@"))
											{	
												newcontact= newcontact.substring(0, newcontact.indexOf("@"));
											}
											cursor =db.query(SplashActivity.tableName, new String[]{"RCSContactInfo"}, "RCSContactID like '"+newcontact+"%'", null, null, null, null);
											//if(SplashActivity.showLog)
        		Log.d("sfhskfjsf         ***********          ","**************** "+newcontact);
											cursor.moveToFirst();
											contactInfoDB = cursor.getString(0);
											
											}catch(Exception e){}
											finally{
												if(cursor!=null)
													cursor.close();
												if(db!=null)
													db.close();
											
											}*/
										//-----------------	
											contactRecord.setContactInfo(contactInfo);
											
									/*		if(accountType.equalsIgnoreCase("skype"))
												contactId="sip:+918410123414@rcstestconnect.net";
											if(accountType.equalsIgnoreCase("gtalk"))
												contactId="sip:+918410123415@rcstestconnect.net";*/
											
											contactRecord.setContactId(contactId);
											
											contactRecord.setResourceURL(resourceURL);
											contactRecord.setDisplayName(displayName);
											contactRecord.setCapabilities(capabilities);
											contactRecord.setIcon(icon);
	//										contactRecord.setStatus(status);
											contactRecord.setAccountType(accountType);
											contactRecord.setHasNewMessage(cs.isNewMessage());
											
											boolean found=false;
											for(ContactExtended s : retrivedExtendedContacts){
												if(s.getDisplayName().equalsIgnoreCase(displayName)){
													found=true;
													if(accountType.equalsIgnoreCase("facebook"))
														s.setFacebookID(contactId);
													
													else if(accountType.equalsIgnoreCase("skype"))
														s.setSkypeID(contactId);
													else if(accountType.equalsIgnoreCase("gtalk"))
														s.setGtalkID(contactId);
													else
													
															s.setRCSID(contactId);
												}
													
											}
											if(!found){
												ContactExtended s = new ContactExtended();
												s.setDisplayName(displayName);
												if(accountType.equalsIgnoreCase("facebook"))
													s.setFacebookID(contactId);
												
												else if(accountType.equalsIgnoreCase("skype"))
													s.setSkypeID(contactId);
												else if(accountType.equalsIgnoreCase("gtalk"))
													s.setGtalkID(contactId);
												else
												
														s.setRCSID(contactId);
												if(retrivedExtendedContacts.size()<myImageList.length)
													s.setIcon(myImageList[retrivedExtendedContacts.size()]);
												else
													s.setIcon(R.drawable.no_thumbnail);
												retrivedExtendedContacts.add(s);
												contactExtendedMap.put(displayName, s);
											}
											
											retrievedContacts.add(contactRecord);	
											contactMap.put(contactId, contactRecord);
										}
										
										changeStateHandler.sendEmptyMessage(0);
										
										if(!gotStatus){
											gotStatus=true;
											
											for(Contact c :retrievedContacts){
												if(SplashActivity.showLog)
        		Log.d("RCSClient" ,"send msg for presenec to "+c.getContactId());
												if(c.getAccountype().equalsIgnoreCase("gtalk")|| c.getAccountype().equalsIgnoreCase("skype")||c.getAccountype().equalsIgnoreCase("facebook")){
													try {
													/*	AsyncHttpClient client1 = new AsyncHttpClient();
														AuthScope authscope1=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
														client1.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope1);*/
														AsyncHttpClient client1 = MainActivity.getAsyncHttpClient();
														String cUri= c.getContactId();
															JSONObject chatMessageJSON=new JSONObject();
															chatMessageJSON.put("reportRequest", "Sent");
															chatMessageJSON.put("text", "<presence>");
															String jsonData="{\"chatMessage\":"+chatMessageJSON.toString()+"}";
															StringEntity requestData=new StringEntity(jsonData);
															String url1 =ServiceURL.sendAdhocIMMessageURL(SplashActivity.userId,cUri);
															client1.post(_instance.getApplication().getApplicationContext(),
														    		url1, requestData, "application/json", new RCSJsonHttpResponseHandler(){
																 public void onSuccess(JSONObject response, int errorCode){
																	 if(SplashActivity.showLog)
        		Log.d("RCSClient" ,"send msg for presenec sucess"); 
																 }
															});
													} catch (UnsupportedEncodingException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													} catch (JSONException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												
														
												}
											}
										}
									}
	
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
	            		
	            	}
	            	
	            }    
	            
	            public void onFailure(Throwable e, String response) {
	            	if(SplashActivity.showLog)
        		Log.d("RCSClient", "No response from "+url);
	            }
	    	});
    	    	
    	
    	}
    }
    
   /* private void getStatusFromNW(int i){
    	Contact c =retrievedContacts.get(i);
    	if(c.getAccountype().equalsIgnoreCase("gtalk")|| c.getAccountype().equalsIgnoreCase("skype")||c.getAccountype().equalsIgnoreCase("facebook")){
			try {
				AsyncHttpClient client1 = new AsyncHttpClient();
				AuthScope authscope1=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
				client1.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope1);        	
				String cUri= c.getContactId();
					JSONObject chatMessageJSON=new JSONObject();
					chatMessageJSON.put("reportRequest", "Sent");
					chatMessageJSON.put("text", "<presence>");
					String jsonData="{\"chatMessage\":"+chatMessageJSON.toString()+"}";
					StringEntity requestData=new StringEntity(jsonData);
					String url1 =ServiceURL.sendAdhocIMMessageURL(SplashActivity.userId,cUri);
					client1.post(_instance.getApplication().getApplicationContext(),
				    		url1, requestData, "application/json", new RCSJsonHttpResponseHandler(){
						 public void onSuccess(JSONObject response, int errorCode){
							 if(SplashActivity.showLog)
        		Log.d("RCSClient" ,"send msg for presenec sucess"); 
							 
						 }

						@Override
						public void onFailure(Throwable error,
								JSONObject content, int responseCode) {
							
							super.onFailure(error, content, responseCode);
						}
						
					});
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
				
		}
    	
    }*/
    
	public Contact getContactFromContactId(String contactURI){
		Contact c=null;
		if(contactURI!=null)
			c= contactMap.get(contactURI);
		return c;
	}
	
	public void run() {
		if(SplashActivity.showLog)
        		Log.d("RCSClient", "Main background thread started");

		long lastRequestTime=0;
		
		while (running) {
			boolean makeRequest=false;
			
			long now=System.currentTimeMillis();
			
			makeRequest=(now-lastRequestTime)>=MAIN_LOOP_DELAY; // Throttle so that do not drive through too many requests 
			
			final String notificationsUrl=SplashActivity.notificationChannelURL!=null?(SplashActivity.notificationChannelURL.replaceAll("tel\\:\\+", "tel%3A%2B").replaceAll("tel\\:", "tel%3A")):null;
			
			if (notificationsUrl!=null && makeRequest ) {
			
				DefaultHttpClient client = new DefaultHttpClient();
				
				HttpParams myParams = new BasicHttpParams();
			    HttpConnectionParams.setConnectionTimeout(myParams, MAIN_LOOP_DELAY); 
			    HttpConnectionParams.setSoTimeout(myParams, LONGPOLL_TIMEOUT);
			    
			    HttpPost httppost = new HttpPost(notificationsUrl);
				JSONObject jsonData=null;
				lastRequestTime=now;
				int statusCode = -1;
				HttpResponse response=null;
				try {
					if(SplashActivity.showLog)
						Log.i("RCSClient","notifiction url : "+notificationsUrl);
					String auth = android.util.Base64.encodeToString((SplashActivity.PrjUsrName+":"+SplashActivity.appCredentialPassword).getBytes("UTF-8"), android.util.Base64.NO_WRAP);
					httppost.addHeader("Authorization", "Basic "+ auth);
					httppost.addHeader("Content-Type", "application/json");
					httppost.addHeader("Accept", "application/json");
					
					
					
						response = client.execute(httppost);
					 statusCode=response!=null&&response.getStatusLine()!=null?response.getStatusLine().getStatusCode():-1;
					if(SplashActivity.showLog)
						Log.i("RCSClient","Response from server was " + response.getStatusLine().getReasonPhrase());
						jsonData = getJSONDataFromResponse(response);
						Log.i("RCSClient","Response from server was JSON : " + jsonData.toString());
				} catch (Exception e) {
					if(SplashActivity.showLog)
						Log.d("RCSClient", "IOException handled 1");
					 	java.io.OutputStream out = new java.io.ByteArrayOutputStream(); 
			            e.printStackTrace(new java.io.PrintStream(out));
			            Log.d("RCSClient", out.toString());
			            showNotificationHandler.sendMessage(Message.obtain(showNotificationHandler, NETWORK_FAILURE,null));
				} 
				
			
			
				

				if (jsonData!=null) {
					try {
						processNotificationResponse(statusCode, jsonData);
					} catch (JSONException e) {
						if(SplashActivity.showLog)
        		Log.d("RCSClient", "JSONException handled");
					}
				}
				
			} else {
	    		try {
	    			Thread.yield();
	    			Thread.sleep (1000); // milliseconds
	    		} catch (InterruptedException ie) {}
			}
		}
	}
    
	public static boolean checkNetworkConnectivity(Context c){
		boolean isConnected =false;
		ConnectivityManager cm =
		        (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if(activeNetwork!=null)
			isConnected = activeNetwork.isConnectedOrConnecting();
		Log.d("RCSClient","Network connectivity is : "+isConnected);
		return isConnected;
	}
	
    private void processNotificationResponse(int statusCode, JSONObject response) throws JSONException {

		JSONArray notifications=response!=null?response.getJSONArray("notificationList"):null;
		if (notifications!=null && notifications.length()>0) {
			for (int i=0; i<notifications.length(); i++) {
				JSONObject notification=notifications.getJSONObject(i);
				
				if(SplashActivity.showLog)
        		Log.d("RCSClient", "Notification ["+i+"] = "+notification.toString());
				
				if (Utils.getJSONObject(notification, "messageNotification")!=null) {
					JSONObject messageNotification=Utils.getJSONObject(notification, "messageNotification");
					
					if (Utils.getJSONObject(messageNotification, "isComposing")!=null) {
						JSONObject isComposing=Utils.getJSONObject(messageNotification, "isComposing");
						String senderAddress=Utils.getJSONStringElement(messageNotification, "senderAddress");
						String status=Utils.getJSONStringElement(isComposing, "status");
						
						if (senderAddress!=null) {
							if (senderAddress.equals(currentChatSessionContactUri)) {
								ChatSessionActivity.updateComposingIndicator(status);
							}
							contactStateManager.updateComposingIndicator(senderAddress, status);
						}
					}
					
					if (Utils.getJSONObject(messageNotification, "chatMessage")!=null) {
						
						
						JSONObject chatMessage=Utils.getJSONObject(messageNotification, "chatMessage");
						String senderAddress=Utils.getJSONStringElement(messageNotification, "senderAddress");
						String messageId=Utils.getJSONStringElement(messageNotification, "messageId");
						String sessionId=Utils.getJSONStringElement(messageNotification, "sessionId");
						String messageText=Utils.getJSONStringElement(chatMessage, "text");
						String dateTime=Utils.getJSONStringElement(messageNotification, "dateTime");
						
						if(senderAddress.equalsIgnoreCase(botContactId)){
							if(messageText.contains("<phototag>")){
								phtoTagMsg = messageText.substring(10);
								showNotificationHandler.sendMessage(Message.obtain(showNotificationHandler, SHOW_NOTIFICATION,new Notifications(sentFile,messageText.substring(10),Notifications.NOTIFICATION_TYPE_FAEBOOK_PHOTOTAG)));
								Log.d("RCSClient"," phototog file : "+sentFile);
								Inbox.add(new Notifications(sentFile,messageText,Notifications.NOTIFICATION_TYPE_FAEBOOK_PHOTOTAG));
							}
								
							else{
								Inbox.add(new Notifications(null,messageText,Notifications.NOTIFICATION_OTHERS));
								showNotificationHandler.sendMessage(Message.obtain(showNotificationHandler, SHOW_NOTIFICATION,new Notifications(null,messageText,Notifications.NOTIFICATION_OTHERS)));
							}
							v.vibrate(200);
							try {
						        Uri ringerNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
						        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), ringerNotification);
						        r.play();
						    } catch (Exception e) {}
							return;
						}
						
						String tempSender = senderAddress.substring(4);
						Contact found=null;
						
						//String contactDisplayName= found.getDisplayName();
						for (Entry<String, Contact> entry : contactMap.entrySet()) {
						    String key = entry.getKey();
						    Object value = entry.getValue();
						   
						   if(key.contains(tempSender))
							   found=(Contact) value;
						}
						
						ContactExtended foundExtended = getContactExtended(tempSender);
						
						if(messageText.contains("<groupchat>")){
							int l=messageText.indexOf("</groupchat>");
							if(l>-1){
								System.out.println("Grp chat STATUS IS " +messageText.substring(11, l));
								String grpStatus = messageText.substring(11,l);
								
								if(grpStatus.equalsIgnoreCase("accepted"))
									ChatSessionActivity.refreshContactStatus(foundExtended.getDisplayName(),"Accepted");
								if(grpStatus.equalsIgnoreCase("declined"))
									ChatSessionActivity.refreshContactStatus(foundExtended.getDisplayName(),"Declined");
								return;
							}
						}
						if(messageText.contains("<presence>")){
							int l=messageText.indexOf("</presence>");
							if(l>-1){
								System.out.println("STATUS IS " +messageText.substring(10, l));
								String presence=messageText.substring(10, l);
								
								if(found.getAccountype().equalsIgnoreCase("joyn"))
									foundExtended.setRCSStatus(presence);
								if(found.getAccountype().equalsIgnoreCase("gtalk"))
									foundExtended.setGtalkStatus(presence);
								if(found.getAccountype().equalsIgnoreCase("skype"))
									foundExtended.setSkypeStatus(presence);
								if(found.getAccountype().equalsIgnoreCase("facebook"))
									foundExtended.setFacebookStatus(presence);
								
								
								
								changeStateHandler.sendEmptyMessage(0);
								return;
							}
						}
						
						ArrayList<ChatMessage> messageBuffer=messageCache.get(senderAddress);
						if (messageBuffer==null) {
							messageBuffer=new ArrayList<ChatMessage>();
							messageCache.put(senderAddress, messageBuffer);
						}
						ChatMessage received=new ChatMessage();
						received.setMessageText(messageText);
						received.setMessageDirection(ChatMessage.MESSAGE_RECEIVED);
						received.setContactUri(senderAddress);
					
						if(dateTime!=null)
						received.setMessageTime(Utils.convertTransferDateToDisplayString(dateTime));
						
						received.setStatus(ChatMessage.MESSAGE_STATUS_RECEIVED);
						
						String url=ServiceURL.getSendMessageStatusReportURL(SplashActivity.userId, senderAddress, sessionId, messageId);
						
						String jsonData=null;
						
						if (senderAddress!=null && sessionId.equalsIgnoreCase("adhoc")) {
							if(SplashActivity.showLog)
        		Log.d("RCSClient","currentChatSessionContactUri : "+currentChatSessionContactUri);
							
							if (currentChatSessionContactUri!=null && currentChatSessionContactUri.contains(tempSender)) {
								jsonData="{\"messageStatusReport\":{\"status\":\"Displayed\"}}";
								ChatSessionActivity.refreshMessageList(senderAddress, SplashActivity.userId, received);
								
							}
							else {
								jsonData="{\"messageStatusReport\":{\"status\":\"Delivered\"}}";
								contactStateManager.storeMessage(senderAddress, received, true, SplashActivity.userId);
								
						
									
					
								if(foundExtended!=null){
									if (foundExtended!=null && !foundExtended.isHasNewMessage()) {
										foundExtended.setHasNewMessage(true,found.getAccountype());
										changeStateHandler.sendEmptyMessage(0);
									}
								}
							}
						}else if(senderAddress!=null && !(sessionId.equalsIgnoreCase("adhoc"))){
							jsonData="{\"messageStatusReport\":{\"status\":\"Displayed\"}}";
							if(!chatCordinator)
								ConferenceActivity.refreshGroupChatMessageList(senderAddress, SplashActivity.userId, received);
							else if(chatCordinator)
								ChatSessionActivity.refreshMessageList(senderAddress, SplashActivity.userId, received);
						}
							
						

					/*	AsyncHttpClient responseClient = new AsyncHttpClient();
				        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
				        responseClient.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
						AsyncHttpClient responseClient = MainActivity.getAsyncHttpClient();

						final String responseURL=url;
						if(SplashActivity.showLog)
        		Log.d("RCSClient", "Sending message status update "+responseURL+" with "+jsonData);

						StringEntity requestData;
						try {
							requestData = new StringEntity(jsonData);
							responseClient.put(_instance.getApplication().getApplicationContext(),
					        		url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
					        	@Override
					            public void onSuccess(JSONObject response, int errorCode) {
					        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "messageStatusReport::success = "+response.toString()+" errorCode="+errorCode);
					        	}
					        	@Override
					            public void onFailure(Throwable e, JSONObject response, int errorCode) {
					        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "messageStatusReport::failure = "+response.toString()+" errorCode="+errorCode);
					        	}
							});
						} catch (UnsupportedEncodingException e) {}										
						v.vibrate(200);
						try {
					        Uri ringerNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), ringerNotification);
					        r.play();
					    } catch (Exception e) {}
					}

				} else if (Utils.getJSONObject(notification, "chatSessionInvitationNotification")!=null) {
					JSONObject chatSessionInvitationNotification=Utils.getJSONObject(notification, "chatSessionInvitationNotification");
					JSONArray link=Utils.getJSONArray(chatSessionInvitationNotification, "link");
					if (link!=null && link.length()>0) {
						for (int li=0; li<link.length(); li++) {
							JSONObject litem=link.getJSONObject(li);
							String rel=Utils.getJSONStringElement(litem, "rel");
							String href=Utils.getJSONStringElement(litem, "href");
							
							if ("ParticipantSessionStatus".equals(rel) && href!=null) {
							/*	AsyncHttpClient acceptClient = new AsyncHttpClient();
								
						        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
						        acceptClient.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
								AsyncHttpClient acceptClient = MainActivity.getAsyncHttpClient();
								try {
									StringEntity requestData = new StringEntity("{\"participantSessionStatus\":{\"status\":\"Connected\"}}");
									acceptClient.put(_instance.getApplication().getApplicationContext(),
							        		href, requestData, "application/json", new RCSJsonHttpResponseHandler() {
							        	@Override
							            public void onSuccess(String response, int errorCode) {
							        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "accept chatSessionInvitationNotification::success = "+response+" errorCode="+errorCode);
							        	}
							        	@Override
							            public void onFailure(Throwable e, JSONObject response, int errorCode) {
							        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "accept chatSessionInvitationNotification::failure = "+response.toString()+" errorCode="+errorCode);
							        		
							        	}
									});
								} catch (UnsupportedEncodingException e) {}										
							}
						}
					}
					
				} else if (Utils.getJSONObject(notification, "sessionEventNotification")!=null) {
					JSONObject sessionEventNotification=Utils.getJSONObject(notification, "sessionEventNotification");
					String event=Utils.getJSONStringElement(sessionEventNotification, "event");
					if ("unregisterSuccess".equalsIgnoreCase(event)) {
						contactChangeHandler.sendEmptyMessage(0);
					}
				}/* else if (Utils.getJSONObject(notification, "contactEventNotification")!=null) {
					contactChangeHandler.sendEmptyMessage(0);	

				}*/ else if (Utils.getJSONObject(notification, "messageStatusNotification")!=null) {
					JSONObject messageStatusNotification=Utils.getJSONObject(notification, "messageStatusNotification");
					String messageId=Utils.getJSONStringElement(messageStatusNotification, "messageId");
					String status=Utils.getJSONStringElement(messageStatusNotification, "status");
					ChatMessage message=ContactStateManager.getMessageForId(messageId);
					if(SplashActivity.showLog)
        		Log.d("RCSClient", "updating status messageId="+messageId+" status="+status+" message="+message);
					if (status!=null && message!=null) {
						String contactUri=message.getContactUri();
						if (status.equalsIgnoreCase("delivered")) {
							if (contactUri!=null && contactUri.equals(currentChatSessionContactUri)) {
								ChatSessionActivity.updateStatus(messageId,ChatMessage.MESSAGE_STATUS_DELIVERED);
							} else {
								ContactStateManager.updateStatusFor(messageId,ChatMessage.MESSAGE_STATUS_DELIVERED);
							}
						} else if (status.equalsIgnoreCase("displayed")) {
							if (contactUri!=null && contactUri.equals(currentChatSessionContactUri)) {
								ChatSessionActivity.updateStatus(messageId,ChatMessage.MESSAGE_STATUS_VIEWED);
							} else {
								ContactStateManager.updateStatusFor(messageId,ChatMessage.MESSAGE_STATUS_VIEWED);
							}
						}
					}

				} else if (Utils.getJSONObject(notification, "chatEventNotification")!=null) {
					JSONObject chatEventNotification=Utils.getJSONObject(notification, "chatEventNotification");
					String sessionId=Utils.getJSONStringElement(chatEventNotification, "sessionId");
					String eventType=Utils.getJSONStringElement(chatEventNotification, "eventType");
					if(!(sessionId.equalsIgnoreCase("adhoc")))
						ChatSessionActivity.setSessionId(sessionId);	
					if(SplashActivity.showLog)
        		Log.d("RCSClient","Session id : "+sessionId+ " eventType "+ eventType);
						
					/*if (eventType!=null && eventType.equalsIgnoreCase("SessionEnded")) {
						contactChangeHandler.sendEmptyMessage(0);
					}*/

				}
				else if (Utils.getJSONObject(notification, "groupSessionInvitationNotification")!=null) {
					
					Intent intent = new Intent(_instance, dialogYesNo.class);
		    	    intent.putExtra("notification",notification.toString());
		    		startActivity(intent);
					
					
				}else if (Utils.getJSONObject(notification,"participantStatusNotification")!=null) {
					if(SplashActivity.showLog)
        		Log.d("RCSClient","participantStatusNotification");
					ArrayList<ContactExtended> contacts = new ArrayList<ContactExtended>();
					JSONObject participantStatusNotification=Utils.getJSONObject(notification, "participantStatusNotification");
					JSONArray participants=Utils.getJSONArray(participantStatusNotification, "participant");
					HashMap<String , String> statusMap = new HashMap<String, String>();
					if(participants!=null ){
						for(int i1=0;i1<participants.length();i1++)
						{
							ContactExtended c = new ContactExtended();

							JSONObject participant = participants.getJSONObject(i1);
							String address = participant.getString("address");
							
							Contact contact= getContactFromConatctURI(address);
							String displayname = null;
							if(contact!=null)
								 displayname = contact.getDisplayName();
							if(displayname==null)
								displayname=address;
								
							String status = participant.getString("status");
							c.setDisplayName(address);
							c.setIcon(myImageList[i1]);

							contacts.add(c);
							//statusMap.put(displayname, status);
						}
					//	ChatSessionActivity.refreshContactStatus(statusMap);
						if(!chatCordinator)
							ConferenceActivity.refreshConfContactList(contacts);
						
						//	ChatSessionActivity.refreshContactStatus(statusMap);
					}
				}else if (Utils.getJSONObject(notification,"ftSessionInvitationNotification")!=null){
					//send an auto-accept instead of dialog.
					if(SplashActivity.showLog)
        		Log.d("RCSClient","ftSessionInvitationNotification");
					
					showNotificationHandler.sendMessage(Message.obtain(showNotificationHandler, FILE_TRANSFER_ACCEPT,notification));
				
				//	NotificationView.setText("Facebook User "+senderName+" has tagged you in a photo ");
				//	showNotificationHandler.sendMessage(Message.obtain(showNotificationHandler, FILE_TRANSFER_RECEIVE,notification));
			/*		JSONObject ftSessionInvitationNotification=Utils.getJSONObject(notification, "ftSessionInvitationNotification");
					JSONArray link=Utils.getJSONArray(ftSessionInvitationNotification, "link");
					String receiverName= Utils.getJSONStringElement(ftSessionInvitationNotification, "receiverName");
					
					
					if (link!=null && link.length()>0) {
						for (int li=0; li<link.length(); li++) {
							JSONObject litem=link.getJSONObject(li);
							String rel=Utils.getJSONStringElement(litem, "rel");
							String href=Utils.getJSONStringElement(litem, "href");

							if ("ReceiverSessionStatus".equals(rel) && href!=null){
								AsyncHttpClient acceptClient = new AsyncHttpClient();	

								AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
								acceptClient.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);

								try {
									StringEntity requestData = new StringEntity("{\"receiverSessionStatus\":{\"status\":\"Connected\"}}");
									acceptClient.post(_instance.getApplication().getApplicationContext(),
											href, requestData, "application/json", new RCSJsonHttpResponseHandler() {
										@Override
										public void onSuccess(String response, int errorCode) {
											if(SplashActivity.showLog)
        		Log.d("RCSClient", "accept ftSessionInvitationNotification::success = "+response+" errorCode="+errorCode);
										}
										@Override
										public void onFailure(Throwable e, JSONObject response, int errorCode) {
											if(SplashActivity.showLog)
        		Log.d("RCSClient", "accept ftSessionInvitationNotification::failure = "+response.toString()+" errorCode="+errorCode);
										}
									});
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}
					}*/
					
					
				}else if (Utils.getJSONObject(notification,"receiverAcceptanceNotification")!=null){
					if(SplashActivity.showLog)
        		Log.d("RCSClient","receiverAcceptanceNotification");
					
				}else if (Utils.getJSONObject(notification,"fileNotification")!=null){
					if(SplashActivity.showLog)
        		Log.d("RCSClient","fileNotification");
					JSONObject fileNotification=Utils.getJSONObject(notification, "fileNotification");
					JSONObject fileInformation=Utils.getJSONObject(fileNotification, "fileInformation");
					JSONObject fileSelector = Utils.getJSONObject(fileInformation,"fileSelector");
					
					String fileURL=Utils.getJSONStringElement(fileInformation, "fileURL");
					final String fileDesc = Utils.getJSONStringElement(fileInformation, "fileDescription");
					
					final String fileName = Utils.getJSONStringElement(fileSelector, "name");
					if(fileURL!=null){
						if(SplashActivity.showLog)
        		Log.d("RCSClient","fileNotification   fileURL  : "+fileURL);
						/*AsyncHttpClient acceptClient = new AsyncHttpClient();	

						AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);*/
						AsyncHttpClient acceptClient =getAsyncHttpClient();
				//		acceptClient.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);
						String[] allowedTypes = new String[] {"image/png","image/jpeg","text/plain"};
						 sentFile =  Environment.getExternalStorageDirectory()+"/"+fileName;
						
				    /*   try {
				        	DefaultHttpClient mHttpClient;
							HttpParams params1 = new BasicHttpParams();
					        params1.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
					        mHttpClient = new DefaultHttpClient(params1);
					        HttpGet get=new HttpGet(fileURL);
							HttpResponse response1 = mHttpClient.execute(get);
							if(response1!=null){
							 org.apache.http.Header[] contentTypeHeaders =response1.getHeaders("Content-Type");
							 String value = contentTypeHeaders[0].getValue();
							 HttpEntity entity = response1.getEntity();
							 BufferedInputStream bis = new BufferedInputStream(entity.getContent());
							 BufferedOutputStream bos = new BufferedOutputStream(openFileOutput(fileName, MODE_APPEND));
							 int inByte;
							 while((inByte = bis.read()) != -1) bos.write(inByte);
							 bis.close();
							 bos.close();
							}
						} catch (ClientProtocolException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
				        acceptClient.get(_instance.getApplication().getApplicationContext(),fileURL,new BinaryHttpResponseHandler(allowedTypes)
				        {
							@Override
							 public void onSuccess(byte[] paramArrayOfByte) {
								if(SplashActivity.showLog)
        		Log.d("RCSClient", "accept ftSessionInvitationNotification::success = "+paramArrayOfByte.length);
								try {
									String FilePath = Environment.getExternalStorageDirectory()+"/"+fileName;
									FileOutputStream fos = new FileOutputStream(FilePath);
									// FileOutputStream fos = new FileOutputStream(someFile);
									 fos.write(paramArrayOfByte);
									 fos.flush();
									 fos.close();
									 if(SplashActivity.showLog)
        		Log.d("RCSClient","File Successfully saved at " +FilePath);
									 
									 	
									 
									/* NotificationView.setText("Facebook User "+fileSenderName+" has tagged you in a photo ");
									 NotificationView.refreshDrawableState();*/
								
									// showNotificationHandler.sendMessage(Message.obtain(showNotificationHandler, SHOW_NOTIFICATION,new Notifications(sentFile,phtoTagMsg,Notifications.NOTIFICATION_TYPE_FAEBOOK_PHOTOTAG)));
									//	Inbox.add(new Notifications(sentFile,phtoTagMsg,Notifications.NOTIFICATION_TYPE_FAEBOOK_PHOTOTAG));
									 /*Intent showFile = new Intent();
									 showFile.setAction(Intent.ACTION_VIEW);
									 String FileUrl="file://"+FilePath;
									 showFile.setDataAndType(Uri.parse(FileUrl),MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(FileUrl)));
									 startActivity(showFile);*/
								//	 Intent i = new Intent(Intent.ACTION_VIEW, Uri.fromFile(fileName));
									 
									 
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							@Override
							 public void onFailure(Throwable paramThrowable, byte[] paramArrayOfByte) {
								if(SplashActivity.showLog)
        		Log.d("RCSClient", "accept ftSessionInvitationNotification::failure = "+paramThrowable.getMessage());
							}
						}
				        		);
						/*acceptClient.get(_instance.getApplication().getApplicationContext(), fileURL, new BinaryHttpResponseHandler(allowedTypes){
							
							 @Override
							public void onFailure(Throwable arg0, byte[] arg1) {
								// TODO Auto-generated method stub
								super.onFailure(arg0, arg1);
								if(SplashActivity.showLog)
        		Log.d("RCSClient","fileNotification download file onFailure"+arg0.getMessage());
							}

							public void onSuccess(byte[] fileData) {
								 try {
									 File someFile = new File(fileName);
									 FileOutputStream fos = new FileOutputStream(someFile);
									 fos.write(fileData);
									 fos.flush();
									 fos.close();
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							    }
						});*/
					}
					
				}
			}
		}
    	
		
	}

public ContactExtended getContactExtended(String tempSender){
//	ContactExtended Found =null;
	for(ContactExtended s : retrivedExtendedContacts){
		if(s.getRCSID().contains(tempSender) || s.getGtalkID().contains(tempSender) || s.getSkypeID().contains(tempSender) || s.getFacebookID().contains(tempSender))
			return s;
	}
	return null;
//	return Found;
}
 
	private JSONObject getJSONDataFromResponse(HttpResponse response) throws IllegalStateException, IOException, JSONException {
    	JSONObject jsonData=null;
		HttpEntity entity = response!=null&&response.getEntity()!=null?response.getEntity():null;
		if (entity != null) {
			
            InputStream instream = entity.getContent();
            final StringBuilder out = new StringBuilder();
            int bufferSize=1024;
            final char[] buffer = new char[bufferSize];
            int nread;
            InputStreamReader reader = new InputStreamReader(instream);
            while ((nread=reader.read(buffer, 0, buffer.length))>=0) {
            	out.append(buffer, 0, nread);
            }
            if (out.length()>0) jsonData = new JSONObject(out.toString());
		}    
		return jsonData;
	}



	public void addContactClicked() {
		Intent intent = new Intent(_instance, AddContactActivity.class);
		startActivity(intent);
    }

	public static void chatSessionClosed(String destinationUri) {
		if (destinationUri.equals(currentChatSessionContactUri)) {
			currentChatSessionContactUri=null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_screen_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.refreshContacts:
	        	contactChangeHandler.sendEmptyMessage(0);
	            return true;
	        case R.id.signOut:
	        	finish();
	            return true;
	       
	        case R.id.addContact:
	        	addContactClicked();
	        	return true;
	     
	        case R.id.Inbox:
	        	showInbox();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showInbox(){
		if(SplashActivity.showLog)
        		Log.d("RCSClient","showInbox");
		Intent showInboxIntent = new Intent(_instance,InboxActivity.class);
		showInboxIntent.putParcelableArrayListExtra(ALL_NOTIFICATION,Inbox);
		startActivity(showInboxIntent);
		
	}
	
	public void editContactClicked(){
		
		if(currentfocused==null){
			Toast.makeText(this, "Select a contact first", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(_instance, EditContactActivity.class);
		intent.putExtra(SELECTED_CONTACT, currentfocused);
		startActivity(intent);
		
		
		
	}
	
	public void startGroupChat(){
		if(SplashActivity.showLog)
        		Log.d("RCSClient","startGroupChat");
		
		
		Intent intent = new Intent(_instance, ConferenceActivity.class);
		intent.putParcelableArrayListExtra(ALL_CONTACTS, retrievedContacts);
		startActivity(intent);
		
	}
	public void sendSkypeMessage(View view){
		if(SplashActivity.showLog)
        		Log.d("RCSClient","sendSkypeMessage");
		Contact selectedRecord=retrievedContacts.get(3);
		
		currentChatSessionContactUri=selectedRecord.getContactId();
		selectedRecord.setHasNewMessage(false);
		
		ContactState contactState=contactStateManager.getOrCreateContactState(currentChatSessionContactUri);
		ChatSessionActivity.setContactState(contactState);
		contactState.setNewMessage(false);
		
		contactStateManager.setChatVisible(currentChatSessionContactUri, true);
		
		Intent intent = new Intent(_instance, ChatSessionActivity.class);
		intent.putExtra(SELECTED_CONTACT, selectedRecord);
		startActivity(intent);
	}
	
	public void cancelNotificationClicked(View view){
		
	}
	
	public void MoreButtonClicked(View view){
		
		if(SplashActivity.showLog)
        		Log.d("RCSClient","MoreButtonClicked");
	
		
		String ntText = (String) NotificationView.getText();
		Intent i = new Intent(_instance, NotificationsActivity.class);
		i.putExtra("NOTIFICATION", currentNotification);
	//i.putExtra("SENT_FILE_NAME", sentFile);
		//i.putExtra("NOTIFICATION_OBJ",NotificationObj);
		startActivity(i);
		NotificationView.setText("");
		moreButton.setVisibility(View.INVISIBLE);
		
	}
public void sendFacebookMessage(View view){
	if(SplashActivity.showLog)
        		Log.d("RCSClient","sendFacebookMessage");
}
	
	
	
	
	
}
