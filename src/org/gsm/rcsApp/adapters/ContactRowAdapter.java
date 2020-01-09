package org.gsm.rcsApp.adapters;

import java.util.ArrayList;

import org.gsm.RCSDemo.R;
import org.gsm.rcsApp.RCS.Contact;
import org.gsm.rcsApp.RCS.ContactExtended;
import org.gsm.rcsApp.activities.ListNetworkContacts;
import org.gsm.rcsApp.activities.MainActivity;
import org.gsm.rcsApp.activities.SplashActivity;

import android.R.style;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

public class ContactRowAdapter extends BaseAdapter {
	 
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
    /**
	 * @uml.property  name="isMain"
	 */
    boolean isMain;
    
    public ContactRowAdapter(Activity a, ArrayList<ContactExtended> retrievedContacts,boolean isMainActivity) {
        activity = a;
        data=retrievedContacts;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
        isMain=isMainActivity;
    }
   
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
    	  ContactExtended contact=data.get(position);
    	  return contact;
    }
 
    public long getItemId(int position) {
        return position;
    }
   

    public void onClick(View v) 
    {
    	if(SplashActivity.showLog)
        		Log.d("RCSClient","List view clicked");
    }
    
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ContactExtended contact=data.get(position);
        
        if (convertView==null) vi = inflater.inflate(R.layout.contact_row, null);
 
        View thumbnail_wrapper=vi.findViewById(R.id.thumbnail_wrapper);
        TextView contactNameView = (TextView)vi.findViewById(R.id.contactName); 
         
   //     TextView contactStatusView = (TextView)vi.findViewById(R.id.contactStatus); 
      //  TextView contactInfoView = (TextView)vi.findViewById(R.id.contactInfo);
        TextView newMessageIndicator = (TextView)vi.findViewById(R.id.newMessageIndicator);
        ImageView thumb_image=(ImageView)thumbnail_wrapper.findViewById(R.id.thumbnail_image); // thumb image
   //    ImageView contactType=(ImageView)vi.findViewById(R.id.ContactType);
        
        ImageButton joynButton = (ImageButton)vi.findViewById(R.id.ContactTypeRCS);
        
        if(contact.getRCSID().equalsIgnoreCase(ContactExtended.ACCOUNT_NOT_AVAILABLE)){
        
        	joynButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.joyn_grey));
        	
        }else{
        	
        	if(contact.getRCSStatus().equalsIgnoreCase(ContactExtended.CONATCT_AWAY))
        		joynButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.joyn));
        	else if(contact.getGtalkStatus().equalsIgnoreCase(ContactExtended.CONTACT_BUSY))
        		joynButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.joyn));
        	else
        		joynButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.joyn));
        }
        
        joynButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View paramView) {
			
				 String ContactId= data.get(position).getRCSID();
					if(SplashActivity.showLog)
        		Log.d("RCSClient"," onClick : "+ position +"Joyn Id "+ContactId);
				 if(isMain){
					MainActivity.setcurrentChatSessionContactUri(ContactId);
					data.get(position).setHasNewMessage(false,ContactId);
					((MainActivity)activity).startChat(data.get(position));
				}
				else{
					((ListNetworkContacts)activity).setSelectedItem(data.get(position),ContactId);
				}
			}
		});
        
        ImageButton gtalkButton = (ImageButton)vi.findViewById(R.id.ContactTypeGtalk);
        
        if(contact.getGtalkID().equalsIgnoreCase(ContactExtended.ACCOUNT_NOT_AVAILABLE )){
        	
        	gtalkButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.gtalk_mini_greyed));
        	
        }else{
        	gtalkButton.setClickable(true);
        	gtalkButton.setEnabled(true);
        	if(contact.getGtalkStatus().equalsIgnoreCase(ContactExtended.CONATCT_AWAY))
        		gtalkButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.gtalk_mini_away));
        	else if(contact.getGtalkStatus().equalsIgnoreCase(ContactExtended.CONTACT_BUSY))
        		gtalkButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.gtalk_mini_busy));
        	else if(contact.getGtalkStatus().equalsIgnoreCase(ContactExtended.CONTACT_OFFLINE))
        		gtalkButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.gtalk_mini_greyed));
        	else
        		gtalkButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.gtalk_mini_online));
        }
        gtalkButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View paramView) {
				  
				if(SplashActivity.showLog)
        		Log.d("RCSClient"," onClick : "+ position +"Joyn Id "+data.get(position).getGtalkID());
				 String ContactId= data.get(position).getGtalkID();
				if(isMain){
					
					if(SplashActivity.showLog)
        		Log.d("RCSClient"," onClick : "+ position +"Joyn Id "+ContactId);
					MainActivity.setcurrentChatSessionContactUri(ContactId);
					data.get(position).setHasNewMessage(false,ContactId);
					((MainActivity)activity).startChat(data.get(position));
				}
				else{
					((ListNetworkContacts)activity).setSelectedItem(data.get(position),ContactId);
				}
				
			}
		});
        
        ImageButton skypeButton = (ImageButton)vi.findViewById(R.id.ContactTypeSkype);
        if(contact.getSkypeID().equalsIgnoreCase(ContactExtended.ACCOUNT_NOT_AVAILABLE)  ){
        	
        	skypeButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.skype_mini_grey));
        	
        }else{
        	skypeButton.setClickable(true);
        	skypeButton.setEnabled(true);
        	if(contact.getSkypeStatus().equalsIgnoreCase(ContactExtended.CONATCT_AWAY))
        		skypeButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.small_skype_icon_away));
        	else if(contact.getSkypeStatus().equalsIgnoreCase(ContactExtended.CONTACT_BUSY))
        		skypeButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.small_skype_icon_busy));
        	else if(contact.getSkypeStatus().equalsIgnoreCase(ContactExtended.CONTACT_OFFLINE))
        		skypeButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.skype_mini_grey));
        	else
        		skypeButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.small_skype_icon_online));
        }
        skypeButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View paramView) {
				  
				if(SplashActivity.showLog)
        		Log.d("RCSClient"," onClick : "+ position +"Joyn Id "+data.get(position).getSkypeID());
				 String ContactId= data.get(position).getSkypeID();
				if(isMain){
					
					if(SplashActivity.showLog)
        		Log.d("RCSClient"," onClick : "+ position +"Joyn Id "+ContactId);
					MainActivity.setcurrentChatSessionContactUri(ContactId);
					data.get(position).setHasNewMessage(false,ContactId);
					((MainActivity)activity).startChat(data.get(position));
				}
				else{
					
					((ListNetworkContacts)activity).setSelectedItem(data.get(position),ContactId);
				}
			}
		});
        
        ImageButton fbButton = (ImageButton)vi.findViewById(R.id.ContactTypeFacebook);
        
        if(contact.getFacebookID().equalsIgnoreCase(ContactExtended.ACCOUNT_NOT_AVAILABLE) ){
        
        	fbButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.facebook_small_grey));
        	
        }else{
        	fbButton.setClickable(true);
        	fbButton.setEnabled(true);
        	if(contact.getFacebookStatus().equalsIgnoreCase(ContactExtended.CONATCT_AWAY))
        		fbButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.facebook_small));
        	else if(contact.getFacebookStatus().equalsIgnoreCase(ContactExtended.CONTACT_BUSY))
        		fbButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.facebook_small));
        	else  if(contact.getFacebookStatus().equalsIgnoreCase(ContactExtended.CONTACT_OFFLINE))
        		fbButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.facebook_small_grey));
        	else
        	fbButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.facebook_mini));
        }
        fbButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View paramView) {
				  
				if(SplashActivity.showLog)
        		Log.d("RCSClient"," onClick : "+ position +"Joyn Id "+data.get(position).getFacebookID() );
				String ContactId= data.get(position).getFacebookID();
				if(isMain){
					 
					if(SplashActivity.showLog)
        		Log.d("RCSClient"," onClick : "+ position +"Joyn Id "+ContactId);
					MainActivity.setcurrentChatSessionContactUri(ContactId);
					data.get(position).setHasNewMessage(false,ContactId);
					((MainActivity)activity).startChat(data.get(position));
				}
				else{
					((ListNetworkContacts)activity).setSelectedItem(data.get(position),ContactId);
				}
			}
		});
        
      
        
        String displayName=contact.getDisplayName();
       
        //joynButton.setBackgroundColor(Color.rgb(255,140,0));
      //  String contactInfo=contact.getContactInfo();
     //  String capabilities=contact.getCapabilities();
     //   String accType = contact.getAccountype();
        
        int icon=contact.getIcon();
 
        contactNameView.setText(displayName);
      //  contactInfoView.setText(contactInfo);
      /*  if(accType.equalsIgnoreCase("skype"))
        	contactType.setImageDrawable(activity.getResources().getDrawable(R.drawable.skype_mini));
        else if(accType.equalsIgnoreCase("gtalk"))
        	   contactType.setImageDrawable(activity.getResources().getDrawable(R.drawable.gtalk_mini));
        else if(accType.equalsIgnoreCase("facebook"))
        		contactType.setImageDrawable(activity.getResources().getDrawable(R.drawable.facebook_mini));
        else if(accType.equalsIgnoreCase("twitter"))
    		contactType.setImageDrawable(activity.getResources().getDrawable(R.drawable.twitter_small));
        else if(accType.equalsIgnoreCase("linkedIn"))
        	contactType.setImageDrawable(activity.getResources().getDrawable(R.drawable.linkedin));
        	
        else
        	contactType.setImageDrawable(activity.getResources().getDrawable(R.drawable.joyn));
        if (capabilities!=null) {
        	if (capabilities.indexOf("IM_SESSION")>-1) {
            //	contactStatusView.setText("online");
            //	contactStatusView.setTextColor(Color.GREEN);
        	} else {
            //	contactStatusView.setText("offline");
            //	contactStatusView.setTextColor(Color.RED);
        	}
        } else {
        	//contactStatusView.setText("unknown");
        	//contactStatusView.setTextColor(Color.BLACK);
        }*/
        
        if (contact.isHasNewMessage()) {
        	//newMessageIndicator.setVisibility(View.VISIBLE);
        	contactNameView.setTypeface(null, Typeface.BOLD);
        	String fromAcc = contact.getnewmesgfrom();
        	if(fromAcc.equalsIgnoreCase("joyn"))
        		joynButton.setBackgroundColor(Color.rgb(255,140,0));
        	else if(fromAcc.equalsIgnoreCase("skype"))
            		skypeButton.setBackgroundColor(Color.rgb(255,140,0));
        	else if(fromAcc.equalsIgnoreCase("gtalk"))
        		gtalkButton.setBackgroundColor(Color.rgb(255,140,0));
        	else if(fromAcc.equalsIgnoreCase("facebook"))
        		fbButton.setBackgroundColor(Color.rgb(255,140,0));
            	
        } else {
        	joynButton.setBackgroundColor(Color.TRANSPARENT);
        	skypeButton.setBackgroundColor(Color.TRANSPARENT);
        	gtalkButton.setBackgroundColor(Color.TRANSPARENT);
        	fbButton.setBackgroundColor(Color.TRANSPARENT);
        	contactNameView.setTypeface(null,Typeface.NORMAL);
        }
        
       
        newMessageIndicator.setVisibility(View.INVISIBLE);
        thumb_image.setImageDrawable(activity.getResources().getDrawable(icon));
        
        
        return vi;
    }
}