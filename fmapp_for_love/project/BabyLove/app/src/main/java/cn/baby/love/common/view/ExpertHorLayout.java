package cn.baby.love.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.baby.love.R;
import cn.baby.love.adapter.ExportItemAdapter;
import cn.baby.love.common.bean.CategoryInfo;
import cn.baby.love.common.bean.CategoryInfoChild;

/**
 * Created by wangguangbin on 2018/12/23.
 */

public class ExpertHorLayout extends LinearLayout implements AdapterView.OnItemClickListener {

    private GridView mGridView;
    private CategoryInfo mCategoryInfo;
    private ExportItemAdapter adapter;
    private TextView viewTitleTv;
    private ImageView viewTitleIcon;

    public ExpertHorLayout(Context context) {
        super(context);
        initViews();
    }

    public ExpertHorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public ExpertHorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        setOrientation(LinearLayout.HORIZONTAL);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.export_layout,null);
        addView(view);

        mGridView = view.findViewById(R.id.mGridView);
        viewTitleTv = view.findViewById(R.id.viewTitleTv);
        viewTitleIcon = view.findViewById(R.id.titleIcon);
    }

    /**
     * 更新数据
     */
    public void initAdapter(CategoryInfo mCategoryInfo){
        this.mCategoryInfo = mCategoryInfo;
        updateAdapter();
    }

    private void updateAdapter(){
        if (null == adapter) {
            adapter = new ExportItemAdapter(getContext(), mCategoryInfo.child);
            mGridView.setAdapter(adapter);
            mGridView.setOnItemClickListener(this);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置左边标题
     * @param str
     */
    public void setItemTitle(String str,int iconId){
        viewTitleTv.setText(str);
        viewTitleIcon.setBackgroundResource(iconId);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(null != mOnItemClickListener){
            mOnItemClickListener.onBtnClick(mCategoryInfo.child.get(position),position);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public interface OnItemClickListener{
        void onBtnClick(CategoryInfoChild mCategoryInfoChild, int position);
    }

}
