package cn.baby.love.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.baby.love.R;

/**
 * 用户协议弹窗
 */
public class UserProtocolDialog extends Dialog {

    public UserProtocolDialog(Context context) {
        super(context);
    }

    public UserProtocolDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected UserProtocolDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public static class Builder {
        Context context;
        View layout;
        TextView contentView;
        Button positiveButtonView;
        TextView negativeButtonView;
        OnClickListener positiveButtonListener;
        OnClickListener negativeButtonListener;
        OnClickListener contentClickListener;
        TextView contentTv;

        public Builder(Context context){
            this.context = context;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.dialog_user_protocal, null);
            contentView = (TextView) layout.findViewById(R.id.content);
            positiveButtonView = (Button) layout.findViewById(R.id.positive_button);
            negativeButtonView = layout.findViewById(R.id.negative_button);
            contentTv = layout.findViewById(R.id.content);
            String contentStr = "欢迎您选择小核桃，我们依照国家法规采用严格的措施保护您的个人权益。\n" +
                    "您选择【同意并开始使用】即表示充分理解接受<u>《小核桃用户服务协议》</u> <u>《小核桃用户隐私政策》</u>";
            contentTv.setText(Html.fromHtml(contentStr));

        }

        public void setMessage(String content){
            contentView.setText(content);
        }

        public void setPositiveButton( OnClickListener listener){
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

        public void setContentClickListener(OnClickListener listener){
            contentClickListener = listener;
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

        public UserProtocolDialog create(){
            final UserProtocolDialog dialog = new UserProtocolDialog(context, R.style.Dialog);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            contentTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contentClickListener.onClick(dialog,DialogInterface.BUTTON_NEUTRAL);
                }
            });
            return dialog;
        }
    }

}
