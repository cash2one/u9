-optimizationpasses 5                   # 指定代码的压缩级别
-dontusemixedcaseclassnames             # 指定代码的压缩级别
-dontskipnonpubliclibraryclasses        # 是否混淆第三方jar
-dontpreverify                          # 混淆时是否做预校验
-dontoptimize
-ignorewarning                          # 忽略警告，避免打包时某些警告出现
-verbose                                # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*    # 混淆时所采用的算法

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}


-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepattributes InnerClasses

-keep class com.tencent.bugly.**{*;}
-keep class com.tencent.stat.**{*;}
-keep class com.tencent.smtt.**{*;}
-keep class com.tencent.beacon.**{*;}
-keep class com.tencent.mm.**{*;}
-keep class com.tencent.apkupdate.**{*;}
-keep class com.tencent.tmassistantsdk.**{*;}
-keep class org.apache.http.** { * ;}
-keep class com.qq.jce.**{*;}
-keep class com.qq.taf.**{*;}
-keep class com.tencent.connect.**{*;}
-keep class com.tencent.map.**{*;}
-keep class com.tencent.open.**{*;}
-keep class com.tencent.qqconnect.**{*;}
-keep class com.tencent.tauth.**{*;}
-keep class com.tencent.feedback.**{*;}
-keep class common.**{*;}
-keep class exceptionupload.**{*;}
-keep class mqq.**{*;}
-keep class qimei.**{*;}
-keep class strategy.**{*;}
-keep class userinfo.**{*;}
-keep class com.tencent.mid.**{*;}
-keep class com.tencent.unipay.**{*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.tencent.ysdk.Fix{ public *;}
-keep class com.tencent.ysdk.a.a{ public *;}
-keep class com.tencent.ysdk.a.e{ public *;}

-keep class com.tencent.ysdk.api.YSDKApi{public static *;}
-keep class com.tencent.ysdk.framework.common.*{*;}
-keep class com.tencent.ysdk.framework.request.RouterDomain{public static *;}

-keep class com.tencent.ysdk.module.bugly.BuglyApi {public *;}
-keep interface com.tencent.ysdk.module.bugly.BuglyListener { * ; }
-keep class com.tencent.ysdk.module.bugly.BuglyNativeListener { * ; }
-keep class com.tencent.ysdk.module.bugly.impl.BuglyModule {public static *;}

-keep class com.tencent.ysdk.module.stat.StatApi { * ; }
-keep class com.tencent.ysdk.module.stat.impl.StatModule {public static *;}

-keep class com.tencent.ysdk.module.report.impl.HaboReportModule {public static *;}

-keep class com.tencent.ysdk.module.pay.PayApi { * ; }
-keep interface com.tencent.ysdk.module.pay.PayListener { * ; }
# 提供给支付C++接口调用
-keep class com.tencent.ysdk.module.pay.PayInnerNativeListener { * ; }
-keep class com.tencent.ysdk.module.pay.PayNativeListener { * ; }
-keep class com.tencent.ysdk.module.pay.PayRet {*;}
-keep class com.tencent.ysdk.module.pay.impl.PayModule {public static *;}

-keep class com.tencent.ysdk.module.user.PersonInfo {*;}
-keep class com.tencent.ysdk.module.user.UserApi {public *;}
-keep interface com.tencent.ysdk.module.user.UserListener { * ; }
-keep class com.tencent.ysdk.module.user.UserNativeListener { * ; }
-keep class com.tencent.ysdk.module.user.UserLoginRet {*;}
-keep class com.tencent.ysdk.module.user.UserRelationRet {*;}
-keep class com.tencent.ysdk.module.user.UserToken {*;}
-keep class com.tencent.ysdk.module.user.WakeupRet {*;}
-keep class com.tencent.ysdk.module.user.impl.UserModule { public static *;}
-keep class com.tencent.ysdk.module.user.impl.qq.QQUserModule{public static *;}
-keep class com.tencent.ysdk.module.user.impl.wx.WXUserModule { public static *;}



-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement