package jp.ac.bemax.sawara;

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
	private final int MAPSIZE = 8;
	
	private LinearLayout[] boxs;
	private ImageView[] imageMap;
	private VTextView[] vTextMap;
	
	
	
	/* (非 Javadoc)
	 * コンストラクタ
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		boxs = new LinearLayout[MAPSIZE];
		imageMap = new ImageView[MAPSIZE];
		vTextMap = new VTextView[MAPSIZE];
		
		for(int i=0; i<MAPSIZE; i++){
			boxs[i] = (LinearLayout)findViewById(getResources().getIdentifier("box"+i, "id", getPackageName()));
			int s = boxs[i].getWidth();
			
			LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
			imgParam.weight = 1.0f;
			imageMap[i] = new ImageView(this);
			imageMap[i].setImageResource(R.drawable.friend);
			imageMap[i].setScaleType(ScaleType.CENTER);
			
			LinearLayout.LayoutParams vtxParam = new LinearLayout.LayoutParams(80, LayoutParams.MATCH_PARENT);
			vTextMap[i] = new VTextView(this, null);
			vTextMap[i].setText("このアイテムの、名前はなにかな");
			
			int size = 250;
			boxs[i].addView(imageMap[i],imgParam);
			boxs[i].addView(vTextMap[i],vtxParam);
		}
		
		/* ウィジェットを登録 */
		
		
		/* フォントを読み込む 
		Typeface yasashisa_B = Typeface.createFromAsset(getAssets(),"yasashisa_bold.ttf");
		Typeface yasashisa = Typeface.createFromAsset(getAssets(),"yasashisa.ttf");
		*/
		
		/* ウィジェットにフォントを適用する */
		//titleView.setTypeface(yasashisa_B);
		//searchButton.setTypeface(yasashisa);
		//registerButton.setTypeface(yasashisa);
		
		/* リスナの登録 

		
		/* ディスプレイ情報を取得 
		WindowManager wm = getWindowManager();
		Display display = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		density = dm.density;
		Log.d("DisplayDensity",""+dm.density);
		*/
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
