package com.hy.game.demo;


import com.test.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class FloatManager{
    private static LinearLayout mFloatLayout; 
    private static WindowManager mWindowManager;  
    private static WindowManager.LayoutParams wmParams; 
    private static ImageButton mFloat;
    private static float mTouchX;
	private static float mTouchY;
	private static float x;
	private static float y;
	//保存当前是否为移动模式
	private static boolean  isMove = false;
	//保存当前机器人在左边还是右边
	private static boolean  isRight = false;
	
	private static boolean isFloat = false;
    
    
	public static long lastClickTime;  
    public static boolean isFastDoubleClick() {  
        long time = System.currentTimeMillis();  
        long timeD = time - lastClickTime;  
        if ( 0 < timeD && timeD < 1000) {     
            return true;     
        }     
        lastClickTime = time;     
        return false;     
    }  
    
    public static void showView(Activity mActivity) {
		mWindowManager = mActivity.getWindowManager();
		updateViewPosition();
		isFloat = true;
	}
    
	public static void removeView(Activity mActivity) {
		
		mWindowManager = mActivity.getWindowManager();
		try{
		mWindowManager.removeView(mFloatLayout);
		isFloat = false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void createView(final Activity mActivity) {
		if(!isFloat){
			initFloatView(mActivity);
		}else{
			showView(mActivity);
		}
	
	}
	
	private static void initFloatView(final Activity mActivity){
		isFloat = true;
		wmParams = new WindowManager.LayoutParams();  
        //获取的是WindowManagerImpl.CompatModeWrapper  
		mWindowManager = mActivity.getWindowManager();  
        //设置图片格式，效果为背景透明  
        wmParams.format = PixelFormat.TRANSLUCENT;
//        		PixelFormat.RGBA_8888;   
        
      
        //调整悬浮窗显示的停靠位置为左侧置顶  
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;         
//        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
        wmParams.x = 0;  
        wmParams.y = 0;  
        //设置悬浮窗口长宽数据    
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）  
        wmParams.flags =  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;    
  
         /*// 设置悬浮窗口长宽数据 
        wmParams.width = 200; 
        wmParams.height = 80;*/  
     
        LayoutInflater inflater =mActivity.getLayoutInflater();
        //获取浮动窗口视图所在布局  
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.hy_demo_float_activity, null);  
        //添加mFloatLayout  
        Handler handle = new Handler();
        handle.post(new Runnable() {
			
			@Override
			public void run() {
				mWindowManager.addView(mFloatLayout, wmParams);
			}
		});  
   
        //浮动窗口按钮  
        mFloat = (ImageButton)mFloatLayout.findViewById(R.id.switch_user);
        
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,  
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec  
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));  
        //设置监听浮动窗口的触摸移动  
        mFloat.setOnTouchListener(new OnTouchListener()   
        {  
              
            @Override  
            public boolean onTouch(View v, MotionEvent event)   
            {  
                // TODO Auto-generated method stub  
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标  
            	Rect frame =  new  Rect();  
            	mFloat.getWindowVisibleDisplayFrame(frame);
            	int  statusBarHeight = frame.top;
                x = event.getRawX();
                //减25为状态栏的高度  
                y =  event.getRawY()  - statusBarHeight;   
                int screenWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
                
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                	// 获取相对View的坐标，即以此View左上角为原点
        			mTouchX = event.getX();
        			mTouchY = event.getY();
        			isMove = false;
                	break;
                case MotionEvent.ACTION_MOVE:// 捕获手指触摸移动动作
                	if(x > 35 && (screenWidth - x) >35) {
        				isMove = true;
        				updateViewPosition();
        			}
        			break;
                case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
                	if(isMove) {
                		isMove = false;
        				float halfScreen = screenWidth/2;
        				if(x <= halfScreen) {
        					x = 0 ;
        				} else {
        					x = screenWidth;
        				}
        				updateViewPosition();
        				
        			} 
                	mTouchX = mTouchY = 0;
                	
                	break;
                
                }
                return false;  //此处必须返回false，否则OnClickListener获取不到监听  
            }  
        });  
        if(!isMove){
        mFloat.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View arg0) {
// 					if(isFastDoubleClick()){
 						Intent intent1 = new Intent(mActivity,HyGameDemo.class);
 						intent1.putExtra("type", 1);
         				mActivity.startActivity(intent1);	
// 				}
 				}
 			});
        }
	}
	
	private static void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchX);
		wmParams.y = (int) (y - mTouchY);
		 //刷新  
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);  
	}

}