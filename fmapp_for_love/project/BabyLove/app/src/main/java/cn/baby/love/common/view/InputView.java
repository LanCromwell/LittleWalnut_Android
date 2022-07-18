package cn.baby.love.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.baby.love.R;

/**
 * Created by wangguangbin on 2018/11/14.
 * 输入框view
 */

public class InputView extends LinearLayout {

    @BindView(R.id.inputEd)
    EditText inputEd;
    @BindView(R.id.clearBtn)
    ImageView clearBtn;
    @BindView(R.id.getCodeTv)
    TextView getCodeTv;
    private MyCountDown mMyCountDown;

    public InputView(Context context) {
        super(context);
        init();
    }

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_input_view, null);
        addView(view);
        ButterKnife.bind(this, view);
        mMyCountDown = new MyCountDown(getCodeTv,60000,1000);
    }

    public TextView getGetCodeTextView(){
        return getCodeTv;
    }

    public void startGetVerifyCode(){
        mMyCountDown.start();
    }

    public void cancelGetVerifyCode(){
        mMyCountDown.cancel();
        mMyCountDown.onFinish();
    }

    public EditText getInputEd(){
        return inputEd;
    }

    /**
     * 是否显示“获取验证码”
     *
     * @param visibility
     */
    public void setRightTvVisibility(boolean visibility) {
        getCodeTv.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.clearBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.clearBtn:
                inputEd.setText(null);
                break;
        }
    }
}
