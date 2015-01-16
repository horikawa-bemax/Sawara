package jp.ac.bemax.sawara;

import java.io.Serializable;

import android.database.sqlite.SQLiteDatabase;

/**
 * GridViewに表示するアイテムに実装するインターフェイス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public interface ListItem extends Serializable{
	public long getId();
	public String getName(SQLiteDatabase db);
	public String getIconPath();
}
