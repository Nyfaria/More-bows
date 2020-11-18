-verbose

# Temporarily turn everything off until I can get this to work correctly, makes everything else in this file completely meaningless.

-dontshrink
-dontoptimize
-dontobfuscate

# We're a bit oldschool

-target 1.6

# Preserve all annotations.

-keepattributes *Annotation*

# Preserve all public applications.

#-keepclasseswithmembers public class * {
#    public static void main(java.lang.String[]);
#}

# Preserve all native method names and the names of their classes.

#-keepclasseswithmembernames,includedescriptorclasses class * {
#    native <methods>;
#}

# Preserve the special static methods that are required in all enumeration
# classes.

#-keepclassmembers,allowoptimization enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your application doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}

# bonus optimisations

#-optimizationpasses 64
#-allowaccessmodification
#-mergeinterfacesaggressively
#-overloadaggressively
#-repackageclasses

# temp

-keep public class * {
    public protected *;
}

-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,
                SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

