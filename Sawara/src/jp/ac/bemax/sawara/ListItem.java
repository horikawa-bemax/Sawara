package jp.ac.bemax.sawara;

import android.graphics.Bitmap;

public interface ListItem {
	public static final int ITEM = 1;
	public static final int NEW = 2;
	public int getType();
	public long getId();
	public String getName();
	public Bitmap getImage();
}
