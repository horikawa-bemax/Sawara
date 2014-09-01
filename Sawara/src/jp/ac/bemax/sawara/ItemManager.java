package jp.ac.bemax.sawara;

import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
	
	public Item newItem(String name, String imageUrl, String movieUrl){
		
		ContentValues values = new ContentValues();
		values.put("item_name", name);
		values.put("item_image", imageUrl);
		values.put("item_movie", movieUrl);
		
		SQLiteDatabase db = helper.getWritableDatabase();
		long rowId = db.insert("item_table", null, values);
		db.close();
		
		return new Item(rowId, helper);
	}
}
