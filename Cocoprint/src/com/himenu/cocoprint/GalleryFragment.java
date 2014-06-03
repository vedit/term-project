package com.himenu.cocoprint;

import java.util.ArrayList;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;


public class GalleryFragment extends Fragment {
    private GridView gridView;
    private GridViewAdapter customGridAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_gallery,
				container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        customGridAdapter = new GridViewAdapter(MainActivity.getAppContext(), R.layout.row_grid, getData());
        gridView.setAdapter(customGridAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(MainActivity.getAppContext(), position + "#Selected",
						Toast.LENGTH_SHORT).show();
			}

		});
		return rootView;
	}
    private ArrayList getData() {
        final ArrayList imageItems = new ArrayList();
        PhotoDao photoDao = new PhotoDao(MainActivity.getAppContext());
        imageItems.addAll(photoDao.getAllPhotos());
 
        return imageItems;
 
    }
    
}

