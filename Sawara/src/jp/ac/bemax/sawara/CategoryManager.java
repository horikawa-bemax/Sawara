package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * CategoryとDBとの間に立って、両者を中継するクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class CategoryManager {
	private static CategoryManager manager;
	private Context mContext;
	private SQLiteOpenHelper mHelper;
	
	public CategoryManager(Context c){
		mContext = c;
		SawaraDBAdapter sdb = new SawaraDBAdapter(c);
		mHelper = sdb.getHelper();
	}

	/**
	 * カテゴリのイメージを作成して返す
	 * @param id カテゴリのID
	 * @return カテゴリのイメージ
	 *
	public Bitmap makeCategoryImage(long id){
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String[] args = {"" + id};
		Cursor mCursor = db.rawQuery("select image_path from image_table where article_id in (select article_id from category_article_table where category_id = ?)", args);
		int size = mCursor.getCount();
		String[] paths = new String[size<6 ? size : 6 ];
		for(int i=0; i<paths.length; i++){
			mCursor.moveToNext();
			paths[i] = mCursor.getString(0);
		}
		db.close();
		
		Bitmap icon = IconFactory.createCategoryIcon(paths);
		
		return icon;
	}*/
	
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
	
	public Category newCategory(String name){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Category category = newCategory(db, name);
		db.close();
		return category;
	}
	
	public Category newCategory(SQLiteDatabase db, String name){
		Category category = new Category();
		
		category.setName(name);
		category.setModified(System.currentTimeMillis());
		
		try{
			ContentValues values = new ContentValues();
			values.put("name", category.getName());
			values.put("modified", category.getModified());
			long cId = db.insert("category_table", null, values);
			
			if(cId == -1) throw new Exception();
			
			values.put("position", cId);
			db.update("category_table", values, "ROWID = " + cId, null);
			
			category.setId(cId);
			category.setPosition(cId);
			
		}catch(Exception e){
			category = null;
		}
		
		return category;
	}
	
	public Category loadCategory(long id){
		Category category = new Category();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String sql = "select ROWID, * from category_table where ROWID = ?";
		String[] selectionArgs = {"" + id};
		Cursor mCursor = db.rawQuery(sql, selectionArgs);
		mCursor.moveToNext();
		category.setId(id);
		category.setName(mCursor.getString(mCursor.getColumnIndex("name")));
		category.setIconPath(mCursor.getString(mCursor.getColumnIndex("icon")));
		category.setPosition(mCursor.getLong(mCursor.getColumnIndex("position")));
		category.setModified(mCursor.getLong(mCursor.getColumnIndex("modified")));
		
		return category;
	}
	
	public Category setCategoryIcon(Category category){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Category result = setCategoryIcon(db, category);
		db.close();
		return result;
	}
	
	public Category setCategoryIcon(SQLiteDatabase db, Category category){
		String sql = "select icon from category_icon_view where category_id = ?";
		String[] selectionArgs = {"" + category.getId()};
		Cursor mCursor = db.rawQuery(sql, selectionArgs);
		
		String[] iconPaths = new String[mCursor.getCount()];
		for(int i=0; i<iconPaths.length; i++){
			mCursor.moveToNext();
			iconPaths[i] = mCursor.getString(mCursor.getColumnIndex("icon"));
		}
		
		Bitmap icon = IconFactory.createCategoryIcon(iconPaths);
		StrageManager sManager = new StrageManager(mContext);
		String iconPath = sManager.saveIcon(icon);
		
		ContentValues values = new ContentValues();
		values.put("icon", iconPath);
		db.update("category_table", values, "ROWID = " + category.getId(), null);
		
		category.setIconPath(iconPath);
		
		return category;
	}
	
	public List<Category> getAllItems(){
		List<Category> list = new ArrayList<Category>();
		
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String sql = "select ROWID, * from category_table";
		String[] selectionArgs = {};
		Cursor mCursor = db.rawQuery(sql, selectionArgs);
		while(mCursor.moveToNext()){
			// カテゴリー作成
			Category cat = new Category();
			cat.setName(mCursor.getString(mCursor.getColumnIndex("name")));
			String log = mCursor.getString(mCursor.getColumnIndex("icon"));
			cat.setIconPath(log);
			Log.d("icon", log);
			cat.setPosition(mCursor.getInt(mCursor.getColumnIndex("position")));
			cat.setModified(mCursor.getLong(mCursor.getColumnIndex("modified")));
			cat.setId(mCursor.getLong(0));
			
			list.add(cat);
		}
		db.close();
		return list;
	}
}
