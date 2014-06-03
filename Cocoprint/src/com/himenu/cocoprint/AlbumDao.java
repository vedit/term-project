package com.himenu.cocoprint;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AlbumDao {

	private SQLiteDatabase database;
	private DbTools dbHelper;
	private String[] allColumns = { DbTools.ALBUM_COLUMN_ID,
			DbTools.ALBUM_COLUMN_NAME, DbTools.ALBUM_COLUMN_CREATED,
			DbTools.ALBUM_COLUMN_UPDATED };

	public AlbumDao(Context context) {
		dbHelper = new DbTools(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Album createAlbum(String name) {
		ContentValues values = new ContentValues();
		values.put(DbTools.ALBUM_COLUMN_NAME, name);
		open();
		long insertId = database.insert(DbTools.ALBUM_TABLE_NAME, null, values);
		Cursor cursor = database.query(DbTools.ALBUM_TABLE_NAME, allColumns,
				DbTools.ALBUM_COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Album newAlbum = cursorToAlbum(cursor);
		cursor.close();
		close();
		return newAlbum;
	}

	public void deleteAlbum(Album album) {
		long id = album.getId();
		open();
		System.out.println("Album deleted with id: " + id);
		database.delete(DbTools.ALBUM_TABLE_NAME, DbTools.ALBUM_COLUMN_ID
				+ " = " + id, null);
		close();
	}

	public List<Album> getAllAlbums() {
		List<Album> albums = new ArrayList<Album>();
		open();
		Cursor cursor = database.query(DbTools.ALBUM_TABLE_NAME, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Album album = cursorToAlbum(cursor);
			albums.add(album);
			cursor.moveToNext();
		}
		cursor.close();
		close();
		return albums;
	}

	private Album cursorToAlbum(Cursor cursor) {
		List<ImageItem> albumContent = null;
		
		PhotoDao photoDao = new PhotoDao(MainActivity.getAppContext());
		InstagramPhotoDao instagramPhotoDao = new InstagramPhotoDao(MainActivity.getAppContext());
		Album album = new Album();
		album.setId(cursor.getLong(0));
		album.setName(cursor.getString(1));
		album.setCreated(cursor.getLong(2));
		album.setUpdated(cursor.getLong(3));
		albumContent.addAll(photoDao.getAlbumPhotos(cursor.getLong(0)));
		albumContent.addAll(instagramPhotoDao.getAlbumInstagramPhotos(cursor.getLong(0)));
		album.setImages(albumContent);
		return album;
	}
}
