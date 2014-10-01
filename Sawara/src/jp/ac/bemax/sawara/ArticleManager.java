package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

/**
 * ArticleとDBとの間に立ち、両者を中継するクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class ArticleManager {
	private Context context;
	private SQLiteOpenHelper helper;
	private SawaraDBAdapter sdba;
	private File imageFileDir;
	private File movieFileDir;
	private static ArticleManager iManager;
	
	/**
	 * 
	 * ItemManager.javaコンストラクタ
	 * @param context
	 */
	private ArticleManager(Context context){
		this.context = context;
		
		// sawaraDBアダプタを登録
		sdba = new SawaraDBAdapter(context);
		helper = sdba.getHelper();
		
		// ImageおよびMovieの保存先ディレクトリを登録
		imageFileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		movieFileDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
	}
	
	/**
	 * 新規のItemManagerを作成する
	 * もしも、すでにItemManagerがあった場合には、それを利用する
	 * @param context
	 * @return ItemManager
	 */
	public static ArticleManager newItemManager(Context context){
		if(iManager == null){
			iManager = new ArticleManager(context);
		}
		return iManager;
	}
	
	/**
	 * Articleオブジェクトを新規作成する
	 * DBのarticle_tableに、valuesの値をinsertする
	 * @param values article_tableにinsertする値
	 * @return Item アイテムオブジェクト
	 */
	public Article newItem(ContentValues values){
		Article resultItem = null;
		// 
		SQLiteDatabase db = null;
		try{
			db = helper.getWritableDatabase();
			long rowId = db.insert("article_table", null, values);
			resultItem = new Article(this);
			resultItem.setId(rowId);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.close();
		}
		return resultItem;
	}
	
	/**
	 * 指定されたROWIDに対応したItemを返す
	 * @param rowId
	 * @return Itemオブジェクト
	 */
	public Article getItem(long rowId){
		Article item = new Article(this);
		item.setId(rowId);
		
		return item;
	}
	
	/*
	 * DB上のすべてのアイテムを取得して、そのリストを返す
	 * @return アイテムのリスト
	 */
	public List<Article> getAllItems(){
		List<Article> items = new ArrayList<Article>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cr = db.rawQuery("select ROWID, * from article_table", null);
		while(cr.moveToNext()){
			Article item = new Article(this);
			item.setId(cr.getLong(0));
			items.add(item);
		}
		db.close();
		return items;
	}
	
	/**
	 * 画像イメージの保存先ディレクトリを返す
	 * @return File 画像イメージの保存先ディレクトリ
	 */
	public File getImageFileDir(){
		return imageFileDir;
	}
	
	/**
	 * 動画の保存先ディレクトリを返す
	 * @return File 動画の保存先ディレクトリ
	 */
	public File getMovieFileDir(){
		return movieFileDir;
	}
	
	/**
	 * データベースのヘルパーを返す
	 * @return SQLiteOpenHelper ヘルパーオブジェクト
	 */
	public SQLiteOpenHelper getSQLiteOpenHelper(){
		return helper;
	}
	
	public void dump(){
		List<Article> list = getAllItems();
		for(Article item: list){
			Log.d("item("+item.getId()+")", item.getName());
		}
	}
}
