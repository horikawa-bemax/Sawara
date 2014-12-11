package jp.ac.bemax.sawara;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Articleを表示するアクティビティ
 * @author Masaaki Horikawa
 * 2014/09/10
 */
public class ArticleActivity extends Activity {
	private ImageView itemImage;
	private VTextView itemDescription;
	private VTextView itemName;
	private SQLiteOpenHelper helper;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
			
	}
	
	
}
