package jp.ac.bemax.sawara;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 言葉事典で取り扱う、１情報単位を表すクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class Article implements ListItem{
	private ArticleManager iManager;
	private ContentValues values;
	private long row_id;

	
	public Article(ArticleManager mng){
		iManager = mng;
		row_id = -1;
		values = new ContentValues();
	}
	
	/**
	 * データの更新を行う
	 */
	public boolean updateItem(){
		SQLiteDatabase db = iManager.getSQLiteOpenHelper().getWritableDatabase();
		String[] args = {""+row_id};
		int rows = db.update("item_table", values, "ROWID = ?", args);
		db.close();
		
		if(rows > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * アイテムを消去する
	 */
	public boolean deleteItem(){
		SQLiteDatabase db = iManager.getSQLiteOpenHelper().getWritableDatabase();
		String[] args = {""+row_id};
		int ret = db.delete("item_table", "ROWID = ", args);
		db.close();
		
		if(ret > 0){
			row_id = -1;
			values.clear();
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * アイテムを検索する
	 */
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
		SQLiteDatabase db = iManager.getSQLiteOpenHelper().getReadableDatabase();
		Cursor cs = db.rawQuery(sql, vals);
		db.close();
	}
	
	/**
	 * データベースからこのアイテムの情報を読み込む
	 */
	public void loadItem(){
		String sql = "select * from article_table where ROWID = ?";
		String[] args = {""+row_id};
		SQLiteDatabase db = iManager.getSQLiteOpenHelper().getReadableDatabase();
		Cursor cr = db.rawQuery(sql, args);
		
		cr.moveToFirst();
		DatabaseUtils.cursorRowToContentValues(cr, values);
		
		db.close();
	}
	
	/**
	 * 
	 */
	public void setId(long id){
		row_id = id;
	}
	
	/**
	 * ROWIDを返す
	 * @return ROWID
	 */
	public long getId(){
		return row_id;
	}
	
	/**
	 * item_nameを返す
	 * @return アイテムの名前
	 */
	public String getName(){
		loadItem();
		return values.getAsString("item_name");
	}
	
	/**
	 * item_descriptionを返す
	 * @return アイテムの詳細
	 */
	public String getDescription(){
		loadItem();
		return values.getAsString("item_description");
	}
	
	/**
	 * アイテムの画像イメージを返す
	 * @return アイテムの画像
	 */
	public Bitmap getImage(){
		String pathName = iManager.getImageFileDir().getPath() + "/" + getImageUrl();
		Bitmap bmp = BitmapFactory.decodeFile(pathName);	
		return bmp;
	}
	
	/**
	 * アイテムの画像イメージのURLを返す
	 * @return アイテム画像のURL
	 */
	public String getImageUrl(){
		loadItem();
		return values.getAsString("item_image");
	}
	
	/**
	 * アイテムの動画のURLを返す
	 * @return アイテム動画のURL
	 */
	public String getMovieUrl(){
		loadItem();
		return values.getAsString("item_movie");
	}
	
	/**
	 * item_tableの内容をLogに書き出す
	 */
	public void dump(){
		SQLiteDatabase db = iManager.getSQLiteOpenHelper().getReadableDatabase();
		Cursor cr = db.rawQuery("select ROWID, * from article_table", null);
		while(cr.moveToNext()){
			Log.d("id_"+cr.getString(0), cr.getString(1)+":"+cr.getString(2)+":"+cr.getString(3));
		}
		db.close();
	}

	@Override
	public int getType() {
		// TODO 自動生成されたメソッド・スタブ
		return ListItem.ITEM;
	}
}
