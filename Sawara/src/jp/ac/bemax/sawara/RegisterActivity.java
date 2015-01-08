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
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 登録画面
 * @author Masaaki Horikawa
 * 2014/07/23
 */
/**
 * @author horikawa
 *
 */
public class RegisterActivity extends Activity implements OnClickListener, OnItemClickListener{
	// ** 各種定数の設定 **
	// 画面モードの定数
	static final int MODE_NOTHING = 0;		// 画面モードの指定が無い → エラー
	static final int NEW_MODE = 1;				// 新規登録モード
	static final int UPDATE_MODE = 2;			// 更新モード
	static final int READ_MODE = 3;				// 閲覧モード
	// ボタン用ID
	static final int RETURN_BUTTON = 101;	// 戻るボタン
	static final int ALBAM_BUTTON = 102;	// アルバムボタン
	static final int MOVIE_BUTTON = 103;		// 動画ボタン
	static final int PHOTO_BUTTON = 104;		// 写真ボタン
	static final int REGIST_BUTTON = 105;	// 決定ボタン
	static final int UPDATE_BUTTON = 106;	// 更新ボタン
	static final int DELETE_BUTTON = 107;	// 削除ボタン
	// 画像アイテムの識別用
	static final int PICTURE = 201;					// 写真
	static final int MOVIE = 202;					// 動画
	// 暗黙インテント呼び出し用
	static final int IMAGE_CAPTUER = 301;	// 画像キャプチャ
	static final int MOVIE_CAPTUER = 302;	// 動画撮影
	static final int STRAGE_READ = 303;		// アルバム呼び出し
	// 動画撮影の最大時間（秒）
	private final int MOVIE_MAX_TIME = 20;	// 最大20秒撮影
	private final int MOVIE_QUALITY = 0;		// 動画のクオリティ（低）
	
	// ** インスタンス変数の宣言 **
	// View
	private RelativeLayout registerLayout;		// レイアウト
	private VTextView nameTextView;				// 名前テキスト
	private VTextView discriptionTextView;		// 詳細テキスト
	private Button albamButton;						// アルバムボタン
	private Button movieButton;						// 動画ボタン
	private Button photoButton;						// 写真ボタン
	private Button registButton;						// 決定ボタン
	private Button returnButton;						// 戻るボタン
	private Button updateButton;					// 更新ボタン
	private Button deleteButton;						// 削除ボタン
	private GridView imageViewerView;			// 画像一覧
	private GridView tagViewerView;				// タグ一覧
	// 画像一覧用のアダプタ
	private ImageAdapter imageViewerAdapter;
	// 画像一覧用の変数 
	private int[] itemType;			// 画像アイテムの種類
	private String[] itemPaths;	// 画像アイテムのパス
	private List<String> mImagePathList;
	private List<String> mMoviePathList;
	// タグ一覧用のアダプタ
	private ArrayAdapter<VTextView> tagViewerAdapter;
	// 写真、動画の保存先ファイル
	private String fileName;
	// 現在のカテゴリ（新規登録時に使用）
	private Category thisCategory;
	// Articleマネージャ
	private ArticleManager mArticleManager;
	// ディスプレイ関連のstaticな変数
	static float displayDensity;
	static int buttonSize;
	static int gridViewColmn;
	static float displayWidth;
	static float displayHeight;
	static int frameSize;
	// 初期設定用のオブジェクト
	static Configuration conf;
	
