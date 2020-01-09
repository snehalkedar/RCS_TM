package org.gsm.rcsApp.activities;

import java.util.ArrayList;

import org.gsm.RCSDemo.R;
import org.gsm.rcsApp.misc.Notifications;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class InboxActivity extends Activity {
	
	/**
	 * @uml.property  name="inbox"
	 * @uml.associationEnd  
	 */
	TextView inbox = null;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inbox_layout);
		inbox=(TextView)findViewById(R.id.inboxdata);
		String inboxMatter = new String();
		ArrayList<Notifications> notifications = getIntent().getParcelableArrayListExtra(MainActivity.ALL_NOTIFICATION);
		if(notifications!=null){
			for(Notifications n : notifications){
				inboxMatter = inboxMatter + System.getProperty ("line.separator")+ n.getText();
			}
			inbox.setText(inboxMatter);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
}
