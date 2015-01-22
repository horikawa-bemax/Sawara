package jp.ac.bemax.sawara;

import java.io.File;
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

public class Media {
	static final long PHOTO = 1;
	static final long MOVIE = 2;
	
	static final String TABLE_NAME = "media_table";
	static final String PATH = "path";
	static final String TYPE = "type";
	static final String ARTICLE_ID = "article_id";
	static final String MODIFIED = "modified";

    static final int IMAGE_SIZE = 480;

	//private Context mContext;
	private long rowid;
	
	public Media(long id){
		rowid = id;
	}
	
	public Media(SQLiteDatabase db, String path, long type){
        String sql = "insert into media_table(path, type, modified) values (?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, path);
        statement.bindLong(2, type);
        statement.bindLong(3, System.currentTimeMillis());
        long id = statement.executeInsert();
        rowid = id;
	}

    public Media(SQLiteDatabase db, String path, long type, Article article){
        String sql = "insert into media_table(path, type, article_id, modified) values (?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, path);
        statement.bindLong(2, type);
        statement.bindLong(3, article.getId());
        statement.bindLong(4, System.currentTimeMillis());
        long id = statement.executeInsert();
        rowid = id;
    }

	static List<Media> findMediasByArticle(SQLiteDatabase db,  Article article){
		List<Media> mediaList = new ArrayList<Media>();
		String sql = "select ROWID from media_table where article_id=?";
		String[] selectionArgs = {""+article.getId()};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		
		while(cursor.moveToNext()){
			long id = cursor.getLong(0);
			Media media = new Media(id);
			mediaList.add(media);
		}
		
		return mediaList;
	}
	
	public void delete(SQLiteDatabase db){
        String sql = "delete from media_table where ROWID=?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindLong(1, rowid);
        statement.executeUpdateDelete();
	}
	
	/**
	 * @return path
	 */
	public String getPath(SQLiteDatabase db) {
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery("select path from media_table where ROWID=?", selectionArgs);
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();

        return path;
	}

	/**
	 * @param path セットする path
	 */
	public void setPath(SQLiteDatabase db, String path) {
        SQLiteStatement statement = db.compileStatement("update media_table set path=? where ROWID=?");
        statement.bindString(1, path);
        statement.bindLong(2, rowid);
        statement.executeUpdateDelete();
	}

	/**
	 * @return type
	 */
	public long getType(SQLiteDatabase db) {
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery("select type from media_table where ROWID=?", selectionArgs);
        cursor.moveToFirst();
        Long type = cursor.getLong(0);
        cursor.close();

		return type;
	}

	/**
	 * @param type セットする type
	 */
	public void setType(SQLiteDatabase db, int type) {
        SQLiteStatement statement = db.compileStatement("update media_table set type=? where ROWID=?");
        statement.bindLong(1, type);
        statement.bindLong(2, rowid);
        statement.executeUpdateDelete();
	}

	/**
	 * @return modified
	 */
	public long getModified(SQLiteDatabase db) {
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery("select modified from media_table where ROWID=?", selectionArgs);
        cursor.moveToFirst();
        Long modified = cursor.getLong(0);
        cursor.close();
		
		return modified;
	}

	/**
	 * @param modified セットする modified
	 */
	public void setModified(SQLiteDatabase db, long modified) {
        SQLiteStatement statement = db.compileStatement("update media_table set medified=? where ROWID=?");
        statement.bindLong(1, modified);
        statement.bindLong(2, rowid);
        statement.executeUpdateDelete();
	}

	/**
	 * @return modified
	 */
	public long getArticleId(SQLiteDatabase db) {
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery("select article_id from media_table where ROWID=?", selectionArgs);
        cursor.moveToFirst();
        Long articleId = cursor.getLong(0);
        cursor.close();
		
		return articleId;
	}

	/**
	 * @param  articleId セットする articleId
	 */
	public void setArticleId(SQLiteDatabase db, long articleId) {
        SQLiteStatement statement = db.compileStatement("update media_table set article_id=? where ROWID=?");
        statement.bindLong(1, articleId);
        statement.bindLong(2, rowid);
        statement.executeUpdateDelete();
	}
	
	/**
	 * @return id
	 */
	public long getId() {
		return rowid;
	}


    public Bitmap getImage(SQLiteDatabase db, Context context){
        Bitmap image = null;

        String path = getPath(db);
        long type = getType(db);
        if(type == Media.PHOTO) {
            //画像ファイルを指定
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir, path);
            // サイズを確定するための仮読み込み
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(imageFile.getPath(), opt);

            // 読み込み時の精度を決定
            int size = opt.outWidth;
            if (opt.outHeight > size) {
                size = opt.outHeight;
            }
            opt.inSampleSize = size / IMAGE_SIZE;

            // 本格的に画像を読み込む
            opt.inJustDecodeBounds = false;
            image = BitmapFactory.decodeFile(imageFile.getPath(), opt);
        }else if(type == Media.MOVIE){
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            File movieFile = new File(dir, path);
            // 動画のサムネイル画像を取得する
            image = ThumbnailUtils.createVideoThumbnail(movieFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        }

        return image;
    }

	public String dump(SQLiteDatabase db){
		String str = "";
		str += "ROWID:" + rowid;
		str += "|PATH:"+ getPath(db);
		str += "|TYPE:" + getType(db);
		str += "|ARTICLE_ID:" + getArticleId(db);
		str += "|MODIFIED:" + getModified(db);
		str += "|";
		
		return str;
	}
}
