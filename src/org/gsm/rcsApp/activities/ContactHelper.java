package org.gsm.rcsApp.activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.BufferedReader;

import org.gsm.RCSDemo.R;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class ContactHelper  {
	
	public static JSONObject contactJson = null;
	
	 
	 
	 
	 public static void CreateJSONFromFIle(Context c){
	  InputStream is = c.getResources().openRawResource(R.raw.json_file);
      Writer writer = new StringWriter();
      char[] buffer = new char[1024];
      try {
          Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
          int n;
          while ((n = reader.read(buffer)) != -1) {
              writer.write(buffer, 0, n);
              
          }
          String jsonString = writer.toString();
          contactJson =(JSONObject) parseResponse(jsonString);
	        if(SplashActivity.showLog)
        		Log.d("RCSClient","In Oncreate MainActivityNew"+contactJson.toString());
      }catch(Exception e){}
      finally {
          try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      }
	 }
	 
	 
	 protected static Object parseResponse(String responseBody) throws JSONException {
	        Object result = null;
	        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If Json is not valid this will return null
			responseBody = responseBody.trim();
			if(responseBody.startsWith("{") || responseBody.startsWith("[")) {
				result = new JSONTokener(responseBody).nextValue();
			}
			if (result == null) {
			      if(SplashActivity.showLog)
        		Log.d("RCSClient","result null");
				result = responseBody;
			}
			return result;
	    }
}
