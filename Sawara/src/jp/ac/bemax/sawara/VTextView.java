package jp.ac.bemax.sawara;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.input.InputManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
public class VTextView extends View {

    private static final int TOP_SPACE = 20;
    private static final int BOTTOM_SPACE = 20;
    private static final int FONT_SIZE = 50;

    private Typeface mFace;
    private Paint mPaint;
    private String mText = "";
    private int width;
    private int height;
    private InputMethodManager mManager;
    private TextInputConnection mInputConnection;
    private StringBuffer mStringBuffer;
    
    public VTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFace = Typeface.createFromAsset(context.getAssets(),"HGRKK.TTC");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(FONT_SIZE);
        mPaint.setColor(Color.BLACK);
        mPaint.setTypeface(mFace);
        
        mManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputConnection = new TextInputConnection(this, false);
        mStringBuffer = new StringBuffer("");
        
        setFocusable(true);
        setFocusableInTouchMode(false);
    }

    public void setText(String text) {
    	mPaint.setTextSize(FONT_SIZE);
        mText = text;
    }
    
    public void setText(String text, int size){
    	mPaint.setTextSize(size);
    	mText = text;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        width = getWidth();
        height = getHeight();
    }

    @Override
    public void onDraw(Canvas canvas) {
        float fontSpacing = mPaint.getFontSpacing();
        float lineSpacing = fontSpacing * 1.5f;
        float x = width - lineSpacing;
        float y = TOP_SPACE + fontSpacing * 1.0f;
        
        String[] s = mText.split("");
        boolean newLine = false;

        for (int i = 1; i <= mText.length(); i++) {
            newLine = false;

            CharSetting setting = CharSetting.getSetting(s[i]);
            if (setting == null) {
                // 文字設定がない場合、そのまま描画
                canvas.drawText(s[i], x, y, mPaint);
            } else {
                // 文字設定が見つかったので、設定に従い描画
                canvas.save();
                canvas.rotate(setting.angle, x, y);
                canvas.drawText(s[i], x + fontSpacing * setting.x, y + fontSpacing * setting.y,
                        mPaint);
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
    }

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		EditText et = new EditText(this.getContext());
		et.onCreateInputConnection(outAttrs);
		return mInputConnection;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		requestFocus();
		mManager.showSoftInput(this, InputMethodManager.RESULT_SHOWN);
		
		return true;
	}
	
	class TextInputConnection extends BaseInputConnection{
		private SpannableStringBuilder mBuffer;

		public TextInputConnection(View targetView, boolean fullEditor) {
			super(targetView, fullEditor);
			mBuffer = new SpannableStringBuilder("");
		}

		@Override
		public Editable getEditable() {
			return mBuffer;
		}

		@Override
		public boolean setComposingText(CharSequence text, int newCursorPosition) {
			boolean ret = super.setComposingText(text, newCursorPosition);
			mBuffer = new SpannableStringBuilder(text);
			invalidate();
			return ret;
		}

		@Override
		public boolean commitText(CharSequence text, int newCursorPosition) {
			boolean ret = super.commitText(text, newCursorPosition);
			mBuffer = new SpannableStringBuilder(text);
			mText += mBuffer.toString();
			invalidate();
			return ret;
		}

		public SpannableStringBuilder getmBuffer() {
			return mBuffer;
		}

		public void setmBuffer(SpannableStringBuilder mBuffer) {
			this.mBuffer = mBuffer;
		}
	}
}
