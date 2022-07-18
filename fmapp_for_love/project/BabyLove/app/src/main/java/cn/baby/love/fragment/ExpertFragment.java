package cn.baby.love.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.baby.love.R;
import cn.baby.love.activity.mine.LikedActivity;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.base.BaseFragment;
import cn.baby.love.common.bean.CategoryInfo;
import cn.baby.love.common.bean.CategoryInfoChild;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.ScreenUtil;
import cn.baby.love.common.utils.StrUtil;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.ExpertHorLayout;
import cn.baby.love.common.view.JmRecyclerView;

/**
 * 专家严选
 */
public class ExpertFragment extends BaseFragment implements ExpertHorLayout.OnItemClickListener,JmRecyclerView.OnRefreshListener {

    @BindView(R.id.searchEt)
    EditText searchEt;
    @BindView(R.id.mExpertHorLayout1)
    ExpertHorLayout mExpertHorLayout1;
    @BindView(R.id.mExpertHorLayout2)
    ExpertHorLayout mExpertHorLayout2;
    @BindView(R.id.mExpertHorLayout3)
    ExpertHorLayout mExpertHorLayout3;
    @BindView(R.id.mExpertHorLayout4)
    ExpertHorLayout mExpertHorLayout4;
    @BindView(R.id.topBtn1)
    TextView topBtn1;
    @BindView(R.id.topBtn2)
    TextView topBtn2;
    @BindView(R.id.topBtn3)
    TextView topBtn3;
    @BindView(R.id.topBtn4)
    TextView topBtn4;

    @BindView(R.id.topBtnLayout)
    LinearLayout topBtnLayout;
    @BindView(R.id.mJmRecyclerView)
    JmRecyclerView mJmRecyclerView;

