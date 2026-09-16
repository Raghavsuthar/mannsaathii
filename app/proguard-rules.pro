# ==============================================================================
# MannSaathi (मनसाथી) — Production ProGuard & R8 Obfuscation Rules
# ==============================================================================

# ------------------------------------------------------------------------------
# 1. General Shrinking & Obfuscation Settings
# ------------------------------------------------------------------------------
-verbose
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses

# Retain line numbers for crash analysis while concealing actual Kotlin file paths
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve runtime annotations for Compose, Room, and Kotlin reflection
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Strip verbose and debug logging in release builds to protect patient/caregiver privacy
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# ------------------------------------------------------------------------------
# 2. Android Core & Application Entry Points
# ------------------------------------------------------------------------------
-keep public class com.example.MainActivity { *; }
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep custom Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# ------------------------------------------------------------------------------
# 3. Room Database & Local Models (Critical to prevent runtime SQLite crashes)
# ------------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**

# Keep our concrete data models and DAOs
-keep class com.example.data.model.** { *; }
-keep class com.example.data.local.** { *; }
-keepclassmembers class com.example.data.local.** { *; }

# ------------------------------------------------------------------------------
# 4. Jetpack Compose & UI Architecture
# ------------------------------------------------------------------------------
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep ViewModels and their public constructors
-keep class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    public <init>(...);
}

# Keep StateFlow and LiveData observation signatures
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public kotlinx.coroutines.flow.StateFlow *;
}

# ------------------------------------------------------------------------------
# 5. Coil Image Loading Library
# ------------------------------------------------------------------------------
-keep class coil.** { *; }
-dontwarn coil.**
-keepclassmembers class * implements coil.decode.Decoder$Factory {
    public <init>(...);
}
-keepclassmembers class * implements coil.fetch.Fetcher$Factory {
    public <init>(...);
}

# ------------------------------------------------------------------------------
# 6. Kotlin Coroutines & Asynchronous Runtime
# ------------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ------------------------------------------------------------------------------
# 7. Security & Secrets Management
# ------------------------------------------------------------------------------
-keep class com.example.util.SecurityHelper { *; }
-keep class com.example.BuildConfig {
    public static final java.lang.String *;
}
