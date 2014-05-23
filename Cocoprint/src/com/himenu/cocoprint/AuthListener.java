package com.himenu.cocoprint;

import android.widget.Toast;

public class AuthListener implements InstaImpl.AuthAuthenticationListener {
	/**
	 * 
	 */
	private final MainActivity mainActivity;

	/**
	 * @param mainActivity
	 */
	AuthListener(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public void onSuccess() {
		Toast.makeText(this.mainActivity,
				"Instagram Authorization Successful", Toast.LENGTH_SHORT)
				.show();
		InstaImpl.instaLoginDialog.dismiss();
	}

	@Override
	public void onFail(String error) {
		Toast.makeText(this.mainActivity, "Authorization Failed",
				Toast.LENGTH_SHORT).show();
		InstaImpl.instaLoginDialog.dismiss();

	}
}