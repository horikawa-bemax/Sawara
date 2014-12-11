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

/**
 * ホーム画面のアクティビティ
 * @author Masaaki Horikawa
 * 2014/07/02
 */
public class HomeActivity extends Activity implements OnClickListener, OnMenuItemClickListener, OnItemClickListener{
	static final int THEME_CHANGE = 0;
	static final int LIST_CHANGE = 1;
	
	static final int REGISTER = 100;
	
	static final int CATEGORY_VIEW = 1;
	static final int ARTICLE_VIEW = 2;
	
	private Handler mHandler;
	//private ActionBar mActionBar;
	private GridView gView;
	private GridAdapter gAdapter;
	private List<ListItem> categoryItems;
	private List<ListItem> articleItems;
	private CategoryManager cManager;
	private ArticleManager aManager;
	private Button settingButton;
	private Button newButton;
	private Button returnButton;
	private HorizontalScrollView mHSView;
	private int viewMode;
	private long categoryId;
	
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
		
		gAdapter = new GridAdapter(this, R.layout.list_item, new ArrayList<ListItem>());
		gAdapter.addAll(categoryItems);
		
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
					
					settingButton = (Button)findViewById(R.id.setting_button);
					settingButton.setOnClickListener(thisObj);
					
					newButton = (Button)findViewById(R.id.new_button);
					newButton.setOnClickListener(thisObj);
					
					returnButton = (Button)findViewById(R.id.return_button);
					returnButton.setOnClickListener(thisObj);
					/**
					switch(viewMode){
					case CATEGORY_VIEW:
						returnButton.setVisibility(View.INVISIBLE);
						break;
					case ARTICLE_VIEW:
						returnButton.setVisibility(View.VISIBLE);
						break;
					}
					*/
					
					mHSView = (HorizontalScrollView)findViewById(R.id.horizontalScrollView);
					mHSView.setVisibility(View.INVISIBLE);
					
					// ウィジェットを登録 
					gView = (GridView)findViewById(R.id.gridView);
					gView.setAdapter(gAdapter);
					
					// 各アイテムをクリックした場合のリスナを登録
					gView.setOnItemClickListener(thisObj);
					
					break;
				case LIST_CHANGE:
					
					switch(viewMode){
					case CATEGORY_VIEW:
						returnButton.setVisibility(View.INVISIBLE);
						break;
					case ARTICLE_VIEW:
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
		case R.id.new_button:
			intent = new Intent(this, RegisterActivity.class);
			
			switch(viewMode){
			case CATEGORY_VIEW:
				break;
			case ARTICLE_VIEW:
				intent.putExtra("categoryId", categoryId);
				break;
			}
			startActivityForResult(intent, REGISTER);
			
			break;
		case R.id.return_button:
			switch(viewMode){
			case ARTICLE_VIEW:
				viewMode = CATEGORY_VIEW;
				categoryItems = cManager.getAllItems();
				gAdapter.clear();
				gAdapter.addAll(categoryItems);
				gAdapter.notifyDataSetChanged();
			}
			mHandler.sendEmptyMessage(LIST_CHANGE);
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
				
				if(resultCode == RESULT_OK){
					switch(viewMode){
					case CATEGORY_VIEW:
						categoryItems = cManager.getAllItems();
						gAdapter.clear();
						gAdapter.addAll(categoryItems);
						gAdapter.notifyDataSetChanged();
						break;
					case ARTICLE_VIEW:
						Article article = (Article)data.getSerializableExtra("article");
						gAdapter.add(article);
						gAdapter.notifyDataSetChanged();
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
			categoryId = categoryItems.get(position).getId();
			articleItems = aManager.getArticlesAtCategory(categoryId);
			gAdapter.clear();
			gAdapter.addAll(articleItems);
			gAdapter.notifyDataSetChanged();
			
			returnButton.setVisibility(View.VISIBLE);
			
			mHandler.sendEmptyMessage(LIST_CHANGE);
			break;
		case ARTICLE_VIEW:
			Intent intent = new Intent();
			
		}
	}
}
