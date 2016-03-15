package com.hy.gametools.manager;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.hy.gametools.utils.HyLog;

public abstract class HY_SplashBaseImage implements HY_ISplash
{
    protected static final String TAG = "HY";
    private View layout;
    private ImageView imageView;

    public HY_SplashBaseImage(View layout, ImageView imageView)
    {
        this.layout = layout;
        this.imageView = imageView;
    }

    abstract void loadImage(Activity paramActivity, ImageView paramImageView,
            LoadSplashCallback paramLoadSplashCallback);

    public void play(final Activity context, final HY_SplashListener listener)
    {
        loadImage(context, this.imageView, new LoadSplashCallback()
        {
            public void onLoadSuccess()
            {
                HY_SplashBaseImage.this.playSplash(context, listener);
            }

            public void onLoadFailed()
            {
                listener.onFinish();
            }
        });
    }

    public void playSplash(Activity context, final HY_SplashListener listener)
    {
        Animation animation = getAnimation();
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationStart(Animation paramAnimation)
            {
                HyLog.d(TAG, "animation start");
            }

            public void onAnimationRepeat(Animation paramAnimation)
            {
                HyLog.d(TAG, "animation repeat");
            }

            public void onAnimationEnd(Animation paramAnimation)
            {
                HyLog.d(TAG, "animation end");
                HY_SplashBaseImage.this.layout.setVisibility(View.INVISIBLE);
                listener.onFinish();
            }
        });
        HyLog.d(TAG, "start animat ion");
        this.layout.startAnimation(animation);
        this.layout.setVisibility(0);
    }

    private Animation getAnimation()
    {
        Animation fadeIn = new AlphaAnimation(0.0F, 1.0F);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500L);

        Animation fadeOut = new AlphaAnimation(1.0F, 0.0F);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(1500L);
        fadeOut.setDuration(500L);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);

        return animation;
    }

    public static abstract interface LoadSplashCallback
    {
        public abstract void onLoadSuccess();

        public abstract void onLoadFailed();
    }
}
