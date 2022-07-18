package cn.baby.love.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import cn.baby.love.R;
import cn.baby.love.common.utils.ScreenUtil;

/**
 * @author wangxin
 * @date 2018-11-29
 * @desc 自定义弹出层显示内容
 */
public class CustomDialog {

    public static Dialog builder(Context context, int layout){
        Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        View rootView = dialog.getLayoutInflater().inflate(layout, null);
        int screenWidth = ScreenUtil.getScreenWidth(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(rootView, params);
        return dialog;
    }

    /**
     * 自定义弹框confirm 一个button
     * @param context
     * @param message
     * @param btnMsg 按钮上的文字
     * @param positiveButton
     */
    public static Dialog confirmOneBtn(Context context, String message, String btnMsg,
                                       final DialogInterface.OnClickListener positiveButton){

        final Dialog dialog = builder(context,R.layout.dialog_one_btn);
        TextView messageTv = dialog.findViewById(R.id.content);
        messageTv.setText(message);
        Button button = dialog.findViewById(R.id.positive_button);
        button.setText(btnMsg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != positiveButton){
                    positiveButton.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);
                }
            }
        });
        dialog.show();
        return dialog;
    }
}
