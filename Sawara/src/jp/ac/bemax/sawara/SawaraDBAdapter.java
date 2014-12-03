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
					"(name text," +				// カテゴリ名
					" icon text," +				// アイコン画像のパス
					" position integer," +		// 表示位置
					" modified integer)";		// 更新日時
			db.execSQL(create_category_table_sql);
			
			// アーティクルテーブルを新規作成
			String create_article_table_sql = "create table article_table " +
					"(name text unique not null, " +	// 名前 
					" description text," +				// 説明
					" icon text," +						// アイコン画像のパス
					" position integer," +				// 表示位置
					" modified integer)";				// 更新日時
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
			String[] catData = {"くるま","ひこうき"};
			for(String cat: catData){
				cv = new ContentValues();
				cv.put("name", cat);
				cv.put("position", 0);
				cv.put("modified", System.currentTimeMillis());
				long cId = db.insert("category_table", null, cv);
			}
			
			// ArticleTable
			String[][] artData = {
					{"レガシィ","普通乗用車","legacy.jpg,legacy2.jpg","buss.mp4"},
					{"アールワン","軽自動車","r1.jpg",""}};
			for(String[] data : artData){
				cv = new ContentValues();
				cv.put("name", data[0]);
				cv.put("description", data[1]);
				cv.put("modified", System.currentTimeMillis());
				long aId = db.insert("article_table", null, cv);
				String[] images = data[2].split(",");
				for(String img : images){
					if(img.length()>0 && copyFromAssets(imageDir, img)){
						File f = new File(imageDir, img);
						cv2 = new ContentValues();
						cv2.put("image_path", f.getPath());
						cv2.put("article_id", aId);
						db.insert("image_table", null, cv2);
					}
				}
				String[] movies = data[3].split(",");
				for(String mov : movies){
					if(mov.length()>0 && copyFromAssets(movieDir, mov)){
						File f = new File(movieDir, mov);
						cv2 = new ContentValues();
						cv2.put("movie_path", f.getPath());
						cv2.put("article_id", aId);
						db.insert("movie_table", null, cv2);
					}
				}
			}
			
			// Category_Article_table
			int[][] ca = {{1,1},{1,2}};
			for(int[] d: ca){
				cv = new ContentValues();
				cv.put("category_id", d[0]);
				cv.put("article_id", d[1]);
				db.insert("category_article_table", null, cv);
			}
		}
	
		public boolean copyFromAssets(File dir ,String filename){
			boolean copied = false;
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
				copied = true;
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
			return copied;
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
			String str = "";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + ",";
			}
			Log.d("article", str);
		}
		cursor = db.rawQuery("select ROWID, * from category_table", null);
		while(cursor.moveToNext()){
			String str = "";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + ",";
			}
			Log.d("category", str);
		}
		cursor = db.rawQuery("select ROWID, * from category_article_table", null);
		while(cursor.moveToNext()){
			String str = "";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + ",";
			}
			Log.d("category_article", str);
		}
		
		cursor = db.rawQuery("select ROWID, * from image_table", null);
		while(cursor.moveToNext()){
			String str = "";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + ",";
			}
			Log.d("image_table", str);
		}
		
		cursor = db.rawQuery("select ROWID, * from movie_table", null);
		while(cursor.moveToNext()){
			String str = "";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + ",";
			}
			Log.d("movie_table", str);
		}
		
		cursor = db.rawQuery("select * from category_image_view", null);
		while(cursor.moveToNext()){
			String str = "";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + ",";
			}
			Log.d("category_image_view", str);
		}
		db.close();
	}
}
