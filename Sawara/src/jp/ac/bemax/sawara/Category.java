package jp.ac.bemax.sawara;

import android.content.ContentValues;

/**
 * Articleが属するカテゴリを表すクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class Category implements ListItem{
	private long rowid;
	private String name;
	private long position;
	private long modified;
	private String iconPath;

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
	public Category(){
		
	}
	
	/**
	 * カテゴリのイメージをセットする
	 * @param image
	 */
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}	

	/**
	 * カテゴリの表示順をゲットする
	 * @return 表示順
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * カテゴリの表示順をセットする
	 * @param cId 表示順
	 */
	public void setPosition(long cId) {
		this.position = cId;
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
