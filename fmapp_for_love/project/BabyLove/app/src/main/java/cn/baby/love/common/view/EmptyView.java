package cn.baby.love.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.baby.love.R;

/**
 * Created by wangguangbin on 2018/11/22.
 */

public class EmptyView extends LinearLayout {

    private ImageView emptyIcon;
    private TextView emptyText;
    private View rootView;

    public EmptyView(Context context) {
        super(context);
        initView();
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        setOrientation(LinearLayout.VERTICAL);
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_empty_view,null);
        addView(rootView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        emptyIcon = rootView.findViewById(R.id.emptyIcon);
        emptyText = rootView.findViewById(R.id.emptyName);
    }

    /**
     * 设置空布局点击事件
     * @param mOnclick
     */
    public void setOnEmptyViewClick(OnClickListener mOnclick){
        rootView.setOnClickListener(mOnclick);
    }

    public TextView getEmptyText(){
        return emptyText;
    }

    public ImageView getEmptyIcon(){
        return emptyIcon;
    }
    /**
     * 显示数据为空
     */
    public void showEmpty(){
        emptyText.setText(R.string.no_content);
        emptyIcon.setBackgroundResource(R.drawable.icon_kongyemian);
    }

    /**
     * 显示数据为空
     */
    public void showEmpty(String msg,int drawableId){
        emptyText.setText(msg);
        emptyIcon.setBackgroundResource(drawableId);
    }

    /**
     * 显示无网络
     */
    public void showNetErr(){
        emptyText.setText(R.string.net_error);
        emptyIcon.setBackgroundResource(R.drawable.icon_wuwangluo);
    }

    /**
     * 显示无网络
     */
    public void showNetErr(String errMsg,int drawableId){
        emptyText.setText(errMsg);
        emptyIcon.setBackgroundResource(drawableId);
    }

    /**
     * 显示加载失败
     */
    public void showFail(){
        emptyText.setText(R.string.load_fail_try_again);
    }

    /**
     * 显示加载失败
     */
    public void showFail(String errMsg,int drawableId){
        emptyText.setText(errMsg);
        emptyIcon.setBackgroundResource(drawableId);
    }

}
