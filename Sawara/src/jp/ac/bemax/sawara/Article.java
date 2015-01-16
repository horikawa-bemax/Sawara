package jp.ac.bemax.sawara;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 言葉事典で取り扱う、１情報単位を表すクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class Article implements ListItem{
	static final String TABLE_NAME = "article_table";
	static final String ID = "ROWID";
	static final String NAME = "name";
	static final String DESCRIPTION = "description";
	static final String POSITION = "position";
	static final String MODIFIED = "modified";
	
	private SQLiteDatabase mDb;
	private ContentValues mContentValues;
	private long id;
	private String[] imagePaths;
	private String[] moviePaths;
	private long[] categoryIds;
	private String iconPath;
	
	static public Article createArticle(SQLiteDatabase db, String name, String description){
		Article article = new Article(db);
		try{
			ContentValues values = new ContentValues();
			values.put(NAME, name);
			values.put(DESCRIPTION, description);
			values.put(MODIFIED, System.currentTimeMillis());
			
			long id = article.insertDB(values);
			article.setPosition(id);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return article;
	}
	
	static public Article findArticleById(SQLiteOpenHelper helper, long id){
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String[] columns = {"ROWID"};
		String[] selectionArgs = {""+id};
		Cursor cursor = db.query(TABLE_NAME, columns, "ROWID=?", selectionArgs, null, null, null);
		
		Article article = null;
		
		if(cursor.getCount() > 0){
			article = new Article(db);
			article.id = id;
		}
		
		db.close();
		
		return article;
	}
	
	static public Article[] getAllArticles(SQLiteOpenHelper helper){
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String[] columns = {"ROWID"};
		Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
		
		Article[] articles = new Article[cursor.getCount()];
		cursor.moveToFirst();
		for(int i=0; i<articles.length; i++){
			articles[i] = new Article(db);
			articles[i].id = cursor.getLong(0);
			cursor.moveToNext();
		}
		cursor.close();
		
		db.close();
		
		return articles;
	}
	
	public void openDB(SQLiteDatabase db){
		mDb = db;
	}
	
	public void closeDB(){
		mDb.close();
	}
	
	public long insertDB(ContentValues values) throws Exception{
		long id = mDb.insert(TABLE_NAME, null, values);
		
		if(id == -1){
			throw new Exception("DBへの挿入に失敗しました");
		}
		this.id = id;
		mDb.close();
		
		return id;
	}
	
	private Cursor readDB() throws Exception{
		if(id < 0){
			throw new Exception("IDが不正です");
		}
		if(mDb == null){
			throw new Exception("DBがnull");
		}
		String[] columns = {NAME, DESCRIPTION, POSITION, MODIFIED};
		String[] selectionArgs = {"" + id};
		Cursor cursor = mDb.query(TABLE_NAME, columns, "ROWID = ?", selectionArgs, null, null, null);
		
		return cursor;
	}
	
	private void writeDB( ContentValues values) throws Exception {
		String[] whereArgs = {""+this.id};
		if(mDb == null){
			throw new Exception("DBがnull");
		}
		int num = mDb.update(TABLE_NAME, values, "ROWID=?", whereArgs);
				
		if(num < 1){
			throw new Exception("DBの更新に失敗しました");
		}
	}
	
	/**
	 * Article.javaコンストラクタ
	 */
	private Article(SQLiteDatabase db){
		mDb = db;
		mContentValues = new ContentValues();
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(long id){
		this.id = id;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public long getModified()  {
		long modified = 0;
		Cursor cursor = null;
		
		try {
			cursor = readDB();
			cursor.moveToFirst();
			modified = cursor.getLong(cursor.getColumnIndex(MODIFIED));
			cursor.close();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}		
		return modified;
	}

	/**
	 * 
	 * @param modified
	 * @throws Exception 
	 */
	public void setModified(long modified) {
		mContentValues.put(MODIFIED, modified);
		try {
			writeDB(mContentValues);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		mContentValues.put(NAME, name);
		try {
			writeDB(mContentValues);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param description
	 * @throws Exception 
	 */
	public void setDescription(String description) {
		mContentValues.put(MODIFIED, description);
		try {
			writeDB(mContentValues);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * ROWIDを返す
	 * @return ROWID
	 */
	public long getId(){
		return id;
	}
	
	/**
	 * item_nameを返す
	 * @return アイテムの名前
	 * @throws Exception 
	 */
	public String getName(){
		String name = null;
		Cursor cursor = null;
		
		try {
			cursor = readDB();
			cursor.moveToFirst();
			name = cursor.getString(cursor.getColumnIndex(NAME));
			cursor.close();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}


		return name;
	}
	
	/**
	 * descriptionを返す
	 * @return アイテムの詳細
	 * @throws Exception 
	 */
	public String getDescription() {
		String description = null;
		Cursor cursor = null;
		
		try {
			cursor = readDB();
			cursor.moveToFirst();
			description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
			cursor.close();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return description;
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getImagePaths() {	
		return this.imagePaths;
	}

	/**
	 * 
	 * @param imagePaths
	 */
	public void setImagePaths(String[] imagePaths) {
		this.imagePaths = imagePaths;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getMoviePaths() {
		return moviePaths;
	}

	/**
	 * 
	 * @param moviePaths
	 */
	public void setMoviePaths(String[] moviePaths) {
		this.moviePaths = moviePaths;
	}

	/** 
	 * このArticleが持つCategoryIDの配列を返す。
	 * @param manager
	 * @return
	 */
	public long[] getCategories(CategoryManager manager){
		long[] cIds = null;
		
		return cIds;
	}

	public long[] getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(long[] categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public long getPosition() {
		long position = 0;
		Cursor cursor = null;
		
		try {
			cursor = readDB();
			cursor.moveToFirst();
			position = cursor.getLong(cursor.getColumnIndex(POSITION));
			cursor.close();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return position;
	}

	public void setPosition(long position) {
		mContentValues.put(POSITION, position);
		try {
			writeDB(mContentValues);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public String dump(){
		String str = "";
		str += "ROWID:" + this.id;
		str += "|NAME:" + getName();
		str += "|DESCRIPTION;" + getDescription();
		str += "|MODIFIED:" + getModified();
		str += "|";
		
		return str;
	}
}
