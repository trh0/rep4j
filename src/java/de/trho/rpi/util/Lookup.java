package de.trho.rpi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * !USE WITH CARE!
 * 
 * @author trh0
 * @see http://openbook.rheinwerk-verlag.de/java7/1507_17_002.html
 */
public class Lookup {
  /**
   * 
   * @param clazz The class to lookup an instance of.
   * @return An instance of the class if there are any.
   */
  public static <T> T find(Class<T> clazz) {
    Iterator<T> iterator = ServiceLoader.load(clazz).iterator();
    return iterator.hasNext() ? iterator.next() : null;
  }

  /**
   * 
   * @param clazz The class to lookup all instances of.
   * @return All instances of the class if there are any.
   */
  public static <T> Collection<? extends T> findAll(Class<T> clazz) {
    Collection<T> result = new ArrayList<T>();
    for (T e : ServiceLoader.load(clazz))
      result.add(e);
    return result;
  }
}
