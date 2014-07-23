package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class RegisterActivity extends Activity implements OnClickListener{
	private final int TAKE_PICTURE = 0;
	
	private ImageView pictureView;
	private Button takePictureBtn;
	private Button searchPictureBtn;
	
	private Bitmap picture;
	private File pictureFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		pictureView = (ImageView)findViewById(R.id.picture_image_view);
		takePictureBtn = (Button)findViewById(R.id.take_picture_button);
		searchPictureBtn = (Button)findViewById(R.id.search_picture_button);
		
		takePictureBtn.setOnClickListener(this);
		searchPictureBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		
		switch(v.getId()){
		case R.id.take_picture_button:	
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			
			if(intent.resolveActivity(getPackageManager()) != null){
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				String imageFileName = "JPEG_" + timeStamp + "_";
				
				File dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
				
				try {
					pictureFile = File.createTempFile(imageFileName, ".jpg", dir);
				
					if(pictureFile != null){
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
						startActivityForResult(intent, TAKE_PICTURE);
					}
					
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode){
		case TAKE_PICTURE:
			if(resultCode == RESULT_OK){
				try {
					FileInputStream is = new FileInputStream(pictureFile);

					BitmapFactory.Options op = new BitmapFactory.Options();
					op.inJustDecodeBounds = true;
					
					/* 写真を取り込まずにサイズだけを取得 */
					BitmapFactory.decodeFile(pictureFile.getPath(), op);
					
					op.inJustDecodeBounds = false;
					
					if(op.outHeight > op.outWidth){
						op.inSampleSize =  op.outHeight / pictureView.getHeight();
					}else{
						op.inSampleSize = op.outWidth / pictureView.getWidth();
					}
					
					/* ここから本格的に取り込み */
					picture = BitmapFactory.decodeFile(pictureFile.getPath(), op);
					pictureView.setImageBitmap(picture);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
