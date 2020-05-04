# This makefile is janky.
junit_jar := junit-platform-console-standalone-1.6.0.jar
junitc := javac -classpath .:$(junit_jar)
junit := java -classpath .:$(junit_jar)
args := org.roundrockisd.stonypoint.test.SimpleConsoleLauncher --classpath . --exclude-engine=junit-vintage

CLASS := ArrayList
PACKAGE := org.roundrockisd.stonypoint.util
# Do not include quotes around the tag list; e.g.
#INCLUDE_TAG := core|basic
INCLUDE_TAG :=

class_under_test := $(PACKAGE).$(CLASS)
class_under_test_source_file := $(CLASS).java
class_under_test_class_file := $(subst .,/,$(class_under_test)).class

# class files
ArrayList := org/roundrockisd/stonypoint/util/ArrayList.class
EnvironmentClassUnderTestParameterResolver := org/roundrockisd/stonypoint/test/EnvironmentClassUnderTestParameterResolver.class
List := org/roundrockisd/stonypoint/util/List.class
ListTests := org/roundrockisd/stonypoint/test/ListTests.class
SimpleConsoleLauncher := org/roundrockisd/stonypoint/test/SimpleConsoleLauncher.class

ifneq ($(strip $(INCLUDE_TAG)),)
args += --include-tag='$(INCLUDE_TAG)'
endif

# Recipes

test: $(class_under_test_class_file) $(ListTests) $(SimpleConsoleLauncher)
	env CLASS_UNDER_TEST=$(class_under_test) $(junit) $(args) --scan-class-path

clean:
	@find . -name '*.class' -type f -delete

$(class_under_test_class_file): $(class_under_test_source_file) $(List)
	javac -d . $(class_under_test_source_file)

$(EnvironmentClassUnderTestParameterResolver): src/EnvironmentClassUnderTestParameterResolver.java $(List)
	$(junitc) -d . src/EnvironmentClassUnderTestParameterResolver.java

$(List): List.java
	javac -d . List.java

$(ListTests): src/ListTests.java $(EnvironmentClassUnderTestParameterResolver) $(List)
	$(junitc) -d . src/ListTests.java

$(SimpleConsoleLauncher): src/SimpleConsoleLauncher.java
	$(junitc) -d . src/SimpleConsoleLauncher.java