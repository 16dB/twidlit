/**
 * Copyright 2015 Pushkar Piggott
 *
 * Persist.java
 */
package pkp.util;

import java.util.Properties;
import java.io.File;
import java.net.URL;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import pkp.io.Io;

///////////////////////////////////////////////////////////////////////////////
public class Persist {

   ////////////////////////////////////////////////////////////////////////////
   public static boolean match(String a, String b) {
      return toTag(a).equals(toTag(b));
   }

   ////////////////////////////////////////////////////////////////////////////
   public static String toTag(String name) {
      return PersistentProperties.toTag(name);
   }

   ////////////////////////////////////////////////////////////////////////////
   public static void init(String fName, String parent,  String jarParent) {
      sm_JarParent = jarParent;
      sm_Persist = new PersistentProperties(fName, parent, jarParent, Io.sm_MUST_EXIST);
      sm_Persist.read();
   }

   ////////////////////////////////////////////////////////////////////////////
   public static String getFolderName() {
      return sm_Persist.getFolderName();
   }

   ////////////////////////////////////////////////////////////////////////////
   public static void write() {
      sm_Persist.write();
   }

   ////////////////////////////////////////////////////////////////////////////
   public static void set(String name, String value) {
      sm_Persist.set(name, value);
   }

   ////////////////////////////////////////////////////////////////////////////
   public static void unset(String name) {
      sm_Persist.unset(name);
   }

   ////////////////////////////////////////////////////////////////////////////
   public static String get(String name) {
      return sm_Persist.get(name);
   }

   ////////////////////////////////////////////////////////////////////////////
   public static String get(String name, String defaultValue) {
      if (sm_Persist == null && !sm_Persist.mustExist()) {
         return defaultValue;
      }
      String value = sm_Persist.get(name);
      if (value == null) {
         return defaultValue;
      }
      return value;
   }

   ////////////////////////////////////////////////////////////////////////////
   public static void set(String name, int value) {
      sm_Persist.set(name, Integer.toString(value));
   }

   ////////////////////////////////////////////////////////////////////////////
   public static int getInt(String name) {
      return Io.parseInt(name, sm_Persist.get(name));
   }

   ////////////////////////////////////////////////////////////////////////////
   public static int getInt(String name, int defaultValue) {
//System.out.printf("Persist.getInt(%s, %d)%n", name, defaultValue);
      if (sm_Persist == null && !sm_Persist.mustExist()) {
         return defaultValue;
      }
      String value = sm_Persist.get(name);
      if (value == null) {
         return defaultValue;
      }
      return Io.parseInt(name, value);
   }

   ////////////////////////////////////////////////////////////////////////////
   public static void set(String name, boolean value) {
      sm_Persist.set(name, Boolean.toString(value));
   }

   ////////////////////////////////////////////////////////////////////////////
   public static boolean getBool(String name) {
      return Io.parseBool(name, sm_Persist.get(name));
   }

   ////////////////////////////////////////////////////////////////////////////
   public static boolean getBool(String name, boolean defaultValue) {
      if (sm_Persist == null && !sm_Persist.mustExist()) {
         return defaultValue;
      }
      String value = sm_Persist.get(name);
      if (value == null) {
         return defaultValue;
      }
      return Io.parseBool(name, value);
   }

   ////////////////////////////////////////////////////////////////////////////
   public static File getFile(String name) {
      String fileName = get(name);
      if (fileName == null || fileName == "") {
         return null;
      }
      File f = new File(fileName);
      while (!f.exists() && f.getParent() != null) {
         f = new File(f.getParent());
      }
      if (!f.exists()) {
         return null;
      }
      return f;
   }

   ////////////////////////////////////////////////////////////////////////////
   public static URL getDirJarUrl(String dirName, String fileName) {
      return Io.toUrl(fileName, get(dirName), sm_JarParent);
   }

   ////////////////////////////////////////////////////////////////////////////
   public static URL getExistDirJarUrl(String dirName, String fileName) {
      return Io.toExistUrl(fileName, get(dirName), sm_JarParent);
   }

   // Data ////////////////////////////////////////////////////////////////////
   private static String sm_JarParent;
   private static PersistentProperties sm_Persist;
}
