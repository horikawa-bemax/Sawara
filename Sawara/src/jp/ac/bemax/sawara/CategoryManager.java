package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
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
	
	public Category newCategory(){
		Category category = new Category(this);
		
		return category;
	}
	/**
	 * カテゴリのイメージを作成して返す
	 * @param id カテゴリのID
	 * @return カテゴリのイメージ
	 */
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
			// カテゴリー作成
			Category cat = new Category(this);
			cat.setName(mCursor.getString(mCursor.getColumnIndex("name")));
			cat.setIconPath(mCursor.getString(mCursor.getColumnIndex("icon")));
			cat.setPosition(mCursor.getInt(mCursor.getColumnIndex("position")));
			cat.setModified(mCursor.getLong(mCursor.getColumnIndex("modified")));
			cat.setId(mCursor.getLong(0));
			
			// カテゴリのイメージ作成＆セット
			Bitmap icon = makeCategoryImage(mCursor.getLong(0));
			StrageManager sManager = new StrageManager(mContext);
			String iconPath = sManager.saveIcon(icon);
			cat.setIconPath(iconPath);
			
			list.add(cat);
		}
		db.close();
		return list;
	}
}
