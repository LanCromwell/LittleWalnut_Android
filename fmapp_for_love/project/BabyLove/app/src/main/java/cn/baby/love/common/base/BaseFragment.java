package cn.baby.love.common.base;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import cn.baby.love.R;
import cn.baby.love.common.api.Api;

/**
 * @author wangxin
 * @date 2018-11-7
 * @desc Fragment基类
 */
public class BaseFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        Api.getInstance().cancelCall(getActivity());
    }
}
