package jp.ac.bemax.sawara;

public class Thema {
	static int themaID;
	static int[] frames = { R.drawable.bat_frame,
							R.drawable.girl_frame};
	static int[] backgrounds = {R.drawable.gorst_back,
								R.drawable.heart_back};
	
	public static int getFrameResource(){
		return frames[themaID];
	}
	
	public static int getBackgroundResource(){
		return backgrounds[themaID];
	}
}
