package jp.ac.bemax.sawara;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


/**
 * 言葉事典で取り扱う、１情報単位を表すクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class Article implements ListItem{
	//static final String TABLE_NAME = "article_table";
	//static final String ID = "ROWID";
	static final String Name = "name";
	static final String Description = "description";
	static final String Position = "position";
	static final String Modified = "modified";

	static final String UpdateSQL = "update article_table set ?=? where ROWID=?";
	static final String SelectSQL = "select ? from article_table where ROWID=?";
	
	private transient SQLiteDatabase db;
	private long rowid;
	
	/**
	 * Article.javaコンストラクタ
	 */
	public Article(SQLiteDatabase db, long id){
		rowid = id;
		this.db = db;
	}
	
	public Article(SQLiteDatabase db, String name, String description){
		this.db = db;
		rowid = insert(name, description);
	}

	public void setDB(SQLiteDatabase db){
		this.db = db;
	}
	
	public int delete(){
		String sql = "delete from article_table where ROWID=?";
		SQLiteStatement statement = db.compileStatement(sql);
		statement.bindLong(1, rowid);
		int row = statement.executeUpdateDelete();
		
		return row;
	}
	
	public long insert(String name, String description){
		String sql = "insert into article_table(name, description, modified) values (?,?,?)";
		SQLiteStatement statement = db.compileStatement(sql);
		statement.bindString(1, name);
		statement.bindString(2, description);
		statement.bindLong(3, System.currentTimeMillis());
		long id = statement.executeInsert();
		
		return id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setId(long id){
		rowid = id;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public long getModified()  {
		String[] selectionArgs = {Modified, ""+rowid};
		Cursor cursor = db.rawQuery(SelectSQL, selectionArgs);
		cursor.moveToFirst();
		long modified = cursor.getLong(0);
		
		return modified;
	}

	/**
	 * 
	 * @param modified
	 * @throws Exception 
	 */
	public void setModified(long modified) {
		SQLiteStatement statement = db.compileStatement(UpdateSQL);
		statement.bindString(1, Modified);
		statement.bindLong(2, modified);
		statement.bindLong(3, rowid);
		statement.executeUpdateDelete();
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		SQLiteStatement statement = db.compileStatement(UpdateSQL);
		statement.bindString(1, Name);
		statement.bindString(2, name);
		statement.bindLong(3, rowid);
		statement.executeUpdateDelete();
	}

	/**
	 * 
	 * @param description
	 * @throws Exception 
	 */
	public void setDescription(String description) {
		SQLiteStatement statement = db.compileStatement(UpdateSQL);
		statement.bindString(1, Description);
		statement.bindString(2, description);
		statement.bindLong(3, rowid);
		statement.executeUpdateDelete();
	}

	/**
	 * ROWIDを返す
	 * @return ROWID
	 */
	public long getId(){
		return rowid;
	}
	
	/**
	 * item_nameを返す
	 * @return アイテムの名前
	 * @throws Exception 
	 */
	public String getName(){
		String[] selectionArgs = {Name, ""+rowid};
		Cursor cursor = db.rawQuery(SelectSQL, selectionArgs);
		cursor.moveToFirst();
		String name = cursor.getString(0);
		
		return name;
	}
	
	/**
	 * descriptionを返す
	 * @return アイテムの詳細
	 * @throws Exception 
	 */
	public String getDescription() {
		String[] selectionArgs = {Description, ""+rowid};
		Cursor cursor = db.rawQuery(SelectSQL, selectionArgs);
		cursor.moveToFirst();
		String description = cursor.getString(0);
		
		return description;
	}

	public long getPosition() {
		String[] selectionArgs = {Position, ""+rowid};
		Cursor cursor = db.rawQuery(SelectSQL, selectionArgs);
		cursor.moveToFirst();
		long position = cursor.getLong(0);
		
		return position;
	}

	public void setPosition(long position) {
		SQLiteStatement statement = db.compileStatement(UpdateSQL);
		statement.bindString(1, Position);
		statement.bindLong(2, position);
		statement.bindLong(3, rowid);
		statement.executeUpdateDelete();
	}
	
	public String dump(){
		String str = "";
		str += "ROWID:" + rowid;
		str += "|NAME:" + getName();
		str += "|DESCRIPTION;" + getDescription();
		str += "|MODIFIED:" + getModified();
		str += "|";
		
		return str;
	}

	@Override
	public String getIconPath() {
		String path=null;
		
		List<Media> list = Media.findMediasByArticle(db, this);
		if(list.size() > 0){
			Media media = list.get(0);
			path = media.getPath();
		}
		
		return path;
	}
}
