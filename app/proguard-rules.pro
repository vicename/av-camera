# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
#
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}
# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/yueguang/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface

-dontwarn com.wiwide.passes

# class:
#内部类的忽略混淆
-keepclassmembers class com.wiwide.wifiplus.GuideActivity$JavaInterface {public *;}
-keepclassmembers class com.wiwide.passes.JsActivity$JavaInterface {public *;}
-keepclassmembers class com.wiwide.passes.RecordActivity$JsRecorder {public *;}
-keepclassmembers class com.wiwide.passes.ExecutorActivity$JsExecutor {public *;}

# 忽略xutils混淆
-keep class * extends java.lang.annotation.Annotation { *; }
-keep public abstract class * extends com.wiwide.wifiplus.BaseActivity{ *; }
-keep public class * extends com.wiwide.passes.JsActivity{ *; }

#友盟
#忽略混淆友盟的包
-dontwarn com.umeng.**
-keep class com.umeng.** { *; }
-keep class com.umeng.analytics.** { *; }
-keep class com.umeng.common.** { *; }
-keep class com.umeng.newxp.** { *; }

-keep   class com.amap.api.location.**{*;}
-keep   class com.aps.**{*;}

-keep public class com.wiwide.wifiplus.R$*{
    public static final int *;
}

-keep public class com.example.R$*{
    public static final int *;
}

-keep class com.wiwide.util.Secret{*;}
-keep class com.wiwide.info.PassDBInfo{*;}

-keep class com.wiwide.data.Wifi {*;}
-keep class com.wiwide.data.Ap {*;}
-keep class com.wiwide.data.PassInfo {*;}
-keep class com.wiwide.advert.HistoryData {*;}
-keep class com.wiwide.advert.AdvertData {*;}

#忽略第三方分享
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}

-dontwarn cn.sharesdk.**
-dontwarn **.R$*

-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class m.framework.**{*;}

-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}

-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
   *;
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}
-dontwarn android.support.**

# ============以上不懂事啥玩意儿，加上就对了==============
-keep public class * extends android.app.Activity{*;}
-keep public class com.wiwide.wifiplussdk.JsActivity{*;}
-keep public class * extends com.wiwide.wifiplussdk.JsActivity{*;}
-keep public class com.wiwide.util.WifiConnectStateObserver{*;}
-keep public class com.wiwide.spider.PortalSpider{*;}
-keep public class com.wiwide.util.NetworkChecker{*;}
-keep class com.wiwide.util.WifiNetworkObserver {*;}
-keep class com.wiwide.location.LocationHandler {*;}
-keep class com.wiwide.wifiplussdk.WifiPlusSdk {*;}
-keep class com.wiwide.wifiplussdk.PassportObserver {*;}
-keep class * implements com.wiwide.wifiplussdk.PassportObserver {*;}

-keep class com.wiwide.util.HttpClient {*;}
-keep class com.wiwide.util.SyncResponse {*;}
-keep class com.wiwide.location.LocationHandler {*;}
-keep class com.wiwide.util.NetworkChecker {*;}

-keep class com.wiwide.util.CommonProgressDialog {*;}
-keep class com.wiwide.util.HttpInfoUtils {*;}
-keep class com.wiwide.util.ProtectJsonHttpResponseHandler {*;}
-keep class com.wiwide.wifiplussdk.PassportObserver{*;}
-keep class com.wiwide.wifiplussdk.PassportObserverAdpter{*;}

# ============orm数据库相关api==============
-keep public class * extends com.j256.ormlite.android.apptools.OpenHelperManager
-keep public class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
-keep class com.j256.ormlite.** { *; }
-keep class com.j256.ormlite.android.** { *; }
-keep class com.j256.ormlite.field.** { *; }
-keep class com.j256.ormlite.stmt.** { *; }

# ============orm数据库，应该是用了反射==============
-keep class com.wiwide.data.Wifi {*;}
-keep class com.wiwide.data.Ap {*;}
-keep class com.wiwide.data.PassInfo {*;}
-keep class com.wiwide.advert.HistoryData {*;}
-keep class com.wiwide.advert.AdvertData {*;}
-keep class com.wiwide.info.PassDBInfo{*;}

# ============orm数据库，应该是用了反射==============
-keep class com.wiwide.util.CacheNode {*;}
-keep class com.wiwide.data.PassUseState {*;}

# ============jni相关==============
-keep class com.wiwide.util.Secret {*;}


# ============DC的jar包忽略============
-keep class com.linj.** { *; }
-keep class com.loopj.** { *; }
-keep class com.squareup.** { *; }
-keep class org.apache.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.http.params { *; }
-keep class org.apache.http.client.** { *; }
-keep class org.apache.http.client.HttpClient.** { *; }
-keep class org.apache.http.auth.** { *; }
-keep class android.net.** { *; }
-keep class android.net.http.Android.net.http.** { *; }

-keep class * extends org.apache.http.** {*;}
-keep class android.content.** {*;}



-dontwarn com.loopj.**
-dontwarn com.squareup.**
-dontwarn org.apache.**
-dontwarn org.apache.auth.**


