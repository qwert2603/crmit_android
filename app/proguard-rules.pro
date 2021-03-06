# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile


# andrlib
-keepclasseswithmembernames class com.hannesdorfmann.mosby3.FragmentMviDelegateImpl { *; }


# andrlib_generator
-dontwarn com.qwert2603.andrlib.generator.**


# dao_generator
-dontwarn com.qwert2603.dao_generator.**


# retrofit & okhttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>


# Crashlytics
-keepattributes *Annotation*
-keep public class * extends java.lang.Exception


-keep class com.qwert2603.crmit_android.entity.** { *; }
-keep class com.qwert2603.crmit_android.rest.params.** { *; }
-keep class com.qwert2603.crmit_android.rest.results.** { *; }


# for FirebaseAnalytics
-keepattributes InnerClasses
-keepnames class ru.terrakok.cicerone.commands.** { *; }
-keepnames class com.qwert2603.crmit_android.navigation.Screen$* { *; }


## Flutter wrapper
-keep class io.flutter.app.** { *; }
-keep class io.flutter.plugin.**  { *; }
-keep class io.flutter.util.**  { *; }
-keep class io.flutter.view.**  { *; }
-keep class io.flutter.**  { *; }
-keep class io.flutter.plugins.**  { *; }