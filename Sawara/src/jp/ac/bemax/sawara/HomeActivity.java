package jp.ac.bemax.sawara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
	// 画面更新用のID
	static final int DISPLAY_CHANGE = 0;
	static final int THEMA_CHANGE = 1;
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
	static final int HOMELAYOUT = 6;
	
	private final int MP = RelativeLayout.LayoutParams.MATCH_PARENT;
	
	private Handler mHandler;
	private RelativeLayout homeLayout;
	private GridView gridView;
	private GridAdapter gridAdapter;
	private List<ListItem> categoryItems;
	private CategoryManager cManager;
	private VTextView categoryTextView;
	private Button settingButton;
	private Button newButton;
	private Button returnButton;
	private int viewMode;
	private Category thisCategory;
	// ディスプレイ関連のstaticな変数
	static float displayDensity;
	static int buttonSize;
	static int gridViewColmn;
	static float displayWidth;
	static float displayHeight;
	static int frameSize;
	// 初期設定用のオブジェクト
	static Configuration conf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ディスプレイサイズを取得する
		WindowManager windowManager = getWindowManager();
		Point displaySize = new Point();
		windowManager.getDefaultDisplay().getSize(displaySize);
		DisplayMetrics outMetrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(outMetrics);
		displayDensity = outMetrics.density;
		displayWidth = displaySize.x;
		displayHeight = displaySize.y - 25 * displayDensity;
		buttonSize = (int)(displayHeight / 5);
		gridViewColmn = (int)((displayWidth - buttonSize) / (buttonSize * 2));
		ButtonFactory.setButtonFrameSize(buttonSize);
		
		// マネージャの設定
		cManager = new CategoryManager(this);
		
		// 設定ファイルを読み込む
		File confFile = new File(getFilesDir(), "sawara.conf");
		conf = Configuration.loadConfig(confFile);
		if(conf == null){
			conf = new Configuration();
			conf.setTheme("DefaultTheme");
			Configuration.storeConfig(confFile, conf);
		}
		String themeVal = conf.getTheme();
		int resid = getResources().getIdentifier(themeVal, "style", getPackageName());
		setTheme(resid);
		
		// viewMode設定
		viewMode = CATEGORY_VIEW;
		// カテゴリーのリストを取得
		categoryItems = cManager.getAllItems();
		// アダプタにカテゴリのリストを設定する
		gridAdapter = new GridAdapter(this, R.layout.list_item, new ArrayList<ListItem>());
		gridAdapter.addAll(categoryItems);
		// homeLayoutを作成
		homeLayout = new RelativeLayout(this);
		homeLayout.setId(HOMELAYOUT);
		// gridViewを作成
		gridView = new GridView(this);
		gridView.setId(GRIDVIEW);
		gridView.setNumColumns(gridViewColmn);
		gridView.setOnItemClickListener(this);
		// categoryTextViewを作成
		categoryTextView = new VTextView(this);
		categoryTextView.setId(CATEGORY_TEXTVIEW);
		// settingButtonを作成
		settingButton = new Button(this);
		settingButton.setId(SETTING_BUTTON);
		settingButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.setting_button_image));
		settingButton.setOnClickListener(this);
		// newButtonを作成
		newButton = new Button(this);
		newButton.setId(NEW_BUTTON);
		newButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.new_button_image));
		newButton.setOnClickListener(this);
		// returnButtonを作成
		returnButton = new Button(this);
		returnButton.setId(RETURN_BUTTON);
		returnButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.return_button_image));
		returnButton.setOnClickListener(this);
		
		setContentView(homeLayout);
		
		// 画面更新用のハンドラを設定する
		final HomeActivity thisObj = this;
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				switch(msg.what){
				case DISPLAY_CHANGE:
					
					// 各VIEWを初期化＆配置する
					switch(viewMode){
					case CATEGORY_VIEW:
						createCategoryModeDisplay(homeLayout);
						break;
					case ARTICLE_VIEW:
						createArticleModeDisplay(homeLayout);
						categoryTextView.setText(thisCategory.getName());
						break;
					}
					
					// ウィジェットを登録 
					gridView.setAdapter(gridAdapter);
					// 各アイテムをクリックした場合のリスナを登録
					gridView.setOnItemClickListener(thisObj);
					
					break;
				case THEMA_CHANGE:
					TypedValue outValue = new TypedValue();
					getTheme().resolveAttribute(R.attr.mainBack, outValue, true);
					Bitmap backBitmap = BitmapFactory.decodeResource(getResources(), outValue.resourceId);
					BitmapDrawable backDrawable = new BitmapDrawable(getResources(), backBitmap);
					backDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
					homeLayout.setBackground(backDrawable);

					settingButton.setBackground(ButtonFactory.getButtonDrawable(thisObj, R.drawable.setting_button_image));
					newButton.setBackground(ButtonFactory.getButtonDrawable(thisObj, R.drawable.new_button_image));
					returnButton.setBackground(ButtonFactory.getButtonDrawable(thisObj, R.drawable.return_button_image));
					
					int count = gridView.getChildCount();
					for(int i=0; i<count; i++){
						View targetView = gridView.getChildAt(i);
						gridView.getAdapter().getView(i, targetView, gridView);
					}
					break;
				}
			}
		};
		
		mHandler.sendEmptyMessage(DISPLAY_CHANGE);
		mHandler.sendEmptyMessage(THEMA_CHANGE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem gorstItem = menu.add(Menu.NONE, 0,Menu.NONE, "おばけ");
		MenuItem heartItem = menu.add(Menu.NONE, 1,Menu.NONE, "夏");
		MenuItem starItem = menu.add(Menu.NONE, 2, Menu.NONE, "秋");
		MenuItem summerItem = menu.add(Menu.NONE, 3, Menu.NONE, "星");
		
		gorstItem.setOnMenuItemClickListener(this);
		
		heartItem.setOnMenuItemClickListener(this);
		
		starItem.setOnMenuItemClickListener(this);
		
		summerItem.setOnMenuItemClickListener(this);
		
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
			mHandler.sendEmptyMessage(DISPLAY_CHANGE);
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
					
					mHandler.sendEmptyMessage(DISPLAY_CHANGE);
				}
				
				break;
			}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId()){
		case 0:
			this.setTheme(R.style.GorstTheme);
			conf.setTheme("GorstTheme");
			break;
		case 1:
			this.setTheme(R.style.SummerTheme);
			conf.setTheme("SummerTheme");
			break;
		case 2:
			this.setTheme(R.style.AutumnTheme);
			conf.setTheme("AutumnTheme");
			break;
		case 3:
			this.setTheme(R.style.StarTheme);
			conf.setTheme("StarTheme");
			break;
		}
		File confFile = new File(getFilesDir(), "sawara.conf");
		Configuration.storeConfig(confFile, conf);
		mHandler.sendEmptyMessage(THEMA_CHANGE);
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
			
			List<ListItem> articleItems = null; //aManager.getArticlesAtCategory(thisCategory);
			
			gridAdapter.clear();
			gridAdapter.addAll(articleItems);
			gridAdapter.notifyDataSetChanged();
			mHandler.sendEmptyMessage(DISPLAY_CHANGE);
			break;
		case ARTICLE_VIEW:
			Intent intent = new Intent(this, RegisterActivity.class);
			Article article = (Article)gridView.getAdapter().getItem(position);
			intent.putExtra("article", article);
			intent.putExtra("mode", RegisterActivity.READ_MODE);
			
			startActivity(intent);
			break;
		}
	}
	
	/**
	 * カテゴリーモードの画面を作成する
	 * @param layout
	 */
	void createCategoryModeDisplay(RelativeLayout layout){
		layout.removeAllViews();
		// LayoutParamsを用意
		RelativeLayout.LayoutParams params;
		
		// 設定ボタンのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		settingButton.setLayoutParams(params);
		layout.addView(settingButton);
		
		// 新規作成ボタンのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		newButton.setLayoutParams(params);
		layout.addView(newButton);
		
		// GridViewのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(MP, MP);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ABOVE, NEW_BUTTON);
		params.addRule(RelativeLayout.LEFT_OF, SETTING_BUTTON);
		gridView.setLayoutParams(params);
		layout.addView(gridView);
		
	}
	
	/**
	 * アーティクルモードの画面を作成する
	 * @param layout 
	 */
	void createArticleModeDisplay(RelativeLayout layout){
		layout.setVisibility(View.INVISIBLE);
		// layoutのViewをリセットする
		layout.removeAllViews();
		// LayoutParamsを用意
		RelativeLayout.LayoutParams params;

		// 設定ボタンのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		settingButton.setLayoutParams(params);
		layout.addView(settingButton);
		
		// 戻るボタンのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		returnButton.setLayoutParams(params);
		layout.addView(returnButton);
		
		// 新規作成ボタンのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
		params.addRule(RelativeLayout.RIGHT_OF, RETURN_BUTTON);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		newButton.setLayoutParams(params);
		layout.addView(newButton);
		
		// GridViewのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(MP, MP);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ABOVE, NEW_BUTTON);
		params.addRule(RelativeLayout.LEFT_OF, SETTING_BUTTON);
		gridView.setLayoutParams(params);
		layout.addView(gridView);
		
		// categoryTextViewのLayoutParamsを設定する
		params = new RelativeLayout.LayoutParams(MP, MP);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_LEFT, SETTING_BUTTON);
		params.addRule(RelativeLayout.ABOVE, SETTING_BUTTON);
		categoryTextView.setLayoutParams(params);
		categoryTextView.setTextSize(100);
		layout.addView(categoryTextView);
		
		layout.setVisibility(View.VISIBLE);
	}
}
