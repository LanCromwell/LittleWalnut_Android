package cn.baby.love.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.logging.Logger;

import cn.baby.love.R;
import cn.baby.love.common.bean.CategoryInfoChild;
import cn.baby.love.common.utils.GlideApp;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.ScreenUtil;


/**
 *
 */
public class ExportItemAdapter extends BaseAdapter {
    private Context context;
    private List<CategoryInfoChild> infoList;
    private final LayoutInflater inflater;

    public ExportItemAdapter(Context context, List<CategoryInfoChild> infoList) {
        this.context = context;
        this.infoList = infoList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public CategoryInfoChild getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if (null == convertView) {
            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_exam_card_item, null);
            mViewHolder.itemTv = (TextView) convertView.findViewById(R.id.itemBtn);
            mViewHolder.topImg = convertView.findViewById(R.id.topImg);
//            mViewHolder.itemTv.setTextSize(context.getResources().getDimension(R.dimen.export_text_size));
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        int width = (ScreenUtil.getScreenWidth(context) - ScreenUtil.dip2px(context , 160))/3;
        ViewGroup.LayoutParams params = mViewHolder.itemTv.getLayoutParams();
        params.width = width;
        mViewHolder.itemTv.setLayoutParams(params);

        CategoryInfoChild item = getItem(position);
        mViewHolder.itemTv.setText(item.name);

        GlideApp.with(context)
                .load(item.img)
                .placeholder(R.drawable.icon_expert_default)
                .error(R.drawable.icon_expert_default)
                .into(mViewHolder.topImg);

        return convertView;
    }

    public class ViewHolder {
        public TextView itemTv;
        public ImageView topImg;
    }
}
