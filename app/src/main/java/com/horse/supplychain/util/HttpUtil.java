package com.horse.supplychain.util;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    private static final String TAG="HttpUtil";

    private static int BUFFER_SIZE=10 * 1024;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();

    public static String post(String sUrl, String sJson){
        Log.d(TAG,sUrl + ", request: " +sJson);

        try{
            RequestBody body = RequestBody.create(sJson, JSON);
            Request request = new Request.Builder()
                    .url(sUrl)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {

                String sResp =  response.body().string();

                Log.d(TAG,sUrl + ", response: " + sResp);

                return sResp;
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String get(String sUrl) {
        Log.d(TAG,sUrl);

        Request request = new Request.Builder()
                .url(sUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {

                String sResp =  response.body().string();

                Log.d(TAG,sUrl + ", response: " + sResp);

                return sResp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String downloadFile(String sUrl, String sSaveDir) {
        String sFilePath = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(sUrl);
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
                dir.mkdir();
            }

            String fileName = sUrl.substring(sUrl.lastIndexOf("/") + 1, sUrl.length());
            File apkFile = new File(dir, fileName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
                    //notificationHelper.updateProgress(progress);
                }
                oldProgress = progress;
            }
            // 下载完成
            sFilePath = sSaveDir  + File.separator + fileName;
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
}
