package me.dinowernli.junit;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * A test runner which scans for types annotated with {@link TestClass} and then delegates to the
 * standard JUnit runner.
 */
public class TestRunner {
  private static final String CLASS_SUFFIX = ".class";
  private static final String PROTOCOL_FILE = "file";

  public static void main(String[] args) {
    // First, find all the test classes.
    Set<Class<?>> testClasses = findTestClasses();
    System.out.println("Number of test classes detected: " + testClasses.size());
    for (Class<?> testClass : testClasses) {
      System.out.println("Test class: " + testClass.getCanonicalName());
    }
    if (testClasses.isEmpty()) {
      throw new RuntimeException("Found no test classes");
    }

    // Then, execute them using JUnit.
    Class<?>[] classes = testClasses.toArray(new Class<?>[0]);
    JUnitCore junit = new JUnitCore();
    junit.addListener(new TextListener(System.out));
    Result result = junit.run(classes);
    if (result.wasSuccessful()) {
      System.exit(0);
    } else {
      System.exit(1);
    }
  }

  private static Set<Class<?>> findTestClasses() {
    ClassLoader classLoader = TestRunner.class.getClassLoader();
    URLClassLoader urlClassLoader;
    if (classLoader instanceof URLClassLoader) {
      urlClassLoader = (URLClassLoader) classLoader;
    } else {
      throw new RuntimeException(
          "Expected URLClassLoader, but got type: " + classLoader.getClass().getName());
    }

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
        result.addAll(scanJarEntry(jarEntries.nextElement()));
      }
    }
    return result;
  }

  /** Returns all the types annotated with {@link TestClass} in the supplied jar entry. */
  private static Set<Class<?>> scanJarEntry(JarEntry jarEntry) {
    Set<Class<?>> result = new HashSet<>();
    if (jarEntry.getName().endsWith(CLASS_SUFFIX)) {
      String className = jarEntry.getName()
          .replace("/", ".")
          .replace(CLASS_SUFFIX, "");
      try {
        Class<?> clazz = TestRunner.class.getClassLoader().loadClass(className);
        if (clazz.isAnnotationPresent(TestClass.class)) {
          result.add(clazz);
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Unable to load class: " + className, e);
      }
    }
    return result;
  }
}
