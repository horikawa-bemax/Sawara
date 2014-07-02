package jp.ac.bemax.sawara;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class TitleActivity extends Activity {
	private TextView titleView;
	private Button searchButton, registerButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* レイアウトを適用 */
		setContentView(R.layout.title);
		
		/* ウィジェットを登録 */
		titleView = (TextView)findViewById(R.id.title_text);
		searchButton = (Button)findViewById(R.id.search_button);
		registerButton = (Button)findViewById(R.id.register_button);
		
		/* フォントを登録 */
		Typeface yasashisa_B = Typeface.createFromAsset(getAssets(),"yasashisa_bold.ttf");
		Typeface yasashisa = Typeface.createFromAsset(getAssets(),"yasashisa.ttf");
		
		/* ウィジェットにフォントを適用する */
		titleView.setTypeface(yasashisa_B);
		searchButton.setTypeface(yasashisa);
		registerButton.setTypeface(yasashisa);
		
	}
}
