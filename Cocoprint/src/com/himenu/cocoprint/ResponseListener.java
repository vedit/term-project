/*package com.himenu.cocoprint;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class ResponseListener extends BroadcastReceiver {

	public static final String ACTION_RESPONSE = "com.himenu.cocoprint.responselistener";
	public static final String EXTRA_NAME = "90293d69-2eae-4ccd-b36c-a8d0c4c1bec6";
	public static final String EXTRA_ACCESS_TOKEN = "bed6838a-65b0-44c9-ab91-ea404aa9eefc";

	@Override
	public void onReceive(Context context, Intent intent) {
		MainActivity.mInstaImpl.dismissDialog();
		Bundle extras = intent.getExtras();
		String name = extras.getString(EXTRA_NAME);
		String accessToken = extras.getString(EXTRA_ACCESS_TOKEN);
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.getAppContext());
		alertDialog.setTitle("Your Details");
		alertDialog.setMessage("Name - " + name + ", Access Token - "
				+ accessToken);
		alertDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		alertDialog.show();
//		MainActivity.mInstaImpl.getGallery();
	}
}*/