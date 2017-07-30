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
  private final int scanDepth;

  /**
   * Returns a new {@link Scanner} which scans the current classloader.
   *
   * @param scanDepth the returned {@link Scanner} will scan at most the first scanDepth entries
   *                  of the classpath.
   */
  static Scanner create(int scanDepth) {
    ClassLoader classLoader = Scanner.class.getClassLoader();
    if (!(classLoader instanceof URLClassLoader)) {
      throw new RuntimeException(
          "Expected URLClassLoader, but got type: " + classLoader.getClass().getName());
    }
    return new Scanner((URLClassLoader) classLoader, scanDepth);
  }

  private Scanner(URLClassLoader urlClassLoader, int scanDepth) {
    if (scanDepth < 0) {
      throw new IllegalArgumentException("Scan depth must be non-negative, but got: " + scanDepth);
    }

    this.urlClassLoader = urlClassLoader;
    this.scanDepth = scanDepth;
  }

  /** Holds the result of a scan through all the classes. */
  class ScanResult {
    final Set<Class<?>> classes;
    final long numSkipped;

    private ScanResult(Set<Class<?>> classes, long numSkipped) {
      this.classes = classes;
      this.numSkipped = numSkipped;
    }
  }

  /** Returns all the types found to be annotated with the supplied annotation. */
  ScanResult findTypesAnnotatedWith(Class<? extends Annotation> annotation) {
    Set<Class<?>> result = new HashSet<>();
    long skippedClasses = 0;
    URL[] urls = urlClassLoader.getURLs();
    for (int i = 0; i < Math.min(urls.length, scanDepth); ++i) {
      URL url = urls[i];

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
        try {
          result.addAll(scanJarEntry(jarEntries.nextElement(), annotation));
        } catch (ClassloadException e) {
          ++skippedClasses;
        }
      }
    }
    return new ScanResult(result, skippedClasses);
  }

  /** Returns all the annotated types in the supplied jar entry. */
  private Set<Class<?>> scanJarEntry(JarEntry jarEntry, Class<? extends Annotation> annotation)
      throws ClassloadException {
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
      } catch (Throwable t) {
        // In some cases, loadClass appears to throw other errors (such as NoClassDefFoundError)
        // with ClassNotFoundException as the underlying cause. So we catch Throwable here.
        throw new ClassloadException("Unable to load class: " + className, t);
      }
    }
    return result;
  }

  private static class ClassloadException extends Exception {
    private ClassloadException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
