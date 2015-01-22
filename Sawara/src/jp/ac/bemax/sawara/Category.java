package jp.ac.bemax.sawara;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Articleが属するカテゴリを表すクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class Category implements Serializable{
	public static final String TABLE_NAME = "category_table";
    public static final String ID = "ROWID";
	public static final String NAME = "name";
	public static final String MODIFIED = "modified";
	public static final String POSITION = "position";
    public static final String ICON = "icon";

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
        db.beginTransaction();
        try {
            rowid = insert(db, name);
            setPosition(db, rowid);
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public Category(SQLiteDatabase db, Context context, String name, Article article){
        db.beginTransaction();
        try {
            rowid = insert(db, name);
            setPosition(db, rowid);

            String sql = "insert into category_article_table(article_id, category_id) values (?,?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindLong(1, article.getId());
            statement.bindLong(2, rowid);
            statement.executeInsert();

            Media media = makeIcon(db, context);
            setIcon(db, media);

            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
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

    public void setIcon(SQLiteDatabase db, Media media){
        SQLiteStatement statement = db.compileStatement("update category_table set icon=? where ROWID=?");
        statement.bindLong(1, media.getId());
        statement.bindLong(2, rowid);
        statement.executeUpdateDelete();
    }

    public Media getIcon(SQLiteDatabase db){
        Media media = null;
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery("select icon from category_table where ROWID=?", selectionArgs);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            long icon = cursor.getLong(0);
            media = new Media(icon);
        }
        cursor.close();

        return media;
    }

	/**
	 * カテゴリの表示順をゲットする
	 * @return 表示順
	 */
	public long getPosition(SQLiteDatabase db) {
        String[] selectionArgs = {""+rowid};
		Cursor cursor = db.rawQuery("select position from category_table where ROWID=?", selectionArgs);
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
		SQLiteStatement statement = db.compileStatement("update category_table set position=? where ROWID=?");
        statement.bindLong(1, position);
        statement.bindLong(2, rowid);
        statement.executeUpdateDelete();
	}

	/**
	 * カテゴリの更新日時をゲットする
	 * @return 更新日時
	 */
	public long getModified(SQLiteDatabase db) {
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery("select modified from category_table where ROWID=?", selectionArgs);
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
        SQLiteStatement statement = db.compileStatement("update category_table set modified=? where ROWID=?");
        statement.bindLong(1, modified);
        statement.bindLong(2, rowid);
        statement.executeUpdateDelete();
	}

	/**
	 * カテゴリの名前をセットする
	 * @param name カテゴリの名前
	 */
	public void setName(SQLiteDatabase db, String name) {
        SQLiteStatement statement = db.compileStatement("update category_table set name=? where ROWID=?");
        statement.bindString(1, name);
        statement.bindLong(2, rowid);
        statement.executeUpdateDelete();
	}

	/**
	 * カテゴリのIDをゲットする
	 */
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
	public String getName(SQLiteDatabase db) {
        String[] selectionArgs = {""+rowid};
        Cursor cursor = db.rawQuery("select name from category_table where ROWID=?", selectionArgs);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        cursor.close();

        return name;
	}

	public Media makeIcon(SQLiteDatabase db, Context context) {
        Media media = null;
        Bitmap icon = Bitmap.createBitmap(ICON_WIDTH, ICON_HEIGHT*3/2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);
        Rect src = new Rect(0, 0, ICON_WIDTH, ICON_HEIGHT);
        Rect dst = null;
        int width = ICON_WIDTH/2;
        int height = ICON_HEIGHT/2;
        FileOutputStream fos = null;

        File iconDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        db.beginTransaction();
        try{
            List<Article> list = getArticles(db);
            // 最大６つの画像を使ってアイコンを作成する
            for(int i=0; i<6 && i<list.size(); i++){
                Article article = list.get(i);
                Media articleIcon = article.getIcon(db);
                Bitmap srcImage = articleIcon.getImage(db, context);

                int left = (i%2) * width;
                int top = (i/2) * height;
                dst = new Rect(left, top, left + width, top + height);
                canvas.drawBitmap(srcImage, src, dst, null);
            }

            String fileName = "icon_category_"+rowid+".png";
            File saveFile = new File(iconDir, fileName);

            fos = new FileOutputStream(saveFile);
            icon.compress(Bitmap.CompressFormat.PNG, 100, fos);

            media = new Media(db, fileName, Media.PHOTO);
            setIcon(db, media);

            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
            Log.d("majide", "majide");
        }finally {
            try {
                fos.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            db.endTransaction();
        }

        return media;
	}

    public static Category findCategoryById(SQLiteDatabase db, long id){
        Category category = null;
        String[] selectionArgs = {""+id};
        Cursor cursor = db.rawQuery("select ROWID from category_table ROWID=?", selectionArgs);
        if(cursor.getCount()>0){
            category = new Category(id);
        }
        return category;
    }

    public static Category findCategoryByName(SQLiteDatabase db, String name){
        Category category = null;
        String[] selectionArgs = {name};
        Cursor cursor = db.rawQuery("select ROWID from category_table where name=?", selectionArgs);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            long id = cursor.getLong(0);
            category = new Category(id);
        }
        return category;
    }

    public List<Article> getArticles(SQLiteDatabase db){
        List<Article> list = new ArrayList<Article>();

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

    public String dump(SQLiteDatabase db){
        String str = "";
        str += "ROWID:" + rowid;
        str += "|NAME:"+ getName(db);
        str += "|ICON:" + getIcon(db).getId();
        str += "|MODIFIED:" + getModified(db);
        str += "|";

        return str;
    }

    public static List<Category> getAllCategory(SQLiteDatabase db){
        List<Category> list = new ArrayList<Category>();

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
