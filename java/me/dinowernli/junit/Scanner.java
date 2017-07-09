package me.dinowernli.junit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class Scanner {
  private static final String CLASS_SUFFIX = ".class";
  private static final String PROTOCOL_FILE = "file";

  private final URLClassLoader urlClassLoader;

  /** Returns a new {@link Scanner} which scans the current classloader. */
  static Scanner create() {
    ClassLoader classLoader = Scanner.class.getClassLoader();
    if (!(classLoader instanceof URLClassLoader)) {
      throw new RuntimeException(
          "Expected URLClassLoader, but got type: " + classLoader.getClass().getName());
    }
    return new Scanner((URLClassLoader) classLoader);
  }

  private Scanner(URLClassLoader urlClassLoader) {
    this.urlClassLoader = urlClassLoader;
  }

  /** Returns all the types found to be annotated with the supplied annotation. */
  Set<Class<?>> findTypesAnnotatedWith(Class<? extends Annotation> annotation) {
    Set<Class<?>> result = new HashSet<>();
    for (URL url : urlClassLoader.getURLs()) {
      JarFile jarFile;
      if (!url.getProtocol().equals(PROTOCOL_FILE)) {
        continue;
      }

      String filePath = url.getFile();
      try {
        jarFile = new JarFile(filePath);
      } catch (IOException e) {
        throw new RuntimeException("Unable to open jar file: " + filePath);
      }

      Enumeration<JarEntry> jarEntries = jarFile.entries();
      while (jarEntries.hasMoreElements()) {
        result.addAll(scanJarEntry(jarEntries.nextElement(), annotation));
      }
    }
    return result;
  }

  /** Returns all the annotated types in the supplied jar entry. */
  private Set<Class<?>> scanJarEntry(JarEntry jarEntry, Class<? extends Annotation> annotation) {
    Set<Class<?>> result = new HashSet<>();
    if (jarEntry.getName().endsWith(CLASS_SUFFIX)) {
      String className = jarEntry.getName()
          .replace("/", ".")
          .replace(CLASS_SUFFIX, "");
      try {
        Class<?> clazz = urlClassLoader.loadClass(className);
        if (clazz.isAnnotationPresent(annotation)) {
          result.add(clazz);
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Unable to load class: " + className, e);
      }
    }
    return result;
  }
}
