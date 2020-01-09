package org.gsm.rcsApp.activities;

import java.util.ArrayList;

import org.gsm.RCSDemo.R;
import org.gsm.rcsApp.RCS.Contact;
import org.gsm.rcsApp.RCS.ContactExtended;
import org.gsm.rcsApp.adapters.ContactRowAdapter;
import org.gsm.rcsApp.adapters.SimpleContactAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.View;

public class ListNetworkContacts extends Activity {
	/**
	 * @uml.property  name="selectedContact"
	 * @uml.associationEnd  
	 */
	ContactExtended selectedContact=null;
	static ContactRowAdapter Chatlistcradapter=null;
	private static ArrayList<ContactExtended> retrievedChatlistContacts=new ArrayList<ContactExtended>();
	static ListView chatlistcontactListView=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		  setContentView(R.layout.contactlist);
		  Intent intent = getIntent();
		  retrievedChatlistContacts =intent.getParcelableArrayListExtra(ChatSessionActivity.ALL_CONTACTS);
		  Chatlistcradapter=new ContactRowAdapter(this, retrievedChatlistContacts,false);
		  chatlistcontactListView =(ListView)findViewById(R.id.contactistforgroupchat);
		  chatlistcontactListView.setAdapter(Chatlistcradapter);
		  chatlistcontactListView.setFocusable(true);
		  
		  chatlistcontactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    	@SuppressWarnings("rawtypes")
				public void onItemClick(AdapterView parent, View view, int position, long id) {
		    		 selectedContact = (ContactExtended) Chatlistcradapter.getItem(position);
		    		 
		    		Intent data = new Intent();
		    		data.putExtra("selectedContact",selectedContact);
		    		setResult(RESULT_OK, data);
		    		finish();
		    	}
		    });
	}

	public void setSelectedItem(ContactExtended selectedContact ,String ContactURI){
		Intent data = new Intent();
		data.putExtra("selectedContact",selectedContact);
		data.putExtra("ContactURI",ContactURI);
		setResult(RESULT_OK, data);
		finish();
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

}
