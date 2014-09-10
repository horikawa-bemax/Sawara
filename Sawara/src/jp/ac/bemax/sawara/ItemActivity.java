package jp.ac.bemax.sawara;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

/**
 * アイテムを表示するアクティビティ
 * @author Masaaki Horikawa
 * 2014/09/10
 */
public class ItemActivity extends Activity {
	private ImageView itemImage;
	private VTextView itemDescription;
	private VTextView itemName;
	private SQLiteOpenHelper helper;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item);
		
		// レイアウト上のビューを紐付け
		itemImage = (ImageView)findViewById(R.id.item_image);
		itemDescription = (VTextView)findViewById(R.id.item_discription);
		itemName = (VTextView)findViewById(R.id.item_name);
		
		// インテントの初期化と、呼び出しもとからのデータ受け取り
		Intent intent = getIntent();
		long rowId = intent.getExtras().getLong("item_id");
		
		// アイテムインスタンスを作成
		ItemManager iManager = ItemManager.newItemManager(this);
		Item item = new Item(rowId);

		// ディスプレイ情報を取得
		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		// アイテム名
		itemName.setText(item.getName(), 100);
		LayoutParams nameParams = new LayoutParams((int)(100*dm.density),LayoutParams.MATCH_PARENT);
		itemName.setLayoutParams(nameParams);
		
		// アイテム説明
		itemDescription.setText(item.getDescription());
		LayoutParams descriptionParams = new LayoutParams((int)(300*dm.density), LayoutParams.MATCH_PARENT);
		itemDescription.setLayoutParams(descriptionParams);
		
		// アイテム画像
		itemImage.setImageBitmap(item.getImage());
		LayoutParams imageParams = new LayoutParams((int)(dm.widthPixels/2), LayoutParams.MATCH_PARENT);
		itemImage.setLayoutParams(imageParams);
	}
}
