package jp.ac.bemax.sawara;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SearchActivity extends Activity{
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		/* データベースへのアクセスアダプター */
		SawaraDBAdapter sdb = new SawaraDBAdapter(this);
		
		/* listViewへの紐付け */
		listView = (ListView)findViewById(R.id.listView);

		/* データベースからグループリストを読み込んでlistViewに反映 */
		ArrayAdapter<String> adp = new ArrayAdapter<String>(
				this,android.R.layout.simple_list_item_1, sdb.getGroupNameList());
		
		listView.setAdapter(adp);
	}
}
