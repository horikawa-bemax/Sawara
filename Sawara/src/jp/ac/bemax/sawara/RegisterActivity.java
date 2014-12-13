package jp.ac.bemax.sawara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

/**
 * 登録画面
 * @author Masaaki Horikawa
 * 2014/07/23
 */
public class RegisterActivity extends Activity implements OnClickListener{
	public static final int NEW_MODE = 1;
	public static final int UPDATE_MODE = 2;
	public static final int READ_MODE = 3;
	
	public static final int RETURN_BUTTON_ID = 1;
	
	private RelativeLayout registerLayout;
	private VTextView registerName;
	private VTextView registerDiscription;
	private Button registerAlbamButton;
	private Button registerMovieButton;
	private Button registerPhotoButton;
	private Button registerRegistButton;
	private GridView registerImageViewer;
	private GridView registerTagViewer;
	//private List<Bitmap> imageList;
	//private List<VTextView> tagList;
	private ImageAdapter imageViewerAdapter;
	private ArrayAdapter<VTextView> tagViewerAdapter;
	//private Handler mHandler;
	private String fileName;
	private Category thisCategory;
	
	private ArticleManager mArticleManager;
	//private CategoryManager mCategoryManager;
	
	private List<String> mImagePathList;
	private List<String> mMoviePathList;
	
	private final int IMAGE_CAPTUER = 200;
	private final int MOVIE_CAPTUER = 300;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		Intent intent = getIntent();
		
		// レイアウトの紐付け
		registerLayout = (RelativeLayout)findViewById(R.id.register_layout);
		registerName = (VTextView)findViewById(R.id.register_name);
		registerDiscription = (VTextView)findViewById(R.id.register_description);
		registerAlbamButton = (Button)findViewById(R.id.register_albam_button);
		registerMovieButton = (Button)findViewById(R.id.register_movie_button);
		registerPhotoButton = (Button)findViewById(R.id.register_photo_button);
		registerImageViewer = (GridView)findViewById(R.id.register_image_viewer);
		registerTagViewer = (GridView)findViewById(R.id.register_tag_viewer);
		registerRegistButton = (Button)findViewById(R.id.register_regist_button);
		
		// イメージビューアの設定
		imageViewerAdapter = new ImageAdapter(this);
		registerImageViewer.setAdapter(imageViewerAdapter);
		
		// ArticleManager設定
		mArticleManager = new ArticleManager(this); 
		
		int mode = intent.getIntExtra("mode", 0);
		
		switch(mode){
		case NEW_MODE:
			// 画像および動画のリスト設定
			mImagePathList = new ArrayList<String>();
			mMoviePathList = new ArrayList<String>();
			
			// ボタンの可視化
			registerAlbamButton.setVisibility(View.VISIBLE);
			registerMovieButton.setVisibility(View.VISIBLE);
			registerPhotoButton.setVisibility(View.VISIBLE);
			
			// クリックリスナー登録
			registerAlbamButton.setOnClickListener(this);
			registerMovieButton.setOnClickListener(this);
			registerPhotoButton.setOnClickListener(this);
			registerName.setOnClickListener(this);
			registerDiscription.setOnClickListener(this);
			registerRegistButton.setOnClickListener(this);
	
			// 縦書きテキストを編集可能にする＆テキストサイズ指定
			registerName.setFocusableInTouchMode(true);
			registerName.setTextSize(100);
			registerDiscription.setFocusableInTouchMode(true);
			registerDiscription.setTextSize(80);
			
			// 
			thisCategory = (Category)intent.getSerializableExtra("category");
		
			break;
		case UPDATE_MODE:
			
			break;
		case READ_MODE:
			Article article = (Article)intent.getSerializableExtra("article");
			
			registerAlbamButton.setVisibility(View.INVISIBLE);
			registerMovieButton.setVisibility(View.INVISIBLE);
			registerPhotoButton.setVisibility(View.INVISIBLE);
			
			registerName.setText(article.getName());
			registerDiscription.setText(article.getDescription());
			
			String[] imagePaths = article.getImagePaths();
			for(String path: imagePaths){
				Bitmap image = IconFactory.createIconImage(BitmapFactory.decodeFile(path));
				imageViewerAdapter.add(image);
			}
			
			String[] moviePaths = article.getMoviePaths();
			for(String path: moviePaths){
				Bitmap image = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
				imageViewerAdapter.add(image);
			}
			
			// 戻るボタンの設置
			Button returnButton = new Button(this);
			returnButton.setBackgroundResource(R.drawable.new_button_image);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300, 300);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			returnButton.setId(RETURN_BUTTON_ID);
			returnButton.setOnClickListener(this);
			registerLayout.addView(returnButton, params);
			
			Button updateButton = new Button(this);
			
			Resources res = getResources();
			Drawable d1 = res.getDrawable(R.drawable.dummy_image);
			Drawable d2 = res.getDrawable(R.drawable.new_button_image);
			Drawable[] layers = {d1, d2};
			Drawable background = new LayerDrawable(layers);
			updateButton.setBackground(background);
			//registerLayout.addView(updateButton, params);
			
			break;
		}
		
		// テキストのフォントを指定 
		Typeface tf = Typeface.createFromAsset(getAssets(),"HGRKK.TTC");
		
		/*
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.d("Handler起動","はんどらあー");
			}
		};
		*/
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		File dir = null;
		//Uri uriPath = null;
		
		switch(v.getId()){
		
		//*** アルバムからデータを読み込む ***
		case R.id.register_albam_button:
			// 結果を呼び出しもとActivityに返す

			
			// Activityを終了
			finish();
			break;
		
		//*** カメラで写真を取り込む ***
		case R.id.register_photo_button:
			// 保存先を作成
			dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			fileName = "" + System.currentTimeMillis() + ".jpg";
			Uri imageUri = Uri.fromFile(new File(dir, fileName));
			
			// 写真撮影用の暗黙インテントを呼び出す準備
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			
			// インテントを呼び出す
			startActivityForResult(intent, IMAGE_CAPTUER);
			break;
			
		//*** カメラで動画を取り込む ***
		case R.id.register_movie_button:
			// 保存先を作成
			dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			fileName = "" + System.currentTimeMillis() + ".mp4";
			Uri movieUri = Uri.fromFile(new File(dir, fileName));
			
			// 動画撮影用の暗黙院展とを呼び出す準備
			intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, movieUri);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);	//MAX２０秒間撮影可能
			
			// インテントを呼び出す
			startActivityForResult(intent, MOVIE_CAPTUER);
			break;
			
		//*** 入力データを登録する ***
		case R.id.register_regist_button:
			// 基本値をセットする
			String name = registerName.getText().toString();
			String description = registerDiscription.getText().toString();
			
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
		case RETURN_BUTTON_ID:
			finish();
			break;
		}
	}

	/* 
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
}
