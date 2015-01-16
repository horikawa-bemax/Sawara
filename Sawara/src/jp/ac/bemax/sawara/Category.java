package jp.ac.bemax.sawara;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;

/**
 * Articleが属するカテゴリを表すクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class Category implements ListItem{
	/* name text unique not null
	 * icon text unique
	 * position integer unique
	 * modified integer unique
	 */
	static final String TABLE_NAME = "category_table";
	static final String NAME = "name";
	static final String POSITION = "position";
	static final String MODIFIED = "modified";
	static final String SELECTION = "ROWID=?";
	
	static final String SELECT_SQL = "select ? from category_table where ROWID=?";
	static final String INSERT_SQL = "insert into category_table(?) values (?)";
	static final String UPDATE_SQL = "update category_table set ? where ROWID=?";
	static final String DELETE_SQL = "delete from category_table where ROWID=?";
	
	private long id;
	private Context mContext;
	private ContentValues mValues;

	private Category(Context context){
		mContext = context;
		mValues = new ContentValues();
	}
	
	public static Category createCategory(Context context, SQLiteDatabase db, String name){
		Category category = new Category(context);
		db.beginTransaction();
		try{
			SQLiteStatement stmt = db.compileStatement(INSERT_SQL);
			stmt.bindString(0, NAME+","+MODIFIED);
			stmt.bindString(1, name+","+System.currentTimeMillis());
			long id = stmt.executeInsert();
			
			stmt = db.compileStatement(UPDATE_SQL);
			stmt.bindString(0, POSITION+"="+id);
			stmt.bindLong(1, id);
			int rows = stmt.executeUpdateDelete();
			
			category.id = id;
			
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
		
		return category;
	}

	/**
	 * カテゴリの表示順をゲットする
	 * @return 表示順
	 */
	public long getPosition(SQLiteDatabase db) {
		Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		Long position = cursor.getLong(cursor.getColumnIndex(POSITION));
		cursor.close();
		return position;
	}

	/**
	 * カテゴリの表示順をセットする
	 * @param cId 表示順
	 */
	public void setPosition(SQLiteDatabase db, long position) {
		mValues.put(POSITION, position);
		db.update(TABLE_NAME, mValues, SELECTION, );
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
	public void setModified(long modified) {
		this.modified = modified;
	}

	/**
	 * カテゴリの名前をセットする
	 * @param name カテゴリの名前
	 */
	public void setName(String name) {
		this.name = name;
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

	/**
	 * カテゴリから得られたContentValues
	 * @return ContentValues
	 */
	public ContentValues getContentValues(){
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("position", position);
		cv.put("modified", modified);
		
		return cv;
	}

	@Override
	public String getIconPath() {
		return iconPath;
	}
}
