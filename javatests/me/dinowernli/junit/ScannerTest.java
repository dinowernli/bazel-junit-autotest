package me.dinowernli.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Unit tests for {@link Scanner}. */
@TestClass
public class ScannerTest {
  private Scanner scanner;

  @Before
  public void setUp() {
    scanner = Scanner.create();
  }

  @Test
  public void findsAnnotatedClass() {
    Set<Class<?>> result = scanner.findTypesAnnotatedWith(MyAnnotation.class);
    assertTrue(result.contains(Annotated.class));
    assertFalse(result.contains(NotAnnotated.class));
  }

  @Test
  public void otherTest() {
    Set<Class<?>> result = scanner.findTypesAnnotatedWith(MyAnnotation.class);
    assertTrue(result.contains(Annotated.class));
    assertFalse(result.contains(NotAnnotated.class));
  }

  @Retention(RetentionPolicy.RUNTIME) @interface MyAnnotation {}
  @MyAnnotation static class Annotated {}
  static class NotAnnotated {}
}

