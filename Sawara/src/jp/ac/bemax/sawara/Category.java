package jp.ac.bemax.sawara;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;


/**
 * Articleが属するカテゴリを表すクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class Category implements ListItem{
	public static final String TABLE_NAME = "category_table";
	public static final String NAME = "name";
	public static final String MODIFIED = "modified";
	public static final String POSITION = "position";

    public static final String SELECT_SQL = "select ? from category_table where ROWID=?";
    public static final String UPDATE_SQL = "update category_table set ?=? where ROWID=?";

    public static final int ICON_WIDTH = 320;
    public static final int ICON_HEIGHT = 240;
	
	private long rowid;
	
	/**
	 * Category.javaコンストラクタ
	 */
	public Category(long id){
		rowid = id;
	}
	
	public Category(SQLiteDatabase db, String name){
		rowid = insert(db, name);
        setPosition(db, rowid);
	}
	
	public long insert(SQLiteDatabase db, String name){
		String sql = "insert into category_table(name, modified) values (?,?)";
		SQLiteStatement statement = db.compileStatement(sql);
		statement.bindString(1, name);
		statement.bindLong(2, System.currentTimeMillis());
		long id = statement.executeInsert();
		
		return id;
	}
	
	public int delete(SQLiteDatabase db){
		String sql = "delete from category_table where ROWID=?";
		SQLiteStatement statement = db.compileStatement(sql);
		statement.bindLong(1, rowid);
		int row = statement.executeUpdateDelete();
		
		if(row > 0){
			rowid = -1;
		}
		
		return row;
	}
	
	/**
	 * カテゴリの表示順をゲットする
	 * @return 表示順
	 */
	public long getPosition(SQLiteDatabase db) {
        String[] selectionArgs = {POSITION, ""+rowid};
		Cursor cursor = db.rawQuery(SELECT_SQL, selectionArgs);
        cursor.moveToFirst();
        long position = cursor.getLong(0);
        cursor.close();

        return position;
	}

	/**
	 * カテゴリの表示順をセットする
	 * @param db database
     * @param position position
	 */
	public void setPosition(SQLiteDatabase db, long position) {
		SQLiteStatement statement = db.compileStatement(UPDATE_SQL);
        statement.bindString(1, POSITION);
        statement.bindLong(2, position);
        statement.bindLong(3, rowid);
        statement.executeUpdateDelete();
	}

	/**
	 * カテゴリの更新日時をゲットする
	 * @return 更新日時
	 */
	public long getModified(SQLiteDatabase db) {
        String[] selectionArgs = {MODIFIED, ""+rowid};
        Cursor cursor = db.rawQuery(SELECT_SQL, selectionArgs);
        cursor.moveToFirst();
        long modified = cursor.getLong(0);
        cursor.close();

        return modified;
	}

	/**
	 * カテゴリの更新日時をセットする
	 * @param modified 更新日時
	 */
	public void setModified(SQLiteDatabase db, long modified) {
        SQLiteStatement statement = db.compileStatement(UPDATE_SQL);
        statement.bindString(1, MODIFIED);
        statement.bindLong(2, modified);
        statement.bindLong(3, rowid);
        statement.executeUpdateDelete();
	}

	/**
	 * カテゴリの名前をセットする
	 * @param name カテゴリの名前
	 */
	public void setName(SQLiteDatabase db, String name) {
        SQLiteStatement statement = db.compileStatement(UPDATE_SQL);
        statement.bindString(1, NAME);
        statement.bindString(2, name);
        statement.bindLong(3, rowid);
        statement.executeUpdateDelete();
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
	public String getName(SQLiteDatabase db) {
        String[] selectionArgs = {NAME, ""+rowid};
        Cursor cursor = db.rawQuery(SELECT_SQL, selectionArgs);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        cursor.close();

        return name;
	}

	@Override
	public Bitmap getIcon(SQLiteDatabase db) {
        Bitmap icon = Bitmap.createBitmap(ICON_WIDTH, ICON_HEIGHT*3/2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);
        Rect src = new Rect(0, 0, ICON_WIDTH, ICON_HEIGHT);
        Rect dst = null;
        int width = ICON_WIDTH/2;
        int height = ICON_HEIGHT/2;

        List<ListItem> list = getArticles(db);
        // 最大６つの画像を使ってアイコンを作成する
        for(int i=0; i<6 && i<list.size(); i++){
            Bitmap srcImage = list.get(i).getIcon(db);
            int left = (i%2) * width;
            int top = (i/2) * height;
            dst = new Rect(left, top, left + width, top + height);
            canvas.drawBitmap(srcImage, src, dst, null);
        }

        return icon;
	}

    public List<ListItem> getArticles(SQLiteDatabase db){
        List<ListItem> list = new ArrayList<ListItem>();

        String sql = "select article_id from category_article_table where category_id=?";
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        while(cursor.moveToNext()){
            long articleId = cursor.getLong(0);
            Article article = new Article(articleId);
            list.add(article);
        }
        return list;
    }

    public static List<ListItem> getAllCategorys(SQLiteDatabase db){
        List<ListItem> list = new ArrayList<ListItem>();

        String sql = "select ROWID from category_table";
        Cursor cursor = db.rawQuery(sql, null);

        while(cursor.moveToNext()){
            long id = cursor.getLong(0);
            Category category = new Category(id);
            list.add(category);
        }

        return list;
    }
}
