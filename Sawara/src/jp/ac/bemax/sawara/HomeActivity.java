package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

/**
 * ホーム画面のアクティビティ
 * @author Masaaki Horikawa
 * 2014/07/02
 */
public class HomeActivity extends Activity{
	private GridView gView;
	private GridAdapter gAdapter;
	private ArrayList<Item> items;
	private ItemManager iManager;
	private int itemHeight; 
	private File capturedFile;
	
	private final static int CAPTUER_IMAGE = 100;
	private final static int NEW_ITEM = 200;
	
	/* (非 Javadoc)
	 * コンストラクタ
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		// ウィジェットを登録 
		gView = (GridView)findViewById(R.id.gridView);
		
		// ディスプレイサイズを取得する
		Display display = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);
		itemHeight = p.y / 2;
		
		// 初期化
		items = new ArrayList<Item>();
		iManager = ItemManager.newItemManager(this);	
		
		// 指定したレイアウトでItemを並べる
		gAdapter = new GridAdapter(this, R.layout.list_item, iManager.getAllItems());
		gView.setAdapter(gAdapter);
		
		gView.setOnItemClickListener(gAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//
		menu.add(0, 1, 0, "さくせい");
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// 写真を撮る
		if(item.getItemId()==1){
			// 保存先のパスとファイル名を指定
			File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			String filename = System.currentTimeMillis() + ".jpg";
	        capturedFile = new File( dir, filename );
			Uri fileUri = Uri.fromFile(capturedFile);
			
			// カメラアプリを呼び出すインテントを作成
			Intent picIntent = new Intent();
			picIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			picIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			picIntent.addCategory(Intent.CATEGORY_DEFAULT);
			
			// カメラアプリを起動
			startActivityForResult(picIntent, CAPTUER_IMAGE);
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case CAPTUER_IMAGE:
			// カメラアプリからの結果を処理
			if(requestCode == CAPTUER_IMAGE){
				
				if(resultCode == RESULT_OK){
					
					// 画像保存先のパスを取得
					if(data != null){
						String path = data.getData().getPath();
						if(!path.equals(capturedFile)){
							// captureFileに保存し直し
							FileOutputStream fos = null;
							try {
								fos = new FileOutputStream(capturedFile);
								Bitmap bmp = BitmapFactory.decodeFile(path);
								bmp.compress(CompressFormat.JPEG, 100, fos);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}finally{
								try {
									fos.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
					
					// RegisterIntentを呼び出す
					Intent intent = new Intent(this, jp.ac.bemax.sawara.RegisterActivity.class);
					intent.putExtra("image_uri", capturedFile.getPath());
					startActivityForResult(intent, NEW_ITEM);
				}else{
					Log.d("result", "canceled");
				}
			}
			break;
		
		case NEW_ITEM:
			// 新規アイテム登録
			ContentValues cv = new ContentValues();
			cv.put("item_name", data.getStringExtra("item_name"));
			cv.put("item_description", data.getStringExtra("item_description"));
			cv.put("item_image", data.getStringExtra("item_image"));
			Item item = iManager.newItem(cv);
			gAdapter.add(item);
			gAdapter.notifyDataSetChanged();
			
			break;
		}
	}
}
