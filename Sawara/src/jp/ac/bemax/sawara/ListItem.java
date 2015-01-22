package jp.ac.bemax.sawara;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.io.File;
import java.io.Serializable;
import java.net.ContentHandler;

/**
 * GridViewに表示するアイテムに実装するインターフェイス
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class ListItem implements Serializable{
    static final int IMAGE_SIZE = 480;
    private long id;
    private String name;
    private String path;
    private long type;

    public ListItem(long id, String name, String path, long type){
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
    }

	public long getId(){ return id;};

    public String getName(){return name;};

    public Bitmap getIcon(){
        Bitmap icon = null;
        if(type == Media.PHOTO){
            // サイズを確定するための仮読み込み
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(path, opt);

            // 読み込み時の精度を決定
            int size = opt.outWidth;
            if (opt.outHeight > size) {
                size = opt.outHeight;
            }
            opt.inSampleSize = size / IMAGE_SIZE;

            // 本格的に画像を読み込む
            opt.inJustDecodeBounds = false;
            icon = BitmapFactory.decodeFile(path, opt);
        }else if(type == Media.MOVIE){
            // 動画のサムネイル画像を取得する
            icon = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        }
        return icon;
    }
}
