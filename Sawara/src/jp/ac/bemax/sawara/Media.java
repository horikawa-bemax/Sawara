package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Media {
	static final int PHOTO = 1;
	static final int MOVIE = 2;
	
	static final String TABLE_NAME = "media_table";
	static final String PATH = "path";
	static final String TYPE = "type";
	static final String ARTICLE_ID = "article_id";
	static final String MODIFIED = "modified";
	
	//private Context mContext;
	private SQLiteDatabase db;
	private long rowid;
	
	public Media(SQLiteDatabase db, long id){
		rowid = id;
		this.db = db;
	}
	
	public Media(SQLiteDatabase db, String path, long Type){
		this.db = db;
		
	}
	
	/*
	static Media[] getMedias(SQLiteOpenHelper helper){
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String[] columns = {"ROWID"};
		Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
		
		Media[] medias = new Media[cursor.getCount()];
		cursor.moveToFirst();
		for(int i=0; i<medias.length; i++){
			medias[i] = new Media();
			medias[i].id = cursor.getLong(0);
			cursor.moveToNext();
		}
		cursor.close();
		
		db.close();
		
		return medias;
	}
	*/
	

	static List<Media> findMediasByArticle(SQLiteDatabase db,  Article article){
		List<Media> mediaList = new ArrayList<Media>();
		String sql = "select ROWID from media_table while article_id=?";
		String[] selectionArgs = {""+article.getId()};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		
		while(cursor.moveToNext()){
			long id = cursor.getLong(0);
			Media media = new Media(db, id);
			mediaList.add(media);
		}
		
		return mediaList;
	}
	
	/*
	static Media findMediaById(db, long id){
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String[] columns = {"ROWID"};
		String[] selectionArgs = {""+id};
		Cursor cursor = db.query(TABLE_NAME, columns, "ROWID=?", selectionArgs, null, null, null);
		
		Media media = null;
		
		if(cursor.getCount() > 0){
			media = new Media();
			media.id = id;
		}
		
		db.close();
		
		return media;
	}
	*/
	
	public void delete(){

	}
	
	/**
	 * @return path
	 */
	public String getPath() {
		String path = null;
		Cursor cursor = null;
		
		try {
			cursor = readDB();
			cursor.moveToFirst();
			path = cursor.getString(cursor.getColumnIndex(PATH));
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return path;
	}

	/**
	 * @param path セットする path
	 */
	public void setPath(String path) {
		SQLiteDatabase db = null;
		contentValues.put(PATH, path);
		try {
			writeDB(contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return type
	 */
	public int getType() {
		int type = 0;
		Cursor cursor = null;
		
		try {
			cursor = readDB();
			cursor.moveToFirst();
			type = cursor.getInt(cursor.getColumnIndex(TYPE));
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return type;
	}

	/**
	 * @param type セットする type
	 */
	public void setType(int type) {
		contentValues.put(TYPE, type);
		try {
			writeDB(contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return modified
	 */
	public long getModified() {
		long modified = 0;
		Cursor cursor = null;
		
		try {
			cursor = readDB();
			cursor.moveToFirst();
			modified = cursor.getLong(cursor.getColumnIndex(MODIFIED));
			cursor.close();
		} catch (Exception e) {
			modified = -1;
			e.printStackTrace();
		}
		
		return modified;
	}

	/**
	 * @param modified セットする modified
	 */
	public void setModified(long modified) {
		contentValues.put(MODIFIED, modified);
		try {
			writeDB(contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return modified
	 */
	public long getArticleId() {
		long id = 0;
		Cursor cursor = null;
		
		try {
			cursor = readDB();
			cursor.moveToFirst();
			id = cursor.getLong(cursor.getColumnIndex(ARTICLE_ID));
			cursor.close();
		} catch (Exception e) {
			id = -1;
			e.printStackTrace();
		}
		
		return id;
	}

	/**
	 * @param modified セットする modified
	 */
	public void setArticleId(long id) {
		contentValues.put(ARTICLE_ID, id);
		try {
			writeDB(contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return id
	 */
	public long getId() {
		return id;
	}

	public String dump(){
		String str = "";
		str += "ROWID:" + this.id;
		str += "|PATH:"+ getPath();
		str += "|TYPE:" + getType();
		str += "|ARTICLE_ID:" + getArticleId();
		str += "|MODIFIED:" + getModified();
		str += "|";
		
		return str;
	}
}
