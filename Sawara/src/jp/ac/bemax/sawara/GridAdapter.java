package jp.ac.bemax.sawara;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, null);
		}
		Item item = getItem(position);
		LinearLayout view = (LinearLayout)convertView;
		ImageView imgView = (ImageView)view.findViewById(R.id.imageView1);
		imgView.setImageResource(R.drawable.friend);
		VTextView vtView = (VTextView)view.findViewById(R.id.vTextView1);
		vtView.setText(item.getName());

		view.setVisibility(View.VISIBLE);
		return view;
	}
}
