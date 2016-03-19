package com.hy.gametools.manager;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import com.hy.gametools.utils.HyLog;

public class HY_SplashSequence
{
    protected static final String TAG = "HY";
    private List<HY_ISplash> list = new ArrayList<HY_ISplash>();

    public void addSplash(HY_ISplash splash)
    {
        this.list.add(splash);
    }

    public void play(Activity context, HY_SplashListener listener)
    {
        playSplashPic(context, listener, 0);
    }

    /**
     * 
     * playSplashPic(播放闪屏图片)
     * 
     * context
     * 
     * listener(闪屏监听，执行完成后回调onFinish方法)
     * 
     * index(播放闪屏图片的索引值)
     */
    private void playSplashPic(final Activity context,
            final HY_SplashListener listener, final int index)
    {
        // 如果index到最后一个，则执行回调方法
        if (index >= this.list.size())
        {
            listener.onFinish();
        }
        // 如果没有执行到最后一个，则不断的按照索引值index的顺序进行播放
        else
        {
            ((HY_ISplash) this.list.get(index)).play(context,
                    new HY_SplashListener()
                    {
                        public void onFinish()
                        {
                            HY_SplashSequence.this.playSplashPic(context,
                                    listener, index + 1);
                        }
                    });
        }
    }
}
