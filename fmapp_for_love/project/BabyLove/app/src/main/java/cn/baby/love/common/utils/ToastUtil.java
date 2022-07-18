package cn.baby.love.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author wangxin
 * @date 2018-11-6
 * @desc Toast去重提示框
 */
public class ToastUtil {
	private static Toast mToast = null;
	public static void showToast(Context context, int Stringid) {
		showToast(context, context.getString(Stringid));
	}

	public static void showToast(Context context, String string){
		if (TextUtils.isEmpty(string))
			return;
		try {
			if (null == mToast) {
				mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
				mToast.setText(string);
			} else {
				mToast.setText(string);
			}
			mToast.setGravity(Gravity.CENTER_VERTICAL,0,0);
			mToast.show();
		} catch (Exception e) {
			LogUtil.e( e.getMessage());
		}
	}
}