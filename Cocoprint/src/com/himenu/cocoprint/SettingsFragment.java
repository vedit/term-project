package com.himenu.cocoprint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SettingsFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.settings_fragment,
				container, false);
		TextView textView = (TextView) rootView
				.findViewById(R.id.section_label);
		Button get_instagram = (Button) rootView
				.findViewById(R.id.login_instagram);
		get_instagram.setOnClickListener((OnClickListener) MainActivity._activity);

		return rootView;
	}
}