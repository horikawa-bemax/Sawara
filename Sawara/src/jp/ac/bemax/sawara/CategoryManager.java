package jp.ac.bemax.sawara;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * CategoryとDBとの間に立って、両者を中継するクラス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class CategoryManager {
	private static CategoryManager manager;
	
	private CategoryManager(Context c){
		
	}
	
	public static CategoryManager newCategoryManager(Context c){
		if(manager == null){
			manager = new CategoryManager(c);
		}
		return manager;
	}
	
	public List<Category> getAllItems(){
		List<Category> list = new ArrayList<Category>();
		
		return list;
	}
}
