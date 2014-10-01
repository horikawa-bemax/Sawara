package jp.ac.bemax.sawara;

import android.graphics.Bitmap;

/**
 * GridViewに表示するアイテムに実装するインターフェイス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public interface ListItem {
	public static final int ITEM = 1;
	public static final int CATEGORY = 2;
	public static final int NEW_BUTTON = 3;
	public int getType();
	public long getId();
	public String getName();
	public Bitmap getImage();
}
