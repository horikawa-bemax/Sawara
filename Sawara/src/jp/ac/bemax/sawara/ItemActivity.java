package jp.ac.bemax.sawara;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class ItemActivity extends Activity {
	private ImageView itemImage;
	private VTextView itemDescription;
	private VTextView itemName;
	private SQLiteOpenHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item);
		
		itemImage = (ImageView)findViewById(R.id.item_image);
		itemDescription = (VTextView)findViewById(R.id.item_discription);
		itemName = (VTextView)findViewById(R.id.item_name);
		
		Intent intent = getIntent();
		long rowId = intent.getExtras().getLong("item_id");
		
		ItemManager iManager = ItemManager.newItemManager(this);
		Item item = new Item(rowId);
		Log.d("item_name", item.getName());
		//itemImage.setImageBitmap(item.getImageUrl());

		DisplayMetrics dm = getResources().getDisplayMetrics();
		LayoutParams params = new LayoutParams((int)(100*dm.density),LayoutParams.MATCH_PARENT);
		
		itemName.setText(item.getName(), 100);
		itemName.setLayoutParams(params);

	}
}
