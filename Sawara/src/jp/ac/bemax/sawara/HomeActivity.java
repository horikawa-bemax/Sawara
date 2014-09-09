package jp.ac.bemax.sawara;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

/**
 * ホーム画面のアクティビティ
 * @author Masaaki Horikawa
 * 2014/07/02
 */
public class HomeActivity extends Activity implements OnClickListener{
	private GridView gView;
	private ArrayList<Item> items;
	private int itemHeight; 
	
	/* (非 Javadoc)
	 * コンストラクタ
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		// ウィジェットを登録 
		gView = (GridView)findViewById(R.id.gridView);
		
		// ディスプレイサイズを取得する
		Display display = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);
		itemHeight = p.y / 2;
		
		// 初期化
		items = new ArrayList<Item>();
		ItemManager iManager = ItemManager.newItemManager(this);	
		
		// 指定したレイアウトでItemを並べる
		GridAdapter gAdapter = new GridAdapter(this, R.layout.list_item, iManager.getAllItems());
		gView.setAdapter(gAdapter);
		
		gView.setOnItemClickListener(gAdapter);
	}

	/* (非 Javadoc)
	 * ボタンがクリックされたときに呼び出されるメソッド
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		
		/*
		// クリックされたボタンによって分岐
		switch(v.getId()){
		case R.id.search_button:
			// さがすボタンがクリックされたとき
			
			intent.setClass(this, SearchActivity.class);
			startActivity(intent);
			
			// ログ出力
			Log.d("clicked Button","SearchButton");
			
			break;
		case R.id.register_button:
			// とうろくするボタンがクリックされたとき
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			
			// ログ出力
			Log.d("clicked Button","RegistarButton");
		
			break;
		}
		*/
		
	}
	
}
