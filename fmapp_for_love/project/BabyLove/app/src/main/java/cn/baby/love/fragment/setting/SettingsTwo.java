package cn.baby.love.fragment.setting;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.baby.love.R;
import cn.baby.love.activity.mine.SettingsActivity;
import cn.baby.love.common.bean.RoleInfo;
import cn.baby.love.common.manager.UserUtil;

/**
 * 选择身份
 */
public class SettingsTwo extends Fragment {


    @BindView(R.id.cb_mama)
    CheckBox cbMama;
    @BindView(R.id.cb_baba)
    CheckBox cbBaba;
    @BindView(R.id.cb_zufumu)
    CheckBox cbZufumu;
    Unbinder unbinder;

    private RoleInfo curRoleInfo;
    /**
     * 是否这是了出生日期
     */
    private boolean isSettings = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_settings_two, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        curRoleInfo = new RoleInfo();
//        curRoleInfo.id = 1;//设置默认的选项
        int role_id = 0;
        if(null != UserUtil.getUserInfo() && UserUtil.getUserInfo().role_id >0){
            isSettings = true;
            role_id = UserUtil.getUserInfo().role_id;
        }
        if(role_id == 1){
            cbMama.setChecked(true);
            cbBaba.setChecked(false);
            cbZufumu.setChecked(false);
            curRoleInfo.id = 1;
        }else if(role_id == 2){
            cbBaba.setChecked(true);
            cbMama.setChecked(false);
            cbZufumu.setChecked(false);
            curRoleInfo.id = 2;
        }else if(role_id == 3){
            cbZufumu.setChecked(true);
            cbMama.setChecked(false);
            cbBaba.setChecked(false);
            curRoleInfo.id = 3;
        }

        cbMama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSettings = true;
                cbMama.setChecked(true);
                cbBaba.setChecked(false);
                cbZufumu.setChecked(false);
                curRoleInfo.id = 1;
                handler.postDelayed(runnable,100);
            }
        });

        cbBaba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSettings = true;
                cbBaba.setChecked(true);
                cbMama.setChecked(false);
                cbZufumu.setChecked(false);
                curRoleInfo.id = 2;
                handler.postDelayed(runnable,100);
            }
        });

        cbZufumu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSettings = true;
                cbZufumu.setChecked(true);
                cbMama.setChecked(false);
                cbBaba.setChecked(false);
                curRoleInfo.id = 3;
                handler.postDelayed(runnable,100);
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ((SettingsActivity)getActivity()).setCurrentPage(2);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 是否选择了角色
     * @return
     */
    public boolean isSettings(){
        return isSettings;
    }

    /**
     * 获取当前选中的角色
     *
     * @return
     */
    public RoleInfo getCurRoleInfo() {
        return curRoleInfo;
    }
}
