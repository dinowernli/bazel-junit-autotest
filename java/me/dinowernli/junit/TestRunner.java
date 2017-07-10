package me.dinowernli.junit;

import java.util.Set;

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
    Set<Class<?>> testClasses = Scanner.create().findTypesAnnotatedWith(TestClass.class);
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
    System.exit(result.wasSuccessful() ? 0 : 1);
  }
}
