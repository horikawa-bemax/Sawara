package jp.ac.bemax.sawara;

import android.graphics.Bitmap;

/**
 * Articleが属するカテゴリを表すクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class Category implements ListItem{

	@Override
	public int getType() {
		return ListItem.CATEGORY;
	}

	@Override
	public long getId() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Bitmap getImage() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
