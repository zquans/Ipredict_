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

    private float startY, endY;

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
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                LogUtil.i("---" + startY);
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                endY = event.getY();
                LogUtil.i(endY + " ------" + startY);
                if (mListener != null) {
                    if (endY > startY) {
                        mListener.show();
                    }
                    if (startY > endY) {
                        mListener.miss();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}