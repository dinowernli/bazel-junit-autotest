def auto_java_test(name, srcs = [], deps = [], **kwargs):
  native.java_test(
      name = name,
      srcs = srcs,
      deps = deps + ["//java/me/dinowernli/junit"],
      main_class = "me.dinowernli.junit.TestRunner",
      **kwargs
  )

# Ensures that there is target called "//external:autotest_junit" available and pointing to a set of
# JUnit jars used to run
def autotest_junit_repo(junit_jar = ""):
  if junit_jar == "":
    native.maven_jar(
      name = "junit_artifact",
      artifact = "junit:junit:4.10",
    )
    junit_jar = "@junit_artifact//jar"

  native.bind(name = "autotest_junit", actual = junit_jar)