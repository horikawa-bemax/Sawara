package jp.ac.bemax.sawara;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 言葉事典で取り扱う、１情報単位を表すクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class Article implements ListItem{
	private long rowid;
	private String name;
	private String description;
	private long modified;
	private String[] imagePaths;
	private String[] moviePaths;
	
	/**
	 * Article.javaコンストラクタ
	 */
	public Article(){
		
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(long id){
		rowid = id;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getModified() {
		return modified;
	}

	/**
	 * 
	 * @param modified
	 */
	public void setModified(long modified) {
		this.modified = modified;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * ROWIDを返す
	 * @return ROWID
	 */
	public long getId(){
		return rowid;
	}
	
	/**
	 * item_nameを返す
	 * @return アイテムの名前
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * descriptionを返す
	 * @return アイテムの詳細
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getImagePaths() {
		return imagePaths;
	}

	/**
	 * 
	 * @param imagePaths
	 */
	public void setImagePaths(String[] imagePaths) {
		this.imagePaths = imagePaths;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getMoviePaths() {
		return moviePaths;
	}

	/**
	 * 
	 * @param moviePaths
	 */
	public void setMoviePaths(String[] moviePaths) {
		this.moviePaths = moviePaths;
	}

	/**
	 * アイテムの画像イメージを返す
	 * @return アイテムの画像
	 */
	public Bitmap getImage(){
		Bitmap image = null;
		// 最初の画像イメージを返す
		if(imagePaths.length > 0){
			String imagePath = imagePaths[0];
			if(imagePath != null){
				image = BitmapFactory.decodeFile(imagePath);
			}
		}
		return image;
	}

	/**
	 * Articleのアイコンをクリックしたときの処理
	 */
	@Override
	public void clicked(Context context) {
		// 
		Intent intent = new Intent();
		intent.setClass(context, ArticleActivity.class);
		intent.putExtra("article_id", rowid);
		context.startActivity(intent);
	}
}
