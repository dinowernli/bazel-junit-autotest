load("@bazel_tools//tools/build_defs/repo:jvm.bzl", "jvm_maven_import_external")

JUNIT_ARTIFACT = "junit:junit:4.10"

MAVEN_SERVERS = [
    "https://jcenter.bintray.com/",
    "https://maven.google.com",
    "https://repo1.maven.org/maven2",
]

def auto_java_test(name, srcs = [], deps = [], args = [], **kwargs):
  native.java_test(
      name = name,
      srcs = srcs,
      deps = deps + ["//external:autotest_runner_target"],
      args = ["--scan_depth"] + args,
      main_class = "me.dinowernli.junit.TestRunner",
      **kwargs
  )

# Ensures that there is target called "//external:autotest_junit" available and pointing to a set of
# JUnit jars used to run
def autotest_junit_repo(junit_jar = "", autotest_workspace = ""):
  if junit_jar == "":
    jvm_maven_import_external(
        name = "junit_artifact",
        artifact = JUNIT_ARTIFACT,
        server_urls = MAVEN_SERVERS,
        licenses = ["notice"],  # Apache 2.0
    )
    junit_jar = "@junit_artifact//jar"
  native.bind(name = "autotest_junit", actual = junit_jar)
  native.bind(name = "autotest_runner_target", actual = autotest_workspace + "//java/me/dinowernli/junit")
