package jp.ac.bemax.sawara;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

/**
 * 画像からアイコンを作成するクラス
 *
 * @author Masaaki Horikawa
 * 2014/12/03
 */
public class IconFactory {
	static final int ICON_WIDTH = 320;
	static final int ICON_HEIGHT = 240;
	
	/**
	 * Articleのアイコンを作成する
	 * @param article 
	 * @return 作成されたアイコン。画像が無い場合はnull。
	 */
	public static Bitmap createArticleIcon(Article article){
		Bitmap icon = null;
		
		String[] path = article.getImagePaths();
		if(path != null && path.length > 0){
			Bitmap bmp = StrageManager.loadImage(path[0]);
			icon = createIconImage(bmp);
		}else{
			path = article.getMoviePaths();
			if(path != null && path.length > 0){
				Bitmap bmp = StrageManager.loadMovieThumbnail(path[0]);
				icon = createIconImage(bmp);
			}
		}
		
		return icon;
	}
	
	/**
	 * カテゴリーのアイコンを作成する
	 * @param paths アイコンの元画像のパスの配列
	 * @return アイコン画像
	 */
	public static Bitmap createCategoryIcon(String[] paths){
		Bitmap bmp = Bitmap.createBitmap(ICON_WIDTH, ICON_HEIGHT*3/2, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		
		Rect src = new Rect(0, 0, ICON_WIDTH, ICON_HEIGHT);
		Rect dst = null;
		int width = ICON_WIDTH/2;
		int height = ICON_HEIGHT/2;
		
		// 最大６つの画像を使ってアイコンを作成する
		for(int i=0; i<6 && i<paths.length; i++){
			Bitmap srcImage = createIconImage(BitmapFactory.decodeFile(paths[i]));
			int left = (i%2) * width;
			int top = (i/2) * height;
			dst = new Rect(left, top, left + width, top + height);
			canvas.drawBitmap(srcImage, src, dst, null);
		}
		
		return bmp;
	}
	
	/**
	 * 与えられた画像を、４：３比率の画像に加工する。
	 * @param bitmap 元の画像
	 * @return 加工後の画像
	 */
	public static Bitmap createIconImage(Bitmap bitmap){
		// アイコン画像の作成準備
		Bitmap icon = Bitmap.createBitmap(ICON_WIDTH, ICON_HEIGHT, Config.ARGB_8888);
		
		if(bitmap != null){
			Canvas c = new Canvas(icon);
			
			// 元画像の大きさ
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			
			// コピー元画像の範囲を設定
			int left = 0;
			int right = w;
			int top = 0;
			int bottom = h;
			if(w * 3 > h * 4){
				left = w / 2 - 4 * h / 6;
				right = w / 2 + 4 * h / 6;
			}else{
				top = h / 2 - 3 * w / 8;
				bottom = h / 2 + 3 * w / 8;
			}
			Rect src = new Rect(left, top, right, bottom);
			
			// コピー先の範囲を設定
			Rect dst = new Rect(0, 0, ICON_WIDTH, ICON_HEIGHT);
			
			// iconに、bitmapの一部をコピー
			c.drawBitmap(bitmap, src, dst, null);
		}
			
		return icon;
	}

}
