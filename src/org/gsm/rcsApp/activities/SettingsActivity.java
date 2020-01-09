package org.gsm.rcsApp.activities;


import org.gsm.RCSDemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class SettingsActivity extends Activity {

	public static String GTALK_ACCOUNT ="tel:+918410123415";
	public static String SKYPE_ACCOUNT ="tel:+918410123414";
	
	public static String FACEBOOK_ACCOUNT ="tel:+918410123416";
	public static String TWITTER_ACCOUNT ="tel:+918410123417";
	/**
	 * @uml.property  name="gtalktext"
	 * @uml.associationEnd  
	 */
	private TextView gtalktext;
	/**
	 * @uml.property  name="skypetext"
	 * @uml.associationEnd  
	 */
	private TextView skypetext;
	/**
	 * @uml.property  name="facebootext"
	 * @uml.associationEnd  
	 */
	private TextView facebootext;
	/**
	 * @uml.property  name="twittertext"
	 * @uml.associationEnd  
	 */
	private TextView twittertext;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
        
		gtalktext=(TextView)findViewById(R.id.GtalkAcount);
		skypetext=(TextView)findViewById(R.id.SkypeAcount);
		facebootext=(TextView)findViewById(R.id.FacebookAcount);
		twittertext=(TextView)findViewById(R.id.TwitterAcount);
	}

	public void SaveAccounts(View v) {
		SettingsActivity.GTALK_ACCOUNT =(String) gtalktext.getText();
		SettingsActivity.SKYPE_ACCOUNT =(String) skypetext.getText();
		SettingsActivity.FACEBOOK_ACCOUNT =(String) facebootext.getText();
		SettingsActivity.TWITTER_ACCOUNT =(String) twittertext.getText();
		
		
	}

}
