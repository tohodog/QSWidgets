package org.song.qswidgets.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;


import org.song.qswidgets.R;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * @author song
 * 验证码/密码输入框框
 */
public class InputFrameView extends FrameLayout {

    private static final int[] STATE_NORMAL = {-android.R.attr.state_selected};
    private static final int[] STATE_SELECTED = {android.R.attr.state_selected};
    private static final int[] STATE_CHECKED = {android.R.attr.state_checked};

    private static final int DEFAULT_TEXT_COLOR = 0xFF323232;
    private static final int DEFAULT_TEXT_SIZE = 30;   //dp
    private static final int DEFAULT_FRAME_SIZE = 50;
    private static final int DEFAULT_FRAME_PADDING = 14;
    private static final int DEFAULT_CODE_LENGTH = 4;
    /**
     * 输入View
     */
    private EditText mEditText;
    private final float density;

    private int mCodeLength = 0;
    private Paint mCodeTextPaint;
    private Rect mTextRect;
    private String mCodeText = "";
    private int mFrameSize = -1;
    private int mFrameGap = -1;
    private int mCodeTextColor = -1;
    private int mCodeTextSize = -1;

    private @DrawableRes
    int mFrameDrawable = -1;
    private SparseArrayCompat<Drawable> mInputDrawable = new SparseArrayCompat<>();
    private InputMethodManager mInputMethodManager;
    private OnInputListener mOnInputListener;

    public InputFrameView(Context context) {
        this(context, null);
    }

    public InputFrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = context.getResources().getDisplayMetrics().density;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputFrameView);
        int size = typedArray.getIndexCount();
        for (int i = 0; i < size; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.InputFrameView_codeTextColor:
                    mCodeTextColor = typedArray.getColor(attr, -1);
                    break;
                case R.styleable.InputFrameView_codeTextSize:
                    mCodeTextSize = typedArray.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputFrameView_frameSize:
                    mFrameSize = typedArray.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.InputFrameView_frameGap:
                    mFrameGap = typedArray.getDimensionPixelOffset(attr, -1);
                    break;
                case R.styleable.InputFrameView_codeLength:
                    mCodeLength = typedArray.getInt(attr, -1);
                    break;
                case R.styleable.InputFrameView_frameDrawable:
                    mFrameDrawable = typedArray.getResourceId(attr, -1);
                    break;
            }
        }
        typedArray.recycle();
        if (mCodeTextColor == -1) {
            mCodeTextColor = DEFAULT_TEXT_COLOR;
        }
        if (mCodeTextSize == -1) {
            mCodeTextSize = (int) (density * DEFAULT_TEXT_SIZE);
        }
        if (mFrameSize == -1) {
            mFrameSize = (int) (density * DEFAULT_FRAME_SIZE);
        }
        if (mFrameGap == -1) {
            mFrameGap = (int) (density * DEFAULT_FRAME_PADDING);
        }
        if (mCodeLength <= 0) {
            mCodeLength = DEFAULT_CODE_LENGTH;
        }
        mTextRect = new Rect();
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        initEditText();
        initTextPaint();
        initStateListDrawable();
        setWillNotDraw(false);
    }

    private void initEditText() {
        mEditText = new EditText(getContext());
        mEditText.addTextChangedListener(mTextWatcher);
        mEditText.setCursorVisible(false);
        ViewCompat.setBackground(mEditText, new ColorDrawable(Color.TRANSPARENT));
        mEditText.setTextColor(Color.TRANSPARENT);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mCodeLength)});
        mEditText.setFocusable(true);
        mEditText.requestFocus();
        mEditText.setTextSize(1);//字体设置小了 光标才不会被点到中间去

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEditText.setShowSoftInputOnFocus(true);
        }
        mEditText.setInputType(TYPE_CLASS_NUMBER);
        mEditText.setSingleLine();
        addView(mEditText, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }


    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int mCurIndex;
            if (!TextUtils.isEmpty(s)) {
                mCodeText = s.toString();
                mCurIndex = mCodeText.length();
            } else {
                mCurIndex = 0;
                mCodeText = "";
            }

            for (int i = 0; i < mCodeLength; i++) {
                if (i < mCurIndex)
                    setDrawableState(i, STATE_CHECKED);
                else if (i == mCurIndex)
                    setDrawableState(i, STATE_SELECTED);
                else
                    setDrawableState(i, STATE_NORMAL);
            }
            invalidate();

            if (mOnInputListener != null) {
                if (mCodeText.length() == mCodeLength) {
                    mOnInputListener.onInputFinish();
                } else {
                    mOnInputListener.onInputIng();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    public void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private boolean showInputFlag;

    public void showInputMethod() {
        showInputFlag = true;
        mEditText.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (!showInputFlag) return;
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null)
                            if (imm.showSoftInput(mEditText, 0))
                                showInputFlag = false;
                    }
                });
