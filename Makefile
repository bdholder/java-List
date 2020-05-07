# This makefile is janky.
junit_jar := junit-platform-console-standalone-1.6.0.jar
junitc := javac -classpath .:$(junit_jar)
junit := java -classpath .:$(junit_jar)
args := --classpath . --exclude-engine=junit-vintage

BASE := org.roundrockisd.stonypoint
CLASS := ArrayList
PACKAGE := $(BASE).util
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

ifneq ($(strip $(INCLUDE_TAG)),)
args += --include-tag='$(INCLUDE_TAG)'
endif

# Recipes
test: $(class_under_test_class_file) $(ListTests)
	env CLASS_UNDER_TEST=$(class_under_test) java -jar $(junit_jar) $(args) --select-class=$(BASE).test.ListTests

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