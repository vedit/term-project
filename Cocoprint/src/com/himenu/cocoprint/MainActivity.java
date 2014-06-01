package com.himenu.cocoprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviary.android.feather.library.Constants;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks, OnClickListener {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	// Instagram Values

	private static final int INSTALOGIN = 555;
	static final int SELECT_PICTURE = 666;
	static final int AVIARY_PICTURE = 777;
	static ImageView selectedImage;
	static Uri selectedImageUri;
	private static Context _appContext;
	public static Activity _activity;
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	static InstaImpl mInstaImpl;
	private ResponseListener mResponseListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		_appContext = this;
		_activity = this;
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (position == 0) {
			PictureFragment pictureFragment = new PictureFragment();
			ft.replace(R.id.container, pictureFragment);
		} else if (position == 1) {
			SettingsFragment settingsFragment = new SettingsFragment();
			ft.replace(R.id.container, settingsFragment);
		} else if (position == 2) {
			FooFragment fooFragment = new FooFragment();
			ft.replace(R.id.container, fooFragment);
		}

		ft.commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static Context getAppContext() {
		return _appContext;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Log.e("SELEEEECT", requestCode + "");
			switch (requestCode) {
			case 131738:
				selectedImageUri = data.getData();
				replacePicture(selectedImageUri);
				break;
			case 131849:
				// output image path
				Uri mImageUri = data.getData();
				Log.e("AVIARYPIC", getPath(mImageUri));
				replacePicture(mImageUri);
				Bundle extra = data.getExtras();
				if (null != extra) {
					// image has been changed by the user?
					boolean changed = extra
							.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
				}
				break;
			}
		}
	}

	public void replacePicture(Uri imageUri) {
		String selectedImagePath = getPath(imageUri);
		Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
		selectedImage.setImageBitmap(bitmap);
	}

	public String getPath(Uri uri) {
		if (uri == null) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		return uri.getPath();
	}

	public static class SettingsFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.settings_fragment,
					container, false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label);
			Button select_image = (Button) rootView
					.findViewById(R.id.select_image);
			Button get_aviary = (Button) rootView.findViewById(R.id.get_aviary);

			return rootView;
		}
	}

	public static class FooFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.foo_fragment, container,
					false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label);
			Button select_image = (Button) rootView
					.findViewById(R.id.select_image);
			Button get_aviary = (Button) rootView.findViewById(R.id.get_aviary);
			Button get_instagram = (Button) rootView
					.findViewById(R.id.login_instagram);

			return rootView;
		}
	}

	@Override
	public void onClick(View v) {
		mInstaImpl = new InstaImpl(getAppContext());
		mInstaImpl.setAuthAuthenticationListener(new AuthListener(this));
	}

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
//			MainActivity.mInstaImpl.getGallery();
		}
	}
}
