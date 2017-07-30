# bazel-junit-autotest

A [JUnit](http://junit.org/junit4/) test runner which auto-detects tests. Intended for use with [Bazel](https://bazel.build/).

## Setup

In order to use autotest, first import the repo into your `WORKSPACE` file:

```python
git_repository(
  name = "autotest",
  remote = "https://github.com/dinowernli/bazel-junit-autotest.git",
  tag = "v0.0.1",
)
```

If you are not already importing the JUnit library jars, you can configure autotest by adding the following to your `WORKSPACE` file:

```python
load("@autotest//bzl:autotest.bzl", "autotest_junit_repo")
autotest_junit_repo(autotest_workspace="@autotest")
```
Alternatively, if you do already have the JUnit library jars available under a target called `//third_party/junit`, you can configure autotest by adding the following:

```python
load("@autotest//bzl:autotest.bzl", "autotest_junit_repo")
autotest_junit_repo(junit_jar = "//third_party/junit", autotest_workspace="@autotest")
```

## Writing tests

Inside your `BUILD` file, add:

```python
load("@autotest//bzl:autotest.bzl", "auto_java_test")
```

Then, to declare a test, use `auto_java_test` as a drop-in replacement for the native `java_test`:

```python
auto_java_test(
  name = "tests",
  srcs = glob(['*.java']),
  size = "small",
)
```

The test runner will then scan the sources for types annotated with `@TestClass`, e.g.,

```java
import me.dinowernli.junit.TestClass;
import org.junit.Test;

@TestClass
public class SomeTestClass {
  @Test
  public void testSomething() {
    // ...
  }
}
```

## Scan depth

By default, only first entry of the classpath is scanned for annotated classes. If you want to scan more entries, you can pass a `test_arg`, e.g.,

```
bazel test //foo/bar:tests --test_arg=--scan_depth=7
```


## Usage examples

For real-life examples, check out the [polyglot](https://github.com/grpc-ecosystem/polyglot/blob/master/src/test/java/me/dinowernli/grpc/polyglot/grpc/CompositeStreamObserverTest.java#L18) repo.
