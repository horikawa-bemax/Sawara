package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Media {
	static final int PHOTO = 1;
	static final int MOVIE = 2;
	
	static final String TABLE_NAME = "media_table";
	static final String PATH = "path";
	static final String TYPE = "type";
	static final String ARTICLE_ID = "article_id";
	static final String MODIFIED = "modified";
	
	//private Context mContext;
	private SQLiteDatabase mDb;
	private ContentValues contentValues;
	private long id;
	
	private Media(){
		contentValues = new ContentValues();
		id = -1;
	}
	
	static Media createMedia(SQLiteOpenHelper helper, String path, int type){
		Media media = new Media();
		
		ContentValues values = new ContentValues(4);
		values.put(Media.PATH, path);
		values.put(Media.TYPE, type);
		values.put(MODIFIED, System.currentTimeMillis());
		
		try {
			media.openDB(helper.getWritableDatabase());
			media.insertDB(values);
			media.closeDB();
		} catch (Exception e) {
			e.printStackTrace();
			media = null;
		}
		
		return media;
	}
	
	public void openDB(SQLiteDatabase db){
		mDb = db;
	}
	
	public void closeDB(){
		mDb.close();
	}
	
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
	
	static List<Media> findMediasByArticleId(SQLiteOpenHelper helper, long article_id){
		List<Media> mediaList = new ArrayList<Media>();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String[] columns = {"ROWID"};
		String[] selectionAStrings = {""+article_id};
		Cursor cursor = db.query(TABLE_NAME, columns, ARTICLE_ID+"=?", selectionAStrings, null, null, null);
		
		while(cursor.moveToNext()){
			Media media = new Media();
			media.id = cursor.getLong(0);
			mediaList.add(media);
			Log.d("dump",media.dump());
		}
		cursor.close();
		db.close();
		
		return mediaList;
	}
	
	static Media findMediaById(SQLiteOpenHelper helper, long id){
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
	
	static int delete(SQLiteOpenHelper helper, long id){
		SQLiteDatabase db = helper.getWritableDatabase();
		
		String[] whereArgs = {""+id};
		int rows = db.delete(Media.TABLE_NAME, "ROWID=?", whereArgs);
		
		return rows;
	}
	
	private void insertDB(ContentValues values) throws Exception{
		
		long id = mDb.insert(TABLE_NAME, null, values);
		
		if(id == -1){
			throw new Exception("DBへの挿入に失敗しました");
		}
		
		this.id = id;

	}
	
	private Cursor readDB() throws Exception{
		
		if(id < 0){
			throw new Exception("IDが不正です");
		}
		
		String[] columns = {PATH, TYPE, ARTICLE_ID, MODIFIED};
		String[] selectionArgs = {"" + id};
		Cursor cursor = mDb.query(TABLE_NAME, columns, "ROWID = ?", selectionArgs, null, null, null);
		
		return cursor;
	}
	
	private void writeDB(ContentValues values) throws Exception{
		
		String[] whereArgs = {""+this.id};
		int num = mDb.update(TABLE_NAME, values, "ROWID=?", whereArgs);
				
		if(num < 1){
			throw new Exception("DBの更新に失敗しました");
		}
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
