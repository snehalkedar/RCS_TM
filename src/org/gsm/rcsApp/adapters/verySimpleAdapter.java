package org.gsm.rcsApp.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.gsm.RCSDemo.R;
import org.gsm.rcsApp.RCS.Contact;
import org.gsm.rcsApp.RCS.ContactExtended;
import org.gsm.rcsApp.activities.ChatSessionActivity;
import org.gsm.rcsApp.activities.MainActivity;
import org.gsm.rcsApp.activities.SplashActivity;

import com.fedorvlasov.lazylist.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class verySimpleAdapter extends BaseAdapter {
	/**
	 * @uml.property  name="activity"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Activity activity;
    /**
	 * @uml.property  name="data"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="org.gsm.rcsApp.RCS.ContactExtended"
	 */
    private ArrayList<ContactExtended> data;
    private static LayoutInflater inflater=null;
    /**
	 * @uml.property  name="imageLoader"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    public ImageLoader imageLoader; 
    
    public verySimpleAdapter(Activity a, ArrayList<ContactExtended> retrievedContacts ) {
      
    	activity = a;
        data=retrievedContacts;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
        
    }
	public int getCount() {
		// TODO Auto-generated method stub
		 return data.size();
		
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		  return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		 return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		  View vi=convertView;
		 if(data.size()==1)
	    	   if(SplashActivity.showLog)
        		Log.d("RCSClient","this is single chat");
		 else
			 if(SplashActivity.showLog)
        		Log.d("RCSClient","this is grp chat");
	
			 
	        if (convertView==null) vi = inflater.inflate(R.layout.verysimplecontact, null);
	      //  View thumbnail_wrapper=vi.findViewById(R.id.chatcontactthumbnail_wrapper);
	        TextView contactNameView = (TextView)vi.findViewById(R.id.simplechatcontactName); 
	        TextView statusView =(TextView)vi.findViewById(R.id.ContactStatus);
	   	 if(data.size()==1)
	   		 statusView.setVisibility(View.INVISIBLE);
	      //  TextView contactInfoView = (TextView)vi.findViewById(R.id.chatcontactInfo);
	        ImageView thumb_image=(ImageView)vi.findViewById(R.id.simplethumbnail_image); // thumb image

	        ContactExtended contact=data.get(position);
	        
	        String displayName=contact.getDisplayName();
	        String status = ChatSessionActivity.statusMap.get(displayName);
	     //   String contactInfo=contact.getContactId();
	     //   String capabilities=contact.getCapabilities();
	        int icon=contact.getIcon();
	 
	        contactNameView.setText(displayName);
	     
	          if(status!=null)
	        	  statusView.setText(status);
	   
	        //     contactInfoView.setText(contactInfo);
	        
	        thumb_image.setImageDrawable(activity.getResources().getDrawable(icon));
	        return vi;
	}

}

