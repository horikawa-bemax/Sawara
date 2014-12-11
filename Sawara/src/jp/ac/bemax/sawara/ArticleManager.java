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

/**
 * ArticleとDBとの間に立ち、両者を中継するクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class ArticleManager {
	private Context mContext;
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
		mContext = context;
		// sawaraDBアダプタを登録
		SawaraDBAdapter sdb = new SawaraDBAdapter(context);
		mHelper = sdb.getHelper();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Article loadArticle(long id){
		// 読み込みモードでDBを開く
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Article article = loadArticle(db, id);
		db.close();
		return article;
	}
	
	/**
	 * IDを指定して、DBからArticleを取得する
	 * @param db データベース
	 * @param id ArticleID
	 * @return 取得したArticle
	 */
	public Article loadArticle(SQLiteDatabase db, long articleId){
		Article article = null;
		
		String[] images, movies, selectionArgs = {"" + articleId};
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
			article.setIconPath(mCursor.getString(mCursor.getColumnIndex("icon")));
			article.setPosition(mCursor.getInt(mCursor.getColumnIndex("position")));
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

		// iconイメージを登録する
		
		
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
		String[] images = null, movies = null, selectionArgs = {""};
		
		mCursor = db.rawQuery("select ROWID, * from article_table", null);
		while(mCursor.moveToNext()){
			// 基本データ取り込み
			Article article = new Article();
			article.setId(mCursor.getLong(0));
			article.setName(mCursor.getString(mCursor.getColumnIndex("name")));
			article.setDescription(mCursor.getString(mCursor.getColumnIndex("description")));
			article.setIconPath(mCursor.getString(mCursor.getColumnIndex("icon")));
			article.setPosition(mCursor.getInt(mCursor.getColumnIndex("position")));
			article.setModified(mCursor.getLong(mCursor.getColumnIndex("modified")));
			
			// 検索条件を設定
			selectionArgs[0] = "" + article.getId();
			
			// 画像テーブルから画像パスを読み込み
			mCursor2 = db.rawQuery("select image_path from image_table where article_id = ?", selectionArgs);
			images = new String[mCursor2.getCount()];
			for(int i=0; mCursor2.moveToNext(); i++){
				images[i] = mCursor2.getString(mCursor2.getColumnIndex("image_path"));
			}
			article.setImagePaths(images);
			
			// 動画テーブルから動画パスを読み込み
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
	 * データを元に、新しいarticleを作成し、DBに保存する。
	 * @param name 名前
	 * @param description 説明
	 * @param modified 更新日
	 * @param imagePaths 画像パスの配列
	 * @param moviePaths 動画パスの配列
	 * @param categoryIds カテゴリIDの配列
	 * @return Articleオブジェクトを返す。失敗したらnullを返す。
	 */
	public Article newArticle(String name, String description, String[] imagePaths, String[] moviePaths, long[] categoryIds){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Article article = newArticle(db, name, description, imagePaths, moviePaths, categoryIds);
		db.close();
		return article;
	}
	
	/**
	 * データを元に、新しいarticleを作成し、DBに保存する。
	 * @param db データベース
	 * @param name 名前
	 * @param description 説明
	 * @param modified 更新日
	 * @param imagePaths 画像パスの配列
	 * @param moviePaths 動画パスの配列
	 * @param categoryIds カテゴリIDの配列
	 * @return Articleオブジェクトを返す。失敗したらnullを返す。
	 */
	public Article newArticle(SQLiteDatabase db, String name, String description, String[] imagePaths, String[] moviePaths, long[] categoryIds){	
		Article article = new Article();
		
		// Articleオブジェクトに値をセットする
		article.setName(name);
		article.setDescription(description);
		article.setModified(System.currentTimeMillis());
		article.setImagePaths(imagePaths);
		article.setMoviePaths(moviePaths);
		article.setCategoryIds(categoryIds);
		
		try{
			// DBに基本値をセットする
			ContentValues values = new ContentValues();
			values.put("name", article.getName());
			values.put("description", article.getDescription());
			values.put("modified", article.getModified());
			long aid = db.insert("article_table", null, values);

			if(aid == -1) throw new Exception();

			article.setId(aid);
			
			// 画像を元にアイコンを作成する
			Bitmap icon = IconFactory.createArticleIcon(article);
			StrageManager manager = new StrageManager(mContext);
			String iconPath = manager.saveIcon(icon);
			article.setIconPath(iconPath);
			article.setPosition(aid);
			
			// 位置情報とアイコン情報をDBに追加する。
			values.put("position", aid);
			values.put("icon", iconPath);
			
			db.update("article_table", values, "ROWID = " + aid, null);
	
			// image_tableへ登録
			if(imagePaths != null){
				values = new ContentValues();
				values.put("article_id", aid);
				for(String path: imagePaths){
					values.put("image_path", path);
					db.insert("image_table", null, values);
				}
			}
			
			// movie_tableへ登録
			if(moviePaths != null){
				values = new ContentValues();
				values.put("article_id", aid);
				for(String path: moviePaths){
					values.put("movie_path", path);
					db.insert("movie_table", null, values);
				}
			}
			
			values = new ContentValues();
			values.put("article_id", aid);
			for(long id: categoryIds){
				values.put("category_id", id);
				db.insert("category_article_table", null, values);
			}
		}catch(Exception e){
			article = null;
			e.printStackTrace();
		}
		
		return article;
	}
	
	/**
	 * カテゴリーIDを元に、Articleのリストを返す
	 * @param categoryId カテゴリーID
	 * @return Articleのリスト
	 */
	public List<ListItem> getArticlesAtCategory(long categoryId){
		List<ListItem> articleList = new ArrayList<ListItem>();
		
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String[] selectionArgs = {"" + categoryId};
		Cursor mCursor = db.rawQuery(
			"select ROWID from article_table where ROWID in (select article_id from category_article_table where category_id = ?)"
			, selectionArgs);

		while(mCursor.moveToNext()){
			long articleId = mCursor.getLong(0);
			Article article = loadArticle(db, articleId);
			articleList.add(article);
		}
		
		db.close();
		
		return articleList;
	}
}
