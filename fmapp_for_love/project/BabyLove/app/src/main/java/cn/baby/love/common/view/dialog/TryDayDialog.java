package cn.baby.love.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.baby.love.R;

/**
 * 试用期倒计时提醒弹窗
 */

public class TryDayDialog extends Dialog {

    public TryDayDialog(Context context) {
        super(context);
    }

    public TryDayDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TryDayDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        Context context;
        View layout;
        TextView contentView;
        Button positiveButtonView;
        Button negativeButtonView;
        OnClickListener positiveButtonListener;
        OnClickListener negativeButtonListener;

        public Builder(Context context){
            this.context = context;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.dialog_confirm, null);
            contentView = (TextView) layout.findViewById(R.id.content);
            positiveButtonView = (Button) layout.findViewById(R.id.positive_button);
            negativeButtonView = (Button) layout.findViewById(R.id.negative_button);

        }

        public void setMessage(String content){
            contentView.setText(content);
        }

        public void setPositiveButton(OnClickListener listener){
            positiveButtonListener = listener;
        }

        /**
         * 设置字体颜色
         * @param color
         */
        public void setPositiveButtonTextColor(int color){
            positiveButtonView.setTextColor(color);
        }

        /**
         * 设置按钮颜色
         * @param resouse
         */
        public void setPositiveButtonBackground(int resouse){
            positiveButtonView.setBackgroundResource(resouse);
        }

        public void setNegativeButton(OnClickListener listener){
            negativeButtonListener = listener;
        }

        /**
         * 设置字体颜色
         * @param color
         */
        public void setNegativeButtonTextColor(int color){
            negativeButtonView.setTextColor(color);
        }

        /**
         * 设置按钮颜色
         * @param resouse
         */
        public void setNegativeButtonBackground(int resouse){
            negativeButtonView.setBackgroundResource(resouse);
        }

        public TryDayDialog create(){
            final TryDayDialog dialog = new TryDayDialog(context, R.style.Dialog);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            positiveButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positiveButtonListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
            });
            negativeButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    negativeButtonListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                }
            });

            dialog.setCancelable(false);
            dialog.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return true;
                }
            });

            return dialog;
        }
    }

}
