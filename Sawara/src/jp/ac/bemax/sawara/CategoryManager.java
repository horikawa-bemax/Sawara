package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * CategoryとDBとの間に立って、両者を中継するクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class CategoryManager {
	private static CategoryManager manager;
	private Context mContext;
	private SQLiteOpenHelper mHelper;
	
	private CategoryManager(Context c){
		mContext = c;
		SawaraDBAdapter sdb = new SawaraDBAdapter(c);
		mHelper = sdb.getHelper();
	}
	
	public static CategoryManager newCategoryManager(Context c){
		if(manager == null){
			manager = new CategoryManager(c);
		}
		return manager;
	}
	
	public long insert(Category category){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues cv = category.getContentValues();
		long rowid = db.insert("category_table", null, cv);
		db.close();
		category.setId(rowid);
		return rowid;
	}
	
	public int update(Category category){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues cv = category.getContentValues();
		String[] args = {"" + category.getId()};
		int result = db.update("category_table", cv, "ROWID=?", args);
		db.close();
		
		return result;
	}
	
	public int delete(Category category){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		String[] args = {"" + category.getId()};
		int result = db.delete("category_table", "ROWID=?", args);
		db.close();
		if(result > 0){
			category = null;
		}
		return result;
	}
	
	public List<Category> getAllItems(){
		List<Category> list = new ArrayList<Category>();
		
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String sql = "select ROWID, * from category_table";
		String[] selectionArgs = {};
		Cursor mCursor = db.rawQuery(sql, selectionArgs);
		while(mCursor.moveToNext()){
			Category cat = new Category(mCursor.getLong(0), mCursor.getString(1), mCursor.getInt(2));
			list.add(cat);
		}
		return list;
	}
}
