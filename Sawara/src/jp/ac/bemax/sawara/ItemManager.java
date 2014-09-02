package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class ItemManager {
	private Context context;
	private SQLiteOpenHelper helper;
	private SawaraDBAdapter sdba;
	
	private ItemManager(Context context){
		this.context = context;
		
		// sawaraDBアダプタを登録
		sdba = new SawaraDBAdapter(context);
		helper = sdba.getHelper();
	}
	
	public static ItemManager newItemManager(Context context){
		return new ItemManager(context);
	}
	
	public Item newItem(String name, Bitmap image, String movieUrl){
		// 画像を保存する
		File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		String imageFileName = name + ".jpg";
		try{
			// 画像をアプリの記憶領域に書き込む
			FileOutputStream fos = new FileOutputStream(new File(dir, imageFileName));
			image.compress(CompressFormat.JPEG, 100, fos);
			fos.close();
		}catch(Exception e){ e.printStackTrace();}
		
		// DBに登録
		ContentValues values = new ContentValues();
		values.put("item_name", name);
		values.put("item_image", imageFileName);
		values.put("item_movie", movieUrl);
		
		SQLiteDatabase db = helper.getWritableDatabase();
		long rowId = db.insert("item_table", null, values);
		db.close();
		
		return new Item(rowId, helper);
	}
}
