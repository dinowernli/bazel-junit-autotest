# bazel-junit-autotest
A JUnit test runner which auto-detects tests. Intended for use with Bazel.

# Setup

In order to use bazel-junit-autotest, add the following to your WORKSPACE. << TODO >>

## Basic

load("//bzl:autotest.bzl", "autotest_junit_repo")
autotest_junit_repo()

## With existing JUnit

load("//bzl:autotest.bzl", "autotest_junit_repo")
autotest_junit_repo(junit_jar = "@junit_artifact//jar")
