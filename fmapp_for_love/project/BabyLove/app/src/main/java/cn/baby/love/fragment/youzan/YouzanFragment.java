package cn.baby.love.fragment.youzan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.common.base.BaseFragment;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.view.share.ShareDialog;

import static android.app.Activity.RESULT_OK;

public class YouzanFragment extends BaseFragment {
    private Unbinder unbinder;

    @BindView(R.id.leftIconLy)
    RelativeLayout leftIconLy;

    @BindView(R.id.centerTv)
    TextView centerTv;

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
        String url = UserUtil.getUserInfo().youzan_info.shop_href;
        centerTv.setText("活动");
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(getActivity(), "有赞商城");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(), "有赞商城");
    }

    final static int REQUEST_CODE_LOGIN = 0x11;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOGIN) {
                // 这里使用登录接口返回的数据，组装成YouzanToken，然后传回SDK
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.leftIconLy)
    public void onViewClicked() {

    }
}
