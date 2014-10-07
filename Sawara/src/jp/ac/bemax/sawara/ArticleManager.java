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
	private SQLiteOpenHelper mHelper;
	private File imageFileDir;
	private File movieFileDir;
	
	private static ArticleManager iManager;
	
	/**
	 * 
	 * ItemManager.javaコンストラクタ
	 * @param context
	 */
	private ArticleManager(Context context){
		// sawaraDBアダプタを登録
		SawaraDBAdapter sdb = new SawaraDBAdapter(context);
		mHelper = sdb.getHelper();
		
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

	public Article getArticle(long id){
		Article article = null;
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String[] selectionArgs = {"" + id};
		Cursor mCursor = db.rawQuery("select ROWID, * from article_table where ROWID = ?", selectionArgs);
		if(mCursor.getCount() == 1){
			mCursor.moveToNext();
			article = new Article(mCursor.getString(1), mCursor.getString(2));
			article.setId(mCursor.getLong(0));
		}
		return article;
	}
	
	/**
	 * DB上のすべてのアイテムを取得して、そのリストを返す
	 * @return アイテムのリスト
	 */
	public List<Article> getAllItems(){
		List<Article> items = new ArrayList<Article>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cr = db.rawQuery("select ROWID, * from article_table", null);
		while(cr.moveToNext()){
			Article article = new Article(cr.getString(1), cr.getString(2));
			article.setId(cr.getLong(0));
			items.add(article);
			String[] args = {"" + cr.getLong(0)};
			Cursor cr2 = db.rawQuery("select * from image_table where article_id = ?", args);
			int size = cr2.getCount();
			String[] paths = new String[size];
			while(cr2.moveToNext()){
				paths[cr2.getPosition()] = cr2.getString(0);
			}
			article.setImagePaths(paths);
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

}
