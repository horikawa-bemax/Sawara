package jp.ac.bemax.sawara;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TategakiView extends View {
	private String text = "TategakiView";
	private int font_size = 20;
	private int rows = 1;
	
	public TategakiView(Context context) {
		super(context);
		setFocusable(true);
	}
	
	public TategakiView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public TategakiView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int width = getWidth();
		int height = getHeight();
		
		char[] chars = text.toCharArray();
		
		int col = width / font_size;
		int row = height / font_size;
		
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.main));
		paint.setTextSize(font_size);
		paint.setAntiAlias(true);
		
		LOOP:
		for(int i=0; i<col && i<rows; i++){
			for(int j=0; j<row; j++){
				if(i*row+j >= text.length())
					break LOOP;

				canvas.drawText(""+chars[i*row+j], (rows-i-1)*font_size , (j+1)*font_size, paint);

			}
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getFont_size() {
		return font_size;
	}

	public void setFont_size(int font_size) {
		this.font_size = font_size;
	}
	
}
