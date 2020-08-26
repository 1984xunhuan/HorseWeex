package com.horse.supplychain.serivce;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.android.volley.manager.LoadController;
import com.android.volley.manager.RequestManager;
import com.leo618.zip.IZipCallback;
import com.leo618.zip.ZipManager;
import com.horse.supplychain.util.LogUtil;
import com.horse.supplychain.util.SharedPreferencesUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResourcePackageService extends AsyncTask<String,Integer,Void> {
    private static String TAG="ResourcePackageService";

    private static int BUFFER_SIZE=10 * 1024;

    public String sUrl;
    public String sSaveDir;
    public String sVerNo;
    private Context context;
    private ProgressDialog progressDialog;

    private String sDownloadedResourcePkgPath;

    public ResourcePackageService(Context context, String sUrl) {
        this.context = context;
        this.sUrl = sUrl;
    }

    public ResourcePackageService(Context context, String sUrl, String sSaveDir, String sVerNo) {
        this.context = context;
        this.sUrl = sUrl;
        this.sSaveDir = sSaveDir;
        this.sVerNo = sVerNo;
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

        checkUpdateResourcePackage();

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


    private void checkUpdateResourcePackage() {

        //下载资源包
        sDownloadedResourcePkgPath = downloadFile(sUrl);
        //解压资源包
        unzipResourcePackage();
        //更新配置文件资源包版本号
        SharedPreferencesUtil.putString(context,"curResourcePkgVersionNo", sVerNo);
    }


    public String downloadFile(String sDownloadUrl) {
        String sFilePath = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(sDownloadUrl);
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
            File dir = new File(sSaveDir);
            if(!dir.exists())
            {
                dir.mkdirs();
            }

            String fileName = sDownloadUrl.substring(sDownloadUrl.lastIndexOf("/") + 1, sDownloadUrl.length());
            File file = new File(dir, fileName);

            out = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
                    publishProgress(progress);
                }

                oldProgress = progress;
            }
            // 下载完成
            sFilePath = sSaveDir  +  fileName;
        } catch (Exception e) {
            Log.e(TAG, "download file error:" + e.getMessage());
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

        return sFilePath;
    }

    public  void unzipResourcePackage(){
        LogUtil.d(TAG, "sDownloadedResourcePkgPath: " + sDownloadedResourcePkgPath);
        LogUtil.d(TAG, "sSaveDir: " + sSaveDir);

        ZipManager.unzip(sDownloadedResourcePkgPath, sSaveDir, "123456",new IZipCallback() {
            @Override
            public void onStart() {
                //loadingShow(-1);
            }

            @Override
            public void onProgress(int percentDone) {
                //loadingShow(percentDone);
            }

            @Override
            public void onFinish(boolean success) {
                //loadingHide();
                //toast(success ? "成功" : "失败");
            }
        });
    }

    public LoadController checkUpdateResourcePackage(RequestManager.RequestListener requestListener) {
        return RequestManager.getInstance().post(sUrl, "", requestListener, 0);
    }
}
