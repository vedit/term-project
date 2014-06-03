package com.himenu.cocoprint;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PhotoDao {

	private SQLiteDatabase database;
	private DbTools dbHelper;
	private String[] allColumns = { DbTools.PHOTOS_COLUMN_ID,
			DbTools.PHOTOS_COLUMN_PATH, DbTools.PHOTOS_COLUMN_ALBUM };

	public PhotoDao(Context context) {
		dbHelper = new DbTools(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Photo createPhoto(String uri, long albumId) {
		ContentValues values = new ContentValues();
		values.put(DbTools.PHOTOS_COLUMN_PATH, uri);
		values.put(DbTools.PHOTOS_COLUMN_ALBUM, albumId);
		open();
		long insertId = database
				.insert(DbTools.PHOTOS_TABLE_NAME, null, values);
		Cursor cursor = database.query(DbTools.PHOTOS_TABLE_NAME, allColumns,
				DbTools.PHOTOS_COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Photo newPhoto = cursorToPhoto(cursor);
		cursor.close();
		close();
		return newPhoto;
	}

	public void deletePhoto(Photo photo) {
		long id = photo.getId();
		System.out.println("Photo deleted with id: " + id);
		database.delete(DbTools.PHOTOS_TABLE_NAME, DbTools.PHOTOS_COLUMN_ID
				+ " = " + id, null);
	}

	public List<Photo> getAllPhotos() {
		List<Photo> photos = new ArrayList<Photo>();
		open();
		Cursor cursor = database.query(DbTools.PHOTOS_TABLE_NAME, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Photo photo = cursorToPhoto(cursor);
			photos.add(photo);
			cursor.moveToNext();
		}
		cursor.close();
		close();
		return photos;
	}

	private Photo cursorToPhoto(Cursor cursor) {
		Photo photo = new Photo();
		photo.setId(cursor.getLong(0));
		photo.setUri(cursor.getString(1));
		photo.setAlbumId(cursor.getLong(2));
		return photo;
	}

	public List<ImageItem> getAlbumPhotos(long albumId) {
		List<ImageItem> photos = new ArrayList<ImageItem>();
		open();
		Cursor cursor = database.query(DbTools.PHOTOS_TABLE_NAME, allColumns,
				DbTools.PHOTOS_COLUMN_ALBUM + "=?", new String[] { albumId + "" },
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Photo photo = cursorToPhoto(cursor);
			photos.add(photo);
			cursor.moveToNext();
		}
		close();
		cursor.close();
		return photos;

	}
}
