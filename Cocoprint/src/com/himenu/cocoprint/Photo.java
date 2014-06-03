package com.himenu.cocoprint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Photo extends ImageItem {
	private long id;
	private String uri;
	private long albumId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}
	
	public String toString(){
		return uri + " " + albumId;
	}

	@Override
	public Bitmap getBitmap() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile("/sdcard/sdcard/CocoPrint/gallery/" + uri, options);
	}
}
