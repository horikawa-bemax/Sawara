package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

/**
 * アイテムをマネジメントするクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class ItemManager {
	private Context context;
	private SQLiteOpenHelper helper;
	private SawaraDBAdapter sdba;
	private static ItemManager iManager;
	
	private ItemManager(Context context){
		this.context = context;
		
		// sawaraDBアダプタを登録
		sdba = new SawaraDBAdapter(context);
		helper = sdba.getHelper();
		Item.setHelper(helper);
	}
	
	public static ItemManager newItemManager(Context context){
		if(iManager == null){
			iManager = new ItemManager(context);
		}
		return iManager;
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

		return new Item(rowId);
	}
	
	public List<Item> getAllItems(){
		List<Item> items = new ArrayList<Item>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from item_table", null);
		while(cr.moveToNext()){
			items.add(new Item(cr.getLong(0)));
		}
		db.close();
		return items;
	}
}
