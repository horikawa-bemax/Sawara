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
	private String categoryName;
	private int viewPosition;
	private long updateDate;
	
	public Category(long id, String name, int position){
		rowid = id;
		categoryName = name;
		viewPosition = position;
		updateDate = System.currentTimeMillis();
	}
	
	@Override
	public int getType() {
		return ListItem.CATEGORY;
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
		return categoryName;
	}

	@Override
	public Bitmap getImage() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void clicked(Context context) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public ContentValues getContentValues(){
		ContentValues cv = new ContentValues();
		cv.put("category_name", categoryName);
		cv.put("view_position", viewPosition);
		cv.put("update_date", updateDate);
		
		return cv;
	}
}
