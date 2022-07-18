package cn.baby.love.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.baby.love.R;
import cn.baby.love.common.utils.ScreenUtil;

/**
 * Created by wangguangbin on 2018/11/28.
 */

public class WelcomAdpater extends PagerAdapter {
    private List<Integer> imgList;
    private Context context;

    public WelcomAdpater(Context context, List<Integer> imgList) {
        this.imgList = imgList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_journal_welcome_item, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.image);
        imageView.setImageResource(imgList.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    private OnLastItemClickListener mOnLastItemClickListener;
    public void setOnLastItemClickListener(OnLastItemClickListener mOnLastItemClickListener){
        this.mOnLastItemClickListener = mOnLastItemClickListener;
    }
    public interface OnLastItemClickListener{
        void onItemClick();
    }

}
