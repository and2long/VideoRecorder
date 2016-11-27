package com.and2long.videorecorder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * -->无预览录制视频
 *
 * 思路:开启一个服务,在服务创建成功时开启一个悬浮窗口,
 * 悬浮窗口中放一个布局界面来预览摄像头画面;
 * 将悬浮窗口大小设置成1px.
 *
 * 注意:录制声音无法取消.
 */
public class MainActivity extends AppCompatActivity {

    //所需权限
    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};
    //权限申请码
    private static int REQUEST_PERMISSION_CODE = 100;
    private Intent recordServiceIntent;

    /**
     * onCreate方法
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasAllPermissions()) {
            //动态申请权限
            requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        } else {
            //开启视频录制服务
            startRecordService();
        }
    }

    /**
     * 检查是否具有全部所需权限
     * @return
     */
    private boolean hasAllPermissions() {
        //检查权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 开启视频录制服务
     */
    private void startRecordService() {
        //开启服务
        recordServiceIntent = new Intent(this, RecordService.class);
        startService(recordServiceIntent);
    }

    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE && hasAllPermissons(grantResults)) {
            //开启视频录制服务
            startRecordService();
        } else {
            //显示权限提示对话框
            showPermissionDialog();
        }
    }

    /**
     * 提示对话框
     */
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.need_permission);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //退出.
                finish();
            }
        });
        builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //进入设置界面手动授予权限
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 判断是否具有所有权限
     *
     * @param grantResults
     * @return
     */
    private boolean hasAllPermissons(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //结束视频录制服务
        stopService(recordServiceIntent);
    }
}
