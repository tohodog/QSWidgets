package org.song.qswidgets.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by song on 2016/9/9.
 * edit on 2018/4/10 增加图片
 * edit on 2018/11/1 支持多规则+优化
 * 自定义部分颜色,图片+点击监听的textview
 */
public class DiyStyleTextView extends AppCompatTextView {

    private boolean underlineText = false;

    private List<String> colorRegexList = new ArrayList<>();
    private List<Integer> colorList = new ArrayList<>();

    private List<String> imageRegexList = new ArrayList<>();
    private List<Bitmap> bitmapList = new ArrayList<>();

    public DiyStyleTextView(Context context) {
        super(context);
    }

    public DiyStyleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //是否显示下划线-全局
    public DiyStyleTextView setUnderlineText(boolean underlineText) {
        this.underlineText = underlineText;
        return this;
    }

    public DiyStyleTextView reset() {
        colorRegexList.clear();
        colorList.clear();
        imageRegexList.clear();
        bitmapList.clear();
        return this;
    }

    //添加自定义颜色规则
    public DiyStyleTextView addColorRegex(String colorRegex, int color) {
        setMovementMethod(LinkMovementMethod.getInstance());
        colorRegexList.add(colorRegex);
        colorList.add(color);
        return this;
    }

    //添加自定义图片规则
    public DiyStyleTextView addImageRegex(String imageRegex, Bitmap bitmap) {
        setMovementMethod(LinkMovementMethod.getInstance());
        imageRegexList.add(imageRegex);
        bitmapList.add(bitmap);
        return this;
    }

    //覆盖父类,setText()生效
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(setTextStyle(text, false), type);
    }

    //覆盖父类,append()生效
    @Override
    public void append(CharSequence text, int start, int end) {
        super.append(setTextStyle(text, false), start, end);
    }


    public void setDiyTextColor(CharSequence text, String regularExpression, int color, DiyTextClick mDiyTextClick) {
        reset().addColorRegex(regularExpression, color).setDiyTextClickListenner(mDiyTextClick).setTextStyle(text, true);
    }

    public void setDiyTextColor(CharSequence text, String regularExpression, int color) {
        setDiyTextColor(text, regularExpression, color, null);
    }

    public void setDiyTextImage(CharSequence text, String regularExpression, Bitmap bitmap, DiyTextClick mDiyTextClick) {
        reset().addImageRegex(regularExpression, bitmap).setDiyTextClickListenner(mDiyTextClick).setTextStyle(text, true);
    }

    public void setDiyTextImage(CharSequence text, String regularExpression, Bitmap bitmap) {
        reset().setDiyTextImage(text, regularExpression, bitmap, null);
    }


    private List<Integer> indexArr = new ArrayList<>();
    private List<String> strArr = new ArrayList<>();

    public CharSequence setTextStyle(CharSequence text, boolean flag) {
        if (TextUtils.isEmpty(text)) {
            if (flag) super.setText(text);
            return text;
        }

        SpannableStringBuilder styledText = new SpannableStringBuilder(text);

        if (colorRegexList != null)
            for (int j = 0; j < colorRegexList.size(); j++) {

                String colorRegex = colorRegexList.get(j);
                if (colorRegex == null)
                    continue;

                int color = colorList.get(j);

                indexArr.clear();
                strArr.clear();
                //正则获取符合的字符串
                Pattern p = Pattern.compile(colorRegex);
                Matcher m = p.matcher(text);
                while (m.find()) {
                    strArr.add(m.group());
                    indexArr.add(m.start());
                }
                for (int i = 0; i < indexArr.size(); i++) {
                    int index = indexArr.get(i);
                    String clickText = strArr.get(i);
                    //替换文本 自定义风格
                    styledText.setSpan(
                            diyTextClickListenner == null ?
                                    new TextViewCharacterStyle(color) :
                                    new TextViewClickSpan(clickText, color),
                            index,
                            index + clickText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            }

        if (imageRegexList != null)
            for (int j = 0; j < imageRegexList.size(); j++) {

                String imageRegex = imageRegexList.get(j);
                Bitmap bitmap = bitmapList.get(j);
                if (imageRegex == null)
                    continue;

                indexArr.clear();
                strArr.clear();

                Pattern p = Pattern.compile(imageRegex);
                Matcher m = p.matcher(text);
                while (m.find()) {
                    strArr.add(m.group());
                    indexArr.add(m.start());
                }
                for (int i = 0; i < indexArr.size(); i++) {
                    int index = indexArr.get(i);
                    String clickText = strArr.get(i);
                    styledText.setSpan(
                            new ImageSpan(bitmap),
                            index,
                            index + clickText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledText.setSpan(
                            diyTextClickListenner == null ?
                                    null :
                                    new TextViewClickSpan(clickText),
                            index,
                            index + clickText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        if (flag) super.setText(styledText);
        return styledText;
    }

    //设置颜色 点击监听的Span
    private class TextViewClickSpan extends ClickableSpan {

        private String clickText;
        private int color;

        TextViewClickSpan(String clickText) {
            this.clickText = clickText;
        }

        TextViewClickSpan(String clickText, int color) {
            this.clickText = clickText;
            this.color = color;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (color != 0) ds.setColor(color);
            ds.setUnderlineText(underlineText); //下划线
        }

        @Override
        public void onClick(View widget) {//点击事件
            if (diyTextClickListenner != null)
                diyTextClickListenner.diyTextClick(clickText);
        }
    }

    //只有设置颜色Span
    private class TextViewCharacterStyle extends CharacterStyle {

        private int color;

        TextViewCharacterStyle(int color) {
            this.color = color;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (color != 0) ds.setColor(color);
            ds.setUnderlineText(underlineText); //下划线
        }

    }

    private DiyTextClick diyTextClickListenner;

    public interface DiyTextClick {
        void diyTextClick(String s);
    }

    //点击监听-全局
    public DiyStyleTextView setDiyTextClickListenner(DiyTextClick mDiyTextClick) {
        this.diyTextClickListenner = mDiyTextClick;
        setClickable(true);
        return this;
    }

}
