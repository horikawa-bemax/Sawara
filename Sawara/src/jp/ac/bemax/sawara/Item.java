package jp.ac.bemax.sawara;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Item {
	private long rowId;
	private SQLiteOpenHelper helper;
	private HashMap<String, String> colmn;
	
	public Item(long rowId, SQLiteOpenHelper helper){
		this.rowId = rowId;
		this.helper = helper;
	}
	
	public boolean updateItem(String name, String imageUrl, String movieUrl){
		ContentValues values = new ContentValues();
		values.put("item_name", name);
		values.put("item_image", imageUrl);
		values.put("item_movie", movieUrl);
		
		String[] s = {""+rowId};
		SQLiteDatabase db = helper.getWritableDatabase();
		int rows = db.update("item_table", values, "ROWID = ?", s);
		db.close();
		
		dump();
		
		if(rows > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean deleteItem(){
		SQLiteDatabase db = helper.getWritableDatabase();
		String[] s = {""+rowId};
		int ret = db.delete("item_table", "ROWID = ", s);
		db.close();
		
		if(ret > 0){
			rowId = -1;
			return true;
		}else{
			return false;
		}
	}
	
	public void searchItem(String[] cols, String[] vals){
		String sql = "select * from item_table";
		if(cols.length > 0){
			sql += " where ";
		}
		for(int i=0; i<cols.length; i++){
			sql += cols[i] + " = ? ";
			if(i<cols.length-1){
				sql += "and ";
			}
		}
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cs = db.rawQuery(sql, vals);
		db.close();
	}
	
	public void loadItem(){
		String sql = "select * from item_table where ROWID = ?";
		String[] selectionArgs = {""+rowId};
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cr = db.rawQuery(sql, selectionArgs);
		
		cr.moveToFirst();
		
		colmn = new HashMap<String, String>();
		colmn.put("item_name", cr.getString(cr.getColumnIndex("item_name")));
		colmn.put("item_image", cr.getString(cr.getColumnIndex("item_image")));
		colmn.put("item_movie", cr.getString(cr.getColumnIndex("item_movie")));
		
		db.close();
	}
	
	public String getName(){
		loadItem();
		return colmn.get("item_name");
	}
	
	public String getImageUrl(){
		loadItem();
		return colmn.get("item_image");
	}
	
	public String getMovieUrl(){
		loadItem();
		return colmn.get("item_movie");
	}
	
	public void dump(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from item_table", null);
		while(cr.moveToNext()){
			Log.d("id_"+cr.getString(0), cr.getString(1)+":"+cr.getString(2)+":"+cr.getString(3));
		}
		db.close();
	}
}
