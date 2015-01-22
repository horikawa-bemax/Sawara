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
            db.beginTransaction();
            try {
                // カテゴリテーブルを新規作成
                sql = "create table category_table " +
                        "(name text unique not null, " +                // カテゴリ名
                        " icon integer unique," +                // アイコン画像のパス
                        " position integer unique," +        // 表示位置
                        " modified integer )";        // 更新日時
                db.execSQL(sql);

                // アーティクルテーブルを新規作成
                sql = "create table article_table " +
                        "(name text unique not null, " +    // 名前
                        " description text not null," +                // 説明
                        " position integer unique," +                // 表示位置
                        " modified integer )";                // 更新日時
                db.execSQL(sql);

                sql = "create table category_article_table " +
                        "(category_id integer not null," +        // カテゴリID
                        " article_id integer not null," +        // アーティクルID
                        " unique (category_id, article_id))";    // ユニーク制約条件
                db.execSQL(sql);

                // メディアテーブルの新規作成
                sql = "create table media_table " +
                        "(path text unique not null, " +
                        "type integer not null," +
                        "icon_path string not null," +
                        "article_id integer," +
                        "modified integer)";
                db.execSQL(sql);

                /****  サンプルデータセット ***/
                File imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File movieDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);

                SQLiteStatement statement, statement2;
                String sql2;

                // CategoryTable作成
                String[] catNames = {"くるま", "その他"};
                for (String name : catNames) {
                    Category category = new Category(db, name);
                }
                // ArticleTable
                String[] artName = {"ふつうしゃ", "けい", "イギリスのバス"};
                String[] artDesc = {"ふつうのおおきさのくるま", "ちいさいくるま", "にかいだてのバス"};
                String[][] paths = {
                        {"legacy.jpg","legacy2.jpg"},
                        {"r1.jpg"},
                        {"buss.mp4"}
                };
                long[][] types = {
                        {Media.PHOTO, Media.PHOTO},
                        {Media.PHOTO},
                        {Media.MOVIE}
                };
                List<Category> cates = new ArrayList<Category>();
                cates.add(Category.findCategoryByName(db, "くるま"));
                for (int i = 0; i < artName.length; i++) {
                    Article article = new Article(db, artName[i], artDesc[i]);
                    article.setCateogories(db, cates);
                    for (int j = 0; j < paths[i].length; j++) {
                        copyFromAssets(types[i][j], paths[i][j]);
                        Media media = new Media(db, context, paths[i][j], types[i][j], article);
                    }
                }

                List<Category> cs = Category.getAllCategory(db);
                for(Category category: cs){
                    category.makeIcon(db, context);
                }

                db.setTransactionSuccessful();
            }finally {
                db.endTransaction();
            }
        }

        public String copyFromAssets(long type, String filename){
            String filePath = null;
            byte[] buffer = new byte[1024*4];
            File dir = null;
            if(type == Media.PHOTO){
                dir =context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            }else if(type == Media.MOVIE){
                dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            }
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
