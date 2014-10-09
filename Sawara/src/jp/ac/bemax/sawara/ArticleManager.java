package jp.ac.bemax.sawara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

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
		
		String[] images, movies, selectionArgs = {"" + id};
		Cursor mCursor, mCursor2;
		// Articleの基本データを取り込み		
		mCursor = db.rawQuery("select ROWID, * from article_table where ROWID = ?", selectionArgs);
		if(mCursor.getCount() == 1){
			mCursor.moveToNext();
			
			// 基本データ取り込み
			article = new Article();
			article.setId(mCursor.getLong(0));
			article.setName(mCursor.getString(mCursor.getColumnIndex("name")));
			article.setDescription(mCursor.getString(mCursor.getColumnIndex("description")));
			article.setModified(mCursor.getLong(mCursor.getColumnIndex("modified")));
			
			// 画像ファイルを取り込み
			mCursor2 = db.rawQuery("select image_path from image_table where article_id = ?", selectionArgs);
			images = new String[mCursor2.getCount()];
			for(int i=0; mCursor2.moveToNext(); i++){
				images[i] = mCursor2.getString(mCursor2.getColumnIndex("image_path"));
			}
			article.setImagePaths(images);
			
			// 動画ファイルを取り込み
			mCursor2 = db.rawQuery("select movie_path from movie_table where article_id = ?", selectionArgs);
			movies = new String[mCursor2.getCount()];
			for(int i=0; mCursor2.moveToNext(); i++){
				movies[i] = mCursor2.getString(mCursor2.getColumnIndex("movie_path"));
			}
			article.setMoviePaths(movies);
		}	
		
		db.close();
		
		return article;
	}
	
	/**
	 * DB上のすべてのアイテムを取得して、そのリストを返す
	 * @return アイテムのリスト
	 */
	public List<Article> getAllItems(){
		List<Article> items = new ArrayList<Article>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor mCursor, mCursor2;
		String[] images, movies, selectionArgs = {""};
		mCursor = db.rawQuery("select ROWID, * from article_table", null);
		while(mCursor.moveToNext()){
			// 基本データ取り込み
			Article article = new Article();
			article.setId(mCursor.getLong(0));
			article.setName(mCursor.getString(mCursor.getColumnIndex("name")));
			article.setDescription(mCursor.getString(mCursor.getColumnIndex("description")));
			article.setModified(mCursor.getLong(mCursor.getColumnIndex("modified")));
			
			// 検索条件を設定
			selectionArgs[0] = "" + article.getId();
			
			// 画像ファイルを取り込み
			mCursor2 = db.rawQuery("select image_path from image_table where article_id = ?", selectionArgs);
			images = new String[mCursor2.getCount()];
			for(int i=0; mCursor2.moveToNext(); i++){
				images[i] = mCursor2.getString(mCursor2.getColumnIndex("image_path"));
			}
			article.setImagePaths(images);
			
			// 動画ファイルを取り込み
			mCursor2 = db.rawQuery("select movie_path from movie_table where article_id = ?", selectionArgs);
			movies = new String[mCursor2.getCount()];
			for(int i=0; mCursor2.moveToNext(); i++){
				movies[i] = mCursor2.getString(mCursor2.getColumnIndex("movie_path"));
			}
			article.setMoviePaths(movies);
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
