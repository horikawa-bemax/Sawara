package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * ホーム画面のグリッドビューに並べる項目を扱うクラス
 * @author Masaaki Horikawa
 * 2014/09/02
 */
public class GridAdapter extends ArrayAdapter<Item>{
	private Context context;
	private int resourceId;
	private List<Item> list;
	
	public GridAdapter(Context context, int resource, List<Item> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resourceId = resource;
		this.list = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			// 
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, null);
		}
		// 各項目
		Item item = getItem(position);
		//
		LinearLayout view = (LinearLayout)convertView;
		// imageView
		ImageView imgView = (ImageView)view.findViewById(R.id.imageView1);
		File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		try {
			InputStream is = new FileInputStream(new File(dir, item.getImageUrl()));
			Bitmap bmp = BitmapFactory.decodeStream(is);
			imgView.setImageBitmap(bmp);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		VTextView vtView = (VTextView)view.findViewById(R.id.vTextView1);
		vtView.setText(item.getName());

		item.dump();
		return view;
	}
}
