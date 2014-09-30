package jp.ac.bemax.sawara;

import java.io.File;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * ホーム画面のグリッドビューに並べる項目を扱うクラス
 * @author Masaaki Horikawa
 * 2014/09/02
 */
public class GridAdapter extends ArrayAdapter<ListItem> implements OnItemClickListener{
	private Context context;	// コンテキスト(HomeActivity)
	private int resourceId;		// グリッドに表示するアイテムのレイアウトXML
	private List<ListItem> list;	// グリッドに表示するアイテムのリスト
	private Point dispSize; 	// 画面のサイズ
	
	public GridAdapter(Context context, int resource, List<ListItem> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resourceId = resource;
		this.list = objects;
		
		// 画面のサイズを取得して、dispSizeにセットする
		WindowManager wManager = ((Activity)context).getWindowManager();
		dispSize = new Point();
		wManager.getDefaultDisplay().getSize(dispSize);
		
	}

	/* (非 Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			// アイテム用のレイアウトXMLを読み込む
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, null);
		}
		// 表示するアイテムを取り出す
		ListItem listItem = getItem(position);
		
		LinearLayout view = (LinearLayout)convertView;
		
		// imageViewにitemの画像をセットする
		ImageView imgView = (ImageView)view.findViewById(R.id.list_item_image);
		File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		Bitmap bmp = listItem.getImage();
		imgView.setImageBitmap(bmp);
		
		// 縦書きのtextViewにアイテムの値をセットする
		VTextView vtView = (VTextView)view.findViewById(R.id.list_vTextView);
		float den = context.getResources().getDisplayMetrics().density;
		LayoutParams params = new LayoutParams((int)(50*den), (int)(dispSize.y/2.3) );
		vtView.setLayoutParams(params);
		vtView.setText(listItem.getName());

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		Intent intent = new Intent();
		ListItem listItem = list.get(position);
		int type = listItem.getType();
		switch(type){
		case ListItem.ITEM:
			Article item = (Article)listItem;
			intent.putExtra("item_id", item.getId());
			intent.setClass(context, ArticleActivity.class);
			context.startActivity(intent);
			break;
		case ListItem.NEW:
			intent.setClass(context, RegisterActivity.class);
		}

	}
}
