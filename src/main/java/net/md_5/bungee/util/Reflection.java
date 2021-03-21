package net.md_5.bungee.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {
  public static <T> T getFieldValue(Object obj, String fieldname) {
    Class<?> clazz = obj.getClass();
    while (true) {
      try {
        Field field = clazz.getDeclaredField(fieldname);
        field.setAccessible(true);
        return (T)field.get(obj);
      } catch (Throwable throwable) {
        if ((clazz = clazz.getSuperclass()) == null)
          return null; 
      } 
    } 
  }
  
  public static void setFieldValue(Object obj, String fieldname, Object value) {
    Class<?> clazz = obj.getClass();
    do {
      try {
        Field field = clazz.getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj, value);
      } catch (Throwable throwable) {}
    } while ((clazz = clazz.getSuperclass()) != null);
  }
  
  public static <T> T getStaticFieldValue(Class<?> clazz, String fieldname) {
    while (true) {
      try {
        Field field = clazz.getDeclaredField(fieldname);
        field.setAccessible(true);
        return (T)field.get((Object)null);
      } catch (Throwable throwable) {
        if ((clazz = clazz.getSuperclass()) == null)
          return null; 
      } 
    } 
  }
  
  public static void invokeMethod(Object obj, String methodname, Object... args) {
    Class<?> clazz = obj.getClass();
    do {
      try {
        byte b;
        int i;
        Method[] arrayOfMethod;
        for (i = (arrayOfMethod = clazz.getDeclaredMethods()).length, b = 0; b < i; ) {
          Method method = arrayOfMethod[b];
          if (method.getName().equals(methodname) && (method.getParameterTypes()).length == args.length) {
            method.setAccessible(true);
            method.invoke(obj, args);
          } 
          b++;
        } 
      } catch (Throwable throwable) {}
    } while ((clazz = clazz.getSuperclass()) != null);
  }
}