	/* (非 Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
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
		displayHeight = displaySize.y;
		buttonSize = (int)(displayHeight / 5);
		gridViewColmn = (int)((displayWidth - buttonSize) / (buttonSize * 2));
		
		setContentView(R.layout.register);
		
		// ** アクティビティにテーマを設定する **
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
		
		// ** インテントの処理 **
		// インテントを取得する
		Intent intent = getIntent();
		// インテントから画面モードを取得
		int mode = intent.getIntExtra("mode", 0); // モード設定が無い場合は、0
		
		// ArticleManager設定
		mArticleManager = new ArticleManager(this);
		
		// ** ビューア用の初期設定 **
		// イメージビューア用のアダプタ設定
		imageViewerAdapter = new ImageAdapter(this);

		//** Viewの設定 **
		// レイアウト
		registerLayout = (RelativeLayout)findViewById(R.id.register_layout);
		TypedValue outValue = new TypedValue();
		getTheme().resolveAttribute(R.attr.mainBack, outValue, true);
		Bitmap backBitmap = BitmapFactory.decodeResource(getResources(), outValue.resourceId);
		BitmapDrawable backDrawable = new BitmapDrawable(getResources(), backBitmap);
		backDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		registerLayout.setBackground(backDrawable);

		// 名前テキスト
		nameTextView = (VTextView)findViewById(R.id.register_name);
		// 詳細テキスト
		discriptionTextView = (VTextView)findViewById(R.id.register_description);
		// アルバムボタン
		albamButton = new Button(this);
		albamButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.album_image));
		albamButton.setId(ALBAM_BUTTON);
		albamButton.setOnClickListener(this);
		// 動画ボタン
		movieButton = new Button(this);
		movieButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.movie_image));
		movieButton.setId(MOVIE_BUTTON);
		movieButton.setOnClickListener(this);
		// 写真ボタン
		photoButton = new Button(this);
		photoButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.camera_image));
		photoButton.setId(PHOTO_BUTTON);
		photoButton.setOnClickListener(this);
		// 決定ボタン
		registButton = new Button(this);	
		registButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.regist_button_image));
		registButton.setId(REGIST_BUTTON);
		registButton.setOnClickListener(this);
		// 戻るボタン
		returnButton = new Button(this);
		returnButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.return_button_image));
		returnButton.setId(RETURN_BUTTON);
		returnButton.setOnClickListener(this);
		// 更新ボタン
		updateButton = new Button(this);
		updateButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.update_button_image));
		updateButton.setId(UPDATE_BUTTON);
		updateButton.setOnClickListener(this);
		// 削除ボタン
		deleteButton = new Button(this);
		deleteButton.setBackground(ButtonFactory.getButtonDrawable(this, R.drawable.delete_button_image));
		deleteButton.setId(DELETE_BUTTON);
		deleteButton.setOnClickListener(this);
		// 画像ビューア
		imageViewerView = (GridView)findViewById(R.id.register_image_viewer);
		imageViewerView.setAdapter(imageViewerAdapter);
		// タグビューア
		tagViewerView = (GridView)findViewById(R.id.register_tag_viewer);

		// ** Viewの配置 **
		// LayoutParamsの宣言
		RelativeLayout.LayoutParams params;
		// モードごとに配置する
		switch(mode){
		case NEW_MODE:	// == 新規登録モード ==
			// ** ボタン配置 **
			// アルバムボタン
			params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.addRule(RelativeLayout.LEFT_OF, R.id.register_description);
			params.setMargins(5, 5, 5, 5);
			albamButton.setLayoutParams(params);
			registerLayout.addView(	albamButton);
			// 動画ボタン
			params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.addRule(RelativeLayout.LEFT_OF, ALBAM_BUTTON);
			params.setMargins(5, 5, 5, 5);
			movieButton.setLayoutParams(params);
			registerLayout.addView(movieButton);
			// 写真ボタン
			params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.addRule(RelativeLayout.LEFT_OF, MOVIE_BUTTON);
			params.setMargins(5, 5, 5, 5);
			photoButton.setLayoutParams(params);
			registerLayout.addView(photoButton);
			// 決定ボタン
			params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.setMargins(5, 5, 5, 5);
			registButton.setLayoutParams(params);
			registerLayout.addView(registButton);
			// タグビューア
			params = new RelativeLayout.LayoutParams(300, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.ABOVE, REGIST_BUTTON);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.setMargins(5, 5, 5, 5);
			tagViewerView.setLayoutParams(params);
			// 画像ビューア
			params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.ABOVE, ALBAM_BUTTON);
			params.addRule(RelativeLayout.ALIGN_RIGHT, ALBAM_BUTTON);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.register_tag_viewer);
			params.setMargins(5, 5, 5, 5);
			imageViewerView.setLayoutParams(params);
			
			// ** 画像および動画のリスト設定 **
			mImagePathList = new ArrayList<String>();
			mMoviePathList = new ArrayList<String>();
			
			// ** リスナー登録 **
			nameTextView.setOnClickListener(this);
			discriptionTextView.setOnClickListener(this);
	
			// ** 縦書きテキストを編集可能にする＆テキストサイズ指定 **
			// 名前テキスト
			nameTextView.setFocusableInTouchMode(true);
			nameTextView.setTextSize(100);
			// 詳細テキスト
			discriptionTextView.setFocusableInTouchMode(true);
			discriptionTextView.setTextSize(80);

			// ** インテントからカテゴリ情報を取得 **
			thisCategory = (Category)intent.getSerializableExtra("category");
			
			break;
		case UPDATE_MODE:	// == 更新モード ==
			
			// リスナを解除する
			imageViewerView.setOnItemClickListener(null);
			
			break;
		case READ_MODE:	// == 閲覧モード ==
			// ** Articleを取得 **
			Article article = (Article)intent.getSerializableExtra("article");
			
			nameTextView.setText(article.getName());
			discriptionTextView.setText(article.getDescription());
			
			// ** 画像ビューア用のデータを準備する **
			// 写真と動画のパスを取得
			String[] imagePaths = article.getImagePaths();
			String[] moviePaths = article.getMoviePaths();
			// アイテムの種類を格納する配列
			itemType = new int[imagePaths.length + moviePaths.length];
			// アイテムのパスを格納する配列
			itemPaths = new String[imagePaths.length + moviePaths.length];
			//　データを格納する
			int j = 0;
			for(String path: imagePaths){
				// パスから写真用サムネイル画像を作成
				Bitmap image = IconFactory.createIconImage(BitmapFactory.decodeFile(path));
				// アダプタに追加
				imageViewerAdapter.add(image);
				// パスの配列に登録
				itemPaths[j] = path;
				// 種別を写真にする
				itemType[j++] = PICTURE;
			}
			for(String path: moviePaths){
				// パスから動画のサムネイル画像を作成する
				Bitmap image = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
				// アダプタに追加
				imageViewerAdapter.add(image);
				// パスの配列に登録
				itemPaths [j] = path;
				// 種別を動画にする
				itemType[j++] = MOVIE;
			}
			
			// ** Viewの設置 **
			// 戻るボタン
			params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.setMargins(5, 5, 5, 5);
			registerLayout.addView(returnButton, params);
			// 更新ボタン
			params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
			params.addRule(RelativeLayout.LEFT_OF, R.id.register_description);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.setMargins(5, 5, 5, 5);
			registerLayout.addView(updateButton, params);
			// 削除ボタン
			params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
			params.addRule(RelativeLayout.LEFT_OF, UPDATE_BUTTON);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.setMargins(5, 5, 5, 5);
			registerLayout.addView(deleteButton, params);
			// タグビューア
			params = new RelativeLayout.LayoutParams(300, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.ABOVE, RETURN_BUTTON);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.setMargins(5, 5, 5, 5);
			tagViewerView.setLayoutParams(params);
			// 画像ビューア
			params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.ABOVE, UPDATE_BUTTON);
			params.addRule(RelativeLayout.ALIGN_RIGHT, UPDATE_BUTTON);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.register_tag_viewer);
			params.setMargins(5, 5, 5, 5);
			imageViewerView.setLayoutParams(params);
			
			// viewerのitemにタッチされた時の設定
			imageViewerView.setOnItemClickListener(this);
			
			break;
		}
		
	}

	/* (非 Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// ** ローカル変数の初期化 **
		Intent intent = new Intent();		// インテント
		File dir = null;							// 保存先ファイル
		
		switch(v.getId()){
		case ALBAM_BUTTON:		// == アルバム読み込みモード ==
			// 結果を呼び出しもとActivityに返す
			
			// Activityを終了
			finish();
			break;
			
		case PHOTO_BUTTON:		// == 写真撮影モード ==
			// ** 保存先を作成 **
			dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			fileName = "" + System.currentTimeMillis() + ".jpg";
			Uri imageUri = Uri.fromFile(new File(dir, fileName));
			
			// ** 写真撮影用の暗黙インテントを呼び出す準備 **
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			
			// インテントを呼び出す
			startActivityForResult(intent, IMAGE_CAPTUER);
			break;
			
		case MOVIE_BUTTON:		// == 動画撮影モード ==
			// ** 保存先を作成 **
			dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			fileName = "" + System.currentTimeMillis() + ".mp4";
			Uri movieUri = Uri.fromFile(new File(dir, fileName));
			
			// ** 動画撮影用の暗黙院展とを呼び出す準備 **
			intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, movieUri);
			// 動画のクオリティを設定
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, MOVIE_QUALITY);
			// 動画の最大撮影時間を設定
			intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MOVIE_MAX_TIME);
			
			// インテントを呼び出す
			startActivityForResult(intent, MOVIE_CAPTUER);
			break;
			
		//*** 入力データを登録する ***
		case REGIST_BUTTON:
			// 基本値をセットする
			String name = nameTextView.getText().toString();
			String description = discriptionTextView.getText().toString();
			
			// 画像ファイルのパスをセットする
			String[] imagePaths = new String[mImagePathList.size()];
			for(int i=0; i<imagePaths.length; i++){
				imagePaths[i] = mImagePathList.get(i);
			}
			
			// 動画ファイルのパスをセットする
			String[] moviePaths = new String[mMoviePathList.size()];
			for(int i=0; i<moviePaths.length; i++){
				moviePaths[i] = mMoviePathList.get(i);
			}
			
			// カテゴリーidの設定
			long[] categoryIds;
			if(thisCategory != null){
				categoryIds = new long[]{thisCategory.getId()};
			}else{
				categoryIds = new long[]{2};
			}
			
			// 新しいArticleを作成する
			Article article = mArticleManager.newArticle(name, description, imagePaths, moviePaths, categoryIds);	
			
			// 成功
			if(article != null){
				// カテゴリーアイコンの更新
				CategoryManager cManager = new CategoryManager(this);
				for(long cId: categoryIds){
					Category category = cManager.loadCategory(cId);
					cManager.setCategoryIcon(category);
				}
			
				intent.putExtra("article", article);
				setResult(RESULT_OK, intent);
			}else{
				setResult(RESULT_CANCELED, intent);
			}
			
			finish();
	
			break;
		case RETURN_BUTTON:
			finish();
			break;
		}
	}

	/* 
	 *
	 * (非 Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		File dir = null;
		String path = null;
		
		// インテントからの返信が成功した場合
		if(resultCode == RESULT_OK){
			
			switch(requestCode){
			
			//*** 写真を撮影した場合 ***
			case IMAGE_CAPTUER:
				// 画像のサイズを読み込む
				dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
				path = new File(dir, fileName).getPath();
				
				// サイズを確定するための仮読み込み
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, opt);
			
				// 読み込み時の精度を決定
				int size = opt.outWidth;
				if(opt.outHeight > size){
					size = opt.outHeight;
				}
				opt.inSampleSize = size / 480;
				
				// 本格的に画像を読み込む
				opt.inJustDecodeBounds = false;
				Bitmap image = BitmapFactory.decodeFile(path, opt);
				
				// imagePathListに登録する
				mImagePathList.add(path);
				
				// ビューアに反映する
				imageViewerAdapter.add(IconFactory.createIconImage(image));
				imageViewerAdapter.notifyDataSetChanged();

				break;
				
			//*** 動画を撮影した場合 ***	
			case MOVIE_CAPTUER:
				dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
				path = new File(dir, fileName).getPath();
				
				// 動画のサムネイル画像を取得する
				Bitmap bmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
				
				// moviePathListに登録する
				mMoviePathList.add(path);
				
				// ビューアに反映する
				imageViewerAdapter.add(IconFactory.createIconImage(bmp));
				imageViewerAdapter.notifyDataSetChanged();

				break;
			}
		}
	}

	
	/*
	 *  viewerアイテムがクリックされたときに呼び出される
	 *  (非 Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		switch(parent.getId()){
		case R.id.register_image_viewer:
			Uri uri =Uri.fromFile(new File(itemPaths[position]));
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			if(itemType[position] == PICTURE){
				intent.setDataAndType(uri, "image/*");
			}else{
				intent.setDataAndType(uri, "video/*");
			}
			startActivity(intent);
			
			break;
		case R.id.register_tag_viewer:
			break;
		}
		
	}
	
	void putNewModeButtons(){
		RelativeLayout.LayoutParams params;
		
		params = new RelativeLayout.LayoutParams(200,200);
		
	}
}
