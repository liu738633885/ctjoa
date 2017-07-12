package com.ctj.oa.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.ctj.oa.R;


/**
 * Created by lewis on 2017/4/27.
 */

public class SearchView extends AppCompatEditText {

    private float searchSize = 0;
    private float textSize = 0;
    private int textColor = 0xFF000000;
    private Drawable mDrawable;
    private Paint paint;
    private String hintText;

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitResource(context, attrs);
        InitPaint();
    }

    private void InitResource(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.searchedit);
        float density = context.getResources().getDisplayMetrics().density;
        searchSize = mTypedArray.getDimension(R.styleable.searchedit_imagewidth, 13 * density + 0.5F);
        textColor = mTypedArray.getColor(R.styleable.searchedit_textColor, ContextCompat.getColor(context, R.color.gray01));
        textSize = mTypedArray.getDimension(R.styleable.searchedit_textSize, 14 * density + 0.5F);
        hintText = mTypedArray.getString(R.styleable.searchedit_hintText);
        mTypedArray.recycle();
        if (TextUtils.isEmpty(hintText)) {
            hintText = "搜索";
        }
    }

    private void InitPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawSearchIcon(canvas);
    }

    private void DrawSearchIcon(Canvas canvas) {
        if (this.getText().toString().length() == 0) {
            float textWidth = paint.measureText(hintText);
            float textHeight = getFontLeading(paint);

            float dx = (getWidth() - searchSize - textWidth - 8) / 2;
            float dy = (getHeight() - searchSize) / 2;

            canvas.save();
            canvas.translate(getScrollX() + dx, getScrollY() + dy);
            if (mDrawable != null) {
                mDrawable.draw(canvas);
            }
            canvas.drawText(hintText, getScrollX() + searchSize + 8, getScrollY() + (getHeight() - (getHeight() - textHeight) / 2) - paint.getFontMetrics().bottom - dy, paint);
            canvas.restore();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mDrawable == null) {
            try {
                mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_search);
                mDrawable.setBounds(0, 0, (int) searchSize, (int) searchSize);
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mDrawable != null) {
            mDrawable.setCallback(null);
            mDrawable = null;
        }
        super.onDetachedFromWindow();
    }

    public float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.bottom - fm.top;
    }

}