//        mEditText.post(new Runnable() {
//            @Override
//            public void run() {
//                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) imm.showSoftInput(mEditText, 0);
//            }
//        });
    }

    public EditText getInputView() {
        return mEditText;
    }

    public void setText(CharSequence text) {
        if (mEditText != null) {
            mEditText.setText(text);
        }
    }

    private void initTextPaint() {
        mCodeTextPaint = new TextPaint();
        mCodeTextPaint.setColor(mCodeTextColor);
        mCodeTextPaint.setAntiAlias(true);
        mCodeTextPaint.setTextSize(mCodeTextSize);
        mCodeTextPaint.setFakeBoldText(true);
        mCodeTextPaint.setTextAlign(Paint.Align.CENTER);
    }


    private void initStateListDrawable() {
        for (int i = 0; i < mCodeLength; i++) {
            mInputDrawable.put(i, getFrameDrawable());
        }
        setDrawableState(0, STATE_SELECTED);
    }

    private Drawable getFrameDrawable() {
        if (mFrameDrawable == -1) {
            //默认框框效果
            GradientDrawable mNormalBackground = new GradientDrawable();
            mNormalBackground.setCornerRadius(density * 6);
            mNormalBackground.setStroke((int) (density * 1), 0xffb4b4b4);

            GradientDrawable mSelectBackground = new GradientDrawable();
            mSelectBackground.setCornerRadius(density * 6);
            mSelectBackground.setStroke((int) (density * 2), 0xff4a4a4a);

            int colors[] = {0xfffffff, 0xffe0e0e0};
            GradientDrawable mCheckBackground = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
            mCheckBackground.setCornerRadius(density * 6);
            mCheckBackground.setStroke((int) (density * 1), 0xffb4b4b4);

            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(STATE_CHECKED, mCheckBackground);
            drawable.addState(STATE_SELECTED, mSelectBackground);
            drawable.addState(STATE_NORMAL, mNormalBackground);
            return drawable;
        } else {
            return ContextCompat.getDrawable(getContext(), mFrameDrawable);
        }
    }


    public void setOnNumberInputListener(OnInputListener listener) {
        this.mOnInputListener = listener;
    }

    /**
     * 设置drawable state
     */
    private void setDrawableState(int index, int[] state) {
        if (index < 0 || index > mInputDrawable.size() - 1) return;
        mInputDrawable.get(index).setState(state);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() != VISIBLE) {
            mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    //设置自己的大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            height = mFrameSize;
        }
        if (widthSpecMode != MeasureSpec.EXACTLY) {
            width = (mCodeLength * mFrameSize) + (mFrameGap * (mCodeLength - 1));
        }

        int childWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0, width);
        int childHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, height);
        mEditText.measure(childWidthSpec, childHeightSpec);
        setMeasuredDimension(width, height);
    }

    public String getInputCode() {
        return mCodeText;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = 0;
        int right = mFrameSize;

        int size = mInputDrawable.size();
        for (int i = 0; i < size; i++) {
            //绘制框框
            Drawable drawable = mInputDrawable.get(i);
            drawable.setBounds(left, 0, right, getMeasuredHeight());
            drawable.draw(canvas);
            //绘制文本
            drawCodeText(canvas, drawable.getBounds(), indexOfCode(i));
            left = right + mFrameGap;
            right = left + mFrameSize;
        }
    }

    private String indexOfCode(int index) {
        if (TextUtils.isEmpty(mCodeText)) {
            return "";
        }
        if (index < 0 || index > mCodeText.length() - 1) {
            return "";
        }
        return String.valueOf(mCodeText.charAt(index));
    }

    private void drawCodeText(Canvas canvas, Rect bound, String text) {
        if (!TextUtils.isEmpty(text)) {
            mCodeTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
            canvas.drawText(text, bound.centerX(), bound.height() / 2 + mTextRect.height() / 2, mCodeTextPaint);
        }
    }

    public interface OnInputListener {
        void onInputFinish();

        void onInputIng();
    }
}