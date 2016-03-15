package com.hy.gametools.manager;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

public class HY_SplashRes extends HY_SplashBaseImage
{
    private int resourceId;

    public HY_SplashRes(View layout, ImageView imageView, int id)
    {
        super(layout, imageView);
        this.resourceId = id;
    }

    void loadImage(Activity context, ImageView imageView,
            HY_SplashBaseImage.LoadSplashCallback callback)
    {
        imageView.setImageResource(this.resourceId);
        callback.onLoadSuccess();
    }
}
