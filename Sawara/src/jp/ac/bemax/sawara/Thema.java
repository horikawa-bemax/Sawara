package jp.ac.bemax.sawara;

public class Thema {
	static int themaID;
	static int[] frames = { R.drawable.theme_gorst_frame,
							R.drawable.theme_heart_frame};
	static int[] backgrounds = {R.drawable.theme_gorst_back_drawable,
								R.drawable.theme_heart_back_drawable};
	
	public static int getFrameResource(){
		return frames[themaID];
	}
	
	public static int getBackgroundResource(){
		return backgrounds[themaID];
	}
}
