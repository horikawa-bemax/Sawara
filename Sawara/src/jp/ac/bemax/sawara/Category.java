package jp.ac.bemax.sawara;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * Articleが属するカテゴリを表すクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class Category implements ListItem{
	private CategoryManager mManager;
	private long rowid;
	private String name;
	private int position;
	private long modified;
	private Bitmap image;

	/**
	 * Category.javaコンストラクタ
	 * @param name
	 * @param position
	 
	public Category(String name, int position){
		this.name = name;
		this.position = position;
		modified = System.currentTimeMillis();
	}
	*/
	
	/**
	 * Category.javaコンストラクタ
	 */
	public Category(CategoryManager manager){
		mManager = manager;
	}
	
	/**
	 * カテゴリのイメージをセットする
	 * @param image
	 */
	public void setImage(Bitmap image) {
		this.image = image;
	}	

	/**
	 * カテゴリの表示順をゲットする
	 * @return 表示順
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * カテゴリの表示順をセットする
	 * @param position 表示順
	 */
	public void setPosition(int position) {
		this.position = position;
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
	 * カテゴリのアイコンをクリックした時の処理
	 */
	@Override
	public void clicked(Context context) {

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
	public Bitmap getImage() {
		Bitmap image = mManager.makeCategoryImage(this.rowid);
		return image;
	}
}
