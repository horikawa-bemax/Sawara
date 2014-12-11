package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 画像リストのためのアダプタ
 * @author Masaaki Horikawa
 * 2014/11/22
 */
public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private List<Bitmap> mList;
	
	/**
	 * ImageAdapter.javaコンストラクタ
	 * @param context
	 */
	public ImageAdapter(Context context){
		mContext = context;
		mList = new ArrayList<Bitmap>();
	}
	
	/**
	 * アダプタに画像を追加する
	 * @param image
	 */
	public void add(Bitmap image){
		mList.add(image);
	}

	/**
	 * アダプタのアイテム数を返す
	 */
	@Override
	public int getCount() {
		return mList.size();
	}

	/**
	 * 指定ポジションのアイテムを返す
	 * @param position アイテムのポジション
	 */
	@Override
	public Bitmap getItem(int position) {
		return mList.get(position);
	}

	/**
	 * アイテムのIDを返す（実際は何もしない）
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * GridViewに表示するViewを返す。
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = new ImageView(mContext);
		}
		
		ImageView imageView = (ImageView)convertView;
		imageView.setImageBitmap(mList.get(position));
		
		return imageView;
	}
	
	public void clear(){
		mList = new ArrayList<Bitmap>();
	}
}
