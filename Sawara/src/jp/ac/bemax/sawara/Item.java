package jp.ac.bemax.sawara;

import java.io.File;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;


/**
 * アイテムクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class Item {
	private static SQLiteOpenHelper helper;
	private static File imageDir;
	private static File movieDir;
	private ContentValues values;
	private long row_id;

	
	public Item(long id){
		row_id = id;
		values = new ContentValues();
	}
	
	// データの更新を行う
	public boolean updateItem(){
		SQLiteDatabase db = helper.getWritableDatabase();
		String[] args = {values.getAsString("ROWID")};
		int rows = db.update("item_table", values, "ROWID = ?", args);
		db.close();
		
		if(rows > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public static void init(SQLiteOpenHelper hlp, File imgdir){
		helper = hlp;
		imageDir = imgdir;
	}
	
	public boolean deleteItem(){
		SQLiteDatabase db = helper.getWritableDatabase();
		String[] args = {""+row_id};
		int ret = db.delete("item_table", "ROWID = ", args);
		db.close();
		
		if(ret > 0){
			values.clear();
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
		String[] args = {""+row_id};
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cr = db.rawQuery(sql, args);
		
		cr.moveToFirst();
		DatabaseUtils.cursorRowToContentValues(cr, values);
		
		db.close();
	}
	
	public long getId(){
		return row_id;
	}
	
	public String getName(){
		loadItem();
		return values.getAsString("item_name");
	}
	
	public String getDescription(){
		loadItem();
		return values.getAsString("item_description");
	}
	
	public Bitmap getImage(){
		String pathName = imageDir.getPath() + "/" + getImageUrl();
		Bitmap bmp = BitmapFactory.decodeFile(pathName);	
		return bmp;
	}
	
	public String getImageUrl(){
		loadItem();
		return values.getAsString("item_image");
	}
	
	public String getMovieUrl(){
		loadItem();
		return values.getAsString("item_movie");
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
