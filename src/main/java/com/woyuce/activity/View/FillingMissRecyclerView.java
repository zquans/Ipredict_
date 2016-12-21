package com.woyuce.activity.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.woyuce.activity.Utils.LogUtil;

/**
 * Created by Administrator on 2016/12/20.
 */
public class FillingMissRecyclerView extends RecyclerView {

    private Integer startY;

    private FillingMissListener mListener;

    public void setMissListener(FillingMissListener listener) {
        this.mListener = listener;
    }

    public FillingMissRecyclerView(Context context) {
        super(context);
    }

    public FillingMissRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FillingMissRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        float rawY = event.getRawY();
        int action = event.getAction();
        LogUtil.i(y + "---" + rawY + "---" + startY);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startY = y;
                LogUtil.i(y + "---" + rawY + "---" + startY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mListener != null) {
                    if (startY - y > 100) {
                        mListener.miss();
                    }
                    if (y - startY > 300) {
                        mListener.show();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }
}