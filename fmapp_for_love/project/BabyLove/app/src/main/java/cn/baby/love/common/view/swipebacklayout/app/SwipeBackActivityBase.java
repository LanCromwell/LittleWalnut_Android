package cn.baby.love.common.view.swipebacklayout.app;


import cn.baby.love.common.view.swipebacklayout.SwipeBackLayout;

public interface SwipeBackActivityBase {
    SwipeBackLayout getSwipeBackLayout();

    void setSwipeBackEnable(boolean enable);

    void scrollToFinishActivity();

}
