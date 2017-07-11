package me.dinowernli.junit;

import java.util.Set;

import me.dinowernli.junit.Scanner.ScanResult;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * A test runner which scans for types annotated with {@link TestClass} and then delegates to the
 * standard JUnit runner.
 */
public class TestRunner {
  public static void main(String[] args) {
    // First, find all the test classes.
    ScanResult scanResult = Scanner.create().findTypesAnnotatedWith(TestClass.class);
    Set<Class<?>> testClasses = scanResult.classes;
    long numSkipped = scanResult.numSkipped;
    System.out.println("Number of test classes detected: " + testClasses.size());
    System.out.println("Number of classes skipped (because they failed to load): " + numSkipped);
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
    System.exit(result.wasSuccessful() ? 0 : 1);
  }
}
