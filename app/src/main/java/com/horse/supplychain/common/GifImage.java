package com.horse.supplychain.common;

import android.content.Context;
import com.horse.supplychain.util.LogUtil;
import org.apache.weex.WXSDKInstance;
import org.apache.weex.ui.action.BasicComponentData;
import org.apache.weex.ui.component.WXComponent;
import org.apache.weex.ui.component.WXComponentProp;
import org.apache.weex.ui.component.WXVContainer;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * weex中扩展gif图片功能
 */
public class GifImage extends WXComponent<GifImageView> {
    private static final String TAG = "ImageAdapter";

    GifImageView gifview;
    Context context;

    public GifImage(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData){
        super(instance, parent,basicComponentData);
    }

    @Override
    protected GifImageView initComponentHostView(Context context) {
        gifview = new GifImageView(context);
        this.context = context;
        return gifview;
    }

    @WXComponentProp(name = "src")//该注解，则为weex中调用的方法名
    public void setSrc(String src){
        LogUtil.e(TAG, src);

        try{
            if (src.startsWith("file://")) {
                src = src.replace("file://", "");
                LogUtil.e(TAG, src);
            }

            GifDrawable gifFromSrc = new GifDrawable(src);
            gifview.setImageDrawable(gifFromSrc);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
