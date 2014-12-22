package jp.ac.bemax.sawara;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * ホーム画面のグリッドビューに並べる項目を扱うクラス
 * @author Masaaki Horikawa
 * 2014/09/02
 */
public class GridAdapter extends ArrayAdapter<ListItem> implements OnItemClickListener{
	private Context context;		// コンテキスト(HomeActivity)
	private int resourceId;			// グリッドに表示するアイテムのレイアウトXML
	private List<ListItem> list;	// グリッドに表示するアイテムのリスト
	private Point dispSize; 		// 画面のサイズ
	static int frameSize;
	
	public GridAdapter(Context context, int resource, List<ListItem> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resourceId = resource;
		this.list = objects;
		
		// 画面のサイズを取得して、dispSizeにセットする
		WindowManager wManager = ((Activity)context).getWindowManager();
		dispSize = new Point();
		wManager.getDefaultDisplay().getSize(dispSize);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wManager.getDefaultDisplay().getMetrics(outMetrics);
		float displayDensity = outMetrics.density;
		int buttonSize = (int)(dispSize.y / 5);
		int frameNum = (int)((dispSize.x - buttonSize) / buttonSize);
		frameSize = (int)(dispSize.y * 2 / 5);
	}

	/* (非 Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null){
			// アイテム用のレイアウトXMLを読み込む
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, null);
            
            holder = new ViewHolder();
            holder.imageView = (ImageView)convertView.findViewById(R.id.list_item_image);
            holder.vTextView = (VTextView)convertView.findViewById(R.id.list_vTextView);
            
            convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		// 表示するアイテムを取り出す
		ListItem listItem = getItem(position);
		
		AbsListView.LayoutParams absParams = new AbsListView.LayoutParams(frameSize, frameSize);
		convertView.setLayoutParams(absParams);
		
		// imageViewにitemの画像をセットする
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		params.setMargins(0, 10, 10, 10);
		String iconPath = listItem.getIconPath();
		Bitmap bmp = StrageManager.loadIcon(iconPath);
		holder.imageView.setImageBitmap(bmp);
		holder.imageView.setBackground(ButtonFactory.getThemaFrame(context));
		holder.imageView.setLayoutParams(params);
		holder.imageView.setPadding(20, 20, 20, 20);
		
		// 縦書きのtextViewにアイテムの値をセットする
		float den = context.getResources().getDisplayMetrics().density;
		params = new LinearLayout.LayoutParams((int)(frameSize / 5), LinearLayout.LayoutParams.MATCH_PARENT);
		params.setMargins(10, 10, 0, 10);
		holder.vTextView.setLayoutParams(params);
		holder.vTextView.setPadding(20, 20, 20, 20);		
		holder.vTextView.setText(listItem.getName());
		
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		ListItem listItem = list.get(position);
		if (listItem instanceof Category) {
			Category category = (Category)listItem;
			category.getId();
			
		} 
	}
}

class ViewHolder{
	ImageView imageView;
	VTextView vTextView;
}