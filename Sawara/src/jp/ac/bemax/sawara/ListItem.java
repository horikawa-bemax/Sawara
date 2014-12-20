package jp.ac.bemax.sawara;

import java.io.Serializable;

/**
 * GridViewに表示するアイテムに実装するインターフェイス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public interface ListItem extends Serializable{
	public long getId();
	public String getName();
	public String getIconPath();
}
