package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 登録画面
 * @author Masaaki Horikawa
 * 2014/07/23
 */
public class RegisterActivity extends Activity implements OnClickListener{
	private final byte TAKE_PICTURE = 0;
	private final byte TAKE_MOVIE = 1;
	
	private Bitmap picture;
	private File pictureFile;
	private ImageView imageView;
	
	private Item item;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		/* レイアウトの紐付け */
		imageView = (ImageView)findViewById(R.id.register_image);
		
		// インテントからデータを受け取る
		Intent intent = getIntent();
		String path = intent.getStringExtra("image_uri");
	
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
		
		/* ボタンにリスナを登録 */

		
		/* アイテム */

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		
		/*
		switch(v.getId()){
		// 写真ボタン
		case R.id.take_picture_button:
			
			// 端末にある静止画撮影アクティビティを呼び出す
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			
			// 対応するアクティビティが存在する
			if(intent.resolveActivity(getPackageManager()) != null){
				
				// 保存するファイル名を決定
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				String imageFileName = "JPEG_" + timeStamp + "_";
				
				// 外部ストレージのURLをゲット
				File dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
				
				try {
					//呼び出したインテントが、画像を保存するファイルを指定
					pictureFile = File.createTempFile(imageFileName, ".jpg", dir);
				
					// 画像ファイルの作成に成功した場合
					if(pictureFile != null){
						// 保存先ファイルを指定して、インテントを呼ぶ
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
						startActivityForResult(intent, TAKE_PICTURE);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		// ムービーボタン
		case R.id.take_movie_button:
			// 呼び出すインテントを指定
			intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			
			
			startActivityForResult(intent, TAKE_MOVIE);
			break;
		case R.id.register_egister_button:
			// データベースに登録
			Editable edit = itemName.getText();
			
			// 名前があって、画像が読み込まれているならば
			if(edit.length() > 0 && (item.getImage_url().length() > 0 || item.getMovie_url().length() > 0)){
				// itemに名前を設定する
				item.setName(itemName.getText().toString());
				
				// データベースに新規追加する
				item.newItem();
				
				// itemをリセットする 
				item = new Item(this);
				itemName.setText("");
				pictureView.setImageResource(R.drawable.dummy_image);
				
				// 登録完了のメッセージ
				Toast.makeText(this, "登録しました", Toast.LENGTH_LONG).show();
				
			}else{
				
				if(edit.length() == 0){
					Toast.makeText(this, "名前がありません", Toast.LENGTH_LONG).show();
				}
				
				if(item.getImage_url().length() == 0 && item.getMovie_url().length() == 0){
					Toast.makeText(this, "画像か動画を指定してください", Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
		*/
	}

	/* (非 Javadoc)
	 * 呼び出し先のインテントからの返信を受け取るメソッド
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		FileInputStream inputStream;
		BitmapFactory.Options bitmapOptions;
		
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode){
		// 静止画撮影インテントからの返信を処理
		case TAKE_PICTURE:
			
			/* 撮影に成功している場合 */
			if(resultCode == RESULT_OK){
				
				try {
					// 画像保存先のInputStreamを取得
					inputStream = new FileInputStream(pictureFile);

					/* 画像読み込まず、サイズだけを取得するオプションを指定 */
					bitmapOptions = new BitmapFactory.Options();
					bitmapOptions.inJustDecodeBounds = true;
					
					/* 写真を取り込まずにサイズだけを取得 */
					BitmapFactory.decodeFile(pictureFile.getPath(), bitmapOptions);
					
					/* 画像を取り込むオプションを指定 */
					bitmapOptions.inJustDecodeBounds = false;
					
					/* 画像を取り込む際の精度を求める */
					if(bitmapOptions.outHeight > bitmapOptions.outWidth){
						//bitmapOptions.inSampleSize =  bitmapOptions.outHeight / pictureView.getHeight();
					}else{
						//bitmapOptions.inSampleSize = bitmapOptions.outWidth / pictureView.getWidth();
					}
					
					/* ここから本格的に画像取り込み */
					picture = BitmapFactory.decodeFile(pictureFile.getPath(), bitmapOptions);
					//pictureView.setImageBitmap(picture);
					
					/* ItemインスタンスにイメージURLを登録 */
					//item.setImage_url(pictureFile.getName());
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		// 動画撮影インテントからの返信を処理
		case TAKE_MOVIE:
			

			break;
		}
	}
}
