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
	private String iconPath;
    private Context context;

    public ImageItem(Context context, String fileName, long mediaType){
        this.mediaId = -1;
        this.context = context;
        this.fileName = fileName;
        this.mediaType = mediaType;
    }

    public ImageItem(Context context, long mediaId, String fileName, long mediaType, String iconPath){
        this(context, fileName, mediaType);
        this.mediaId = mediaId;
		this.iconPath = iconPath;
    }

	public String getFileName(){
		return fileName;
	}

	public String getIconPath(){
		return iconPath;
	}

    public Bitmap getIcon(){
        Bitmap icon = null;
        if(mediaId == -1){
            icon = IconFactory.getIcon(context, fileName, mediaType);
        }else{
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File iconFile = new File(dir, iconPath);
            icon = BitmapFactory.decodeFile(iconFile.getPath());
        }
        return icon;
    }

    public long getId(){
        return this.mediaId;
    }

    public long getType(){
        return this.mediaType;
    }
}
