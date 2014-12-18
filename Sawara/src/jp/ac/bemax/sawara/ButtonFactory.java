package jp.ac.bemax.sawara;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;


/**
 * ボタンの画像を生成するクラス
 * @author horikawa
 * 2014/12/16
 */
public class ButtonFactory {
	// テーマから取得する値
	static int  baseColor;
	static int mainColor;
	static int accentColor;

	/**
	 * 新規登録ボタンを生成する
	 * @param context
	 * @return 新規登録ボタンのDrawable
	 */
	public static Drawable createNewButtonDrawable(Context context){
		
		setThemaColors(context);
		
		// ボタンイラストを読み込む
		Drawable image = context.getResources().getDrawable(R.drawable.new_button_image);
		
		Drawable[] layers = {createBackFrame(), image};
		LayerDrawable lDrawable = new LayerDrawable(layers);
		
		return lDrawable;
	}
	
	public static Drawable createUpdateButtonDrawable(){
		
		return null;
	}
	
	public static Drawable createSettingButtonDrawable(){
		
		return null;
	}
	
	public static Drawable createBackFrame(){

		// 通常時の枠線を作成
		GradientDrawable back = new GradientDrawable();
		back.setStroke(10, mainColor);
		back.setCornerRadius(10);
		back.setColor(baseColor);
		
		// クリック時の枠線を作成
		GradientDrawable touchBack = new GradientDrawable();
		touchBack.setStroke(10,  mainColor);
		touchBack.setCornerRadius(10);
		touchBack.setColor(accentColor);
		
		StateListDrawable sDrawable = new StateListDrawable();
		sDrawable.addState(new int[]{android.R.attr.state_pressed}, touchBack);
		sDrawable.addState(new int[0], back);
		
		return sDrawable;
	}
	
	public static void setThemaColors(Context context){
		Resources.Theme thema = context.getTheme();
		
		TypedValue baseColorValue = new TypedValue();
		TypedValue mainColorValue = new TypedValue();
		TypedValue accentColorValue = new TypedValue();
		
		// テーマのデータを変数にセットする
		thema.resolveAttribute(R.attr.baseColor, baseColorValue, true);
		thema.resolveAttribute(R.attr.mainColor, mainColorValue, true);
		thema.resolveAttribute(R.attr.accentColor, accentColorValue, true);
		
		baseColor = context.getResources().getColor(baseColorValue.resourceId);
		mainColor = context.getResources().getColor(mainColorValue.resourceId);
		accentColor = context.getResources().getColor(accentColorValue.resourceId);
	}
}
