package me.dinowernli.junit;

import java.util.Optional;
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
  private static final String FLAG_PREFIX_SCAN_DEPTH = "--scan_depth=";

  /** By default only scan the jar produced by bazel from the test sources. */
  private static final int DEFAULT_SCAN_DEPTH = 1;

  public static void main(String[] args) {
    int scanDepth = parseScanDepth(args).orElse(DEFAULT_SCAN_DEPTH);
    System.out.println("Using scan depth: " + scanDepth);

    // First, find all the test classes.
    ScanResult scanResult = Scanner.create(scanDepth).findTypesAnnotatedWith(TestClass.class);
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

  private static Optional<Integer> parseScanDepth(String[] args) {
    if (args.length < 2) {
      return Optional.empty();
    }
    if (!args[1].startsWith(FLAG_PREFIX_SCAN_DEPTH)) {
      return Optional.empty();
    }

    String rest = args[1].substring(FLAG_PREFIX_SCAN_DEPTH.length());
    int depth;
    try {
      depth = Integer.parseInt(rest);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Unable to parse scan depth from arg: " + args[1], e);
    }
    if (depth < 0) {
      throw new RuntimeException("Scan depth must be non-negative, but got " + depth);
    }
    return Optional.of(depth);
  }
}
