package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

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
			String create_tag_table_sql = "create table tag_table " +
					"(group_name text," +			// タグ名
					" view_position integer," +		// 表示位置
					" update_date integer)";		// 更新日時
			db.execSQL(create_tag_table_sql);
			
			// アーティクルテーブルを新規作成
			String create_article_table_sql = "create table article_table " +
					"(article_name text unique not null, " +	// 名前 
					" article_description," +					// 説明
					" view_position integer," +					// 表示位置
					" article_reg_date integer," +				// 登録日時
					" article_update_date integer";				// 更新日時
			db.execSQL(create_article_table_sql);
			
			String create_tag_article_table_sql = "create table category_article_table " +
					"(tag_id integer not null," +			// タグID
					" article_id integer not null," +		// アーティクルID
					" unique (category_id, article_id))";	// ユニーク制約条件
			db.execSQL(create_tag_article_table_sql);
					
			// イメージテーブルを新規作成
			String create_image_table_sql = "create table image_table " +
					"(image_url text unique not null, " +	// 画像URL
					" article_id integer not null)";		// アーティクルID
			db.execSQL(create_image_table_sql);
			
			// ムービーテーブルを新規作成
			String create_movie_table_sql = "create table movie_table " +
					"(movie_url text unique not null," +	// 動画URL
					" article_id integer not null)";		// アーティクルID
			db.execSQL(create_movie_table_sql);
			
			// アーティクルとイメージの結合ビューを作成
			String create_article_image_view_sql = "create view article_image_view as " +
					"select article_id, image_url " +
					"from article_table inner join image_table " +
					"on article_table.RQWID = image_table.article_id " +
					"order by article_id";
			db.execSQL(create_article_image_view_sql);
			
			// アーティクルとムービーの結合ビューを作成
			String create_article_movie_view_sql = "create view article_movie_view as " +
					"select article_id, movie_url " +
					"from article_table inner join movie_table " +
					"on article_table.ROWID = movie_table.article_id " +
					"order by article_id";
			db.execSQL(create_article_movie_view_sql);
			
			// アーティクルごとのタグを表すビューを作成
			String create_tags_on_article_view_sql = "create view tags_on_article_view as " +
					"select tag_name, article_id" +
					"from (article_table inner join tag_article_table " +
					"on article_table.ROWID = tag_article_table.article_id) " +
					"inner join tag_table " +
					"on tag_article_table.tag_id = tag_table.ROWID " +
					"order by article_id";
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
