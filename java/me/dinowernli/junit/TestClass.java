package me.dinowernli.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a type contains JUnit tests.
 *
 * Any class annotated with this annotation will be detected by {@link TestRunner} and any
 * contained JUnit tests will be executed as part of running {@link TestRunner}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestClass {}
