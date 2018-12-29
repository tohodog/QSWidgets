package org.song.qswidgets.widget.roll;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 滚动文字view
 *
 * @author song
 */
public class RollView extends View {

    private final int densityDpi;

    private int nol_color = 0x88ffffff;
    private int pre_color = 0xffffffff;

    private TextPaint textPaint;
    private String[] contentArr = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private int pageSize;


    private int size;//文字大小px
    private int gap;//文字间隔
    private int height;//文本高度px

    private int offset;//文字位移
    private int currentPage;
    private Rect rect = new Rect();

    public RollView(Context context) {
        this(context, null);
    }

    public RollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        densityDpi = getResources().getDisplayMetrics().densityDpi;
        pageSize = this.contentArr.length;

        textPaint = new TextPaint();
        //textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        size = 24 * densityDpi / 160;
        setGap(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int W = getWidth();
        int H = getHeight();

        textPaint.setTextSize(size);
        textPaint.setColor(pre_color);

        int currentY = H / 2 - offset;
        int index = 0;
        int nextPage = getNextPage();
        for (String str : contentArr) {
            if (index == currentPage)
                textPaint.setColor(colorChange(pre_color, nol_color, Math.abs(getCurrentPageRate())));
            else if (index == nextPage)
                textPaint.setColor(colorChange(nol_color, pre_color, Math.abs(getCurrentPageRate())));
            else
                textPaint.setColor(nol_color);

            //绘制文字
            textPaint.getTextBounds(str, 0, str.length(), rect);
            canvas.drawText(str, W / 2, currentY + rect.height() / 2, textPaint);//绘制居中标题

            currentY += height;
            index++;
        }
    }

    public void onNext() {
        currentPage++;
        if (currentPage >= pageSize)
            currentPage = 0;
        onScroll(currentPage);
    }

    private ValueAnimator mScrollAnimator;

    public void onScroll(int page) {
        int newOffset = height * page;

        //插值动画 模拟拖曳
        if (mScrollAnimator != null)
            mScrollAnimator.cancel();
        mScrollAnimator = new ValueAnimator();
        mScrollAnimator.setIntValues(offset, newOffset);
        //mScrollAnimator.setInterpolator(animeInterpolator);
        mScrollAnimator.setDuration(300);
        //mScrollAnimator.setStartDelay(0);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                changeOffset((int) animation.getAnimatedValue());
            }
        });
        mScrollAnimator.start();
    }

    @Override//确定view大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = View.getDefaultSize(0, heightMeasureSpec);
        int width = 0;

        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                textPaint.setTextSize(size);
                for (String str : contentArr) {
                    textPaint.getTextBounds(str, 0, str.length(), rect);
                    int w = rect.width();
                    if (w > width) width = w;
                }
                break;
            case MeasureSpec.EXACTLY:
                width = specSize;
                break;
        }
        setMeasuredDimension(width == 0 ? size : width, height);
    }

    private void changeOffset(int offset) {
        //Log.e("offset", offset + "");
        if (this.offset == offset)
            return;
        this.offset = offset;
        currentPage = (offset + height / 2) / height;
        invalidate();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    private float getCurrentPageRate() {
        return 1.f * (offset - currentPage * height) / height;
    }

    private int getNextPage() {
        if (offset - height * currentPage >= 0)
            return currentPage + 1;
        else
            return currentPage - 1;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        height = size + gap;
        requestLayout();
    }

    public void setGap(int gap) {
        this.gap = gap;
        height = size + gap;
        requestLayout();
    }

    public int getNol_color() {
        return nol_color;
    }

    public void setNol_color(int nol_color) {
        this.nol_color = nol_color;
    }

    public int getPre_color() {
        return pre_color;
    }

    public void setPre_color(int pre_color) {
        this.pre_color = pre_color;
    }

    public String[] getContentArr() {
        return contentArr;
    }

    public void setContentArr(String[] contentArr) {
        if (contentArr != null)
            this.contentArr = contentArr;
        else
            this.contentArr = new String[]{};
        pageSize = this.contentArr.length;
        requestLayout();
    }


    private int colorChange(int startColor, int endColor, float progress) {
        int a1 = (startColor >> 24) & 0x000000FF;
        int r1 = (startColor >> 16) & 0x000000FF;
        int g1 = (startColor >> 8) & 0x000000FF;
        int b1 = startColor & 0x000000FF;

        int a2 = (endColor >> 24) & 0x000000FF;
        int r2 = (endColor >> 16) & 0x000000FF;
        int g2 = (endColor >> 8) & 0x000000FF;
        int b2 = endColor & 0x000000FF;

        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);
        int a = (int) (a1 + (a2 - a1) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
