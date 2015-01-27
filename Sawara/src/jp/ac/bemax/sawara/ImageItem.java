package jp.ac.bemax.sawara;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;

/**
 * Created by 雅章 on 2015/01/23.
 */
public class ImageItem {
    private long mediaId;
    private long mediaType;
    private String fileName;
	private Bitmap icon;
    private Context context;

    public ImageItem(Context context, String fileName, long mediaType, Bitmap icon){
        this.mediaId = -1;
        this.context = context;
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.icon = icon;
    }

    public ImageItem(Context context, long mediaId, String fileName, long mediaType, Bitmap icon){
        this(context, fileName, mediaType, icon);
        this.mediaId = mediaId;
    }

	public String getFilePath(){
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(mediaType == Media.MOVIE){
            dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        }
		return new File(dir, fileName).getPath();
	}

    public String getFileName(){
        return fileName;
    }

	public Bitmap getIcon(){
		return icon;
	}

    public long getId(){
        return this.mediaId;
    }

    public long getType(){
        return this.mediaType;
    }
}
