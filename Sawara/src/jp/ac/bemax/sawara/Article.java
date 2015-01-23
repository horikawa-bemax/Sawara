package jp.ac.bemax.sawara;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;


/**
 * 言葉事典で取り扱う、１情報単位を表すクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class Article implements Serializable{
	//static final String TABLE_NAME = "article_table";
	//static final String ID = "ROWID";
	static final String NAME = "name";
	static final String DESCRIPTION = "description";
	static final String POSITION = "position";
	static final String MODIFIED = "modified";

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
		String[] selectionArgs = {""+rowid};
		Cursor cursor = db.rawQuery("select modified from article_table where ROWID=?", selectionArgs);
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
		SQLiteStatement statement = db.compileStatement("update article_table set modified=? where ROWID=?");
		statement.bindLong(1, modified);
		statement.bindLong(2, rowid);
		statement.executeUpdateDelete();
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(SQLiteDatabase db, String name) {
		SQLiteStatement statement = db.compileStatement("update article_table set name=? where ROWID=?");
		statement.bindString(1, name);
		statement.bindLong(2, rowid);
		statement.executeUpdateDelete();
	}

	/**
	 * 
	 * @param description
	 * @throws Exception 
	 */
	public void setDescription(SQLiteDatabase db, String description) {
		SQLiteStatement statement = db.compileStatement("update article_table set description=? where ROWID=?");
		statement.bindString(1, description);
		statement.bindLong(2, rowid);
		statement.executeUpdateDelete();
	}
	
	/**
	 * item_nameを返す
	 * @return アイテムの名前
	 * @throws Exception 
	 */
	public String getName(SQLiteDatabase db){
		String[] selectionArgs = {""+rowid};
		Cursor cursor = db.rawQuery("select name from article_table where ROWID=?", selectionArgs);
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
		String[] selectionArgs = {""+rowid};
		Cursor cursor = db.rawQuery("select description from article_table where ROWID=?", selectionArgs);
		cursor.moveToFirst();
		String description = cursor.getString(0);
		
		return description;
	}

	public long getPosition(SQLiteDatabase db) {
		String[] selectionArgs = {""+rowid};
		Cursor cursor = db.rawQuery("select position from article_table where ROWID=?", selectionArgs);
		cursor.moveToFirst();
		long position = cursor.getLong(0);
		
		return position;
	}

	public void setPosition(SQLiteDatabase db, long position) {
		SQLiteStatement statement = db.compileStatement("update article_table set position=? where ROWID=?");
		statement.bindLong(1, position);
		statement.bindLong(2, rowid);
		statement.executeUpdateDelete();
	}

    public void setCateogories(SQLiteDatabase db, List<Category> categories){
        String sql = "insert into category_article_table(category_id, article_id) values (?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);
        for(Category category: categories){
            statement.bindLong(1, category.getId());
            statement.bindLong(2, rowid);
            statement.executeInsert();
        }
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

	public Media getIcon(SQLiteDatabase db) {
	    Media media = null;
        List<Media> medias = getMedias(db);
        if(medias.size()>0) {
            media = medias.get(0);
        }
		return media;
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

    public static List<Article> getAllArticles(SQLiteDatabase db){
        List<Article> articles = new ArrayList<Article>();

        db.beginTransaction();
        try {
            String sql = "select ROWID from article_table";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                Article article = new Article(id);
                articles.add(article);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }

        return articles;
    }

    public List<ImageItem> getImageItems(SQLiteDatabase db, Context context){
        List<ImageItem> mediaList = new ArrayList<ImageItem>();

        db.beginTransaction();
        try {
            String sql = "select ROWID from media_table where article_id=?";
            String[] selectionArgs = {"" + rowid};
            Cursor cursor = db.rawQuery(sql, selectionArgs);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                Media media = new Media(id);
                File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                String fileName = media.getIconPath(db);
                File file = new File(dir, fileName);
                ImageItem item = new ImageItem(context, id, file.getPath(), media.getType(db), media.getIconPath(db));
                mediaList.add(item);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
        return mediaList;
    }
}
