package com.horse.supplychain.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PermisionUtils {

    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private AppCompatActivity activity;

    //权限组  当同一组内的一个权限被授权 ，组内的其他权限同时授权
    public void getPermission(AppCompatActivity activity) {
        this.activity = activity;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();

            if (!addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {// <!-- STORAGE 存储组 -->
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {// <!-- STORAGE 存储组 -->
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!addPermission(Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (!addPermission(Manifest.permission.VIBRATE)) {
                permissions.add(Manifest.permission.VIBRATE);
            }
            if (!addPermission(Manifest.permission.WAKE_LOCK)) {
                permissions.add(Manifest.permission.WAKE_LOCK);
            }

            if (permissions.size() > 0) {
                activity.requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }

    private boolean addPermission(String permission) {
        boolean hasPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
            } else {
                hasPermission = true;
            }
        }
        return hasPermission;
    }
}
