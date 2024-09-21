
-keep class com.shockwave.**

-keep class android.support.v8.renderscript.** { *; }
-keep class androidx.renderscript.** { *; }
-keepattributes *Annotation*
# Gson specific classes
-dontwarn sun.misc.**

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

 -keep class com.gyf.immersionbar.* {*;}
 -dontwarn com.gyf.immersionbar.**
 -keep class com.airbnb.lottie.samples.** { *; }
 -keep class com.wang.avi.** { *; }
 -keep class com.wang.avi.indicators.** { *; }
-dontwarn org.jetbrains.annotations.**

-dontnote junit.**
-dontnote org.xmlpull.v1.**
-dontwarn junit.**
-dontwarn org.junit.**
-dontwarn android.content.**

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# Dexter
-keepattributes InnerClasses, Signature, *Annotation*
-keep class com.karumi.dexter.** { *; }
-keep interface com.karumi.dexter.** { *; }
-keepclasseswithmembernames class com.karumi.dexter.** { *; }
-keepclasseswithmembernames interface com.karumi.dexter.** { *; }

# lottie-android
-keep class com.airbnb.lottie.samples.** { *; }

#aliyunplayer
-keep class com.alivc.**{*;}
-keep class com.aliyun.**{*;}
-keep class com.cicada.**{*;}
-dontwarn com.alivc.**
-dontwarn com.aliyun.**
-dontwarn com.cicada.**

# optimize
-optimizationpasses 2
-optimizations !code/simplification/arithmetic
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses

## Please add these rules to your existing keep rules in order to suppress warnings.
## This is generated automatically by the Android Gradle plugin.
-dontwarn androidx.lifecycle.ViewModelKt
-dontwarn coil.request.LoadRequest$Companion
-dontwarn coil.request.LoadRequest
-dontwarn coil.request.LoadRequestBuilder
-dontwarn coil.request.RequestBuilder
-dontwarn coil.request.RequestDisposable