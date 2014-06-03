package com.himenu.cocoprint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbTools extends SQLiteOpenHelper {

	public static final String ALBUM_TABLE_NAME = "album";
	public static final String ALBUM_COLUMN_ID = "_id";
	public static final String ALBUM_COLUMN_NAME = "name";
	public static final String ALBUM_COLUMN_CREATED = "created";
	public static final String ALBUM_COLUMN_UPDATED = "updated";

	public static final String PHOTOS_TABLE_NAME = "photos";
	public static final String PHOTOS_COLUMN_ID = "_id";
	public static final String PHOTOS_COLUMN_PATH = "path";
	public static final String PHOTOS_COLUMN_ALBUM = "album_id";

	public static final String INSTAGRAM_TABLE_NAME = "instagram";
	public static final String INSTAGRAM_COLUMN_ID = "_id";
	public static final String INSTAGRAM_COLUMN_THUMB = "thumbnail";
	public static final String INSTAGRAM_COLUMN_LARGE = "large";
	public static final String INSTAGRAM_COLUMN_ALBUM = "album_id";

	private static final String DATABASE_NAME = "cocoprint.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String ALBUM_CREATE = "CREATE TABLE "
			+ ALBUM_TABLE_NAME + "(" + ALBUM_COLUMN_ID
			+ " integer primary key autoincrement, " + ALBUM_COLUMN_NAME
			+ " text not null, " + ALBUM_COLUMN_CREATED
			+ " Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, "
			+ ALBUM_COLUMN_UPDATED
			+ "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL);";

	private static final String PHOTO_CREATE = "CREATE TABLE "
			+ PHOTOS_TABLE_NAME + "(" + PHOTOS_COLUMN_ID
			+ " integer primary key autoincrement, " + PHOTOS_COLUMN_PATH
			+ " text not null, " + PHOTOS_COLUMN_ALBUM + " integer not null);";

	private static final String INSTAGRAM_CREATE = "CREATE TABLE "
			+ INSTAGRAM_TABLE_NAME + "(" + INSTAGRAM_COLUMN_ID
			+ " integer primary key autoincrement, " + INSTAGRAM_COLUMN_THUMB
			+ " text not null, " + INSTAGRAM_COLUMN_LARGE + " text not null,"
			+ INSTAGRAM_COLUMN_ALBUM + " integer not null);";

	public DbTools(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(ALBUM_CREATE);
		database.execSQL(PHOTO_CREATE);
		database.execSQL(INSTAGRAM_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DbTools.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + ALBUM_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + PHOTOS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + INSTAGRAM_TABLE_NAME);
		onCreate(db);
	}

}