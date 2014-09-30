package jp.ac.bemax.sawara;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * GridView内に表示するボタンを表すクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class ListButton implements ListItem{
	private String name;
	private int resId;
	private Context context;
	
	public ListButton(String name, int resId, Context context){
		this.name = name;
		this.resId = resId;
		this.context = context;
	}
	
	@Override
	public int getType() {
		// TODO 自動生成されたメソッド・スタブ
		return ListItem.NEW;
	}

	@Override
	public long getId() {
		// TODO 自動生成されたメソッド・スタブ
		return -1;
	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return name;
	}

	@Override
	public Bitmap getImage() {
		// TODO 自動生成されたメソッド・スタブ
		return BitmapFactory.decodeResource(context.getResources(), resId);
	}
}
