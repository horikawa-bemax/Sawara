package jp.ac.bemax.sawara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * ストレージとのやり取りをするクラス
 *
 * @author Masaaki Horikawa
 * 2014/12/02
 */
public class StrageManager {
	private File pictureDir;
	private File movieDir;
	private File iconDir;
	private Context mContext;
	
	public StrageManager(Context context){
		mContext = context;
		pictureDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		movieDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
		iconDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
	}

	/**
	 * イメージ画像をストレージから呼び出す
	 * @param path 画像の保存先のパス
	 * @return 画像
	 */
	public static Bitmap loadImage(String path){
		Bitmap image = BitmapFactory.decodeFile(path);
		return image;
	}
	
	/**
	 * 動画のサムネイル画像を返す
	 * @param path
	 * @return
	 */
	public static Bitmap loadMovieThumbnail(String path){
		Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
		return thumbnail;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap loadIcon(String path){
		Bitmap icon = BitmapFactory.decodeFile(path);
		return icon;
	}
	
	/**
	 * icon画像をストレージに保存する。
	 * @param icon アイコン画像
	 * @return 保存先のパス
	 */
	public String saveIcon(Bitmap icon){
		// 透明色を使うので、PNG形式で保存する
		String fName = "icon_" + System.currentTimeMillis() + ".png";
		File iconFile = new File(iconDir, fName);
		String path = iconFile.getPath();
		
		FileOutputStream fos = null;
		try{
			// アイコンファイルをストレージに保存する
			fos = new FileOutputStream(iconFile);
			icon.compress(CompressFormat.PNG, 100, fos);
			
		}catch(IOException e){
			// 保存に失敗した場合は、nullを返す。
			path = null;
			e.printStackTrace();
			
		}finally{
			try{
				fos.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		return path;
	}
}
