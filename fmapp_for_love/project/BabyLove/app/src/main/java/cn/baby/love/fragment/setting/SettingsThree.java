package cn.baby.love.fragment.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.baby.love.R;
import cn.baby.love.activity.mine.SettingsActivity;
import cn.baby.love.common.api.RoleLanguageRequest;
import cn.baby.love.common.bean.LanguageInfo;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.PreferUtil;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.LWheelView;

/**
 * 方言设置
 */
public class SettingsThree extends Fragment {

    @BindView(R.id.luaTv)
    TextView luaTv;
    @BindView(R.id.timeWheelLy)
    RelativeLayout timeWheelLy;

    @BindView(R.id.lauWheelView)
    LWheelView lauWheelView;

    private Unbinder mUnbinder;

    List<LanguageInfo> languageInfoList = new ArrayList<>();

    /**
     * 是否这是了语言
     */
    private boolean isSettings = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_layout_settings_three, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        getInfos();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }


    private void initWheelInfo() {
        List<LanguageInfo> mlanguageInfoList = (List<LanguageInfo>) PreferUtil.getInstance(getContext()).getObject(PreferUtil.KEY_LANGUAGE_INFOS, null);
        if (null != mlanguageInfoList) {
            languageInfoList.addAll(mlanguageInfoList);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initWheelView();
                }
            });
        }
    }

    private void initWheelView() {
        lauWheelView.setCyclic(false);
        lauWheelView.setDividerColor(0x00000000);
        lauWheelView.setTextSize(20);
        String oldLanguageInfo = (null == UserUtil.getUserInfo() || TextUtils.isEmpty(UserUtil.getUserInfo().language_name)) ? "" : UserUtil.getUserInfo().language_name;
        for (int i = 0; i < languageInfoList.size(); i++) {
            if (languageInfoList.get(i).name.equals(oldLanguageInfo)) {
                lauWheelView.setCurrentItem(i);
                break;
            }
        }
        lauWheelView.setAdapter(new ArrayWheelAdapter(languageInfoList));
    }

    private void getInfos() {
        try {
            if(null != UserUtil.getUserInfo() && !TextUtils.isEmpty(UserUtil.getUserInfo().language_name)){
                isSettings = true;
                luaTv.setText(UserUtil.getUserInfo().language_name+" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<LanguageInfo> mlanguageInfoList = (List<LanguageInfo>) PreferUtil.getInstance(getContext()).getObject(PreferUtil.KEY_LANGUAGE_INFOS, null);

        if (null != mlanguageInfoList) {
            initWheelInfo();
        } else {
            RoleLanguageRequest.getInstance().getLaugage(new RoleLanguageRequest.OnCollectRequestListener() {
                @Override
                public void onCollectRequestListener_result(boolean isSuccess, String msg) {
                    initWheelInfo();
                    if (!isSuccess) {
                        ToastUtil.showToast(getContext(), "信息加载失败");
                        return;
                    }
                }
            });
        }
    }

    @OnClick({ R.id.completeBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.completeBtn:
                int languangeIndex = lauWheelView.getCurrentItem();
                try {
                    isSettings = true;
                    luaTv.setText(languageInfoList.get(languangeIndex).name+" ");

                    ((SettingsActivity)getActivity()).saveUserInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 是否选择了日期
     * @return
     */
    public boolean isSettings(){
        return isSettings;
    }

    /**
     * 当前选中的方言
     *
     * @return
     */
    public LanguageInfo getCurLanguageInfo() {
        try {
            int languangeIndex = lauWheelView.getCurrentItem();
            return languageInfoList.get(languangeIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
