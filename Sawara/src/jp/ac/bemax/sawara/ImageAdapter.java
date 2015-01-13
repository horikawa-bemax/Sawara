package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 画像リストのためのアダプタ
 * @author Masaaki Horikawa
 * 2014/11/22
 */
public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private List<Bitmap> mList;
	private int backDrawable;
	
	/**
	 * ImageAdapter.javaコンストラクタ
	 * @param context
	 */
	public ImageAdapter(Context context){
		mContext = context;
		TypedValue outValue = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.frameBack, outValue, true);
		backDrawable = outValue.resourceId;
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
		ViewHolder holder;
		
		if(convertView == null){
            holder = new ViewHolder();
            holder.imageView = new ImageView(mContext);
            holder.imageView.setBackgroundResource(backDrawable);
                        
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(10,10,10,10);
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setLayoutParams(params);
            linearLayout.addView(holder.imageView);
            
            convertView = linearLayout;           
            AbsListView.LayoutParams absParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(absParams);            
            convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.imageView.setImageBitmap(mList.get(position));
		
		return convertView;
	}
	
	public void clear(){
		mList = new ArrayList<Bitmap>();
	}
	
	class ViewHolder{
		ImageView imageView;
	}
}

