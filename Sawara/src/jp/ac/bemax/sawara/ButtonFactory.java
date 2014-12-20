package jp.ac.bemax.sawara;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
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
	 * 新規登録ボタンのDrawableを返す
	 * @param context
	 * @return 新規登録ボタンのDrawable
	 */
	public static Drawable createNewButtonDrawable(Context context){
		// contextから色コードを取得。static変数に設定する
		setThemaColors(context);
		
		// ボタンイラストを読み込む
		Drawable image = context.getResources().getDrawable(R.drawable.new_button_image);
		
		// 背景Drawableと画像を合体
		Drawable[] layers = {createBackFrame(), image};
		Drawable layerDrawable = new LayerDrawable(layers);
		
		return layerDrawable;
	}
	
	/**
	 * 更新ボタンのDrawableを返す
	 * @param context
	 * @return 更新ボタンのDrawable
	 */
	public static Drawable createUpdateButtonDrawable(Context context){
		// contextから色コードを取得。static変数に設定する
		setThemaColors(context);
		
		// ボタンイラストを読み込む
		Drawable image = context.getResources().getDrawable(R.drawable.album_image);
		
		// 背景Drawableと画像を合体
		Drawable[] layers = {createBackFrame(), image};
		Drawable layerDrawable = new LayerDrawable(layers);
		
		return layerDrawable;
	}
	
	/**
	 * 設定ボタンのDrawableを返す
	 * @param context
	 * @return 設定ボタンのDrawable
	 */
	public static Drawable createSettingButtonDrawable(Context context){
		// contextから色コードを取得。static変数に設定する
		setThemaColors(context);
		
		// ボタンイラストを読み込む
		Drawable image = context.getResources().getDrawable(R.drawable.setting_button_image);
		
		// 背景Drawableと画像を合体
		Drawable[] layers = {createBackFrame(), image};
		Drawable layerDrawable = new LayerDrawable(layers);
		
		return layerDrawable;
	}
	
	/**
	 * 戻るボタンのDrawableを返す
	 * @param context
	 * @return 戻るボタンのDrawable
	 */
	public static Drawable createReturnButtonDrawable(Context context){
		// contextから色コードを取得。static変数に設定する
		setThemaColors(context);
		
		// ボタンイラストを読み込む
		Drawable image = context.getResources().getDrawable(R.drawable.return_button_image);
		
		// 背景Drawableと画像を合体
		Drawable[] layers = {createBackFrame(), image};
		Drawable layerDrawable = new LayerDrawable(layers);
		
		return layerDrawable;
	}
	
	/**
	 * 決定ボタンのDrawableを返す
	 * @param context
	 * @return 決定ボタンのDrawable
	 */
	public static Drawable createRegistButtonDrawable(Context context){
		// contextから色コードを取得。static変数に設定する
		setThemaColors(context);
		
		// ボタンイラストを読み込む
		Drawable image = context.getResources().getDrawable(R.drawable.regist_button_image);
		
		// 背景Drawableと画像を合体
		Drawable[] layers = {createBackFrame(), image};
		Drawable layerDrawable = new LayerDrawable(layers);
		
		return layerDrawable;
	}
	
	/**
	 * アルバムボタンのDrawableを返す
	 * @param context
	 * @return アルバムボタンのDrawable
	 */
	public static Drawable createAlbamButtonDrawable(Context context){
		// contextから色コードを取得。static変数に設定する
		setThemaColors(context);
		
		// ボタンイラストを読み込む
		Drawable image = context.getResources().getDrawable(R.drawable.album_image);
		
		// 背景Drawableと画像を合体
		Drawable[] layers = {createBackFrame(), image};
		Drawable layerDrawable = new LayerDrawable(layers);
		
		return layerDrawable;
	}
	
	/**
	 * ムービーボタンのDrawableを返す
	 * @param context
	 * @return ムービーボタンのDrawable
	 */
	public static Drawable createMovieButtonDrawable(Context context){
		// contextから色コードを取得。static変数に設定する
		setThemaColors(context);
		
		// ボタンイラストを読み込む
		Drawable image = context.getResources().getDrawable(R.drawable.movie_image);
		
		// 背景Drawableと画像を合体
		Drawable[] layers = {createBackFrame(), image};
		Drawable layerDrawable = new LayerDrawable(layers);
		
		return layerDrawable;
	}
	
	/**
	 * 写真ボタンのDrawableを返す
	 * @param context
	 * @return 写真ボタンのDrawable
	 */
	public static Drawable createPhotoButtonDrawable(Context context){
		// contextから色コードを取得。static変数に設定する
		setThemaColors(context);
		
		// ボタンイラストを読み込む
		Drawable image = context.getResources().getDrawable(R.drawable.camera_image);
		
		// 背景Drawableと画像を合体
		Drawable[] layers = {createBackFrame(), image};
		Drawable layerDrawable = new LayerDrawable(layers);
		
		return layerDrawable;
	}
	
	/**
	 * ボタンの枠画像を作成する
	 * @return 枠画像のDrawable
	 */
	public static Drawable createBackFrame(){
		float[] outerRadii = new float[]{10,10,10,10,10,10,10,10};
		RectF insetF = new RectF(10,10,10,10);
		Rect inset = new Rect(10,10,10,10);
		float[] innerRadii = new float[]{10,10,10,10,10,10,10,10}; 
		
		// 枠線
		Shape lineShape = new RoundRectShape(outerRadii, insetF, innerRadii);
		ShapeDrawable line = new ShapeDrawable(lineShape);
		line.setPadding(inset);
		line.getPaint().setColor(mainColor);
		//
		Shape backShape = new RoundRectShape(outerRadii, null, null);
		ShapeDrawable back = new ShapeDrawable(backShape);
		back.getPaint().setColor(baseColor);
		//
		Drawable[] backLayers = {line, back};
		LayerDrawable backLayer = new LayerDrawable(backLayers);
		
		// 
		Shape touchBackShape = new RoundRectShape(outerRadii, null, null);
		ShapeDrawable touchBack = new ShapeDrawable(touchBackShape);
		touchBack.getPaint().setColor(accentColor);
		Drawable[] touchBackLayers = {line, touchBack};
		LayerDrawable touchBackLayer = new LayerDrawable(touchBackLayers);
		
		// 状態Drawable作成
		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, touchBackLayer);
		stateListDrawable.addState(new int[0], backLayer);
		
		return stateListDrawable;
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static Drawable getThemaBackground(Context context){
		// コンテキストからテーマを取得
		Resources.Theme thema = context.getTheme();
		
		TypedValue backgroundDrawableValue = new TypedValue();
		thema.resolveAttribute(R.attr.mainBack, backgroundDrawableValue, true);
		Drawable backgroundDrawable = context.getResources().getDrawable(backgroundDrawableValue.resourceId);
		
		return backgroundDrawable;
	}
	
	public static Drawable getThemaFrame(Context context){
		// コンテキストからテーマを取得
		Resources.Theme thema = context.getTheme();
		
		TypedValue frameDrawableValue = new TypedValue();
		thema.resolveAttribute(R.attr.frameBack, frameDrawableValue, true);
		Drawable frameDrawable = context.getResources().getDrawable(frameDrawableValue.resourceId);
		
		return frameDrawable;
	}
	
	/**
	 * contextのテーマに沿って、３つの色変数に値を設定する
	 * @param context
	 */
	public static void setThemaColors(Context context){
		// コンテキストからテーマを取得
		Resources.Theme thema = context.getTheme();
		
		// テーマデータの受け皿
		TypedValue baseColorValue = new TypedValue();
		TypedValue mainColorValue = new TypedValue();
		TypedValue accentColorValue = new TypedValue();
		
		// テーマのデータを変数にセットする
		thema.resolveAttribute(R.attr.baseColor, baseColorValue, true);
		thema.resolveAttribute(R.attr.mainColor, mainColorValue, true);
		thema.resolveAttribute(R.attr.accentColor, accentColorValue, true);

		// テーマの値から、色コードを取得
		baseColor = context.getResources().getColor(baseColorValue.resourceId);
		mainColor = context.getResources().getColor(mainColorValue.resourceId);
		accentColor = context.getResources().getColor(accentColorValue.resourceId);

	}
}
