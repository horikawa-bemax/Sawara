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
import android.os.Environment;

/**
 * 写真および動画を扱うテーブル
 *
 */
public class Media {
	static final long PHOTO = 1;
	static final long MOVIE = 2;

	private long rowid;
    private Context context;

    /**
     * IDを指定して、メディアを作成する。
     * データベース上にメディアがあることを確かめ、あればメディアオブジェクトを返す。なければnull
     * @param db データベース もしnullの場合は、データベース上にあることを確かめない
     * @param context コンテキスト
     * @param id ID
     */
	public static Media getMedia(SQLiteDatabase db, Context context, long id){
        Media media = null;
        if(db != null) {
            String sql = "select ROWID from media_table where ROWID=?";
            String[] selectionArgs = {"" + id};
            Cursor cursor = db.rawQuery(sql, selectionArgs);
            if (cursor.getCount() > 0) {
                media = new Media(context, id);
            }
        }else{
            media = new Media(context, id);
        }
        return media;
	}

    /**
     * コンストラクタ
     * @param context
     * @param id
     */
    private Media(Context context, long id){
        this.context = context;
        this.rowid = id;
    }

    private File getDir(long mediaType){
        if(mediaType == Media.PHOTO){
            return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else if(mediaType == Media.MOVIE){
            return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        }else{
            return null;
        }
    }

    /**
     * 画像ファイル名とタイプ（画像、動画）を指定して、メディアを作る。
     * DBへのインサートあり。
     * @param db データベース
     * @param context コンテキスト
     * @param fileName 画像ファイル名
     * @param mediaType 画像のタイプ
     * @throws Exception 適当な例外を投げます
     */
	public Media(SQLiteDatabase db, Context context, Bitmap bitmap, String fileName, long mediaType) throws Exception{
        this.context = context;
        db.beginTransaction();
        try {
            File file = new File(getDir(getType(db)), fileName);
            // 画像をファイルに保存する
            IconFactory.storeBitmapToFile(file, bitmap);

            // メディアテーブルにデータを登録
            String sql = "insert into media_table(path, type, modified) values (?,?,?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, fileName);
            statement.bindLong(2, mediaType);
            statement.bindLong(3, System.currentTimeMillis());
            long id = statement.executeInsert();
            rowid = id;

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("メディアを作成できませんでしたよ。");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 画像と画像タイプ、アーティクルを指定してメディアを作成する
     * DBへのインサートあり
     * @param db データベース
     * @param context コンテキスト
     * @param fileName 画像ファイル名
     * @param mediaType 画像タイプ
     * @param article アーティクル
     * @throws Exception 適当な例外を投げます
     */
    public Media(SQLiteDatabase db, Context context, Bitmap bitmap, String fileName, long mediaType, Article article) throws Exception {
        this.context = context;
        db.beginTransaction();
        try {
            File mediaFile = new File(getDir(getType(db)), fileName);
            // 画像をファイルに保存する
            IconFactory.storeBitmapToFile(mediaFile, bitmap);

            // メディアテーブルにデータを登録
            String sql = "insert into media_table(path, type, article_id, modified) values (?,?,?,?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, fileName);
            statement.bindLong(2, mediaType);
            statement.bindLong(3, article.getId());
            statement.bindLong(4, System.currentTimeMillis());
            long id = statement.executeInsert();
            rowid = id;

            // アイコン画像を作成する
            Bitmap icon = IconFactory.makeNormalIcon(bitmap);
            // アイコンに名前を付ける
            String iconName = "article_icon_" + getId() + ".png";
            File iconFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), iconName);
            // アイコンをファイルに保存する
            IconFactory.storeBitmapToFile(iconFile, icon);
            // アイコンのメディアを新規作成
            Media iconMedia = new Media(db, context, icon, iconFile.getPath(), Media.PHOTO);
            // articleテーブルのicon_pathを更新する。
            article.setIcon(db, iconMedia.getId());

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("メディアを作成できませんでしたよ。");
        } finally {
            db.endTransaction();
        }
    }

    private String getFileName(SQLiteDatabase db){
        String fileName = null;
        String sql = "select file_name from media_table where ROWID=?";
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            fileName = cursor.getString(0);
        }
        cursor.close();
        return fileName;
    }

    /**
     * データベースのデータから、画像ファイルを取得する
     * @param db データベース
     * @return メディアの画像ファイル
     * @throws Exception 適当な例外を投げます
     */
    public File getMediaFile(SQLiteDatabase db) throws Exception {
        String fileName = getFileName(db);
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(getType(db) == Media.MOVIE){
            dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        }
        return new File(dir, fileName);
    }

    /**
     * このメディアに関連するカテゴリを返す
     * @param db データベース
     * @return このメディアが関連するカテゴリの配列
     */
    public Category[] getCategoriesRelationThisMedia(SQLiteDatabase db){
        String sql = "select category_id from category_media_view where media_id=?";
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        Category[] categories = new Category[cursor.getCount()];
        while(cursor.moveToNext()){
            categories[cursor.getPosition()] = Category.getCategory(null, cursor.getLong(0));
        }
        return categories;
    }

    /**
     * このメディアを削除する
     * @param db
     */
	public void delete(SQLiteDatabase db){
        String sql = "delete from media_table where ROWID=?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindLong(1, rowid);
        statement.executeUpdateDelete();

        // 関連するカテゴリのアイコンを変更する
        Category[] categories = getCategoriesRelationThisMedia(db);
        for(Category category: categories){
            Bitmap newIcon = category.makeCategoryIconBitmap(db, context);
            category.updateIcon(db, context, newIcon);
        }
	}

    /**
     * 画像のファイル名を、DBから取得する
     * @param db データベース
     * @return 取得した画像ファイルの絶対パス
     * @throws Exception 適当な例外を投げる
     */
	public String getMediaFilePath(SQLiteDatabase db) throws Exception {
        return getMediaFile(db).getPath();
	}

    public Bitmap getMediaIconBitmap(SQLiteDatabase db) throws Exception {
        File mediaFile = getMediaFile(db);
        Bitmap bitmap = IconFactory.loadBitmapFromFileAndType(mediaFile, getType(db));
        Bitmap icon = IconFactory.makeNormalIcon(bitmap);
        return icon;
    }

	/**
	 * @param path セットする path
	 */
	public void setMediaFileName(SQLiteDatabase db, String path) {
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

    /*
	public static List<Media> getMedias(SQLiteDatabase db, Context context, List<ImageItem> items, Article article) throws Exception {
		List<Media> medias = new ArrayList<Media>();
		for(ImageItem item: items){
			if(item.getId() == -1){
				Media media = new Media(db, context, item.getFileName(), item.getType(), article);
				medias.add(media);
			}
		}

		return medias;
	}
	*/

	public String dump(SQLiteDatabase db) throws Exception {
		String str = "";
		str += "ROWID:" + rowid;
		str += "|PATH:"+ getMediaFilePath(db);
		str += "|TYPE:" + getType(db);
		str += "|ARTICLE_ID:" + getArticleId(db);
		str += "|MODIFIED:" + getModified(db);
		str += "|";
		
		return str;
	}
}
