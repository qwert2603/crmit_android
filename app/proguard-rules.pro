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

# andrlib_generator
-dontwarn com.qwert2603.andrlib.generator.**

# dao_generator
-dontwarn com.qwert2603.dao_generator.**

# retrofit & okhttp
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn retrofit2.**
-dontwarn retrofit2.Platform$Java8
-dontnote retrofit2.Platform
-keepattributes Signature
-keepattributes Exceptions

-keep class com.qwert2603.crmit_android.entity.** { *; }
-keep class com.qwert2603.crmit_android.rest.params.** { *; }
-keep class com.qwert2603.crmit_android.rest.results.** { *; }