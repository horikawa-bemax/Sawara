package jp.ac.bemax.sawara;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;


/**
 * ボタンの画像を生成するクラス
 * @author horikawa
 * 2014/12/16
 */
public class ButtonFactory {
	// テーマから取得する値
	static TypedValue baseColor = new TypedValue();
	static TypedValue mainColor = new TypedValue();
	static TypedValue accentColor = new TypedValue();

	/**
	 * 新規登録ボタンを生成する
	 * @param context
	 * @return 新規登録ボタンのDrawable
	 */
	public static Drawable createNewButtonDrawable(Context context){
		Resources.Theme thema = context.getTheme();
		
		thema.resolveAttribute(R.attr.baseColor, baseColor, true);
		thema.resolveAttribute(R.attr.mainColor, mainColor, true);
		thema.resolveAttribute(R.attr.accentColor, accentColor, true);
		
		Drawable image = context.getResources().getDrawable(R.drawable.new_button_image);
		
		GradientDrawable back = new GradientDrawable();
		back.setStroke(10, context.getResources().getColor(mainColor.resourceId));
		back.setCornerRadius(10);
		back.setColor(context.getResources().getColor(baseColor.resourceId));
		
		GradientDrawable touchBack = new GradientDrawable();
		touchBack.setStroke(10,  context.getResources().getColor(mainColor.resourceId));
		touchBack.setCornerRadius(10);
		touchBack.setColor(context.getResources().getColor(accentColor.resourceId));

		Drawable[] layer = {back, image};
		LayerDrawable layerDrawable = new LayerDrawable(layer);
		
		return layerDrawable;
	}
	
	public static Drawable createUpdateButtonDrawable(){
		
		return null;
	}
	
	public static Drawable createSettingButtonDrawable(){
		
		return null;
	}
}
