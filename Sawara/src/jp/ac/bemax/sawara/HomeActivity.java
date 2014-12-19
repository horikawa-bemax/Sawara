package jp.ac.bemax.sawara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

/**
 * ホーム画面のアクティビティ
 * @author Masaaki Horikawa
 * 2014/07/02
 */
public class HomeActivity extends Activity implements OnClickListener, OnMenuItemClickListener, OnItemClickListener{
	//
	static final int THEME_CHANGE = 0;
	static final int LIST_CHANGE = 1;
	// インテント呼び出し用ID 
	static final int REGISTER = 100;
	// 画面用のID
	static final int CATEGORY_VIEW = 1;
	static final int ARTICLE_VIEW = 2;
	// 各VIEW用のID
	static final int SETTING_BUTTON = 1;
	static final int NEW_BUTTON = 2;
	static final int RETURN_BUTTON = 3;
	static final int GRIDVIEW = 4;
	static final int CATEGORY_TEXTVIEW = 5;
	
	private Handler mHandler;
	private GridView gridView;
	private GridAdapter gridAdapter;
	private List<ListItem> categoryItems;
	private List<ListItem> articleItems;
	private CategoryManager cManager;
	private ArticleManager aManager;
	private VTextView categoryTextView;
	private Button settingButton;
	private Button newButton;
	private Button returnButton;
	private int viewMode;
	private Category thisCategory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ディスプレイサイズを取得する
		Display display = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);
		
		// マネージャの設定
		cManager = new CategoryManager(this);
		aManager = new ArticleManager(this);
		

		/***********
		 * 設定ファイルの処理
		 ***********/
		Configuration.confFile = new File(getFilesDir(), "sawara.conf");
		
		if(!Configuration.confFile.exists()){
			Configuration.makeConfFile();
		}
		String themeVal = Configuration.getConfig("theme");
		int resid = getResources().getIdentifier(themeVal, "style", getPackageName());
		setTheme(resid);
		
		/***********
		 * データベースアクセス
		 ***********/
		//SawaraDBAdapter sdb = new SawaraDBAdapter(this);
		//sdb.dump();
		
		// viewMode設定
		viewMode = CATEGORY_VIEW;
		
		// カテゴリーのリストを取得
		categoryItems = cManager.getAllItems();
		
		gridAdapter = new GridAdapter(this, R.layout.list_item, new ArrayList<ListItem>());
		gridAdapter.addAll(categoryItems);
		
		/**************
		 *  ハンドラーの設定
		 **************/
		final HomeActivity thisObj = this;
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				switch(msg.what){
				case THEME_CHANGE:
					/***********
					 * 画面コンテンツの初期化
					 ***********/
					setContentView(R.layout.home);
					
					categoryTextView = (VTextView)findViewById(R.id.category_text_view);
					categoryTextView.setTextSize(80);
					
					// 各VIEWを初期化＆配置する
					RelativeLayout homeLayout = (RelativeLayout)findViewById(R.id.home_base_layout);
					createCategoryModeDisplay(homeLayout);
					
					// ウィジェットを登録 
					gridView.setAdapter(gridAdapter);
					
					// 各アイテムをクリックした場合のリスナを登録
					gridView.setOnItemClickListener(thisObj);
					
					break;
				case LIST_CHANGE:
					
					switch(viewMode){
					case CATEGORY_VIEW:

	
						break;
					case ARTICLE_VIEW:
						categoryTextView.setVisibility(View.VISIBLE);
						categoryTextView.setText(thisCategory.getName());
						returnButton.setVisibility(View.VISIBLE);
						break;
					}
					
					break;
				}
			}
			
		};
		
		mHandler.sendEmptyMessage(THEME_CHANGE);
		mHandler.sendEmptyMessage(LIST_CHANGE); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem gorstItem = menu.add(Menu.NONE, 0,Menu.NONE, "おばけ");
		MenuItem heartItem = menu.add(Menu.NONE, 1,Menu.NONE, "はーと");
		MenuItem starItem = menu.add(Menu.NONE, 2, Menu.NONE, "ほし");
		
		gorstItem.setIcon(R.drawable.theme_gorst_back_image);
		//gorstItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		gorstItem.setOnMenuItemClickListener(this);
		
		heartItem.setIcon(R.drawable.theme_heart_back_image);
		heartItem.setOnMenuItemClickListener(this);
		
		starItem.setIcon(R.drawable.theme_star_back_image);
		starItem.setOnMenuItemClickListener(this);
		
		return true;
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch(v.getId()){
		case NEW_BUTTON:
			intent = new Intent(this, RegisterActivity.class);
			intent.putExtra("mode", RegisterActivity.NEW_MODE);
			
			switch(viewMode){
			case CATEGORY_VIEW:
				break;
			case ARTICLE_VIEW:
				intent.putExtra("category", thisCategory);
				break;
			}
			startActivityForResult(intent, REGISTER);
			
			break;
		case RETURN_BUTTON:
			switch(viewMode){
			case ARTICLE_VIEW:
				viewMode = CATEGORY_VIEW;
				categoryItems = cManager.getAllItems();
				gridAdapter.clear();
				gridAdapter.addAll(categoryItems);
				gridAdapter.notifyDataSetChanged();
			}
			mHandler.sendEmptyMessage(LIST_CHANGE);
			break;
		case SETTING_BUTTON:
			
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			
			switch(requestCode){
			case REGISTER:
				
				if(resultCode == RESULT_OK){
					switch(viewMode){
					case CATEGORY_VIEW:
						categoryItems = cManager.getAllItems();
						gridAdapter.clear();
						gridAdapter.addAll(categoryItems);
						gridAdapter.notifyDataSetChanged();
						break;
					case ARTICLE_VIEW:
						Article article = (Article)data.getSerializableExtra("article");
						gridAdapter.add(article);
						gridAdapter.notifyDataSetChanged();
						break;
					}
					
					mHandler.sendEmptyMessage(LIST_CHANGE);
				}
				
				break;
			}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId()){
		case 0:
			this.setTheme(R.style.GorstTheme);
			Configuration.setConfig("theme", "GorstTheme");
			break;
		case 1:
			this.setTheme(R.style.HeartTheme);
			Configuration.setConfig("theme", "HeartTheme");
			break;
		case 2:
			this.setTheme(R.style.StarTheme);
			Configuration.setConfig("theme", "StarTheme");
			break;
		}
		mHandler.sendEmptyMessage(THEME_CHANGE);
		return true;
	}

	/* 
	 * GridView上のアイテムをクリックしたときに呼び出されるメソッド
	 * (非 Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch(viewMode){
		case CATEGORY_VIEW:
			viewMode = ARTICLE_VIEW;
			thisCategory = (Category)categoryItems.get(position);
			articleItems = aManager.getArticlesAtCategory(thisCategory);
			gridAdapter.clear();
			gridAdapter.addAll(articleItems);
			gridAdapter.notifyDataSetChanged();
			
			returnButton.setVisibility(View.VISIBLE);
			
			mHandler.sendEmptyMessage(LIST_CHANGE);
			break;
		case ARTICLE_VIEW:
			Intent intent = new Intent(this, RegisterActivity.class);
			Article article = (Article)articleItems.get(position);
			intent.putExtra("article", article);
			intent.putExtra("mode", RegisterActivity.READ_MODE);
			
			startActivity(intent);
			break;
		}
	}
	
	void createCategoryModeDisplay(RelativeLayout layout){
		// LayoutParamsを用意
		RelativeLayout.LayoutParams params;
		
		// 設定ボタンを設置する
		settingButton = new Button(this);
		settingButton.setId(SETTING_BUTTON);
		// 設定ボタンのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(200, 200);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		settingButton.setLayoutParams(params);
		// ボタンの画像を設定する
		settingButton.setBackground(ButtonFactory.createSettingButtonDrawable(this));
		// layoutにボタンを登録する
		layout.addView(settingButton);
		
		// 新規作成ボタンを設定する
		newButton = new Button(this);
		newButton.setId(NEW_BUTTON);
		// 新規作成ボタンのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(100, 100);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		newButton.setLayoutParams(params);
		
		
		// GridViewを設定する
		gridView = new GridView(this);
		gridView.setId(GRIDVIEW);
		// GridViewのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ABOVE, NEW_BUTTON);
		params.addRule(RelativeLayout.LEFT_OF, SETTING_BUTTON);
		gridView.setLayoutParams(params);
		
	}
	
	void articleModeButtons(){
		
	}
}
