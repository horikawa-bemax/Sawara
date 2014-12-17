package jp.ac.bemax.sawara;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 縦書きのテキストView
 * @author Masaaki Horikawa
 * 2014/09/30
 */
public class VTextView extends EditText{

    private static final int TOP_SPACE = 20;
    private static final int BOTTOM_SPACE = 20;
    private static final int FONT_SIZE = 50;

    private Typeface mFace;
    private Paint mPaint;
    private String mText = "";
    private int width;
    private int height;
    private InputMethodManager mManager;
    //private TextInputConnection mInputConnection;
    private Editable mEditable;
    //private boolean editing;
    
    public VTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFace = Typeface.createFromAsset(context.getAssets(),"HGRKK.TTC");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(FONT_SIZE);
        mPaint.setColor(Color.BLACK);
        mPaint.setTypeface(mFace);
        
        // 
        //mManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //mInputConnection = new TextInputConnection(this, false);

        mEditable = super.getEditableText();
        //editing = false;
        
        setFocusable(false);
        setFocusableInTouchMode(false);
    }
     
    @Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	width = w;
    	height = h;
    	Log.d("size",""+w+":"+h);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/*
    public void setText(String text) {
    	super.setText(text);
    	mPaint.setTextSize(FONT_SIZE);
        mText = getText().toString();
    }
	*/ 
    
    /**
     * テキストサイズつき、setText
     */
    public void setText(String text, int size){
    	mPaint.setTextSize(size);
    	setText(text);
    }
    
    /**
     * テキストサイズをセットする
     * @param size
     */
    public void setTextSize(int size){
    	mPaint.setTextSize(size);
    }

    /**
     * 
     */
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    /**
     * 描画
     */
    @Override
    public void onDraw(Canvas canvas) {
        float fontSpacing = mPaint.getFontSpacing();
        float lineSpacing = fontSpacing * 1.5f;
        float x = width - lineSpacing;
        float y = TOP_SPACE + fontSpacing * 1.0f;
        
        if(getText().length() > 0){
        	mText = getText().toString();
        }else{
        	if(getHint() != null)
        		mText = getHint().toString();
        }
        
        // 改行キーで切り分ける
        String[]ss = mText.split("\r\n|[\n\r\u2028\u2029\u0085]", 0);
        
        for(int j=0; j<ss.length; j++){
        	boolean newLine = false;
        	String[] s = ss[j].split("");
        	for (int i = 1; i <= s.length-1; i++) {
        		newLine = false;
	
        		CharSetting setting = CharSetting.getSetting(s[i]);
        		if (setting == null) {
        			// 文字設定がない場合、そのまま描画
        			canvas.drawText(s[i], x, y, mPaint);
        		} else {
        			// 文字設定が見つかったので、設定に従い描画
        			canvas.save();
        			canvas.rotate(setting.angle, x, y);
        			canvas.drawText(s[i], x + fontSpacing * setting.x, y + fontSpacing * setting.y, mPaint);
        			canvas.restore();
	            }
	
	            if (y + fontSpacing > height - BOTTOM_SPACE) {
	                // もう文字が入らない場合
	                newLine = true;
	            } else {
	                // まだ文字が入る場合
	                newLine = false;
	            }
	
	            if (newLine) {
	                // 改行処理
	                x -= lineSpacing;
	                y = TOP_SPACE + fontSpacing;
	            } else {
	                // 文字を送る
	                y += fontSpacing;
	            }
	        }
        	x -= lineSpacing;
        	y = TOP_SPACE + fontSpacing * 1.0f;
        }
    }

    /**
     * インプットコネクション（必要ないかも）
     */
	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		InputConnection ic = super.onCreateInputConnection(outAttrs);

		return ic; //new TextInputConnection(this, false);
	}

	/**
	 * インナークラス
	 * キー入力を受け付けるコネクター
	 * @author Masaaki Horikawa
	 * 2014/11/19
	 */
	class TextInputConnection extends BaseInputConnection{

		public TextInputConnection(View targetView, boolean fullEditor) {
			super(targetView, fullEditor);
		}

		@Override
		public Editable getEditable() {
			return new SpannableStringBuilder("");
		}

		@Override
		public boolean setComposingText(CharSequence text, int newCursorPosition) {
			boolean ret = super.setComposingText(text, newCursorPosition);
			mText = mEditable.toString() + text;
			//editing = true;
			invalidate();
			Log.d("編集中"+newCursorPosition, mEditable.toString());
			return ret;
		}

		@Override
		public boolean commitText(CharSequence text, int newCursorPosition) {
			boolean ret = super.commitText(text, newCursorPosition);
			mEditable.append(text);
			//mText = mEditable.toString();
			//editing = false;
			invalidate();
			Log.d("確定済み"+newCursorPosition, mText.toString());
			return ret;
		}
	}
}
