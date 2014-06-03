package com.himenu.cocoprint;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
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

	private NavigationDrawerFragment mNavigationDrawerFragment;

	// Instagram Values

	static final int SELECT_PICTURE = 666;
	static final int AVIARY_PICTURE = 777;
	static ImageView selectedImage;
	static Uri selectedImageUri;
	private static Context _appContext;
	public static Activity _activity;
	private static long albumId;
	
	private CharSequence mTitle;
	static InstaImpl mInstaImpl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SessionStore store = new SessionStore(this);
		SharedPreferences prefs = store.getSharedPreferences();
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		_appContext = this;
		_activity = this;
		albumId = store.getCurrentAlbum();
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
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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

	public void replacePicture(Uri uri) {
		ParcelFileDescriptor parcelFD = null;
		try {
			PhotoDao photoDao = new PhotoDao(this);
			parcelFD = getContentResolver().openFileDescriptor(uri, "r");
			FileDescriptor imageSource = parcelFD.getFileDescriptor();
			Log.e("ReplacePic", imageSource.toString());
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(imageSource, null, o);

			final int REQUIRED_SIZE = 1024;

			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource,
					null, o2);
			String imageName = generateFileName(imageSource);
			savePic(bitmap, imageName);
			selectedImage.setImageBitmap(bitmap);
			Log.e("ALBUMID", albumId+"");
			photoDao.createPhoto(imageName, albumId);
			List<Photo> a = photoDao.getAllPhotos();
			for(int i=0; i<a.size(); i++){
				Log.e("pics", a.get(i).toString());
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (parcelFD != null)
				try {
					parcelFD.close();
				} catch (IOException e) {
				}
		}
	}

	public String generateMd5(String input) throws Exception{
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.reset();
		m.update(input.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1,digest);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while(hashtext.length() < 32 ){
		  hashtext = "0"+hashtext;
		}
		return hashtext;
	}
	
	public String generateFileName(FileDescriptor fd) {
		String hashcode = ""+fd.hashCode();
		String digest = ""+fd.hashCode();
		try {
			digest = generateMd5(hashcode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return digest + ".png";
//		return RandomStringUtils.randomAlphanumeric(10).toUpperCase() + ".png";
	}

	public void savePic(Bitmap bitmap, String filename) {
		Log.e("FILENAME", filename);
		FileOutputStream out = null;
		try {
			out = openFileOutput(filename, Context.MODE_PRIVATE);
			// out = new FileOutputStream(filename);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Throwable ignore) {
			}
		}
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public boolean createDirIfNotExists(String path) {
		boolean ret = true;
		if (isExternalStorageWritable()) {
			File file = new File(Environment.getExternalStorageDirectory(),
					path);
			if (!file.exists()) {
				if (!file.mkdirs()) {
					Log.e("TravellerLog :: ", "Problem creating Image folder");
					ret = false;
				}
			}
		} else {
			ret = false;
		}
		return ret;
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
			// MainActivity.mInstaImpl.getGallery();
		}
	}
}
