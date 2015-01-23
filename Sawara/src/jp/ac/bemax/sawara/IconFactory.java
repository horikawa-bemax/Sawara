package jp.ac.bemax.sawara;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 画像からアイコンを作成するクラス
 *
 * @author Masaaki Horikawa
 * 2014/12/03
 */
public class IconFactory {
	static final int ICON_WIDTH = 320;
	static final int ICON_HEIGHT = 240;
	static final int IMAGE_SIZE = 480;
	/**
	 * Articleのアイコンを作成する
	 * @return 作成されたアイコン。画像が無い場合はnull。

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
*/

    public static boolean saveIcon(Context context, String fileName, Bitmap image){
        boolean compress = false;
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(new File(dir,fileName));
            compress = image.compress(Bitmap.CompressFormat.PNG, 100, fos);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(fos!=null)
                    fos.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return compress;
    }

    public static Bitmap makeBitmap(Context context, String fileName, long mediaType){
        Bitmap image = null;

        if(mediaType == Media.PHOTO) {
            //画像ファイルを指定
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir, fileName);
            // サイズを確定するための仮読み込み
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(imageFile.getPath(), opt);

            // 読み込み時の精度を決定
            int size = opt.outWidth;
            if (opt.outHeight > size) {
                size = opt.outHeight;
            }
            opt.inSampleSize = size / IMAGE_SIZE;

            // 本格的に画像を読み込む
            opt.inJustDecodeBounds = false;
            image = BitmapFactory.decodeFile(imageFile.getPath(), opt);
        }
        if(mediaType == Media.MOVIE){
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            File movieFile = new File(dir, fileName);
            // 動画のサムネイル画像を取得する
            image = ThumbnailUtils.createVideoThumbnail(movieFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        }
        return image;
    }

	public static Bitmap getIcon(Context context, String fileName, long mediaType){
        Bitmap bitmap = makeBitmap(context, fileName, mediaType);
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
