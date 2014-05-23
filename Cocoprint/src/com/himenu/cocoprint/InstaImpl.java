package com.himenu.cocoprint;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class InstaImpl {

	private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
	private static final String AUTHURL = "https://api.instagram.com/oauth/authorize/";
	public static final String APIURL = "https://api.instagram.com/v1";
	public static String CALLBACKURL;
	// private static final String TAG = "Instagram Demo";

	private SessionStore mSessionStore;

	private String mAuthURLString;
	private String mTokenURLString;
	private String mAccessTokenString;
	public String mClient_id;
	public String mClient_secret;
	private String mToken;
	public static InstaLoginDialog instaLoginDialog;
	private AuthAuthenticationListener mAuthAuthenticationListener;

	private ProgressDialog mProgressDialog;
	private Context mContext;

	public InstaImpl(Context context) {
		mContext = context;
		mSessionStore = new SessionStore(context);
		mClient_id = context.getResources().getString(R.string.instagram_id);
		mClient_secret = context.getResources().getString(
				R.string.instagram_secret);
		CALLBACKURL = context.getResources().getString(R.string.callbackurl);

		mAuthURLString = AUTHURL
				+ "?client_id="
				+ mClient_id
				+ "&redirect_uri="
				+ CALLBACKURL
				+ "&response_type=code&display=touch&scope=likes+comments+relationships";
		mTokenURLString = TOKENURL + "?client_id=" + mClient_id
				+ "&client_secret=" + mClient_secret + "&redirect_uri="
				+ CALLBACKURL + "&grant_type=authorization_code";

		InstaAuthDialogListener instaAuthDialogListener = new InstaAuthDialogListener();
		mAccessTokenString = mSessionStore.getInstaAccessToken();
		instaLoginDialog = new InstaLoginDialog(context, mAuthURLString,
				instaAuthDialogListener);
		instaLoginDialog.show();
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setTitle("Please Wait");
		mProgressDialog.setCancelable(true);

	}

	public class InstaAuthDialogListener implements
			InstaLoginDialog.AuthDialogListener {

		@Override
		public void onComplete(String token) {
			getAccessToken(token);
			instaLoginDialog.hide();
			// getGallery();
		}

		@Override
		public void onError(String error) {

		}

	}

	private void getAccessToken(String token) {
		this.mToken = token;
		new GetInstagramTokenAsyncTask().execute();
	}

	/*
	 * public void getGallery(){ new GetInstagramGallery().execute(); }
	 */

	public class GetInstagramTokenAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				URL url = new URL(mTokenURLString);
				HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url
						.openConnection();
				httpsURLConnection.setRequestMethod("POST");
				httpsURLConnection.setDoInput(true);
				httpsURLConnection.setDoOutput(true);

				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
						httpsURLConnection.getOutputStream());
				outputStreamWriter.write("client_id=" + mClient_id
						+ "&client_secret=" + mClient_secret
						+ "&grant_type=authorization_code" + "&redirect_uri="
						+ CALLBACKURL + "&code=" + mToken);

				outputStreamWriter.flush();

				// Response would be a JSON response sent by instagram
				String response = streamToString(httpsURLConnection
						.getInputStream());
				Log.e("USER Response", response);
				JSONObject jsonObject = (JSONObject) new JSONTokener(response)
						.nextValue();

				// Your access token that you can use to make future request
				mAccessTokenString = jsonObject.getString("access_token");
				// Log.e(TAG, mAccessTokenString);

				// User details like, name, id, tagline, profile pic etc.
				JSONObject userJsonObject = jsonObject.getJSONObject("user");
				// Log.e("USER DETAIL", userJsonObject.toString());

				// User ID
				String id = userJsonObject.getString("id");
				// Log.e(TAG, id);

				// Username
				String username = userJsonObject.getString("username");
				// Log.e(TAG, username);

				// User full name
				String name = userJsonObject.getString("full_name");
				// Log.e(TAG, name);
				mSessionStore.saveInstagramSession(mAccessTokenString, id,
						username, name);
				Log.e("TOKKKKKKEEEN", mAccessTokenString);
				instaLoginDialog.cancel();
				showResponseDialog(name, mAccessTokenString);
				;
			} catch (Exception e) {
				mAuthAuthenticationListener
						.onFail("Failed to get access token");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dismissDialog();
			mAuthAuthenticationListener.onSuccess();
			super.onPostExecute(result);
			new GetInstagramGallery().execute();
		}

		@Override
		protected void onPreExecute() {
			showDialog("Getting Access Token..");
			super.onPreExecute();
		}

	}

	public class GetInstagramGallery extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				SessionStore store = new SessionStore(mContext);
				SharedPreferences prefs = store.getSharedPreferences();
				String mApiURLString = APIURL + "/users/"
						+ prefs.getString("Api_Id", null)
						+ "/media/recent?access_token="
						+ prefs.getString("Api_access_token", null);
				URL url = new URL(mApiURLString);
				HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url
						.openConnection();
				String response = streamToString(httpsURLConnection
						.getInputStream());
				JSONObject jsonObject = (JSONObject) new JSONTokener(response)
						.nextValue();
				if (jsonObject.getJSONObject("meta").getInt("code") == 200) {
					JSONArray data_list = jsonObject.getJSONArray("data");
					for (int i = 0; i < data_list.length(); i++) {
						JSONObject data = (JSONObject) data_list.get(i);
							Log.e("STD", "COMON");
							JSONObject images = data.getJSONObject("images");
							JSONObject thumb = images.getJSONObject("thumbnail");
							JSONObject std = images.getJSONObject("standard_resolution");
							downloadFile(thumb.getString("url"));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}
	
	
	public void downloadFile (String f_url){
		
		try {
			URL url = new URL(f_url);
			URLConnection conection = url.openConnection();
			conection.connect();

			// this will be useful so that you can show a tipical 0-100%
			// progress bar
			int lenghtOfFile = conection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream(),
			        8192);

			// Output stream
			OutputStream output = new FileOutputStream(Environment
			        .getExternalStorageDirectory().toString()
			        + "/2011.kml");

			byte data[] = new byte[1024];

			// flushing output
			output.flush();

			// closing streams
			output.close();
			input.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setAuthAuthenticationListener(
			AuthAuthenticationListener authAuthenticationListener) {
		this.mAuthAuthenticationListener = authAuthenticationListener;
	}

	public interface AuthAuthenticationListener {
		public abstract void onSuccess();

		public abstract void onFail(String error);
	}

	public String streamToString(InputStream is) throws IOException {
		String string = "";

		if (is != null) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			string = stringBuilder.toString();
		}

		return string;
	}

	public void showDialog(String message) {
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
	}

	public void dismissDialog() {
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	public void showResponseDialog(String name, String accessToken) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(ResponseListener.ACTION_RESPONSE);
		broadcastIntent.putExtra(ResponseListener.EXTRA_NAME, name);
		broadcastIntent.putExtra(ResponseListener.EXTRA_ACCESS_TOKEN,
				accessToken);
		mContext.sendBroadcast(broadcastIntent);
	}
	/**
	 * Background Async Task to download file
	 * */
	
}