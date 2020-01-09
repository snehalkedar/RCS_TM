package org.gsm.rcsApp.adapters;

import java.util.ArrayList;

import org.gsm.RCSDemo.R;
import org.gsm.rcsApp.RCS.Contact;

import com.fedorvlasov.lazylist.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleContactAdapter extends BaseAdapter {
	/**
	 * @uml.property  name="activity"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Activity activity;
    /**
	 * @uml.property  name="data"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="org.gsm.rcsApp.RCS.Contact"
	 */
    private ArrayList<Contact> data;
    private static LayoutInflater inflater=null;
    /**
	 * @uml.property  name="imageLoader"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    public ImageLoader imageLoader; 
    public SimpleContactAdapter(Activity a, ArrayList<Contact> retrievedContacts) {
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
	        
	        if (convertView==null) vi = inflater.inflate(R.layout.simplecontact, null);
	        View thumbnail_wrapper=vi.findViewById(R.id.chatcontactthumbnail_wrapper);
	        TextView contactNameView = (TextView)vi.findViewById(R.id.chatcontactName); 
	        TextView contactInfoView = (TextView)vi.findViewById(R.id.chatcontactInfo);
	        ImageView thumb_image=(ImageView)thumbnail_wrapper.findViewById(R.id.chatcontactthumbnail_image); // thumb image

	        Contact contact=data.get(position);
	        
	        String displayName=contact.getDisplayName();
	        String contactInfo=contact.getContactId();
	        String capabilities=contact.getCapabilities();
	        int icon=contact.getIcon();
	 
	        contactNameView.setText(displayName);
	        contactInfoView.setText(contactInfo);
	        
	        thumb_image.setImageDrawable(activity.getResources().getDrawable(icon));
	        return vi;
	}

}
