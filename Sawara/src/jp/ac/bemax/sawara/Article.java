package jp.ac.bemax.sawara;

import android.content.Context;
import android.content.Intent;

/**
 * 言葉事典で取り扱う、１情報単位を表すクラス
 * @author Masaaki Horikawa
 * 2014/09/05
 */
public class Article implements ListItem{
	private long rowid;
	private String name;
	private String description;
	private long position;
	private long modified;
	private String[] imagePaths;
	private String[] moviePaths;
	private long[] categoryIds;
	private String iconPath;
	
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
		return this.imagePaths;
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
	 * このArticleが持つCategoryIDの配列を返す。
	 * @param manager
	 * @return
	 */
	public long[] getCategories(CategoryManager manager){
		long[] cIds = null;
		
		return cIds;
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

	public long[] getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(long[] categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

}
