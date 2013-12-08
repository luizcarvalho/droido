def patch(version)
    patch_file = File.new("proxima_versao.patch","w+")
patch_file.puts <<-eos
diff --git droido.torpedos/src/main/AndroidManifest.xml droido.torpedos/src/main/AndroidManifest.xml
index 53ff11c..48496c0 100644
--- droido.torpedos/src/main/AndroidManifest.xml
+++ droido.torpedos/src/main/AndroidManifest.xml
@@ -1,7 +1,7 @@
 <?xml version="1.0" encoding="utf-8"?>
 <manifest xmlns:android="http://schemas.android.com/apk/res/android"
     package="br.com.redrails.torpedos"
-    android:versionCode="#{version-1}"
+    android:versionCode="#{version}"
     android:versionName="@string/version_name" >
 
     <uses-permission android:name="om.android.launcher.permission.INSTALL_SHORTCUT" />
diff --git droido.torpedos/src/main/java/br/com/redrails/torpedos/DataBaseHelper.java droido.torpedos/src/main/java/br/com/redrails/torpedos/DataBaseHelper.java
index 33cbae4..03c3eef 100644
--- droido.torpedos/src/main/java/br/com/redrails/torpedos/DataBaseHelper.java
+++ droido.torpedos/src/main/java/br/com/redrails/torpedos/DataBaseHelper.java
@@ -26,7 +26,7 @@ public class DataBaseHelper extends SQLiteOpenHelper{
 
     private static String DB_NAME = "database.sqlite";
     public static String TEMP_DB_NAME = "database_temp.sqlite";
-    private static int DB_VERSION=#{version-1};//change to version of code
+    private static int DB_VERSION=#{version};//change to version of code
     public static boolean upgrading = false;
eos
patch_file.close
end


begin
	system "git checkout -- droido.torpedos/src/main/AndroidManifest.xml"
	system "git checkout -- droido.torpedos/src/main/java/br/com/redrails/torpedos/DataBaseHelper.java"
	system "adb shell am start -a android.intent.action.DELETE -d package:br.com.redrails.torpedos"
	(22..100).each do |version|
		patch(version)
		system "patch -p0 < proxima_versao.patch"
		#system "gradle clean"
		system "./gradlew build"
		system "adb install -r droido.torpedos/build/apk/droido.torpedos-release.apk"
		system "adb shell am start -n br.com.redrails.torpedos/br.com.redrails.torpedos.LoadScreenActivity -a android.intent.action.MAIN -c android.intent.category.LAUNCHER"
		system "git stash drop"
	end
rescue Exception => e
	puts e	
end
