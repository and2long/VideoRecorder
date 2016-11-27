package com.and2long.videorecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by and2long on 2016/11/25.
 */

public class RecordService extends Service implements SurfaceHolder.Callback {

    private WindowManager windowManager;
    private SurfaceView mSurfaceView;
    private RecordThread recordThread;
    private View mRecorderView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //开启一个悬浮窗
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //预览界面
        mRecorderView = LayoutInflater.from(this).inflate(R.layout.layout_surface_view, null);
        mSurfaceView = (SurfaceView) mRecorderView.findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);
        //设置参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        //设置悬浮窗的位置,默认居中
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = 1;
        params.height = 1;
        //添加布局
        windowManager.addView(mRecorderView, params);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //开一个子线程录制视频
        recordThread = new RecordThread(this, surfaceHolder);
        recordThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onDestroy() {
        windowManager.removeView(mRecorderView);
    }
}
