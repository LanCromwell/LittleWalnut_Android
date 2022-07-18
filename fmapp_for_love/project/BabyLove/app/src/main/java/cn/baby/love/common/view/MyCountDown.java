package cn.baby.love.common.view;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by wangguangbin on 2018/11/14.
 * 倒计时
 */

public class MyCountDown extends CountDownTimer {
    TextView textView;

    public MyCountDown(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.textView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        textView.setClickable(false); //设置不可点击
        textView.setText(millisUntilFinished / 1000 + " s"); //设置倒计时时间  
        //SpannableString spannableString = new SpannableString(textView.getText().toString()); //获取按钮上的文字  
        //ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        //spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色  
        //textView.setText(spannableString);
    }

    @Override
    public void onFinish() {
        textView.setText("获取验证码");
        textView.setClickable(true);//重新获得点击
    }
}
