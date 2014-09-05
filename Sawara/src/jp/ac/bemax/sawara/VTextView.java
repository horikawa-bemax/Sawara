package jp.ac.bemax.sawara;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class VTextView extends View {

    private static final int TOP_SPACE = 20;
    private static final int BOTTOM_SPACE = 20;
    private static final int FONT_SIZE = 50;

    private Typeface mFace;
    private Paint mPaint;
    private String text = "";
    private int width;
    private int height;

    public VTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFace = Typeface.createFromAsset(context.getAssets(),"yasashisa.ttf");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(FONT_SIZE);
        mPaint.setColor(context.getResources().getColor(R.color.main));
        mPaint.setTypeface(mFace);
    }

    public void setText(String text) {
        this.text = text;
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
        String[] s = text.split("");
        boolean newLine = false;

        for (int i = 1; i <= text.length(); i++) {
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
}
