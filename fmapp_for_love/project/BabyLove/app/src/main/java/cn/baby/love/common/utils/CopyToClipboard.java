package cn.baby.love.common.utils;

import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by wangguangbin on 2018/9/30.
 * 复制文本到剪切板
 */
public class CopyToClipboard {
    /**
     * 复制到剪切板并弹Toast
     * @param copyStr 需要复制的内容
     * @param toastStr 需要提示用户的内容
     */
    public static void onClickCopyWithToast(Context context, String copyStr, String toastStr){
        onClickCopy(context,copyStr);
        ToastUtil.showToast(context,toastStr);
    }

    /**
     * 复制到剪切板
     * @param copyStr 需要复制的内容
     */
    public static void onClickCopy(Context context, String copyStr) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(copyStr);
    }

}
