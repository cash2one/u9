package com.tencent.tmgp.yybtestsdk.module;


import android.widget.LinearLayout;
import com.tencent.tmgp.yybtestsdk.MainActivity;

public abstract class BaseModule {
	public String name;
	protected MainActivity mMainActivity;
	
	public abstract void init(LinearLayout parent, MainActivity mainActivity);
	
}
