package jp.ac.bemax.sawara;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * GridView内に表示するボタンを表すクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class NewButton implements ListItem{
	private Context context;
	
	public NewButton(Context context){
		this.context = context;
	}
	
	@Override
	public int getType() {
		return ListItem.NEW_BUTTON;
	}

	@Override
	public long getId() {
		return -1;
	}

	@Override
	public String getName() {
		return "あたらしくつくる";
	}

	@Override
	public Bitmap getImage() {
		return BitmapFactory.decodeResource(context.getResources(), R.drawable.friend);
	}
}
