package(default_visibility = ["//visibility:public"])

java_binary(
  name = "main",
  main_class = "me.dinowernli.junit.TestRunner",
  runtime_deps = [
    ":junit",
  ],
)

java_library(
  name = "junit",
  srcs = glob(["*.java"]),
  deps = [
    "@junit_artifact//jar",
  ],
)
