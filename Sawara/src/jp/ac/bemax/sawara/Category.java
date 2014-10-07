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
	private long rowid;
	private String name;
	private int position;
	private long modified;
	private Bitmap image;
	
	public void setImage(Bitmap image) {
		this.image = image;
	}

	public Category(String name, int position){
		this.name = name;
		this.position = position;
		modified = System.currentTimeMillis();
	}
	
	public Category(){
		
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public long getId() {
		// TODO 自動生成されたメソッド・スタブ
		return rowid;
	}

	public void setId(long id){
		rowid = id;
	}
	
	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return name;
	}

	@Override
	public Bitmap getImage() {
		
		return image;
	}

	@Override
	public void clicked(Context context) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public ContentValues getContentValues(){
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("position", position);
		cv.put("modified", modified);
		
		return cv;
	}
}
