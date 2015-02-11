package com.jeryalthaf.multiple;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainListActivity extends ListActivity {
	
	protected String[] mBlogPostTitles;
	public static final int NUMBER_OF_POSTS = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		if(isNetworkAvailable()){
			GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
			getBlogPostsTask.execute();
		}
		else{
			Toast.makeText(this,"Network is unavailable", Toast.LENGTH_LONG).show();
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		boolean isAvailable = false;
		if(networkinfo != null && networkinfo.isConnected()){
			isAvailable = true;
		}
		return isAvailable;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}
	
	private class GetBlogPostsTask extends AsyncTask<String, String, String> {
		
		String responseData = null;

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			int responseCode = -1;
			try {
				URL blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=20");
				HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
				connection.connect();
				
				responseCode = connection.getResponseCode();
				if(responseCode == HttpURLConnection.HTTP_OK){
					InputStream inputStream = connection.getInputStream();
					Reader reader = new InputStreamReader(inputStream);
					int contentlength = connection.getContentLength();
					char[] charArray = new char[contentlength];
					reader.read(charArray);
					responseData = new String(charArray);
					Log.v(TAG,responseData);
				}
				
				Log.i(TAG, "Code : " + responseCode);
						
			} catch (MalformedURLException e) {
				Log.e(TAG,"Exception caught ;", e);
			}
			catch (IOException e){
				Log.e(TAG,"Exception caught ;", e);
			}
			catch (Exception e){
				Log.e(TAG,"Exception caught ;", e);
			}
			
			return responseData;
		}	
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			List<String> list = new ArrayList<String>();
			list.add("Apple");
			
			try {
				JSONObject jso = new JSONObject(result);
				JSONArray jsa = jso.getJSONArray("posts");
				for (int i = 0; i < jsa.length(); i++) {
					list.add(jsa.getJSONObject(i).getString("title"));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			ListView lv = (ListView) findViewById(android.R.id.list);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
			lv.setAdapter(adapter);
			
		}
	}
	
	
}
