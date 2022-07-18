package cn.baby.love.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.baby.love.R;
import cn.baby.love.common.bean.LikeInfo;
import cn.baby.love.common.utils.StrUtil;

/**
 * Created by wangguangbin on 2018/11/16.
 * 收藏列表
 */

public class LikeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LikeInfo> infoList;
    private Context context;
    private LayoutInflater inflater;
    public LikeInfo curLikeInfo = null;

    public LikeListAdapter(Context context, List<LikeInfo> msgList) {
        this.infoList = msgList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_like_item, parent, false);
        return new ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LikeInfo info = infoList.get(position);
        ViewHolderItem mViewHolderItem = (ViewHolderItem) holder;
        mViewHolderItem.titleTv.setText(info.title);
        if (info.is_study == 1) {//已读
            mViewHolderItem.audioTimeTv.setTextColor(context.getResources().getColor(R.color.gray_999));
            mViewHolderItem.titleTv.setTextColor((null != curLikeInfo && curLikeInfo.id == info.id) ? context.getResources().getColor(R.color.login_btn_color) : context.getResources().getColor(R.color.gray_999));
        } else {//未读
            mViewHolderItem.audioTimeTv.setTextColor(context.getResources().getColor(R.color.gray_666));
            mViewHolderItem.titleTv.setTextColor((null != curLikeInfo && curLikeInfo.id == info.id) ? context.getResources().getColor(R.color.login_btn_color) : context.getResources().getColor(R.color.like_list_item_name_color));
        }
        mViewHolderItem.onclick(info, position);
        mViewHolderItem.audioTimeTv.setText(info.duration);
        mViewHolderItem.iconLikeCb.setBackgroundResource((1 == info.is_collect) ? R.drawable.icon_like_yes : R.drawable.icon_like_no_black);
        mViewHolderItem.iconPlayCb.setBackgroundResource(info.isPlaying ? R.drawable.icon_playing_little : R.drawable.icon_no_play_little);
        if(null != info.tag && info.tag.size()>0){
            mViewHolderItem.tag1.setVisibility(View.VISIBLE);
            mViewHolderItem.tag2.setVisibility(View.VISIBLE);
            try {
                mViewHolderItem.tag1.setText(info.tag.get(0));
                mViewHolderItem.tag2.setText(info.tag.get(1));
            }catch (Exception e){e.printStackTrace();}
        }else{
            mViewHolderItem.tag1.setVisibility(View.GONE);
            mViewHolderItem.tag2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.audioTimeTv)
        TextView audioTimeTv;
        @BindView(R.id.iconPlayCb)
        ImageView iconPlayCb;
        @BindView(R.id.iconShareImg)
        ImageView iconShareImg;
        @BindView(R.id.iconLikeCb)
        ImageView iconLikeCb;

        @BindView(R.id.tag1)
        TextView tag1;
        @BindView(R.id.tag2)
        TextView tag2;

        View itemView;

        public ViewHolderItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }

        public void onclick(final LikeInfo info, final int position) {
            iconShareImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemClickListener_clickShareIcon(info, position);
                    }
                }
            });
            //播放
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemClickListener_clickPlayIcon(info, position);
                    }
                }
            });
            //播放
            iconPlayCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemClickListener_clickPlayIcon(info, position);
                    }
                }
            });

            iconLikeCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemClickListener_clicklikedIcon(info, position);
                    }
                }
            });
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener_clickShareIcon(LikeInfo info, int position);

        void onItemClickListener_clicklikedIcon(LikeInfo info, int position);

        void onItemClickListener_clickPlayIcon(LikeInfo info, int position);
    }


}
