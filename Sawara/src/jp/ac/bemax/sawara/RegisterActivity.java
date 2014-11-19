package jp.ac.bemax.sawara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * 登録画面
 * @author Masaaki Horikawa
 * 2014/07/23
 */
public class RegisterActivity extends Activity implements OnClickListener{
	private VTextView registerName;
	private VTextView registerDiscription;
	private Button registerAlbamButton;
	private Button registerMovieButton;
	private Button registerPhotoButton;
	private GridView registerImageViewer;
	private GridView registerTagViewer;
	private List<Bitmap> imageList;
	private List<VTextView> tagList;
	private ArrayAdapter<Bitmap> imageViewerAdapter;
	private ArrayAdapter<VTextView> tagViewerAdapter;
	
	private String fileName;
	
	private final int IMAGE_CAPTUER = 200;
	private final int MOVIE_CAPTUER = 300;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		// レイアウトの紐付け
		registerName = (VTextView)findViewById(R.id.register_name);
		registerDiscription = (VTextView)findViewById(R.id.register_description);
		registerAlbamButton = (Button)findViewById(R.id.register_albam_button);
		registerMovieButton = (Button)findViewById(R.id.register_movie_button);
		registerPhotoButton = (Button)findViewById(R.id.register_photo_button);
		registerImageViewer = (GridView)findViewById(R.id.register_image_viewer);
		registerTagViewer = (GridView)findViewById(R.id.register_tag_viewer);
		
		// イメージビューアの設定
		imageList = new ArrayList<Bitmap>();
		imageViewerAdapter = new ArrayAdapter<Bitmap>(this, R.layout.image_item, imageList);
		registerImageViewer.setAdapter(imageViewerAdapter);
		
		// タグビューアの設定
		tagList = new ArrayList<VTextView>();
		
		// クリックリスナー登録
		registerAlbamButton.setOnClickListener(this);
		registerMovieButton.setOnClickListener(this);
		registerPhotoButton.setOnClickListener(this);
		registerName.setOnClickListener(this);
		registerDiscription.setOnClickListener(this);
		
		// 編集可能にする
		registerName.setFocusableInTouchMode(true);
		registerDiscription.setFocusableInTouchMode(true);

		// テキストのフォントを指定 
		Typeface tf = Typeface.createFromAsset(getAssets(),"HGRKK.TTC");
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		File dir = null;
		Uri uriPath = null;
		
		switch(v.getId()){
		case R.id.register_albam_button:
			// 結果を呼び出しもとActivityに返す

			
			// Activityを終了
			finish();
			break;
		case R.id.register_photo_button:
			// 保存先を作成
			dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			String filename = "" + System.currentTimeMillis() + ".jpg";
			uriPath = Uri.fromFile(new File(dir, filename));
			
			// 写真撮影用の暗黙インテントを呼び出す準備
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
			
			// インテントを呼び出す
			startActivityForResult(intent, IMAGE_CAPTUER);
			break;
		case R.id.register_movie_button:
			// 保存先を作成
			dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			uriPath = Uri.fromFile(dir);
			
			// 動画撮影用の暗黙院展とを呼び出す準備
			intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);
			
			// インテントを呼び出す
			startActivityForResult(intent, MOVIE_CAPTUER);
			break;
		case R.id.register_name:
			
		case R.id.register_description:
			
		}
	}

	/* 
	 * (非 Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Uri uriPath = null;
		
		switch(requestCode){
		case IMAGE_CAPTUER:
			// 画像のサイズを読み込む
			if(data != null){
				uriPath = data.getData();
				String path = uriPath.getPath();
				
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
				
				imageList.add(image);
				imageViewerAdapter.notifyDataSetChanged();
				
			}
			break;
		case MOVIE_CAPTUER:
			
			if(resultCode == RESULT_OK){
				if(data != null){
					Uri moviePath = data.getData();
					
				}
			}
			
			break;
		}
	}
	
	@Override
	protected void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
	}
}
