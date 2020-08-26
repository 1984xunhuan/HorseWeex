package com.horse.supplychain.serivce;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.android.volley.manager.LoadController;
import com.android.volley.manager.RequestManager;
import com.horse.supplychain.util.ApkUtils;
import com.horse.supplychain.util.LogUtil;
import com.horse.supplychain.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class APKService extends AsyncTask<String,Integer,Void> {
    private static String TAG="APKService";

    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
    private Context context;
    private String sUrl;
    private ProgressDialog progressDialog;

    public APKService(Context context, String sUrl) {
        this.context = context;
        this.sUrl = sUrl;
    }

    @Override
    protected void onPreExecute() {
        LogUtil.d(TAG,"onPreExecute");
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(String... strings) {
        LogUtil.d(TAG,"doInBackground");

        downloadAPK(sUrl);

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        LogUtil.d(TAG,"onProgressUpdate: " + values[0]);
        super.onProgressUpdate(values);

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        LogUtil.d(TAG,"onPostExecute");
        super.onPostExecute(aVoid);

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void downloadAPK(String sDownloadUrl) {

        String urlStr = sDownloadUrl;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            File dir = context.getCacheDir();
            String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            File apkFile = new File(dir, apkName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
                    //进度处理
                    publishProgress(progress);
                }
                oldProgress = progress;
            }

            ApkUtils.installAPk(context, apkFile);
        } catch (Exception e) {
            Log.e(TAG, "download apk file error:" + e.getMessage());
            ToastUtil.showToast((Activity) context, "更新APK失败： " + e.getMessage());
            ((Activity) context).finish();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    public LoadController checkUpdateAPK(RequestManager.RequestListener requestListener) {
        return RequestManager.getInstance().post(sUrl, "", requestListener, 0);
    }
}
