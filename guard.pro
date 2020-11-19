-verbose

# Make sure to check everything always, can't hurt.

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-forceprocessing

# We're a bit oldschool

-target 1.5

# TODO Figure out how to do this but for annotations

-adaptclassstrings
#-adaptresourcefilenames
#-adaptresourcefilecontents

# Keep anything that overrides a super method, or is an event handler.

-keepclassmembers,allowobfuscation class * {
    @java.lang.Override,cpw.mods.fml.common.eventhandler.SubscribeEvent,cpw.mods.fml.common.Mod$EventHandler <methods>;
}

# Keep all classes with public methods

-keep,allowobfuscation,allowoptimization public class * {
    public <methods>;
}

# ProGuard really wants to remove the mod instance and sided proxies. This stops it from doing that.

-keepclassmembers,allowobfuscation class * {
    private static iDiamondhunter.morebows.MoreBows *;
}

# These two classes are referred to in String constants inside of annotations. TODO see above (around -adaptclassstrings).

-keep,allowoptimization public class iDiamondhunter.morebows.Client
-keep,allowoptimization public class iDiamondhunter.morebows.MoreBows

# Keep entities having a consistent name internally for Forge

-keep,allowoptimization public class iDiamondhunter.morebows.entities.**

# Keep all initialisers, because ProGuard keeps trying to make them private.

-keepclassmembers class * { public <init>(...); }

# Don't put the mod into the default package. That would be bad.

-keeppackagenames

# TODO Check if anything else should be kept. Annotations are mandatory, as Forge uses them for reflection.

-keepattributes *Annotation*

# Bonus optimisations

-optimizationpasses 64
-mergeinterfacesaggressively
-overloadaggressively

# TODO Decide if this is a good idea. allowaccessmodification is probably safe. dontpreverify seems to work on modern JVMs, need to do more exhaustive testing.

-dontpreverify
-allowaccessmodification

# Debug info

-printusage
-whyareyoukeeping class iDiamondhunter.**
