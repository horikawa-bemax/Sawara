package jp.ac.bemax.sawara;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
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
		//Typeface yasashisa_B = Typeface.createFromAsset(getAssets(),"yasashisa_bold.ttf");
		//Typeface yasashisa = Typeface.createFromAsset(getAssets(),"yasashisa.ttf");
		
		// 初期化
		items = new ArrayList<Item>();
		ItemManager iManager = ItemManager.newItemManager(this);	
		
		// Itemリストを作成（サンプル作成）
		String[] strs = {"ともだち","じどうしゃ","こうえん","いえ","いぬ","やま","でんしんばしら","ひこうき"};
		String[] images = {"friend","car","park","house","dog","mountain","pole","airplane"};
		InputStream is;
		
		for(int i=0; i<8; i++){
			try {
				is = getAssets().open("sample/"+images[i]+".jpg");
				Bitmap bmp = BitmapFactory.decodeStream(is);
				Item item = iManager.newItem(strs[i], bmp, null);
				items.add(item);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		
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
	
}
