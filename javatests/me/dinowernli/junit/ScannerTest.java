package me.dinowernli.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Unit tests for {@link Scanner}. */
@TestClass
public class ScannerTest {
  private Scanner scanner;

  @Test
  public void findsAnnotatedClass() {
    scanner = Scanner.create(1000 /* scanDepth */);

    Set<Class<?>> result = scanner.findTypesAnnotatedWith(MyAnnotation.class).classes;
    assertTrue(result.contains(Annotated.class));
    assertFalse(result.contains(NotAnnotated.class));
  }

  @Test
  public void respectsScanDepth() {
    scanner = Scanner.create(0 /* scanDepth */);  // Should not scan anything this way.

    Set<Class<?>> result = scanner.findTypesAnnotatedWith(MyAnnotation.class).classes;
    assertFalse(result.contains(Annotated.class));
    assertFalse(result.contains(NotAnnotated.class));
  }

  @Test
  public void findsAnnotatedClassWithScanDepth() {
    scanner = Scanner.create(2 /* scanDepth */);  // Should not scan anything this way.

    Set<Class<?>> result = scanner.findTypesAnnotatedWith(MyAnnotation.class).classes;
    assertTrue(result.contains(Annotated.class));
    assertFalse(result.contains(NotAnnotated.class));
  }

  @Retention(RetentionPolicy.RUNTIME) @interface MyAnnotation {}
  @MyAnnotation static class Annotated {}
  static class NotAnnotated {}
}

