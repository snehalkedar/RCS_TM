package org.gsm.rcsApp.activities;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.gsm.RCSDemo.R;
import org.gsm.rcsApp.misc.Notifications;

public class NotificationsActivity extends Activity {
	/**
	 * @uml.property  name="txt"
	 * @uml.associationEnd  
	 */
	TextView txt = null;
	/**
	 * @uml.property  name="viewButton"
	 * @uml.associationEnd  
	 */
	Button viewButton = null;
	/**
	 * @uml.property  name="img"
	 * @uml.associationEnd  
	 */
	ImageView img = null;
	/**
	 * @uml.property  name="filename"
	 */
	String filename;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications);
		
		
		
	    
	}

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		txt=(TextView)findViewById(R.id.NotificationText);
		img=(ImageView)findViewById(R.id.NotificationImg);
		
		Notifications notification = getIntent().getParcelableExtra("NOTIFICATION");
		if(notification!=null){
			String messageText = notification.getText();
			String type = notification.getMessageType();
		
		txt.setText(messageText);
		
		if(type.equalsIgnoreCase(Notifications.NOTIFICATION_TYPE_FAEBOOK_PHOTOTAG)){
			 filename = notification.getfilePath();
			 Log.d("RCSClient","NOTIFICATION_TYPE_FAEBOOK_PHOTOTAG : "+filename);
			img.setImageDrawable(new BitmapDrawable(filename));
		}else
			img.setVisibility(View.INVISIBLE);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	public void ViewFile(View view){
		if(SplashActivity.showLog)
        		Log.d("RCSClient","ViewFile");
		Intent showFile = new Intent();
		 showFile.setAction(Intent.ACTION_VIEW);
		 String FileUrl="file://"+filename;
		 showFile.setDataAndType(Uri.parse(FileUrl),MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(FileUrl)));
		 startActivity(showFile);
		
	}
	public void AcceptFiletransfer(View view){
		if(SplashActivity.showLog)
        		Log.d("RCSClient","AcceptFiletransfer");
		
	}
	
	public void CancelFileTransferButton(View view){
		if(SplashActivity.showLog)
        		Log.d("RCSClient","CancelFileTransferButton");
		
	}
}

