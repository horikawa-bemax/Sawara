package jp.ac.bemax.sawara;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Item {
	private long rowId;
	private String name;
	private String image_url;
	private String movie_url;
	
	private SQLiteOpenHelper helper;
	private SawaraDBAdapter sdba;
	
	public Item(Context context){
		sdba = new SawaraDBAdapter(context);
		helper = sdba.getHelper();
	}
	
	public boolean newItem(){
		ContentValues values = new ContentValues();
		values.put("item_name", name);
		values.put("item_image", image_url);
		values.put("item_movie", movie_url);
		
		SQLiteDatabase db = helper.getWritableDatabase();
		rowId = db.insert("item_table", null, values);
		db.close();
		
		// データベースの中身をログに表示する
		dump();
		
		if(rowId != -1){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean updateItem(){
		ContentValues values = new ContentValues();
		values.put("item_name", name);
		values.put("item_image", image_url);
		values.put("item_movie", movie_url);
		
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
	
	public void loadItem(long rowId){
		String sql = "select * from item_table where ROWID = ?";
		String[] selectionArgs = {""+rowId};
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cr = db.rawQuery(sql, selectionArgs);
		
		cr.moveToFirst();
		
		name = cr.getString(cr.getColumnIndex("item_name"));
		image_url = cr.getString(cr.getColumnIndex("item_image"));
		movie_url = cr.getString(cr.getColumnIndex("item_movie"));
		
		db.close();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		if(rowId != 0)
			updateItem();
	}
	
	public String getImage_url() {
		return image_url;
	}
	
	public void setImage_url(String image_url) {
		this.image_url = image_url;
		if(rowId != 0)
			updateItem();
	}
	
	public String getMovie_url() {
		return movie_url;
	}
	
	public void setMovie_url(String movie_url) {
		this.movie_url = movie_url;
		if(rowId != 0)
			updateItem();
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
