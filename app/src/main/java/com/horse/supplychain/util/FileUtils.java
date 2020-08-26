package com.horse.supplychain.util;

import java.io.File;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class FileUtils {
	private static final String TAG = "FileUtils";

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}

	public static String mkdirs(String path) {
		String sdcard = getSDPath();
		if (path.indexOf(getSDPath()) == -1) {
			path = sdcard + (path.indexOf("/") == 0 ? "" : "/") + path;
		}
		File destDir = new File(path);
		if (!destDir.exists()) {
			path = makedir(path);
			if (path == null) {
				return null;
			}
		}
		return path;
	}

	public static boolean isExists(String sPath){
		File destDir = new File(sPath);
		return destDir.exists();
	}

	private static String makedir(String path) {
		String sdPath = getSDPath();
		String[] dirs = path.replace(sdPath, "").split("/");
		StringBuffer filePath = new StringBuffer(sdPath);
		for (String dir : dirs) {
			if (!"".equals(dir) && !dir.equals(sdPath)) {
				filePath.append("/").append(dir);
				File destDir = new File(filePath.toString());
				if (!destDir.exists()) {
					boolean b = destDir.mkdirs();
					if (!b) {
						return null;
					}
				}
			}
		}
		return filePath.toString();
	}

	public static String mkdirs(Context context, String path) {
		String sdcard = getSDPath();
		if (path.indexOf(getSDPath()) == -1) {
			path = sdcard + (path.indexOf("/") == 0 ? "" : "/") + path;
		}
		File destDir = new File(path);
		if (!destDir.exists()) {
			path = makedir(context,path);
			if (path == null) {
				return null;
			}
		}
		return path;
	}

	private static String makedir(Context context,String path) {
		String sdPath = getSDPath();
		String[] dirs = path.replace(sdPath, "").split("/");
		StringBuffer filePath = new StringBuffer(sdPath);
		for (String dir : dirs) {
			if (!"".equals(dir) && !dir.equals(sdPath)) {
				filePath.append("/").append(dir);
				File destDir = new File(filePath.toString());
				if (!destDir.exists()) {
					//boolean b = destDir.mkdirs();
					int b=mkFolder(context,filePath.toString());
					if (b!=0) {
						return null;
					}
				}
			}
		}
		return filePath.toString();
	}

	public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

	public static int mkFolder(Context context, String folderName){ // make a folder under Environment.DIRECTORY_DCIM
		LogUtil.d(TAG, "folderName: " + folderName);

		String state = Environment.getExternalStorageState();

		LogUtil.d(TAG, "state: " + state);

		if (!Environment.MEDIA_MOUNTED.equals(state)){
			LogUtil.d(TAG, "Error: external storage is unavailable");

			return 0;
		}

		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			LogUtil.d(TAG, "Error: external storage is read only.");

			return 0;
		}

		LogUtil.d(TAG, "External storage is not read only or unavailable");

		if (ContextCompat.checkSelfPermission(context, // request permission when it is not granted.
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			LogUtil.d(TAG, "permission:WRITE_EXTERNAL_STORAGE: NOT granted!");

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				// Show an expanation to the user *asynchronously* -- don't block

				// this thread waiting for the user's response! After the user

				// sees the explanation, try again to request the permission.
			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions((Activity) context,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}

		//File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),folderName);
		File folder = new File(Environment.getExternalStorageDirectory(),folderName);
		int result = 0;

		LogUtil.d(TAG, "folder: " + folder.toString());

		if (folder.exists()) {
			LogUtil.d(TAG,"folder exist:"+folder.toString());
			result = 2; // folder exist
		}else{
			try {
				if (folder.mkdir()) {
					LogUtil.d(TAG, "folder created:" + folder.toString());
					result = 1; // folder created
				} else {
					LogUtil.d(TAG, "creat folder fails:" + folder.toString());
					result = 0; // creat folder fails
				}
			}catch (Exception ecp){
				ecp.printStackTrace();
			}
		}

		return result;
	}


}
