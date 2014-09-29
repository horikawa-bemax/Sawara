package jp.ac.bemax.sawara;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * 登録画面
 * @author Masaaki Horikawa
 * 2014/07/23
 */
public class RegisterActivity extends Activity implements OnClickListener{
	
	private ImageView imageView;
	private TextView labelName, labelDescription;
	private EditText textName, textDescription;
	private Button submitButton, movieButton;
	private VideoView movieView;
	
	private String fileName;
	
	private final int MOVIE_CAPTUER = 300;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		// レイアウトの紐付け
		imageView = (ImageView)findViewById(R.id.register_image);
		labelName = (TextView)findViewById(R.id.register_label_name);
		labelDescription = (TextView)findViewById(R.id.register_label_description);
		textName = (EditText)findViewById(R.id.register_name);
		textDescription = (EditText)findViewById(R.id.register_description);
		submitButton = (Button)findViewById(R.id.register_button);
		movieButton = (Button)findViewById(R.id.register_movie_button);
		movieView = (VideoView)findViewById(R.id.register_video_view);
		
		// インテントからデータを受け取る
		Intent intent = getIntent();
		String path = intent.getStringExtra("image_uri");
		File file = new File(path);
		fileName = file.getName();
	
		// 画像のサイズを読み込む
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
		
		// imageViewに画像を表示
		imageView.setImageBitmap(image);
		
		// テキストのフォントを指定 
		Typeface tf = Typeface.createFromAsset(getAssets(),"yasashisa.ttf");
		
		// 名前ラベルの設定
		labelName.setTypeface(tf);
		labelName.setTextSize(35);
		labelName.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		
		// 説明ラベルの設定
		labelDescription.setTypeface(tf);
		labelDescription.setTextSize(35);
		labelDescription.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		
		// 名前入力の設定
		textName.setTypeface(tf);
		textName.setTextSize(30);
		textName.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		
		// 説明入力の設定
		textDescription.setTypeface(tf);
		textDescription.setTextSize(30);
		textDescription.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 300));
		
		//
		movieButton.setOnClickListener(this);
		
		// 登録ボタン
		submitButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		
		switch(v.getId()){
		
		// 登録ボタンが押された
		case R.id.register_button:
			// 結果を呼び出しもとActivityに返す
			intent.putExtra("item_name", textName.getText().toString());
			intent.putExtra("item_description", textDescription.getText().toString());
			intent.putExtra("item_image", fileName);
			setResult(RESULT_OK, intent);
			
			// Activityを終了
			finish();
			break;
			
		// movieボタンが押された
		case R.id.register_movie_button:
			File dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			Uri uriPath = Uri.fromFile(dir);
			
			intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);
			// intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1);
			startActivityForResult(intent, MOVIE_CAPTUER);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 
		switch(requestCode){
		case MOVIE_CAPTUER:
			
			if(resultCode == RESULT_OK){
				if(data != null){
					Uri moviePath= data.getData();
					movieView.setVideoURI(moviePath);
					
					movieView.start();
					
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
