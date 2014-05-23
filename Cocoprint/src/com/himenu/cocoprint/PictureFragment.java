package com.himenu.cocoprint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class PictureFragment extends Fragment {
	 


	public PictureFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);

		Button get_instagram = (Button) rootView
				.findViewById(R.id.login_instagram);
		get_instagram.setOnClickListener((OnClickListener) MainActivity._activity);
		TextView textView = (TextView) rootView
				.findViewById(R.id.section_label);
		Button select_image = (Button) rootView
				.findViewById(R.id.select_image);
		Button get_aviary = (Button) rootView.findViewById(R.id.get_aviary);

		MainActivity.selectedImage = (ImageView) rootView
				.findViewById(R.id.current_image);
		select_image.setOnClickListener(imageSelector);
		get_aviary.setOnClickListener(aviaryGetter);
		return rootView;
	}

	View.OnClickListener aviaryGetter = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.getAppContext(),
					FeatherActivity.class);
			intent.setData(MainActivity.selectedImageUri);
			intent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET,
					"98b2bc152b0bb064");
			startActivityForResult(intent, MainActivity.AVIARY_PICTURE);

		}
	};

	View.OnClickListener imageSelector = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, MainActivity.SELECT_PICTURE);
		}
	};

}