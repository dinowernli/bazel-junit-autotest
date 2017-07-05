def auto_java_test(name, srcs = [], deps = []):
  native.java_test(
      name = name,
      srcs = srcs,
      deps = deps + ["//java/me/dinowernli/junit"],
      main_class = "me.dinowernli.junit.TestRunner",
  )
