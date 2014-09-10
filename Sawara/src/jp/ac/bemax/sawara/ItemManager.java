package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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
/**
 *
 * @author Masaaki Horikawa
 * 2014/09/06
 */
/**
 *
 * @author Masaaki Horikawa
 * 2014/09/06
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
		
		// Itemクラスのstatic変数を初期化
		helper = sdba.getHelper();
		File imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		Item.init(helper, imageDir);
	}
	
	/*
	 * アイテムマネージャを新規作成する
	 */
	public static ItemManager newItemManager(Context context){
		if(iManager == null){
			iManager = new ItemManager(context);
		}
		return iManager;
	}
	
	/*
	 * アイテムを新規作成する
	 */
	public Item newItem(ContentValues values){
		Item resultItem = null;
		// 
		SQLiteDatabase db = null;
		try{
			db = helper.getWritableDatabase();
			long rowId = db.insertOrThrow("item_table", null, values);
			resultItem = new Item(rowId);
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			db.close();
		}
		return resultItem;
	}
	
	/*
	 * DB上のすべてのアイテムを取得して、そのリストを返す
	 * @return アイテムのリスト
	 */
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
	
	public void dump(){
		List<Item> list = getAllItems();
		for(Item item: list){
			Log.d("item("+item.getId()+")", item.getName());
		}
	}
}
