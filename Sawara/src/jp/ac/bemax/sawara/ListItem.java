package jp.ac.bemax.sawara;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import java.io.Serializable;

/**
 * GridViewに表示するアイテムに実装するインターフェイス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public interface ListItem extends Serializable{
	public long getId();
	public String getName(SQLiteDatabase db);
	public Bitmap getIcon(SQLiteDatabase db);
}
