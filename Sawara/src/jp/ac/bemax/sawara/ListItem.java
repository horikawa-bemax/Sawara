package jp.ac.bemax.sawara;

import java.io.Serializable;

import android.content.Context;

/**
 * GridViewに表示するアイテムに実装するインターフェイス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public interface ListItem extends Serializable{
	//public static final int ITEM = 1;
	//public static final int CATEGORY = 2;
	//public static final int NEW_BUTTON = 3;
	public long getId();
	public String getName();
	public String getIconPath();
	public void clicked(Context context);
}
