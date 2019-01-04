package org.song.qswidgets.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;


/**
 * Created by song
 * Contact github.com/tohodog
 * Date 2018/3/24
 * 支持两个滑动view 竖直翻页的容器
 * Future:support adater and cycle
 */

public class VerticalPageView extends ViewGroup {

    private final float dragRate = 1f;//拖曳速率
    private int animaDuration = 365;

    public static final int STATUS_NORMAL = 0;//普通
    public static final int STATUS_DRAGGING = 1;//拖曳中-
    public static final int STATUS_DRAGGING_REACH = 2;//拖曳中-超过
    public static final int STATUS_DRAGGED = 3;//拖曳完毕
    private int refreshStatus;

    private Interpolator animeInterpolator = new AccelerateInterpolator(0.5f);//自动回弹动画插值器

    private int triggerDistance;//触发的长度
    private int touchSlop;
    private int nowPage;

    private int currentOffset;//拖动的位移量

    public VerticalPageView(Context context) {
        this(context, null);
    }


    public VerticalPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        triggerDistance = touchSlop * 12;
        ensureTarget();
    }

    //获取子view,false表示没有
    private boolean ensureTarget() {
        return (getChildCount() > 1);
    }


    @Override//确定子view大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        final LayoutParams lp = child.getLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom(), lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {


        final int width = r - l;
        final int height = b - t;
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();


        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            final int w = child.getMeasuredWidth();
            final int h = child.getMeasuredHeight();

            int d = i * getH();
            int offset = d + currentOffset;
            child.layout(left, top + offset, width - right, height - bottom + offset);

        }
    }

    private boolean isHeadBeingDragged = false, isFootBeingDragged = false;

    @Override//判断是否截取事件进行刷新
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || !isNormal() || !ensureTarget())
            return false;
        if (nowPage > 0 && !canChildScrollUp())
            isHeadBeingDragged = handlerInterceptTouchEvent(ev, true);
        if (!isHeadBeingDragged) {
            if (nowPage < getChildCount() - 1 && !canChildScrollDown())
                isFootBeingDragged = handlerInterceptTouchEvent(ev, false);
        }
        boolean b = isHeadBeingDragged || isFootBeingDragged;

        if (b) {
            setRefreshStatus(STATUS_DRAGGING);
        }
        return b;
    }

    private int mActivePointerId;
    private float mInitialMotionY, mInitialMotionX;
    private long time;//计算拖曳速度

    //是否截取事件
    private boolean handlerInterceptTouchEvent(MotionEvent ev, boolean isHead) {
        final int action = ev.getActionMasked();
        boolean mIsBeingDragged = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                final float initialMotionY = ev.getY(mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionX = ev.getX(mActivePointerId);
                mInitialMotionY = initialMotionY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == MotionEvent.INVALID_POINTER_ID) {
                    return false;
                }
                final int index = ev.findPointerIndex(mActivePointerId);
                if (index < 0) {
                    return false;
                }
                final float y = ev.getY(mActivePointerId);
                final float x = ev.getX(mActivePointerId);

                if (y == -1) {
                    return false;
                }
                float yDiff = y - mInitialMotionY;
                float xDiff = x - mInitialMotionX;
                if (!isHead)
                    yDiff = -yDiff;

                if (yDiff > touchSlop && Math.abs(yDiff) > Math.abs(xDiff)) {
                    mInitialMotionY = y;//触发拖曳 刷新下Y值
                    time = System.currentTimeMillis();
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP://兼容多个手指
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
        }
        return mIsBeingDragged;
    }


    private float scrollTop = 0;

    @Override//拖曳view
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isBeingDragged()) {
            return super.onTouchEvent(ev);
        }

        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                final float yDiff = y - mInitialMotionY;
                scrollTop = yDiff * dragRate;
                //防止拖回去
                if (isHeadBeingDragged) {
                    if (scrollTop < 0)
                        scrollTop = 0;
                } else {
                    if (scrollTop > 0)
                        scrollTop = 0;
                }
                //先设置好值才改变状态
                setDragViewOffsetAndPro((int) scrollTop, true);
                if (Math.abs(scrollTop) >= triggerDistance)//拖曳超过阈值 触发换页
                    setRefreshStatus(STATUS_DRAGGING_REACH);
                else
                    setRefreshStatus(STATUS_DRAGGING);
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
                final int index = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(index);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == MotionEvent.INVALID_POINTER_ID)
                    return false;
                float speed = 1000.f * Math.abs(scrollTop) / (System.currentTimeMillis() - time);

                //速度快也视为换页
                if (speed > getContext().getResources().getDisplayMetrics().density * 200) ;
                setRefreshStatus(STATUS_DRAGGING_REACH);//松手

                if (refreshStatus == STATUS_DRAGGING_REACH) {
                    scrollAnimation(isHeadBeingDragged ? nowPage - 1 : nowPage + 1, animaDuration, animeInterpolator);
                } else if (refreshStatus == STATUS_DRAGGING) {//距离不够取消刷新
                    scrollAnimation(nowPage, 100, animeInterpolator);
                }
                setRefreshStatus(STATUS_DRAGGED);//松手

                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                return false;
            }
        }

        return true;
    }


    private ValueAnimator mScrollAnimator;


    //插值动画 模拟拖曳
    protected void scrollAnimation(final int newPage, int time, Interpolator i) {
        if (pageListenner != null)
            pageListenner.onPageChange(newPage);


        int startOffset = currentOffset + nowPage * getH();

        int endOffset = nowPage * getH() - newPage * getH();

        if (mScrollAnimator != null)
            mScrollAnimator.cancel();
        mScrollAnimator = new ValueAnimator();
        mScrollAnimator.setIntValues(startOffset, endOffset);
        mScrollAnimator.setInterpolator(i);
        mScrollAnimator.setDuration(time);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setDragViewOffsetAndPro((int) animation.getAnimatedValue(), true);
            }
        });
        mScrollAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                nowPage = newPage;
                setRefreshStatus(STATUS_NORMAL);
                mScrollAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mScrollAnimator.start();
    }

    //实现view移动变化的方法
    private void setDragViewOffsetAndPro(int offset, boolean requiresUpdate) {
        int h = getH();
        currentOffset = -nowPage * h + offset;

        if (requiresUpdate) {
            requestLayout();
        }

        if (pageListenner != null)
            pageListenner.onPageScrolled(offset < 0 ? nowPage + 1 : nowPage - 1, -1.f * offset / getH());
    }

    private int getH() {
        return getMeasuredHeight();
    }


    private void setRefreshStatus(int status) {
        if (refreshStatus == status)
            return;
        refreshStatus = status;
        if (status == STATUS_NORMAL) {
            isHeadBeingDragged = false;
            isFootBeingDragged = false;
        }
    }


    protected boolean canChildScrollUp() {
        View mTarget = getChildAt(nowPage);
        return ViewCompat.canScrollVertically(mTarget, -1);
    }


    protected boolean canChildScrollDown() {
        View mTarget = getChildAt(nowPage);
        return ViewCompat.canScrollVertically(mTarget, 1);
    }


    public boolean isBeingDragged() {
        return refreshStatus == STATUS_DRAGGING | refreshStatus == STATUS_DRAGGING_REACH;
    }

    public boolean isNormal() {
        return refreshStatus == STATUS_NORMAL;
    }


    private PageListenner pageListenner;

    public void setPageListenner(PageListenner pageListenner) {
        this.pageListenner = pageListenner;
    }

    public interface PageListenner {
        void onPageChange(int nowPage);

        void onPageScrolled(int nextPage, float rate);
    }


}
