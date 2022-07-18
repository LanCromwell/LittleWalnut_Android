package cn.baby.love.fragment.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.baby.love.R;
import cn.baby.love.activity.mine.SettingsActivity;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.manager.UserUtil;

/**
 * 宝宝出生日期
 */
public class SettingsOne extends Fragment {


    @BindView(R.id.timeTv)
    TextView timeTv;
    @BindView(R.id.timeLayout)
    FrameLayout timeLayout;


    @BindView(R.id.timeWheelLy)
    RelativeLayout timeWheelLy;

    private Unbinder mUnbinder;
    private TimePickerView pvTime;
    /**
     * 是否这是了出生日期
     */
    private boolean isSettings = false;
    private Date curDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_layout_settings_one, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initView();
        return v;
    }

    private void initView() {

        UserBean userBean = UserUtil.getUserInfo();
        if (null != userBean && userBean.child_birthday > 0) {
            curDate = new Date(userBean.child_birthday * 1000);
            isSettings = true;
            setTimeTvStr();
        } else {
            curDate = Calendar.getInstance().getTime();
            isSettings = false;
            setTimeTvStr();
        }

        initTimePicker();
    }

    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(curDate);
        //时间选择器
        pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                curDate = date;
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {

                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(Color.DKGRAY)
                .setContentTextSize(20)
                .setDate(selectedDate)
                .setDividerColor(0x00000000)
                .isDialog(false)
//                .setRangDate(startDate, selectedDate)
                .setDecorView(timeLayout)//非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
                .setBackgroundId(0x00000000)
                .setOutSideCancelable(false)
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        curDate = date;
                    }
                })
                .build();
        pvTime.setKeyBackCancelable(false);//系统返回键监听屏蔽掉
        pvTime.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick({ R.id.completeBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.completeBtn:
                isSettings = true;
                setTimeTvStr();
                handler.postDelayed(runnable,100);
                break;
        }
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ((SettingsActivity)getActivity()).setCurrentPage(1);
        }
    };

    private void setTimeTvStr() {
        if(!isSettings){
            timeTv.setText("");
        }else{
            Calendar selectedDate = Calendar.getInstance();
            if (null != curDate) {
                selectedDate.setTime(curDate);
            }
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH) + 1;
            int day = selectedDate.get(Calendar.DAY_OF_MONTH);
            String monthStr = month < 10 ? "0" + month : "" + month;
            String dayStr = day < 10 ? "0" + day : "" + day;
            timeTv.setText(year + "." + monthStr + "." + dayStr+" ");
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
     * 获取当前选中的时间
     *
     * @return
     */
    public Date getCurDate() {
        return curDate;
    }
}
