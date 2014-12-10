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
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

/**
 * ホーム画面のアクティビティ
 * @author Masaaki Horikawa
 * 2014/07/02
 */
public class HomeActivity extends Activity implements OnClickListener, OnMenuItemClickListener, OnItemClickListener{
	static final int THEME_CHANGE = 0;
	
	static final int REGISTER = 100;
	
	static final int CATEGORY_VIEW = 1;
	static final int ARTICLE_VIEW = 2;
	
	private Handler mHandler;
	//private ActionBar mActionBar;
	private GridView gView;
	private GridAdapter gAdapter;
	private List<ListItem> listItems;
	private CategoryManager cManager;
	private ArticleManager aManager;
	private RelativeLayout layout;
	private Button settingButton;
	private Button newButton;
	private HorizontalScrollView mHSView;
	private int viewMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ディスプレイサイズを取得する
		Display display = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);
		
		// viewMode設定
		viewMode = CATEGORY_VIEW;
		
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

		// カテゴリーのリストを取得
		listItems = getCategoryItems();

		// グリッドビューにセット
		gAdapter = new GridAdapter(this, R.layout.list_item, listItems);
		
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
					
					layout = (RelativeLayout)findViewById(R.id.RelativeLayout1);
					
					settingButton = (Button)findViewById(R.id.setting_button);
					settingButton.setOnClickListener(thisObj);
					
					newButton = (Button)findViewById(R.id.new_button);
					newButton.setOnClickListener(thisObj);
					
					mHSView = (HorizontalScrollView)findViewById(R.id.horizontalScrollView);
					mHSView.setVisibility(View.INVISIBLE);
					
					// ウィジェットを登録 
					gView = (GridView)findViewById(R.id.gridView);
					
					gView.setAdapter(gAdapter);
					
					// 各アイテムをクリックした場合のリスナを登録
					gView.setOnItemClickListener(thisObj);
				}
			}
			
		};
		
		mHandler.sendEmptyMessage(THEME_CHANGE);
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
		case R.id.new_button:
			
			intent = new Intent(this, RegisterActivity.class);
			startActivityForResult(intent, REGISTER);
			
			break;
		case R.id.setting_button:
			
			if(mHSView.getVisibility() == View.INVISIBLE){
				mHSView.setVisibility(View.VISIBLE);
			}else{
				mHSView.setVisibility(View.INVISIBLE);
			}
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			
			switch(requestCode){
			case REGISTER:
				for(int i=0; i<listItems.size(); i++){
					ListItem item = listItems.get(i);
					if(item instanceof Category){
						Category cat = (Category)item;
						if(cat.getId() == 2){
							CategoryManager manager = new CategoryManager(this);
							manager.setCategoryIcon(cat);
						}
					}
				}
				gView.invalidate();
				mHandler.sendEmptyMessage(THEME_CHANGE);
				
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
	
	/**
	 * カテゴリーのリストを返す。
	 * @return カテゴリーのリスト
	 */
	public List<ListItem> getCategoryItems(){
		List<ListItem> items = new ArrayList<ListItem>();
		List<Category> categoryList = cManager.getAllItems();
		for(Category category: categoryList){
			items.add(category);
		}
		return items;
	}
	
	/**
	 * Articleのリストを返す
	 * @param categoryId カテゴリのID
	 * @return Articleのリスト
	 */
	public List<ListItem> getArticleItems(long categoryId){
		List<ListItem> items = new ArrayList<ListItem>();
		List<Article> articleList = aManager.getArticlesAtCategory(categoryId);
		for(Article article: articleList){
			items.add(article);
		}
		return items;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
