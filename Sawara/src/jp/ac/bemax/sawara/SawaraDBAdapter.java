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
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

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
					" position integer," +		// 表示位置
					" modified integer)";		// 更新日時
			db.execSQL(create_category_table_sql);
			
			// アーティクルテーブルを新規作成
			String create_article_table_sql = "create table article_table " +
					"(name text unique not null, " +	// 名前 
					" description," +					// 説明
					" position integer," +				// 表示位置
					" modified integer)";					// 更新日時
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
			
			// アーティクルとイメージの結合ビューを作成
			String create_article_image_view_sql = "create view article_image_view as " +
					"select A.ROWID article_id, image_path " +
					"from article_table A inner join image_table B " +
					"on A.ROWID = B.article_id " +
					"order by article_id";
			db.execSQL(create_article_image_view_sql);
			
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
			// サンプルカテゴリセット
			ContentValues cv = new ContentValues();
			cv.put("name", "じどうしゃ");
			cv.put("position", 1);
			cv.put("modified", System.currentTimeMillis());
			long catId = db.insert("category_table", null, cv);
			// サンプルアーティクルセット
			cv = new ContentValues();
			cv.put("name", "レガシィ");
			cv.put("description", "スバルの旗艦車種");
			cv.put("position", 1);
			cv.put("modified", System.currentTimeMillis());
			long artId = db.insert("article_table", null, cv);
			//サンプル画像セット
			File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			String filename = System.currentTimeMillis() + ".jpg";
			File imageFile = new File(dir, filename);
			try{
				InputStream is = context.getAssets().open("legacy.jpg");
				Bitmap image = BitmapFactory.decodeStream(is);
				is.close();
				FileOutputStream fos = new FileOutputStream(imageFile);
				image.compress(CompressFormat.JPEG, 100, fos);
				fos.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			// image_tableインポート
			cv = new ContentValues();
			cv.put("image_path", imageFile.getPath());
			cv.put("article_id", artId);
			db.insert("image_table", null, cv);
			// category_tableインポート
			cv = new ContentValues();
			cv.put("category_id", catId);
			cv.put("article_id", artId);
			db.insert("category_article_table", null, cv);
		}
	
		/* (非 Javadoc)
		 * データベースがアップデートされたときに実行される
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
}
