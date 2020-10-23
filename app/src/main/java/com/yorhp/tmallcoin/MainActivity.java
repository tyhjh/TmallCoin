package com.yorhp.tmallcoin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Switch;

import com.yorhp.commonlibrary.util.AccessbilityUtil;
import com.yorhp.recordlibrary.ScreenShotUtil;
import com.yorhp.tmall.service.TmallService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import permison.FloatWindowManager;
import toast.ToastUtil;

public class MainActivity extends AppCompatActivity {

    private Switch swTmall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //请求相关权限
        if (lacksPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }else {
            //开启录屏
            ScreenShotUtil.getInstance().screenShot(MainActivity.this, null);
        }
        swTmall=findViewById(R.id.swTmall);
        swTmall.setOnClickListener(v->{
            //开启悬浮窗
            if (FloatWindowManager.getInstance().applyOrShowFloatWindow(MainActivity.this)) {
                //进入设置界面
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新服务状态
        swTmall.setChecked(AccessbilityUtil.isAccessibilitySettingsOn(this, TmallService.class));
    }

    /**
     * 判断是否缺少权限
     *
     * @param permission
     * @return
     */
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            ToastUtil.showShort("无权限，应用自动关闭");
        }else {
            //开始录屏
            ScreenShotUtil.getInstance().screenShot(MainActivity.this, null);
        }
    }
}