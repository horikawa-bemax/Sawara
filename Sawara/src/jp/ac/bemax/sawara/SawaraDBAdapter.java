package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SawaraDBAdapter{
	/* 初期のグループ */
	private final String[] groupNames = {"おうち","がっこう","あそび","べんきょう","たのしい","こわい"}; 
	
	private SQLiteDatabase db;
	
	public SawaraDBAdapter(Context context) {
		SQLiteOpenHelper dba = new DBAdapter(context, "sawara.db", null, 1);
		db = dba.getWritableDatabase();
	}
	
	/**
	 * group_tableのgroup_nameの写像リストを返す
	 * @return group_nameのリスト
	 */
	public List<String> getGroupNameList(){
		List<String> list = new ArrayList<String>();
		
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
		public DBAdapter(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		/* (非 Javadoc)
		 * 初めてデータベースを作成したときに実行される
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			/* テーブルを新規作成 */
			db.execSQL("create table group_table (group_id integer primary key autoincrement, group_name nchar(20))");
			db.execSQL("create table item_table (item_id integer primary key autoincrement, item_name nchar(30), item_image nchar(20))");
	
			/* group_tableに初期値を設定 */
			for(String name: groupNames){
				db.execSQL("insert into group_table(group_name) values ('"+ name +"')");
			}
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
