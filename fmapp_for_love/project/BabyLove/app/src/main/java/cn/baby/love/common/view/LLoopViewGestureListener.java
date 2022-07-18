package cn.baby.love.common.view;

import android.view.MotionEvent;

import com.contrarywind.view.WheelView;

/**
 * Created by wangguangbin on 2019/1/7.
 */

public class LLoopViewGestureListener  extends android.view.GestureDetector.SimpleOnGestureListener {
    private final LWheelView wheelView;


    public LLoopViewGestureListener(LWheelView wheelView) {
        this.wheelView = wheelView;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        wheelView.scrollBy(velocityY);
        return true;
    }
}
