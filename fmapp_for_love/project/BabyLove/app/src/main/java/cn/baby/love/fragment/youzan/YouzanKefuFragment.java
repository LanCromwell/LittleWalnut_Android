package cn.baby.love.fragment.youzan;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.common.base.BaseConfig;
import cn.baby.love.common.base.BaseFragment;
import cn.baby.love.common.manager.UserUtil;

import static android.app.Activity.RESULT_OK;

/**
 * 有赞客服
 */
public class YouzanKefuFragment extends BaseFragment {
    private Unbinder unbinder;
    @BindView(R.id.titleLy)
    LinearLayout titleLy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_youzan, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //        final String url = BaseConfig.YOU_ZAN_SHOP_URL;
        final String url = UserUtil.getUserInfo().youzan_info.customer_service_href;
        titleLy.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(getActivity(), "有赞客服");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(), "有赞客服");
    }

    final static int REQUEST_CODE_LOGIN = 0x11;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOGIN) {
                // 这里使用登录接口返回的数据，组装成YouzanToken，然后传回SDK
            } else {
            }
        }
    }
}
