package cn.baby.love.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.baby.love.R;
import cn.baby.love.common.view.GifView;

/**
 * Created by wangguangbin on 2018/12/28.
 * 可以更改弹窗的内容
 */
public class LoadingDialog {

    private TextView tipTextView;
    private Dialog dialog;
    private ImageView spaceshipImage;
    private GifView mGifView;

    private LoadingDialog(Context context, String msg){
        dialog = createDIYMsgDialog(context, msg);
    }

    public static LoadingDialog getInstance(Context context, String msg){
        return new LoadingDialog(context,msg);
    }

    private Dialog createDIYMsgDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading_change_msg, null);//得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);//加载布局
        //main.xml中的ImageView
        spaceshipImage = (ImageView) v.findViewById(R.id.img);
        //提示文字
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        //加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        //使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);//设置加载信息

        mGifView = v.findViewById(R.id.mGiftView);
        mGifView.setMovieResource("loading.gif");

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);//创建自定义样式dialog

        //loadingDialog.setCancelable(false);//不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));//设置布局
        loadingDialog.setCanceledOnTouchOutside(false);
        return loadingDialog;
    }

    /**
     * 设置等待文字
     * @param msg
     */
    public void setTipText(String msg){
        if(null != tipTextView){
            tipTextView.setText(msg);
        }
    }

    public void showGif(){
        if(null != dialog){
            try {
                dialog.show();
                dialog.getWindow().setDimAmount(0);
                mGifView.setVisibility(View.VISIBLE);
                spaceshipImage.setVisibility(View.GONE);
                tipTextView.setVisibility(View.INVISIBLE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void show(){
        if(null != dialog){
            dialog.show();
            mGifView.setVisibility(View.GONE);
            spaceshipImage.setVisibility(View.VISIBLE);
            tipTextView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isShowing(){
        if(null != dialog){
           return dialog.isShowing();
        }
        return false;
    }


    public void cancel(){
        if(null != dialog){
            dialog.cancel();
        }
    }

}
