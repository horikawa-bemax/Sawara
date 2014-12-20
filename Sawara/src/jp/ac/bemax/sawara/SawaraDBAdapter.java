package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

/**
 * 言葉事典アプリのDBを扱うクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class SawaraDBAdapter{
	private SQLiteOpenHelper helper;
	
	public SawaraDBAdapter(Context context) {
		helper = new DBAdapter(context, "sawara.db", null, 1);
	}
	
	public SQLiteOpenHelper getHelper(){
		return helper;
	}
	
	/**
	 * group_tableのgroup_nameの写像リストを返す
	 * @return group_nameのリスト
	 */
	public List<String> getGroupNameList(){
		List<String> list = new ArrayList<String>();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		try{
			Cursor cursor = db.rawQuery("select group_name from group_table", null);
			while(cursor.moveToNext()){
				list.add(cursor.getString(0));
			}
		}finally{
			db.close();
		}

		return list;
	}
	
	/**
	 * データベースの初期化・更新処理をおこなうインナークラス
	 * @author Masaaki Horikawa
	 * 2014/07/09
	 */
	class DBAdapter extends SQLiteOpenHelper{
		private Context context;
		
		public DBAdapter(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
			this.context = context;
		}

		/* (非 Javadoc)
		 * 初めてデータベースを作成したときに実行される
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// カテゴリテーブルを新規作成
			String create_category_table_sql = "create table category_table " +
					"(name text unique not null, " +				// カテゴリ名
					" icon text unique," +				// アイコン画像のパス
					" position integer unique," +		// 表示位置
					" modified integer unique)";		// 更新日時
			db.execSQL(create_category_table_sql);
			
			// アーティクルテーブルを新規作成
			String create_article_table_sql = "create table article_table " +
					"(name text unique not null, " +	// 名前 
					" description text not null," +				// 説明
					" icon text unique," +						// アイコン画像のパス
					" position integer unique," +				// 表示位置
					" modified integer unique)";				// 更新日時
			db.execSQL(create_article_table_sql);
			
			String create_category_article_table_sql = "create table category_article_table " +
					"(category_id integer not null," +		// カテゴリID
					" article_id integer not null," +		// アーティクルID
					" unique (category_id, article_id))";	// ユニーク制約条件
			db.execSQL(create_category_article_table_sql);
					
			// イメージテーブルを新規作成
			String create_image_table_sql = "create table image_table " +
					"(image_path text unique not null, " +	// 画像URL
					" article_id integer not null)";		// アーティクルID
			db.execSQL(create_image_table_sql);
			
			// ムービーテーブルを新規作成
			String create_movie_table_sql = "create table movie_table " +
					"(movie_path text unique not null," +	// 動画URL
					" article_id integer not null)";		// アーティクルID
			db.execSQL(create_movie_table_sql);
			
			String create_category_icon_view_sql = "create view category_icon_view as " +
					"select category_id, icon from category_article_table A inner join article_table B on a.article_id = B.ROWID " + 
					"order by A.category_id";
			db.execSQL(create_category_icon_view_sql);
			
			String create_category_image_view_sql = "create view category_image_view as " +
					"select category_id, image_path " +
					"from (category_article_table A inner join article_table B on A.article_id = B.ROWID) " +
					"inner join image_table C on B.ROWID = C.article_id " +
					"order by A.category_id";
			db.execSQL(create_category_image_view_sql);
			
			// アーティクルとムービーの結合ビューを作成
			String create_article_movie_view_sql = "create view article_movie_view as " +
					"select A.ROWID article_id, movie_path " +
					"from article_table A inner join movie_table B " +
					"on A.ROWID = B.article_id " +
					"order by A.ROWID";
			db.execSQL(create_article_movie_view_sql);
			
			// アーティクルごとのタグを表すビューを作成
			String create_categorys_on_article_view_sql = "create view categorys_on_article_view as " +
					"select C.name name, A.ROWID article_id " +
					"from (article_table A inner join category_article_table B " +
					"on A.ROWID = B.article_id) " +
					"inner join category_table C " +
					"on B.category_id = C.ROWID " +
					"order by A.ROWID";
			db.execSQL(create_categorys_on_article_view_sql);
			
		/****  サンプルデータセット ***/
			ContentValues cv, cv2;
			File imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			File movieDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			
			// CategoryTable
			String[] catNames = {"くるま","その他"};
			
			CategoryManager cManager = new CategoryManager(context);
			Category[] categorys = new Category[catNames.length];
			for(int i=0; i<categorys.length; i++){
				categorys[i] = cManager.newCategory(db, catNames[i]);
				
				String logStr = "|" +
						categorys[i].getId() + "|" +
						categorys[i].getName() + "|" +
						categorys[i].getIconPath() + "|" +
						categorys[i].getPosition() + "|" +
						categorys[i].getModified() + "|";
				Log.d("category", logStr);
			}
			
			// ArticleTable
			String[] artName = {"レガシィ","アールワン","イギリスのバス"};
			String[] artDesc = {"乗用車","軽自動車","二階建てのバス"};
			String[][] imagePathsS = {
					{copyFromAssets(imageDir, "legacy.jpg"),copyFromAssets(imageDir, "legacy2.jpg")},
					{copyFromAssets(imageDir, "r1.jpg")},
					null
				};
			String[][] moviePathsS = {
					null,null,{copyFromAssets(movieDir, "buss.mp4")}
				};
			long[] caegoryIds = {1};
			
			ArticleManager articleManager = new ArticleManager(context);		
			Article[] articles = new Article[artName.length];
			for(int i=0; i<artName.length; i++){				
				articles[i] = articleManager.newArticle(db, artName[i], artDesc[i], imagePathsS[i], moviePathsS[i], caegoryIds);
		
				String logStr = "|" +
						articles[i].getId() + "|" +
						articles[i].getName() + "|" +
						articles[i].getDescription() + "|" +
						articles[i].getIconPath() + "|" +
						articles[i].getPosition() + "|" +
						articles[i].getModified() + "|";
				
				Log.d("article", logStr);
			}
			
			for(Category cat: categorys){
				cManager.setCategoryIcon(db, cat);
				
				String logStr = "|" +
						cat.getId() + "|" +
						cat.getName() + "|" +
						cat.getIconPath() + "|" +
						cat.getPosition() + "|" +
						cat.getModified() + "|";
				Log.d("category", logStr);
			}
		}
	
		public String copyFromAssets(File dir ,String filename){
			String filePath = null;
			byte[] buffer = new byte[1024*4];
			File outFile = new File(dir, filename);
			InputStream is = null;
			FileOutputStream fos = null;
			try{
				is = context.getAssets().open(filename);
				fos = new FileOutputStream(outFile);
				int num = -1;
				while(-1 != (num = is.read(buffer))){
					fos.write(buffer, 0, buffer.length);
				}
				filePath = outFile.getPath();
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{
					fos.close();
					is.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			return filePath;
		}
		
		/* (非 Javadoc)
		 * データベースがアップデートされたときに実行される
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
	
	/**
	 * データベースをダンプする
	 */
	public void dump(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select ROWID, * from article_table",null);
		
		while(cursor.moveToNext()){
			String str = "|";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + "|";
			}
			Log.d("article", str);
		}
		
		cursor = db.rawQuery("select ROWID, * from category_table", null);
		while(cursor.moveToNext()){
			String str = "|";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + "|";
			}
			Log.d("category", str);
		}
		cursor = db.rawQuery("select ROWID, * from category_article_table", null);
		while(cursor.moveToNext()){
			String str = "|";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + "|";
			}
			Log.d("category_article", str);
		}
		
		cursor = db.rawQuery("select ROWID, * from image_table", null);
		while(cursor.moveToNext()){
			String str = "|";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + "|";
			}
			Log.d("image_table", str);
		}
		
		cursor = db.rawQuery("select ROWID, * from movie_table", null);
		while(cursor.moveToNext()){
			String str = "|";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + "|";
			}
			Log.d("movie_table", str);
		}
		
		cursor = db.rawQuery("select * from category_image_view", null);
		while(cursor.moveToNext()){
			String str = "|";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + "|";
			}
			Log.d("category_image_view", str);
		}
		db.close();
	}
}
