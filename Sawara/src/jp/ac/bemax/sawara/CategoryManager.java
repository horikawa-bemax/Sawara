package jp.ac.bemax.sawara;

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
	
	public Category newCategory(){
		Category category = new Category();
		
		return category;
	}
	/**
	 * カテゴリのイメージを作成して返す
	 * @param id カテゴリのID
	 * @return カテゴリのイメージ
	 */
	public Bitmap makeCategoryImage(long id){
		Bitmap categoryImage = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
		Canvas offScreen = new Canvas(categoryImage);
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String[] args = {"" + id};
		Cursor mCursor = db.rawQuery("select image_path from category_image_view where category_id = ?", args);
		int size = mCursor.getCount();
		for(int i=0; i<4 && mCursor.moveToNext(); i++){
			String imagePath = mCursor.getString(0);
			Bitmap image = BitmapFactory.decodeFile(imagePath);
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			int top, left, right, buttom;
			if(imageWidth > imageHeight){
				top = 0;
				left = (imageWidth - imageHeight) / 2;
				right = imageWidth - (imageWidth - imageHeight) / 2;
				buttom = imageHeight;
			}else{
				top = (imageHeight - imageWidth) / 2;
				left = 0;
				right = imageWidth;
				buttom = imageHeight - (imageHeight - imageWidth) / 2;
			}
			Rect srcRect = new Rect(left, top, right, buttom);
			Rect dstRect = new Rect(i%2 * 200, i/2 * 200, (i%2+1) * 200, (i/2+1) * 200);
			offScreen.drawBitmap(image, srcRect, dstRect, null);
		}
		Log.d("CursorSize",""+size);
		db.close();
		
		return categoryImage;
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
			Category cat = new Category(mCursor.getString(1), mCursor.getInt(2));
			cat.setId(mCursor.getLong(0));
			// カテゴリのイメージ作成＆セット
			Bitmap catImage = makeCategoryImage(mCursor.getLong(0));
			cat.setImage(catImage);
			list.add(cat);
		}
		db.close();
		return list;
	}
}
