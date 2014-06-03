package com.himenu.cocoprint;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class InstagramPhotoDao {

	private SQLiteDatabase database;
	private DbTools dbHelper;
	private String[] allColumns = { DbTools.INSTAGRAM_COLUMN_ID,
			DbTools.INSTAGRAM_COLUMN_THUMB, DbTools.INSTAGRAM_COLUMN_LARGE };

	public InstagramPhotoDao(Context context) {
		dbHelper = new DbTools(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public InstagramPhoto createInstagramPhoto(String thumb, String large) {
		ContentValues values = new ContentValues();
		values.put(DbTools.INSTAGRAM_COLUMN_THUMB, thumb);
		values.put(DbTools.INSTAGRAM_COLUMN_LARGE, large);
		open();
		long insertId = database
				.insert(DbTools.INSTAGRAM_TABLE_NAME, null, values);
		Cursor cursor = database.query(DbTools.INSTAGRAM_TABLE_NAME, allColumns,
				DbTools.INSTAGRAM_COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		InstagramPhoto newPhoto = cursorToInstagramPhoto(cursor);
		cursor.close();
		close();
		return newPhoto;
	}

	public void deleteInstagramPhoto(Photo photo) {
		long id = photo.getId();
		open();
		System.out.println("InstagramPhoto deleted with id: " + id);
		database.delete(DbTools.INSTAGRAM_TABLE_NAME, DbTools.PHOTOS_COLUMN_ID
				+ " = " + id, null);
		close();
	}

	public List<InstagramPhoto> getAllInstagramPhotos() {
		List<InstagramPhoto> instagramPhotos = new ArrayList<InstagramPhoto>();
		open();
		Cursor cursor = database.query(DbTools.INSTAGRAM_TABLE_NAME, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			InstagramPhoto instagramPhoto = cursorToInstagramPhoto(cursor);
			instagramPhotos.add(instagramPhoto);
			cursor.moveToNext();
		}
		cursor.close();
		close();
		return instagramPhotos;
	}

	private InstagramPhoto cursorToInstagramPhoto(Cursor cursor) {
		InstagramPhoto instagramPhoto = new InstagramPhoto();
		instagramPhoto.setId(cursor.getLong(0));
		instagramPhoto.setThumb(cursor.getString(1));
		instagramPhoto.setLarge(cursor.getString(2));
		instagramPhoto.setAlbumId(cursor.getLong(2));
		return instagramPhoto;
	}

	public List<ImageMedia> getAlbumInstagramPhotos(long albumId) {
		List<ImageMedia> photos = new ArrayList<ImageMedia>();
		open();
		Cursor cursor = database.query(DbTools.INSTAGRAM_TABLE_NAME, allColumns,
				DbTools.INSTAGRAM_COLUMN_ALBUM + "=?", new String[] { albumId + "" },
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			InstagramPhoto photo = cursorToInstagramPhoto(cursor);
			photos.add(photo);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		close();
		return photos;

	}
}
