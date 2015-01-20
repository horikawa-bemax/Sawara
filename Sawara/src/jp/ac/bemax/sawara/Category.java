package jp.ac.bemax.sawara;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;



/**
 * Articleが属するカテゴリを表すクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class Category implements ListItem{
	public static final String TabeName = "category_table";
	public static final String Name = "name";
	public static final String Modified = "modified";
	public static final String Position = "position"; 
	
	private long rowid;
	private String name;
	private long modified;
	private long position;
	
	/**
	 * Category.javaコンストラクタ
	 */
	public Category(SQLiteDatabase db, long id){
		rowid = id;
		load(db);
	}
	
	public Category(SQLiteDatabase db, String name){
		rowid = insert(db, name);
		if(rowid != -1){
			this.name = name;
		}
	}
	
	public void load(SQLiteDatabase db){
		String sql = "select name, position, modified from category_table where ROWID=?";
		String[] selectionArgs = {""+rowid};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		cursor.moveToFirst();
		name = cursor.getString(cursor.getColumnIndex(Name));
		position = cursor.getLong(cursor.getColumnIndex(Position));
		modified = cursor.getLong(cursor.getColumnIndex(Modified));
		cursor.close();
	}
	
	public long insert(SQLiteDatabase db, String name){
		String sql = "insert into category_table(name, modified) values (?,?)";
		SQLiteStatement statement = db.compileStatement(sql);
		statement.bindString(1, name);
		statement.bindLong(2, System.currentTimeMillis());
		long id = statement.executeInsert();
		
		return id;
	}
	
	public int update(SQLiteDatabase db){
		String sql = "update category_table set name=?, position=?, modified=?) where ROWID=?";
		SQLiteStatement statement = db.compileStatement(sql);
		statement.bindString(1, name);
		statement.bindLong(2, position);
		statement.bindLong(3, modified);
		statement.bindLong(4, rowid);
		int row = statement.executeUpdateDelete();
		
		return row;
	}
	
	public int delete(SQLiteDatabase db){
		String sql = "delete from category_table where ROWID=?";
		SQLiteStatement statement = db.compileStatement(sql);
		statement.bindLong(1, rowid);
		int row = statement.executeUpdateDelete();
		
		if(row > 0){
			rowid = -1;
			name = null;
			position = -1;
			modified = -1;
		}
		
		return row;
	}
	
	/**
	 * カテゴリの表示順をゲットする
	 * @return 表示順
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * カテゴリの表示順をセットする
	 * @param cId 表示順
	 */
	public void setPosition(SQLiteDatabase db, long position) {
		this.position = position;
		update(db);
	}

	/**
	 * カテゴリの更新日時をゲットする
	 * @return 更新日時
	 */
	public long getModified() {
		
		return modified;
	}

	/**
	 * カテゴリの更新日時をセットする
	 * @param modified 更新日時
	 */
	public void setModified(SQLiteDatabase db, long modified) {
		this.modified = modified;
		update(db);
	}

	/**
	 * カテゴリの名前をセットする
	 * @param name カテゴリの名前
	 */
	public void setName(SQLiteDatabase db, String name) {
		this.name = name;
		update(db);
	}

	/**
	 * カテゴリのIDをゲットする
	 */
	@Override
	public long getId() {
		return rowid;
	}

	/**
	 * カテゴリのIDセットする
	 * @param id カテゴリのID
	 */
	public void setId(long id){
		rowid = id;
	}
	
	/**
	 * カテゴリの名前をゲットする
	 */
	@Override
	public String getName() {
		return name;
	}


	@Override
	public String getIconPath() {
		String path = null;;
		
		return path;
	}
}
