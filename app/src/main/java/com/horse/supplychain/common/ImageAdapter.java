package com.horse.supplychain.common;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ImageView;

import com.horse.supplychain.util.ConfigUtil;
import com.horse.supplychain.util.LoadLocalImageUtil;
import com.horse.supplychain.util.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.weex.adapter.IWXImgLoaderAdapter;
import org.apache.weex.common.WXImageStrategy;
import org.apache.weex.dom.WXImageQuality;


public class ImageAdapter implements IWXImgLoaderAdapter {
    private static final String TAG = "ImageAdapter";

    private Context mContext;

    public ImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public void setImage(String url, ImageView view, WXImageQuality quality, WXImageStrategy strategy) {
        LogUtil.d(TAG,"url: " + url);

        //实现你自己的图片下载，否则图片无法显示。
        if (view == null || view.getLayoutParams() == null) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            view.setImageBitmap(null);
            return;
        }
        if (view.getLayoutParams().width <= 0 || view.getLayoutParams().height <= 0) {
            return;
        }

        if(ConfigUtil.industryIdString == null || ConfigUtil.industryIdString.isEmpty()){
            String sSDCardPath = Environment.getExternalStorageDirectory().getPath();
            LogUtil.d(TAG, "sSDCardPath: " + sSDCardPath);
            sSDCardPath +="/horse_weex/";

            if (url.startsWith(sSDCardPath)) {
                //String aa = url.replace("file:///mnt/sdcard/qdonesaas/res/", "");
                //LogUtil.e("aa---industryIdStringnull", aa);
                // String imageUri = "assets://image.png"; // from assets
                LoadLocalImageUtil.getInstance().dispalyFromAssets(sSDCardPath,view);
            } else if (url.startsWith("http")) {
                LogUtil.e("url","网络地址");
                ImageLoader.getInstance().displayImage(url, view);
            }
        }else {
            if (url.startsWith("file://")) {
                String aa = url.replace("file://", "");
                LogUtil.e("aa", aa);
                LoadLocalImageUtil.getInstance().displayFromSDCard(aa, view);
            } else if (url.startsWith("http")) {
                LogUtil.e("url", "网络地址");
                ImageLoader.getInstance().displayImage(url, view);
            }
            else
            {
                LogUtil.e(TAG,"url: " + url);
                LoadLocalImageUtil.getInstance().displayFromSDCard(url, view);
            }
        }
    }
}
