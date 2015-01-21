package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
    private SawaraDBAdapter dbAdapter;
	private List<Media> mList;
	private int backDrawable;
	
	private final int LMP = LinearLayout.LayoutParams.MATCH_PARENT;
	private final int AMP = AbsListView.LayoutParams.MATCH_PARENT;
	
	/**
	 * ImageAdapter.javaコンストラクタ
	 * @param context
	 */
	public ImageAdapter(SawaraDBAdapter adapter, Context context){
		mContext = context;
        dbAdapter = adapter;
		TypedValue outValue = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.frameBack, outValue, true);
		backDrawable = outValue.resourceId;
		mList = new ArrayList<Media>();
	}
	
	/**
	 * アダプタに画像を追加する
	 * @param media
	 */
	public void add(Media media){
		mList.add(media);
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
	public Media getItem(int position) {
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
                        
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LMP, LMP);
            params.setMargins(10,10,10,10);
            holder.imageView.setLayoutParams(params);
            
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.addView(holder.imageView);
            convertView = linearLayout;
            
            AbsListView.LayoutParams absParams = new AbsListView.LayoutParams(AMP, AMP);
            convertView.setLayoutParams(absParams);            
            convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

        SQLiteDatabase db = dbAdapter.openDb();
		holder.imageView.setImageBitmap(mList.get(position).getImage(db));
		db.close();

		return convertView;
	}
	
	public void clear(){
		mList = new ArrayList<Media>();
	}
	
	class ViewHolder{
		ImageView imageView;
	}
}

