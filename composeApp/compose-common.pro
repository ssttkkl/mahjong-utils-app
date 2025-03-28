# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Don't print notes about potential mistakes or omissions in the configuration for kotlinx-serialization classes
# See also https://github.com/Kotlin/kotlinx.serialization/issues/1900
-dontnote kotlinx.serialization.**

# Serialization core uses `java.lang.ClassValue` for caching inside these specified classes.
# If there is no `java.lang.ClassValue` (for example, in Android), then R8/ProGuard will print a warning.
# However, since in this case they will not be used, we can disable these warnings
-dontwarn kotlinx.serialization.internal.ClassValueReferences

# keep没留住Serializer，干脆把自己的代码全部keep了
-dontnote mahjongutils.**
-dontnote io.ssttkkl.mahjongutils.app.**
-keep class mahjongutils.** {*;}
-keep class io.ssttkkl.mahjongutils.app.** {*;}

# 协程相关
-keep class * implements kotlinx.coroutines.internal.MainDispatcherFactory { *; }

# jna相关
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }

# TODO: compose 1.8.0-beta01后出现
# Warning: com.mikepenz.aboutlibraries.ui.compose.SharedLibrariesKt: can't find referenced method 'void FlowRow(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement$Horizontal,androidx.compose.foundation.layout.Arrangement$Vertical,int,int,androidx.compose.foundation.layout.FlowRowOverflow,kotlin.jvm.functions.Function3,androidx.compose.runtime.Composer,int,int)' in program class androidx.compose.foundation.layout.FlowLayoutKt
# Warning: com.mikepenz.aboutlibraries.ui.compose.SharedLibrariesKt$LicenseDialog$1$1: can't find referenced method 'void FlowRow(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement$Horizontal,androidx.compose.foundation.layout.Arrangement$Vertical,int,int,androidx.compose.foundation.layout.FlowRowOverflow,kotlin.jvm.functions.Function3,androidx.compose.runtime.Composer,int,int)' in program class androidx.compose.foundation.layout.FlowLayoutKt
-dontwarn com.mikepenz.aboutlibraries.ui.compose.SharedLibrariesKt