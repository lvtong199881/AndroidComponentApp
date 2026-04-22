# React Native proguard rules

# Keep React Native classes
-keep class com.facebook.react.** { *; }
-keep class com.facebook.react.shell.** { *; }

# Keep bridgeless mode
-keep class * extends com.facebook.react.turbomodule.core.TurboModuleManager { *; }
