package org.gsm.rcsApp.activities;

import java.io.UnsupportedEncodingException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.StringEntity;
import org.gsm.rcsApp.ServiceURL;
import org.gsm.rcsApp.misc.RCSJsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import org.gsm.RCSDemo.R;

public class AddContactActivity extends Activity {

	private static Handler closeHandler = null;
	private static Handler errorHandler = null;
	
	private static AddContactActivity _instance=null;
	  /**
	 * @uml.property  name="spinner"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	Spinner spinner=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        
        _instance=this;

        closeHandler = new Handler() {
    		public void handleMessage(Message msg) {
    			super.handleMessage(msg);
        		finish();
    		}
        };
        errorHandler = new Handler() {
    		public void handleMessage(Message msg) {
    			super.handleMessage(msg);
        		int code=msg.what;
        		String error=(String) msg.obj;
        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "AddContactActivity Error "+code+" description="+error);
        		Context context = getApplicationContext();
        		CharSequence text = "Error "+code+(error!=null?" \""+error+"\"":"");
        		Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        		toast.show();
    		}
        };
        TextView addContactUriInput=(TextView) findViewById(R.id.addRCSIdentityInput);
        addContactUriInput.setText("tel:");
         spinner = (Spinner) findViewById(R.id.accounts_spinner);
     // Create an ArrayAdapter using the string array and a default spinner layout
     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
             R.array.accounts_array, android.R.layout.simple_spinner_item);
     // Specify the layout to use when the list of choices appears
     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     // Apply the adapter to the spinner
     spinner.setAdapter(adapter);
    }
    
    public void saveContact(View view) {
        TextView addContactDisplayNameInput=(TextView) findViewById(R.id.addContactDisplayNameInput);
        final String displayName=addContactDisplayNameInput.getText().toString();
        String str = (String) spinner.getSelectedItem();
        
        TextView addRCSIdentity = (TextView)findViewById(R.id.addRCSIdentityInput);
        final String rcsIdentity = addRCSIdentity.getText().toString(); //this is the new contact uri
        
        TextView addContactUriInput=(TextView) findViewById(R.id.addContactUriInput);
        final String contactUri=addContactUriInput.getText().toString();
        
      
        
        try {
            JSONObject contact=new JSONObject();
            
            /*if(str.equalsIgnoreCase("skype"))
            	contactUri=SettingsActivity.SKYPE_ACCOUNT;
            if(str.equalsIgnoreCase("gtalk"))
            	contactUri=SettingsActivity.GTALK_ACCOUNT;
            if(str.equalsIgnoreCase("facebook"))
            	contactUri=SettingsActivity.FACEBOOK_ACCOUNT;
            if(str.equalsIgnoreCase("twitter"))
            	contactUri=SettingsActivity.TWITTER_ACCOUNT;*/
            
            final String addurl=ServiceURL.getAddContactURL(SplashActivity.userId, rcsIdentity);
            contact.put("contactId", rcsIdentity);
            
            
            
            JSONObject attributeList=new JSONObject();
			contact.put("attributeList", attributeList);
			JSONArray attribute=new JSONArray();
			
			attributeList.put("attribute", attribute);
			
			JSONObject displayNameAttribute=new JSONObject();
			attribute.put(0,displayNameAttribute);
			
			displayNameAttribute.put("name", "display-name");
			displayNameAttribute.put("value", displayName+" "+str+";"+contactUri);
			
		/*	JSONArray attribute2=new JSONArray();
			
			attributeList.put("attribute2", attribute);
			
			JSONObject displayNameAttribute2=new JSONObject();
			attribute2.put(0,displayNameAttribute2);
			
			displayNameAttribute2.put("name", "display-name");
			displayNameAttribute2.put("value", displayName);
				*/
			
	       /* AsyncHttpClient client = new AsyncHttpClient();
	        AuthScope authscope=new AuthScope(ServiceURL.serverName, ServiceURL.serverPort, AuthScope.ANY_REALM);
	        client.setBasicAuth(SplashActivity.PrjUsrName, SplashActivity.appCredentialPassword, authscope);*/
			AsyncHttpClient client = MainActivity.getAsyncHttpClient();

	    	
	        String jsonData="{\"contact\":"+contact.toString()+"}";
	        if(SplashActivity.showLog)
        		Log.d("RCSClient","Json : "+ jsonData);
			StringEntity requestData=new StringEntity(jsonData);
	        
	        client.put(_instance.getApplication().getApplicationContext(),
	        		addurl, requestData, "application/json", new RCSJsonHttpResponseHandler() {
	        	@Override
	            public void onSuccess(JSONObject response, int errorCode) {
	        		if(SplashActivity.showLog)
        		Log.d("RCSClient", "saveContact::success = "+response.toString()+" errorCode="+errorCode);
	        		SQLiteDatabase db = null;
	        		if (errorCode==201 || errorCode==200) {
	        			Toast.makeText(_instance,"Conatct Saved successfully please restart the app",Toast.LENGTH_SHORT).show();
	        			
	        			/*try {  
	        			
	        			  
	        			    db = openOrCreateDatabase( "RCSContacts.db"        , SQLiteDatabase.CREATE_IF_NECESSARY 
	        			    		, null          );
	        			   
	        		        String newRCSContactId= rcsIdentity.substring(5);
	        		        if(newRCSContactId.contains("@")){
	        		        	newRCSContactId= newRCSContactId.substring(0, newRCSContactId.indexOf("@"));
	        		        }
	        		        String sql =
	        		                "INSERT INTO "+ SplashActivity.tableName +"(RCSContactID, RCSContactInfo) VALUES('"+newRCSContactId +"','"+contactUri+"')" ;       
	        		                    db.execSQL(sql);
	        		                   
	        			}
	        		    catch (Exception e) {}finally{
	        		    	 db.close();
	        		    }*/
	        			closeHandler.sendEmptyMessage(0);
	        		} else {
	        			if(SplashActivity.showLog)
        		Log.d("RCSClient","Json exception");
						Message msg=new Message();
						msg.what=errorCode;
						errorHandler.sendMessage(msg);
	        		}
	        	}
	        });

		} catch (JSONException e1) { 
			if(SplashActivity.showLog)
        		Log.d("RCSClient","Json exception");
		} catch (UnsupportedEncodingException e) {
		}
    }

    public void cancelSave(View view) {
    	finish();
    }
    
}
