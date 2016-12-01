package com.and2long.videorecorder;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by and2long on 2016/11/25.
 */

public class RecordThread extends Thread {
    private static final String TAG = "RecordThread";
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private SurfaceHolder surfaceHolder;
    private Runnable runnable;
    //录制状态
    private boolean isRecording = false;
    //文件保存路径
    private File dir;
    private Handler handler;

    public RecordThread(Context context, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        dir = context.getExternalFilesDir(null);
    }

    @Override
    public void run() {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    isRecording = false;
                }
                //开始录制
                startRecord();
                //定时执行
                handler.postDelayed(this, 5 * 1000);
            }
        };
        handler.post(runnable);
    }


    /**
     * 获取摄像头实例对象
     *
     * @return
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0);
        } catch (Exception e) {
            // 打开摄像头错误
            Log.i(TAG, "打开摄像头错误");
        }
        return c;
    }

    /**
     * 开始录像
     */
    public void startRecord() {
        try {
            mMediaRecorder = new MediaRecorder();
            if (mCamera == null) {
                initCamera();
            }
            //取消录制后再次进行录制时必须加如下两步操作，不然会报错
            mCamera.lock();
            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setOrientationHint(90);
            //设置录制视频源为Camera(相机)
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
            //设置录制文件质量，格式，分辨率之类，这个全部包括了
            mMediaRecorder.setProfile(CamcorderProfile
                    .get(CamcorderProfile.QUALITY_480P));
            mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            //设置视频文件输出的路径
            String savePath = new File(dir, getSaveName()).getAbsolutePath();
            mMediaRecorder.setOutputFile(savePath);
            //准备录制
            mMediaRecorder.prepare();
            //开始录制
            mMediaRecorder.start();
            isRecording = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化摄像头
     */
    private void initCamera() {
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        //解锁camera
        mCamera.unlock();
    }

    /**
     * 停止录像
     */
    public void stopRecord() {
        handler.removeCallbacks(runnable);
    }

    /**
     * 保存文件命名
     *
     * @return
     */
    public String getSaveName() {
        String saveName = null;
        saveName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".mp4";
        return saveName;
    }


}
