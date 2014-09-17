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
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;


public class SawaraDBAdapter{
	private SQLiteOpenHelper helper;
	
	/* 初期のグループ */
	private final String[] groupNames = {"おうち","がっこう","あそび","べんきょう","たのしい","こわい"}; 
	
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
			// テーブルを新規作成
			db.execSQL("create table group_table (group_id integer primary key autoincrement, group_name nchar(20))");
			db.execSQL("create table item_table (item_id integer primary key autoincrement, item_name nchar(30) UNIQUE, item_description nchar(50), item_image nchar(50), item_movie nchar(50))");
			
			// group_tableに初期値を設定
			for(String name: groupNames){
				db.execSQL("insert into group_table(group_name) values ('"+ name +"')");
			}
			
			// Itemリストを作成（サンプル作成）
			String[] strs = {"ともだち","じどうしゃ","こうえん","いえ","いぬ","やま","でんしんばしら","ひこうき"};
			String[] descriptions = {"なかよしなひと","どうろをはしるのりもの","みんながあそぶところ","おうち","わんわんとほえるいきもの","おおきくてたかい","みちのそばにたっているぼう","そらをとぶのりもの"};
			String[] images = {"friend","car","park","house","dog","mountain","pole","airplane"};
			InputStream inputStream = null;
			FileOutputStream fileOutputStream = null;
			
			ItemManager iManager = ItemManager.newItemManager(context);
			ContentValues values = new ContentValues();
			for(int i=0; i<8; i++){
				try {
					// サンプル画像をassetから読み込む
					inputStream = context.getAssets().open("sample/"+images[i]+".jpg");
					Bitmap bmp = BitmapFactory.decodeStream(inputStream);
					
					// サンプル画像をアプリの記憶領域に書き込む準備
					File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
					String imageFileName = images[i] + ".jpg";
					
					// 画像をアプリの記憶領域に書き込む
					fileOutputStream = new FileOutputStream(new File(dir, imageFileName));
					bmp.compress(CompressFormat.JPEG, 100, fileOutputStream);
					
					// DBにサンプルデータを登録する
					values.put("item_name", strs[i]);
					values.put("item_description", descriptions[i]);
					values.put("item_image", imageFileName);
					db.insert("item_table", null, values);
					
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						inputStream.close();
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
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
