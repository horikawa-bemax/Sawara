package jp.ac.bemax.sawara;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * タイトル画面のアクティビティ
 * @author Masaaki Horikawa
 * 2014/07/02
 */
public class HomeActivity extends Activity implements OnClickListener{
	private GridView gView;
	private ArrayList<Item> items;
	
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
		
		// フォントを読み込む 
		Typeface yasashisa_B = Typeface.createFromAsset(getAssets(),"yasashisa_bold.ttf");
		Typeface yasashisa = Typeface.createFromAsset(getAssets(),"yasashisa.ttf");
		
		// ウィジェットにフォントを適用する
		//titleView.setTypeface(yasashisa_B);
		//searchButton.setTypeface(yasashisa);
		//registerButton.setTypeface(yasashisa);
		 
		
		// Itemsを読み込む
		items = new ArrayList<Item>();
		
		ItemManager iManager = ItemManager.newItemManager(this);
		Item item = iManager.newItem("ともだち","friend", null);
		items.add(item);
		item = iManager.newItem("じどうしゃ","car", null);
		items.add(item);
		
		// 指定したレイアウトでItemを並べる
		GridAdapter gAdapter = new GridAdapter(this, R.layout.list_item, items);
		gView.setAdapter(gAdapter);
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

	/* (非 Javadoc)
	 * 画面が表示されたときに呼び出されるメソッド
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		int sb_height = searchButton.getHeight();
		int sb_width = searchButton.getWidth();
		int tv_width = titleView.getWidth();
		
		float max_height = sb_height/3;
		float max_width = sb_width * 0.5f / searchButton.getText().length();
		float bt_text_size = (max_height < max_width) ? max_height : max_width;
		searchButton.setTextSize(bt_text_size/density);
		
		max_width = sb_width * 0.5f / registerButton.getText().length();
		bt_text_size = (max_height < max_width) ? max_height : max_width;
		registerButton.setTextSize(bt_text_size/density);
		
		titleView.setTextSize(tv_width/(titleView.getText().length()+1)/density);
	}
	*/
	
}
