package com.zly.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.zly.utils.CharUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhuleiyue on 2017/3/12.
 */

public class CollapsedTextView extends AppCompatTextView {
    /**
     * 末尾省略号
     */
    private static final String ELLIPSE = "...";
    /**
     * 默认的折叠行数
     */
    public static final int COLLAPSED_LINES = 4;
    /**
     * 折叠时的默认文本
     */
    private static final String EXPANDED_TEXT = "展开全文";
    /**
     * 展开时的默认文本
     */
    private static final String COLLAPSED_TEXT = "收起全文";
    /**
     * 在文本末尾
     */
    public static final int END = 0;
    /**
     * 在文本下方
     */
    public static final int BOTTOM = 1;

    /**
     * 提示文字展示的位置
     */
    @IntDef({END, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TipsGravityMode {
    }

    /**
     * 折叠的行数
     */
    private int mCollapsedLines;
    /**
     * 折叠时的文本
     */
    private String mExpandedText;
    /**
     * 展开时的文本
     */
    private String mCollapsedText;
    /**
     * 折叠时的图片资源
     */
    private Drawable mExpandedDrawable;
    /**
     * 展开时的图片资源
     */
    private Drawable mCollapsedDrawable;
    /**
     * 原始的文本
     */
    private CharSequence mOriginalText;
    /**
     * TextView中文字可显示的宽度
     */
    private int mShowWidth;
    /**
     * 是否是展开的
     */
    private boolean mIsExpanded;
    /**
     * 提示文字位置
     */
    private int mTipsGravity;
    /**
     * 提示文字颜色
     */
    private int mTipsColor;
    /**
     * 提示文字是否显示下划线
     */
    private boolean mTipsUnderline;
    /**
     * 提示是否可点击
     */
    private boolean mTipsClickable;


    public CollapsedTextView(Context context) {
        this(context, null);
    }

    public CollapsedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        if (!TextUtils.isEmpty(getText())) {
            setText(getText());
        }
    }

    /**
     * 初始化属性
     */
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.CollapsedTextView);
            mCollapsedLines = typed.getInt(R.styleable.CollapsedTextView_collapsedLines, COLLAPSED_LINES);
            setExpandedText(typed.getString(R.styleable.CollapsedTextView_expandedText));
            setCollapsedText(typed.getString(R.styleable.CollapsedTextView_collapsedText));
            setExpandedDrawable(typed.getDrawable(R.styleable.CollapsedTextView_expandedDrawable));
            setCollapsedDrawable(typed.getDrawable(R.styleable.CollapsedTextView_collapsedDrawable));
            mTipsGravity = typed.getInt(R.styleable.CollapsedTextView_tipsGravity, END);
            mTipsColor = typed.getColor(R.styleable.CollapsedTextView_tipsColor, 0);
            mTipsUnderline = typed.getBoolean(R.styleable.CollapsedTextView_tipsUnderline, false);
            mTipsClickable = typed.getBoolean(R.styleable.CollapsedTextView_tipsClickable, true);
            typed.recycle();
        }
    }

    /**
     * 设置折叠行数
     *
     * @param collapsedLines 折叠行数
     */
    public void setCollapsedLines(@IntRange(from = 0) int collapsedLines) {
        this.mCollapsedLines = collapsedLines;
    }

    /**
     * 设置折叠时的提示你好i文本
     *
     * @param expandedText 提示文本
     */
    public void setExpandedText(String expandedText) {
        this.mExpandedText = TextUtils.isEmpty(expandedText) ? EXPANDED_TEXT : expandedText;
    }

    /**
     * 设置展开时的提示文本
     *
     * @param collapsedText 提示文本
     */
    public void setCollapsedText(String collapsedText) {
        this.mCollapsedText = TextUtils.isEmpty(collapsedText) ? COLLAPSED_TEXT : collapsedText;
    }

    /**
     * 设置折叠时的提示图片
     *
     * @param resId 图片资源
     */
    public void setExpandedDrawableRes(@DrawableRes int resId) {
        setExpandedDrawable(ContextCompat.getDrawable(getContext(), resId));
    }

    /**
     * 设置折叠时的提示图片
     *
     * @param expandedDrawable 图片
     */
    public void setExpandedDrawable(Drawable expandedDrawable) {
        if (expandedDrawable != null) {
            this.mExpandedDrawable = expandedDrawable;
            this.mExpandedDrawable.setBounds(0, 0, mExpandedDrawable.getIntrinsicWidth(), mExpandedDrawable.getIntrinsicHeight());
        }
    }

    /**
     * 设置展开时的提示图片
     *
     * @param resId 图片资源
     */
    public void setCollapsedDrawableRes(@DrawableRes int resId) {
        setCollapsedDrawable(ContextCompat.getDrawable(getContext(), resId));
    }

    /**
     * 设置展开时的提示图片
     *
     * @param collapsedDrawable 图片
     */
    public void setCollapsedDrawable(Drawable collapsedDrawable) {
        if (collapsedDrawable != null) {
            this.mCollapsedDrawable = collapsedDrawable;
            this.mCollapsedDrawable.setBounds(0, 0, mCollapsedDrawable.getIntrinsicWidth(), mCollapsedDrawable.getIntrinsicHeight());
        }
    }

    /**
     * 设置提示的位置
     *
     * @param tipsGravity END 表示在文字末尾，BOTTOM 表示在文字下方
     */
    public void setTipsGravity(@TipsGravityMode int tipsGravity) {
        this.mTipsGravity = tipsGravity;
    }

    /**
     * 设置文字提示的颜色
     *
     * @param tipsColor 颜色
     */
    public void setTipsColor(@ColorInt int tipsColor) {
        this.mTipsColor = tipsColor;
    }

    /**
     * 设置提示文字是否有下划线
     *
     * @param tipsUnderline true 表示有下划线
     */
    public void setTipsUnderline(boolean tipsUnderline) {
        this.mTipsUnderline = tipsUnderline;
    }

    /**
     * 设置提示文字是否可点击
     *
     * @param tipsClickable true 表示可点击
     */
    public void setTipsClickable(boolean tipsClickable) {
        this.mTipsClickable = tipsClickable;
    }

    @Override
    public void setText(CharSequence text, final BufferType type) {
        // 如果text为空或mCollapsedLines为0则直接显示
        if (TextUtils.isEmpty(text) || mCollapsedLines == 0) {
            super.setText(text, type);
        } else if (mIsExpanded) {
            this.mOriginalText = CharUtil.trimFrom(text);
            formatExpandedText(type);
        } else {
            this.mOriginalText = CharUtil.trimFrom(text);
            // 获取TextView中文字显示的宽度，需要在layout之后才能获取到，避免在列表中重复获取
            if (mCollapsedLines > 0 && mShowWidth == 0) {
                getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mShowWidth = getWidth() - getPaddingLeft() - getPaddingRight();
                        formatCollapsedText(type);
                    }
                });
            } else {
                formatCollapsedText(type);
            }
        }
    }

    /**
     * 格式化折叠时的文本
     *
     * @param type ref android.R.styleable#TextView_bufferType
     */
    private void formatCollapsedText(BufferType type) {
        // 将原始文本按换行符拆分成段落
        String[] paragraphs = mOriginalText.toString().split("\\n");
        // 获取paint，用于计算文字宽度
        TextPaint paint = getPaint();
        // 文字宽度
        float textWidth;
        int charCount = 0;
        int lastLines = mCollapsedLines;
        for (int i = 0; i < paragraphs.length; i++) {
            // 每个段落
            String paragraph = paragraphs[i];
            // 每个段落文本的宽度
            textWidth = paint.measureText(paragraph);
            // 计算每段的行数
            int paragraphLines = (int) (textWidth / mShowWidth);
            // 如果该段为空（表示空行）或还有余，多加一行
            if (TextUtils.isEmpty(paragraph) || textWidth % mShowWidth != 0) {
                paragraphLines++;
            }
            // 如果该段落行数小于等于剩余的行数，则增加currentLines，并把该段落文本添加到格式化的文本上去
            if (paragraphLines < lastLines) {
                charCount += paragraph.length() + 1;
                lastLines -= paragraphLines;
                if (i == paragraphs.length - 1) {
                    super.setText(mOriginalText, type);
                    break;
                }
            } else if (paragraphLines == lastLines && i == paragraphs.length - 1) {
                super.setText(mOriginalText, type);
                break;
            } else { // 如果该段落的行数大于等于剩余的行数，则格式化该段文本
                SpannableStringBuilder spannable = new SpannableStringBuilder(mOriginalText, 0, charCount);
                int expandedTextWidth = 2 * (int) (paint.measureText(ELLIPSE + mExpandedText));
                CharSequence lastParagraph = mOriginalText.subSequence(charCount, charCount + paragraph.length());
                CharSequence ellipsizeText = TextUtils.ellipsize(lastParagraph, paint, mShowWidth * lastLines - expandedTextWidth, TextUtils.TruncateAt.END);
                spannable.append(ellipsizeText);
                if (lastParagraph == ellipsizeText) {
                    spannable.append(ELLIPSE);
                }
                setSpan(spannable);
                setMovementMethod(LinkMovementMethod.getInstance());
                super.setText(spannable, type);
                break;
            }
        }
    }

    /**
     * 格式化展开式的文本
     *
     * @param type
     */
    private void formatExpandedText(BufferType type) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(mOriginalText);
        setSpan(spannable);
        super.setText(spannable, type);
    }

    /**
     * 设置提示的样式
     *
     * @param spannable 需修改样式的文本
     */
    private void setSpan(SpannableStringBuilder spannable) {
        Drawable drawable;
        if (mTipsGravity == END) {
            spannable.append(" ");
        } else {
            spannable.append("\n");
        }
        int tipsLen;
        if (mIsExpanded) {
            spannable.append(mCollapsedText);
            drawable = mCollapsedDrawable;
            tipsLen = mCollapsedText.length();
        } else {
            spannable.append(mExpandedText);
            drawable = mExpandedDrawable;
            tipsLen = mExpandedText.length();
        }
        spannable.setSpan(new ExpandedClickableSpan(), spannable.length() - tipsLen, spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        if (drawable != null) {
            spannable.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE), spannable.length() - tipsLen, spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 提示的点击事件
     */
    private class ExpandedClickableSpan extends ClickableSpan {

        @Override
        public void onClick(View widget) {
            if (mTipsClickable) {
                mIsExpanded = !mIsExpanded;
                setText(mOriginalText);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mTipsColor == 0 ? ds.linkColor : mTipsColor);
            ds.setUnderlineText(mTipsUnderline);
        }
    }
}
