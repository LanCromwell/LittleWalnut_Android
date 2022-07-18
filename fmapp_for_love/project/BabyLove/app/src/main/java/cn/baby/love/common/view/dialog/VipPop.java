package cn.baby.love.common.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import cn.baby.love.R;
import cn.baby.love.common.api.GetVipRequest;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.ScreenUtil;
import cn.baby.love.common.utils.ToastUtil;

/**
 * pop
 */
public class VipPop extends PopupWindow {

	private View mPopView;

	public VipPop(final Activity context,OnClickListener mOnClickListener) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopView = inflater.inflate(R.layout.dialog_red_bag, null);
		mPopView.findViewById(R.id.closeBtn).setOnClickListener(mOnClickListener);

		mPopView.findViewById(R.id.lignquNow).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GetVipRequest.getInstance().getVip(new GetVipRequest.OnVipGetRequestListener() {
					@Override
					public void onOnVipGetRequestListenerr_result(boolean isSuccess, String msg) {
						ToastUtil.showToast(context,msg);
						if(isSuccess){
							UserBean userBean = UserUtil.getUserInfo();
							userBean.is_receive_vip = 1;
							UserUtil.saveUserInfo(userBean);
							dismiss();
						}
					}
				});
			}
		});

		this.setContentView(mPopView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		//this.setAnimationStyle(R.style.popwindow_animation_style);
		ColorDrawable dw = new ColorDrawable(0x88000000);
		this.setBackgroundDrawable(dw);
		this.setOutsideTouchable(false);
		mPopView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
//		this.setOnDismissListener();
	}

}
