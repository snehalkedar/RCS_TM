package org.gsm.rcsApp.activities;


import java.io.UnsupportedEncodingException;

import org.apache.http.auth.AuthScope;
import org.apache.http.entity.StringEntity;
import org.gsm.RCSDemo.R;
import org.gsm.rcsApp.ServiceURL;
import org.gsm.rcsApp.misc.RCSJsonHttpResponseHandler;
import org.gsm.rcsApp.misc.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class dialogYesNo extends Activity {
	static dialogYesNo _instance=null;
	static JSONObject notification;
	/**
	 * @uml.property  name="sessionId"
	 */
	String sessionId= null;
	/**
	 * @uml.property  name="subject"
	 */
	String subject = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		  requestWindowFeature(Window.FEATURE_NO_TITLE);
		  
		setContentView(R.layout.dialog_yes_no);
		
		final String notiyStr = getIntent().getStringExtra("notification");
		
		try {
			notification = new JSONObject(notiyStr);
			subject = Utils.getJSONStringElement(Utils.getJSONObject(dialogYesNo.notification, "groupSessionInvitationNotification"),"subject");
			subject = subject.replace("sip:", " ");
			subject =subject.replace("@rcstestconnect.net", " ");
			TextView txt=(TextView)findViewById(R.id.Tv1);
			txt.setText(subject);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		_instance =this;
		Button accept =(Button)findViewById(R.id.Btn1);
		Button deny =(Button)findViewById(R.id.Btn2);
		accept.setOnClickListener(new OnClickListener() {
			
			public void onClick(View paramView) {
				try {
					
					String url=null;
					
					JSONObject groupSessionInvitationNotification=Utils.getJSONObject(dialogYesNo.notification, "groupSessionInvitationNotification");
					 sessionId=Utils.getJSONStringElement(groupSessionInvitationNotification, "sessionId");
					JSONArray link = Utils.getJSONArray(groupSessionInvitationNotification, "link");
					for (int i1=0; i1<link.length(); i1++) {
						JSONObject linkElement=link.getJSONObject(i1);
						String rel=	linkElement.getString("rel");
						if(rel.equalsIgnoreCase("ParticipantInformationStatus")){
							url=Utils.getJSONStringElement(linkElement,"href");
							break;
						}
							
					}
					  JSONObject participant=new JSONObject();
					 participant.put("status","Connected");
					  
					 String jsonData="{\"participantSessionStatus\":"+participant.toString()+"}";
					
						StringEntity requestData=new StringEntity(jsonData);
					/* AsyncHttpClient client = new AsyncHttpClient();
					    AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
					    client.setBasicAuth(SplashActivity.userId, SplashActivity.appCredentialPassword, authscope);*/
						AsyncHttpClient client = MainActivity.getAsyncHttpClient();
					    	
					    if(SplashActivity.showLog)
        		Log.d("RCSClient"," accept grp chat url : "+url);
					client.put(_instance.getApplication().getApplicationContext(),
							url, requestData, "application/json", new RCSJsonHttpResponseHandler() {
						@Override
					    public void onSuccess(JSONObject response, int errorCode) {
						}
					
					  });
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent data = new Intent(dialogYesNo._instance,ConferenceActivity.class);
	    		data.putExtra("sessionId",sessionId);
	    		startActivity(data);
	    		finish();
			}
		});
		
			deny.setOnClickListener(new OnClickListener() {
			public void onClick(View paramView) {
				
				finish();
				
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
