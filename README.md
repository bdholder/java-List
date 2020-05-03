# List tests
This repository contains a collection of unit tests for classes implementing a simplified version of Java's [`List`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html) interface. This interface contains only a subset of those in Java's `List` and is not generic: classes implementing this interface are expected to accept and return `Object`s.

The unit tests expect the observable behavior of the included methods to be identical to that of the "real" `List`, except there are no tests to check that iterators throw a [`ConcurrentModificationException`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ConcurrentModificationException.html). Only the non-default methods of [`Iterator`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Iterator.html) are tested.

## Running the tests
From repl.it, you should only need to click the "Run" button to run all of the tests. You can also run the command `make` from the shell.

### Running only selected tests
You may not want to run tests for features that you haven't implemented yet. Tests are selected using tags. To run only a particular set of tests, define the variable `INCLUDE_TAG` when you invoke `make`, providing a `|`-separated list of tags enclosed in quotation marks. For example, to only run tests with the tag `core` or `basic`, you would run `make INCLUDE_TAG="core|basic"`. Notice that `INCLUDE_TAG` is singular.

There are five tags: `core`, `basic`, `exception`, `Iterator`, and `ListIterator`. You should probably get the tests to pass in that order; that is, you should get all `core` tests to pass before moving on to `basic` tests, etc.

The `core` tests only check the methods `add(Object)` and `get(int)`. Since this repository contains only black box tests, we need to have reasonable confidence that these methods are working correctly before moving on to other tests. The `basic` tests check all other functionality aside from throwing exceptions and the correct operation of iterators. There are currently no tests that check for exceptions thrown from iterator methods.

The `Iterator` and `ListIterator` tags are self-explanatory.

There's nothing wrong with always running all the tests. The only drawbacks are that it may take a bit longer and will always display failures for features not yet implemented.

## Modifying the Makefile
There are three variables in the Makefile that you may want to modify. By changing their default values, you can get desired behavior simply by clicking the "Run" button.

The first is `INCLUDE_TAG`. Modify this variable if you don't want to always have to type `make INCLUDE_TAG="..."` to run only selected tests.

The other two are `CLASS` and `PACKAGE`, useful for testing `List` implementations in different files. For example, suppose that you have a class `LinkedList` in package `foo.util`. To invoke the unit tests on it, you would ensure that the file `LinkedList.java` is in the top-level of this repository, and then change the variables `CLASS` and `PACKAGE` to `LinkedList` and `foo.util`, respectively.

On repl.it, as an alternative to tweaking the Makefile, you could modify the setting of the `run` variable in `.replit` to, for example, `"make CLASS=ArrayList PACKAGE=org.roundrockisd.stonypoint.util INCLUDE_TAG='core|basic'"`.