    private CategoryInfo categoryInfo;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_xiaokan, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mJmRecyclerView.setOnRefreshListener(true,false,this);
        getInfoList();
        setSearchKey();
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(getActivity(),"专家严选");
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(),"专家严选");
    }

    /**
     * 设置键盘右下角为搜索
     */
    private void setSearchKey(){
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){  //搜索按钮
                    toSearch();
                }
                return false;
            }
        });
    }

    @Override
    public void onRefresh() {
        getInfoList();
    }

    @Override
    public void onLoadMore() {}

    private void initViews() {
        if (null != categoryInfo && null != categoryInfo.data) {
            if (categoryInfo.data.size() >= 1) {
                CategoryInfo topInfo = categoryInfo.data.get(0);
                if (null != topInfo.child && topInfo.child.size() > 0) {
                    for (int i = 0; i < topInfo.child.size(); i++) {
                        CategoryInfoChild infoChild = topInfo.child.get(i);
                        switch (i) {
                            case 0:
                                topBtn1.setText(StrUtil.parseEmpty(infoChild.name));
                                topBtn1.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                topBtn2.setText(StrUtil.parseEmpty(infoChild.name));
                                topBtn2.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                topBtn3.setText(StrUtil.parseEmpty(infoChild.name));
                                topBtn3.setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                topBtn4.setVisibility(View.VISIBLE);
                                topBtn4.setText(StrUtil.parseEmpty(infoChild.name));
                                break;
                        }
                    }
                }else{
                    topBtnLayout.setVisibility(View.GONE);
                }
            }else{
                topBtnLayout.setVisibility(View.GONE);
            }

            if (categoryInfo.data.size() >= 2) {
                setItemHeight(categoryInfo.data.get(1).child.size(),mExpertHorLayout1);
                mExpertHorLayout1.setItemTitle(categoryInfo.data.get(1).name, R.drawable.icon_milk);
                mExpertHorLayout1.initAdapter(categoryInfo.data.get(1));
                mExpertHorLayout1.setOnItemClickListener(this);
                if(null != categoryInfo.data.get(1).child && categoryInfo.data.get(1).child.size()>0){
                    mExpertHorLayout1.setVisibility(View.VISIBLE);
                }
            }
            if (categoryInfo.data.size() >= 3) {
                setItemHeight(categoryInfo.data.get(2).child.size(),mExpertHorLayout2);
                mExpertHorLayout2.setItemTitle(categoryInfo.data.get(2).name, R.drawable.icon_fengche);
                mExpertHorLayout2.initAdapter(categoryInfo.data.get(2));
                mExpertHorLayout2.setOnItemClickListener(this);
                if(null != categoryInfo.data.get(2).child && categoryInfo.data.get(2).child.size()>0){
                    mExpertHorLayout2.setVisibility(View.VISIBLE);
                }
            }
            if (categoryInfo.data.size() >= 4) {
                setItemHeight(categoryInfo.data.get(3).child.size(),mExpertHorLayout3);
                mExpertHorLayout3.setItemTitle(categoryInfo.data.get(3).name, R.drawable.icon_muma);
                mExpertHorLayout3.initAdapter(categoryInfo.data.get(3));
                mExpertHorLayout3.setOnItemClickListener(this);
                if(null != categoryInfo.data.get(3).child && categoryInfo.data.get(3).child.size()>0){
                    mExpertHorLayout3.setVisibility(View.VISIBLE);
                }
            }
            if (categoryInfo.data.size() >= 5) {
                setItemHeight(categoryInfo.data.get(4).child.size(),mExpertHorLayout4);
                mExpertHorLayout4.setItemTitle(categoryInfo.data.get(4).name, R.drawable.icon_book);
                mExpertHorLayout4.initAdapter(categoryInfo.data.get(4));
                mExpertHorLayout4.setOnItemClickListener(this);
                if(null != categoryInfo.data.get(4).child && categoryInfo.data.get(4).child.size()>0){
                    mExpertHorLayout4.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setItemHeight(int count,ExpertHorLayout mExpertHorLayout){
        int lines = 1;
        if(count % 3==0){
            lines = count / 3;
        }else{
            lines = count/3 + 1;
        }
        ViewGroup.LayoutParams params = mExpertHorLayout.getLayoutParams();
        if(lines == 1){
            //左侧图标的高度+文字距离图标的高度+文字高度+最外层上线padding
            params.height = ScreenUtil.dip2px(getContext(),getResources().getDimension(R.dimen.item_left_icon_height)+6+21+20);
        }else{
            params.height = lines * ScreenUtil.dip2px(getContext(),
                    28+17+4+20);
        }

        mExpertHorLayout.setLayoutParams(params);
    }

    private void getInfoList() {
        Api.getInstance().getCategoryList(new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                if (isSuccess) {
                    mJmRecyclerView.showNormal();
                    mJmRecyclerView.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    categoryInfo = gson.fromJson(result.toString(), CategoryInfo.class);
                    initViews();
                } else {
                    mJmRecyclerView.showFail();
                }
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                mJmRecyclerView.showNetErr();
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                mJmRecyclerView.showFail();
            }
        });
    }

    // 搜索
    private void toSearch(){
        if(TextUtils.isEmpty(searchEt.getText().toString())){
            ToastUtil.showToast(getActivity(),"请输入搜索内容");
            return;
        }
        startListActivity(null);
    }

    @OnClick({R.id.right_icon, R.id.topBtn1, R.id.topBtn2, R.id.topBtn3, R.id.topBtn4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.right_icon:
                toSearch();
                break;
            case R.id.topBtn1:
                // 顶部按钮1
                startListActivity(categoryInfo.data.get(0).child.get(0));
                break;
            case R.id.topBtn2:
                // 顶部按钮2
                startListActivity(categoryInfo.data.get(0).child.get(1));
                break;
            case R.id.topBtn3:
                // 顶部按钮3
                startListActivity(categoryInfo.data.get(0).child.get(2));
                break;
            case R.id.topBtn4:
                // 顶部按钮4
                startListActivity(categoryInfo.data.get(0).child.get(3));
                break;
        }
    }

    @Override
    public void onBtnClick(CategoryInfoChild mExportItemInfo, int position) {
        startListActivity(mExportItemInfo);
    }

    private void startListActivity(CategoryInfoChild mExportItemInfo) {
        Intent intent = new Intent(getActivity(), LikedActivity.class);
        if(null == mExportItemInfo){//搜索
            intent.putExtra(LikedActivity.KEY_PARAM_TYPE, 1);
            intent.putExtra(LikedActivity.KEY_PARAM_SEARCH_CONTENT,searchEt.getText().toString());
            intent.putExtra(LikedActivity.KEY_PARAM_TITLE,"搜索");
        }else{ //专家严选
            intent.putExtra(LikedActivity.KEY_PARAM_TYPE, 2);
            intent.putExtra(LikedActivity.KEY_PARAM_TITLE,mExportItemInfo.name);
            intent.putExtra(LikedActivity.KEY_PARAM_INFO, mExportItemInfo);
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
