# 防止混淆crash的代码定位
-dontwarn **.**
-dontoptimize
-dontshrink
-dontskipnonpubliclibraryclassmembers
-renamesourcefileattribute ProGuard
-keepattributes SourceFile,LineNumberTable
-printmapping proguard_mapping.txt

#整个SDK类不混淆
-keep class com.flamingo.sdk.** {*;}

#keep住assets相关
-keep class assets.** {*;}
#keep住bugly的类
-keep public class com.tencent.bugly.** {*;}
