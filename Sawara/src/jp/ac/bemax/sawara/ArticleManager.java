package jp.ac.bemax.sawara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * ArticleとDBとの間に立ち、両者を中継するクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class ArticleManager {
	private Context context;
	private SQLiteOpenHelper mHelper;
	private File imageFileDir;
	private File movieFileDir;
	
	private static ArticleManager aManager;
	
	/**
	 * 
	 * ItemManager.javaコンストラクタ
	 * @param context
	 */
	public ArticleManager(Context context){
		this.context = context;
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
	/*public static ArticleManager newItemManager(Context context){
		if(aManager == null){
			aManager = new ArticleManager(context);
		}
		return aManager;
	}*/
	
	public static Article getArticle(long id, Context context){
		Article article = null;
		SawaraDBAdapter sdb = new SawaraDBAdapter(context);
		SQLiteOpenHelper helper = sdb.getHelper();
		SQLiteDatabase db = helper.getReadableDatabase();
		
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
	 * article_tableにデータをインサートする
	 * @param article インサートするArticle
	 */
	public void insert(Article article){
		// データベースを設定
		SQLiteDatabase db = mHelper.getWritableDatabase();
		
		// article_tableに登録する各値をセットする
		ContentValues cv = new ContentValues();
		cv.put("name", article.getName());
		cv.put("description", article.getDescription());
		cv.put("position", "0");
		cv.put("modified", article.getModified());

		// article_tableにインサートする
		long aid = db.insert("article_table", null, cv);
		article.setId(aid);
		
		// image_tableに静止画を登録する
		String[] imagePaths = article.getImagePaths();
		for(int i=0; i<imagePaths.length; i++){
			// image_tableに登録する値をセットする
			cv = new ContentValues();
			cv.put("image_path", imagePaths[i]);
			cv.put("article_id", aid);
		
			// image_tableにインサート
			long iid = db.insert("image_table", null, cv);
		}
		
		// movie_tableに動画を登録する
		String[] moviePaths = article.getMoviePaths();
		for(int i=0; i<moviePaths.length; i++){
			// movie_tableに登録する値をセットする
			cv = new ContentValues();
			cv.put("movie_path", moviePaths[i]);
			cv.put("article_id", aid);
			
			// movie_tableにインサート
			long mid = db.insert("movie_table", null, cv);
		}

		db.close();
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

	
	/**
	 * @param article
	 * @return
	 
	public Bitmap createArticleImage(Article article){
		Bitmap image = null;
		// 最初の画像イメージを返す
		String[] imagePaths = article.getImagePaths();
		String[] moviePaths = article.getMoviePaths();
		if(imagePaths.length > 0){
			String imagePath = imagePaths[0];
			if(imagePath != null){
				image = BitmapFactory.decodeFile(imagePath);
			}
		}else if(moviePaths.length > 0){
			// TODO このままでは、NULL
			String moviePath = moviePaths[0];
			Uri uri = Uri.fromFile(new File(moviePath));
			SurfaceView sv = new SurfaceView(context);
			SurfaceHolder sh = sv.getHolder();
			MediaPlayer mp = MediaPlayer.create(context, uri, sh);
			image = sv.getDrawingCache();
		}
		return image;
	}
	*/
	
	public void insertCategory(Article article){
		// データベースを設定
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues cv = null;
		
		long[] ids = article.getCategoryIds();
		for(int i=0; i < ids.length; i++){
			cv = new ContentValues();
			cv.put("category_id", ids[i]);
			cv.put("article_id", article.getId());
			
			db.insert("category_article_table", null, cv);
		}
		
		db.close();
	}
	
	
}
