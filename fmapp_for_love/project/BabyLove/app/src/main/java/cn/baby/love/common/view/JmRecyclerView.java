package cn.baby.love.common.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import cn.baby.love.R;

/**
 * Created by wangguangbin on 2018/11/22.
 */

public class JmRecyclerView extends LinearLayout implements OnRefreshLoadMoreListener{

    private View rootView;
    private EmptyView mEmptyView;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private OnRefreshListener mOnRefreshListener;
    private DividerItemDecoration mDividerItemDecoration;
    private int curPage = 1;
    private int mDownY;


    public JmRecyclerView(Context context) {
        super(context);
        initView();
    }

    public JmRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public JmRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView(){
        setOrientation(LinearLayout.VERTICAL);
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_jm_recyclerview,null);
        addView(rootView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

        mSmartRefreshLayout = rootView.findViewById(R.id.refreshLayout);
        mEmptyView = rootView.findViewById(R.id.emptyView);
        mRecyclerView = rootView.findViewById(R.id.jmRecycleView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mDividerItemDecoration =new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        mEmptyView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRefreshListener.onRefresh();
            }
        });

        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);

    }

    public void autoRefresh(){
        mSmartRefreshLayout.autoRefresh();
    }

    public void autoLoadMore(){
        mSmartRefreshLayout.autoLoadMore();
    }

    public int getCurPage(){
        return curPage;
    }

    public void setCurPage(int curPage){
        this.curPage = curPage;
    }

    public EmptyView getEmptyView(){
        return mEmptyView;
    }
    public RecyclerView getmRecyclerView(){
        return mRecyclerView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN)
            mDownY = (int) ev.getY();
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * 主要用来子View获取父View（当前组件）的touch位置（e.getY()）
     * 这里用来在精选首页上下滑动隐藏topbar组件
     * @return
     */
    public int getTouchDownY(){
        return mDownY;
    }

    /**
     * 移除分割线
     */
    public void removeItemDecoration(){
        mRecyclerView.removeItemDecoration(mDividerItemDecoration);
    }

    //显示异常布局
    private void showException(){
        finishRefresh();
        if(curPage == 1){ //只有第一页加载失败才会显示异常布局
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            curPage --;
        }

    }

    private void finishRefresh(){
        mSmartRefreshLayout.finishLoadMore();
        mSmartRefreshLayout.finishRefresh();
    }

    public void showNormal(){
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        finishRefresh();
    }

    /**
     * 显示数据为空
     */
    public void showEmpty(){
        mEmptyView.showEmpty();
        showException();
    }

    /**
     * 显示数据为空
     */
    public void showEmpty(String msg,int drawableId){
        mEmptyView.showEmpty(msg,drawableId);
        showException();
    }

    /**
     * 显示无网络
     */
    public void showNetErr(){
        mEmptyView.showNetErr();
        showException();
    }

    /**
     * 显示无网络
     */
    public void showNetErr(String errMsg,int drawableId){
        mEmptyView.showNetErr(errMsg,drawableId);
        showException();
    }

    /**
     * 显示加载失败
     */
    public void showFail(){
        mEmptyView.showFail();
        showException();
    }

    /**
     * 显示加载失败
     */
    public void showFail(String errMsg,int drawableId){
        mEmptyView.showFail(errMsg,drawableId);
        showException();
    }

    /**
     * 恢复没有更多数据的原始状态
     * @param noMoreData 是否有更多数据
     * @return SmartRefreshLayout
     */
    public void setNoMoreData(boolean noMoreData){
        if(noMoreData){
            mSmartRefreshLayout.finishLoadMoreWithNoMoreData();
        }else{
            mSmartRefreshLayout.setNoMoreData(noMoreData);
        }
    }

    /**
     * 设置上拉和下拉的监听
     * @param mOnRefreshListener
     * @param isRefreshEnable 是否下拉刷新
     * @param isLoadMoreEnable 是否上拉加载
     */
    public void setOnRefreshListener(boolean isRefreshEnable,boolean isLoadMoreEnable, OnRefreshListener mOnRefreshListener){
        this.mOnRefreshListener = mOnRefreshListener;
        mSmartRefreshLayout.setEnableRefresh(isRefreshEnable);
        mSmartRefreshLayout.setEnableLoadMore(isLoadMoreEnable);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if(null != mOnRefreshListener){
            curPage++;
            mOnRefreshListener.onLoadMore();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if(null != mOnRefreshListener){
            curPage = 1;
            mOnRefreshListener.onRefresh();
        }
    }

    public interface OnRefreshListener{
        void onRefresh();
        void onLoadMore();
    }

}
