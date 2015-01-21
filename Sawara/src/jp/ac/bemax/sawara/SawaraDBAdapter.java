package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

/**
 * 言葉事典アプリのDBを扱うクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class SawaraDBAdapter{
	private SQLiteOpenHelper helper;
    private SQLiteDatabase mDb;
	
	public SawaraDBAdapter(Context context) {
		helper = new DBAdapter(context, "sawara.db", null, 1);
        mDb = null;
	}
	
	public SQLiteOpenHelper getHelper(){
		return helper;
	}

    public SQLiteDatabase openDb(){
        if(mDb == null){
            mDb = helper.getWritableDatabase();
        }
        if(!mDb.isOpen()){
            mDb = helper.getWritableDatabase();
        }
        return mDb;
    }

    public void closeDb(){
        if(mDb != null && mDb.isOpen()){
            mDb.close();
        }
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
			String sql;
			
			// カテゴリテーブルを新規作成
			sql = "create table category_table " +
					"(name text unique not null, " +				// カテゴリ名
					" icon text unique," +				// アイコン画像のパス
					" position integer unique," +		// 表示位置
					" modified integer )";		// 更新日時
			db.execSQL(sql);
			
			// アーティクルテーブルを新規作成
			sql = "create table article_table " +
					"(name text unique not null, " +	// 名前 
					" description text not null," +				// 説明
					" icon text unique," +						// アイコン画像のパス
					" position integer unique," +				// 表示位置
					" modified integer )";				// 更新日時
			db.execSQL(sql);
			
			sql = "create table category_article_table " +
					"(category_id integer not null," +		// カテゴリID
					" article_id integer not null," +		// アーティクルID
					" unique (category_id, article_id))";	// ユニーク制約条件
			db.execSQL(sql);
			
			// メディアテーブルの新規作成
			sql = "create table media_table " +
					"(path text unique not null, " +
					"type integer not null," +
					"article_id integer," +
					"modified integer)";
			db.execSQL(sql);
			
			// 
			sql = "create view category_icon_view as " +
					"select category_id, icon from category_article_table A inner join article_table B on a.article_id = B.ROWID " + 
					"order by A.category_id";
			db.execSQL(sql);
			
		/****  サンプルデータセット ***/
			File imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			File movieDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			
			SQLiteStatement statement, statement2;
			String  sql2;
			
			// CategoryTable
			String[] catNames = {"くるま","その他"};
			sql = "insert into category_table(name, modified) values (?,?)";
			long[] catIds = new long[catNames.length];
			for(int i=0; i<catNames.length; i++){
				statement = db.compileStatement(sql);
				statement.bindString(1, catNames[i]);
				statement.bindLong(2, System.currentTimeMillis());
				catIds[i] = statement.executeInsert();
			}
			// ArticleTable
			String[] artName = {"ふつうしゃ","けい","イギリスのバス"};
			String[] artDesc = {"ふつうのおおきさのくるま","ちいさいくるま","にかいだてのバス"};
			String[][] paths = {
					{copyFromAssets(imageDir, "legacy.jpg"),copyFromAssets(imageDir, "legacy2.jpg")},
					{copyFromAssets(imageDir, "r1.jpg")},
					{copyFromAssets(movieDir, "buss.mp4")}
				};
			long[][] types = {
					{Media.PHOTO, Media.PHOTO},
					{Media.PHOTO},
					{Media.MOVIE}
				};
			sql = "insert into article_table(name, description, modified) values (?,?,?)";
			statement = db.compileStatement(sql);
			sql2 = "insert into media_table(path, type, article_id, modified) values (?,?,?,?)";
			statement2 = db.compileStatement(sql2);			
			for(int i=0; i<artName.length; i++){

				statement.bindString(1, artName[i]);
				statement.bindString(2, artDesc[i]);
				statement.bindLong(3, System.currentTimeMillis());
				long id = statement.executeInsert();
				
				for(int j=0; j<paths[i].length; j++){
					statement2.bindString(1, paths[i][j]);
					statement2.bindLong(2, types[i][j]);
					statement2.bindLong(3, id);
					statement2.bindLong(4, System.currentTimeMillis());
					statement2.executeInsert();
				}
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
	public void dump(SQLiteDatabase db){
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
		
		cursor = db.rawQuery("select ROWID, * from media_table", null);
		while(cursor.moveToNext()){
			String str = "|";
			for(int i = 0; i < cursor.getColumnCount(); i++){
				str += cursor.getString(i) + "|";
			}
			Log.d("media_table", str);
		}

	}
}
