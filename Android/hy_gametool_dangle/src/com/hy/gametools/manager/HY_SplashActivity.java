package com.hy.gametools.manager;

import java.io.IOException;
import java.util.Arrays;

import com.hy.gametools.utils.Constants;
import com.hy.gametools.utils.HyLog;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @CLASS:HY_SplashBaseActivity
 * @AUTHOR:smile
 **/

public abstract class HY_SplashActivity extends Activity
{

    private static final String TAG = "HY";
    private RelativeLayout mRel_lin;
    private ImageView imageView;
    private HY_SplashSequence sequence = new HY_SplashSequence();

    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        // 全屏无标题
        HY_GameInit.initHYGameInfo(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 相对全布局
        this.mRel_lin = new RelativeLayout(this);
        // 设置背景颜色,回调给用户进行设置
        this.mRel_lin.setBackgroundColor(getBackgroundColor());
        this.mRel_lin.setVisibility(4);
        RelativeLayout.LayoutParams mRel_lin_params = new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        // 相对布局内的图片布局
        this.imageView = new ImageView(this);
        this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        this.mRel_lin.addView(this.imageView, imageViewParams);

        // 需要展示的闪屏图片保存到assets文件下assetDir下面
        String assetDir = "splash_photo";
        String[] assetPaths = new String[0];
        try
        {
            assetPaths = getAssets().list(assetDir);
        }
        catch (IOException e)
        {
            HyLog.e(TAG, "load assets splash error" + e.toString());
        }
        HyLog.d(TAG, "assetsPaths size " + assetPaths.length);
        int count = 0;
        // 对数组assetPaths进行排序，按照顺序显示
        Arrays.sort(assetPaths);
        for (String str : assetPaths)
        {
            HyLog.d(TAG, "assets splash " + str);
        }

        // 资源文件前缀
        String resourcePrefix = "splash_photo";
        count = 0;
        while (true)
        {
            if (count < assetPaths.length)
            {
                this.sequence.addSplash(new HY_SpashAsset(this.mRel_lin,
                        this.imageView, assetDir + "/" + assetPaths[count]));
            }
            else
            {
                int id = getResources().getIdentifier(resourcePrefix + count,
                        "drawable", getPackageName());
                if (id == 0)
                {
                    break;
                }
                HyLog.d(TAG, "此代码不执行，因为闪屏图片默认都要求用户保存到assets下，并非drawable下");
                System.out.println("---------asdasd---");

                this.sequence.addSplash(new HY_SplashRes(this.mRel_lin,
                        this.imageView, id));
            }
            count++;
        }

        setContentView(this.mRel_lin, mRel_lin_params);
    }

    protected void onResume()
    {
        super.onResume();
        HyLog.d(TAG, "onResume");

        this.sequence.play(this, new HY_SplashListener()
        {
            public void onFinish()
            {
                HY_SplashActivity.this.onSplashStop();
            }
        });
    }

    /**
     * @description: 当闪屏PNG图片无法铺满部分机型的屏幕时，设置与闪屏颜色配合的背景色会给用户更好的体验
     * @author:smile
     * @return:int
     * @return
     */

    public abstract int getBackgroundColor();

    /**
     * @description:闪屏结束后，启动游戏的Activity
     * @author:smile
     * @return:void
     */

    public abstract void onSplashStop();
}
