package jp.ac.bemax.sawara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;


/**
 * 言葉事典で取り扱う、１情報単位を表すクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class Article implements ListItem{
	//static final String TABLE_NAME = "article_table";
	//static final String ID = "ROWID";
	static final String NAME = "name";
	static final String DESCRIPTION = "description";
	static final String POSITION = "position";
	static final String MODIFIED = "modified";

	static final String UPDATE_SQL = "update article_table set ?=? where ROWID=?";
	static final String SELECT_SQL = "select ? from article_table where ROWID=?";

	private long rowid;
	
	/**
	 * Article.javaコンストラクタ
	 */
	public Article(long id){
		rowid = id;
	}
	
	public Article(SQLiteDatabase db, String name, String description){
		rowid = insert(db, name, description);
        setPosition(db, rowid);
	}
	
	public int delete(SQLiteDatabase db){
		String sql = "delete from article_table where ROWID=?";
		SQLiteStatement statement = db.compileStatement(sql);
		statement.bindLong(1, rowid);
		int row = statement.executeUpdateDelete();
		
		return row;
	}
	
	public long insert(SQLiteDatabase db, String name, String description) {
        String sql = "insert into article_table(name, description, modified) values (?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, name);
        statement.bindString(2, description);
        statement.bindLong(3, System.currentTimeMillis());
        long id = statement.executeInsert();

        return id;
    }

    public long getId(){
        return rowid;
    }

	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public long getModified(SQLiteDatabase db)  {
		String[] selectionArgs = {MODIFIED, ""+rowid};
		Cursor cursor = db.rawQuery(SELECT_SQL, selectionArgs);
		cursor.moveToFirst();
		long modified = cursor.getLong(0);
		
		return modified;
	}

	/**
	 * 
	 * @param modified
	 * @throws Exception 
	 */
	public void setModified(SQLiteDatabase db, long modified) {
		SQLiteStatement statement = db.compileStatement(UPDATE_SQL);
		statement.bindString(1, MODIFIED);
		statement.bindLong(2, modified);
		statement.bindLong(3, rowid);
		statement.executeUpdateDelete();
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(SQLiteDatabase db, String name) {
		SQLiteStatement statement = db.compileStatement(UPDATE_SQL);
		statement.bindString(1, NAME);
		statement.bindString(2, name);
		statement.bindLong(3, rowid);
		statement.executeUpdateDelete();
	}

	/**
	 * 
	 * @param description
	 * @throws Exception 
	 */
	public void setDescription(SQLiteDatabase db, String description) {
		SQLiteStatement statement = db.compileStatement(UPDATE_SQL);
		statement.bindString(1, DESCRIPTION);
		statement.bindString(2, description);
		statement.bindLong(3, rowid);
		statement.executeUpdateDelete();
	}
	
	/**
	 * item_nameを返す
	 * @return アイテムの名前
	 * @throws Exception 
	 */
	public String getName(SQLiteDatabase db){
		String[] selectionArgs = {NAME, ""+rowid};
		Cursor cursor = db.rawQuery(SELECT_SQL, selectionArgs);
		cursor.moveToFirst();
		String name = cursor.getString(0);
		
		return name;
	}
	
	/**
	 * descriptionを返す
	 * @return アイテムの詳細
	 * @throws Exception 
	 */
	public String getDescription(SQLiteDatabase db) {
		String[] selectionArgs = {DESCRIPTION, ""+rowid};
		Cursor cursor = db.rawQuery(SELECT_SQL, selectionArgs);
		cursor.moveToFirst();
		String description = cursor.getString(0);
		
		return description;
	}

	public long getPosition(SQLiteDatabase db) {
		String[] selectionArgs = {POSITION, ""+rowid};
		Cursor cursor = db.rawQuery(SELECT_SQL, selectionArgs);
		cursor.moveToFirst();
		long position = cursor.getLong(0);
		
		return position;
	}

	public void setPosition(SQLiteDatabase db, long position) {
		SQLiteStatement statement = db.compileStatement(UPDATE_SQL);
		statement.bindString(1, POSITION);
		statement.bindLong(2, position);
		statement.bindLong(3, rowid);
		statement.executeUpdateDelete();
	}
	
	public String dump(SQLiteDatabase db){
		String str = "";
		str += "ROWID:" + rowid;
		str += "|NAME:" + getName(db);
		str += "|DESCRIPTION;" + getDescription(db);
		str += "|MODIFIED:" + getModified(db);
		str += "|";
		
		return str;
	}

	public Bitmap getIcon(SQLiteDatabase db) {
		Bitmap icon = null;
		
		List<Media> list = Media.findMediasByArticle(db, this);
		if(list.size() > 0){
			Media media = list.get(0);
			String path = media.getPath(db);
            long type = media.getType(db);

            if(type == Media.PHOTO){
                icon = BitmapFactory.decodeFile(path);
            }else if(type == Media.MOVIE){
                icon = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
            }
		}

		return icon;
	}

    public List<Media> getMedias(SQLiteDatabase db){
        List<Media> list = new ArrayList<Media>();

        String sql = "select ROWID from media_table where article_id=?";
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        while(cursor.moveToNext()){
            long mediaId = cursor.getLong(0);
            Media media = new Media(mediaId);
            list.add(media);
        }

        return list;
    }
}
