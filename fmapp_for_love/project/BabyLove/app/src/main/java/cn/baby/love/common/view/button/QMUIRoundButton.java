package cn.baby.love.common.view.button;

import android.content.Context;
import android.util.AttributeSet;

import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

/**
 * @author wangxin
 * @date 2018-11-28
 * @desc 重写QMUIRoundButton类，默认开启点击交互
 */
public class QMUIRoundButton extends QMUIAlphaButton {

    public QMUIRoundButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public QMUIRoundButton(Context context, AttributeSet attrs) {
        super(context, attrs, com.qmuiteam.qmui.R.attr.QMUIButtonStyle);
        init(context, attrs, com.qmuiteam.qmui.R.attr.QMUIButtonStyle);
    }

    public QMUIRoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        QMUIRoundButtonDrawable bg = QMUIRoundButtonDrawable.fromAttributeSet(context, attrs, defStyleAttr);
        QMUIViewHelper.setBackgroundKeepingPadding(this, bg);
        setChangeAlphaWhenDisable(true);
        setChangeAlphaWhenPress(true);
    }
}
