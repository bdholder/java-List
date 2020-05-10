# Implementing `List` with an array
We're going to walk through how to implement the simplified `List` interface using an array for storage. Basically, we're going to be creating a simplified version of Java's [`ArrayList`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ArrayList.html). I have partly modeled the design on the OpenJDK implementation of `ArrayList`.

## Why use an `ArrayList` instead of an array?
Arrays are a very useful data structure because they offer fast access to all of their contents. Accessing an array element costs the same amount of time regardless of the element's location in the array. The principal disadvantage of an array is that its length is fixed at the time it is created.

This is problematic if we are trying to store data in an array, but don't know how many items we need to store. As a simple example, we might prompt a user to enter a sequence of integers so that we can compute their average, but we can't easily store the integers in an array because we don't know how many the user will enter.

One option would be to create an array large enough that it could contain any "reasonable" number of elements, but this approach may still fail, and it wastes a lot of memory besides. Another approach would be to create an array of some initial length, say 10, and create a new array of a larger size, say 20, if needed, repeating this process until the user has entered all of his data.

Allocating new arrays as needed is an attractive approach because it cuts down on wasted space, but we now face the challenge of creating a new array and making sure all data from the previous array is correctly copied over. This is why `ArrayList` exists: it manages an array internally, handling the issues of allocating new arrays and copying data when needed, but looks to the outside world like a stretchy array. The internal array is isolated from the outside to prevent a programmer from accidentally breaking some aspect of the array or depending on some attribute which may change in the future. This strategy of isolation is generally called [encapsulation](https://en.wikipedia.org/wiki/Encapsulation_(computer_programming)) in object-oriented programming.

## First steps: passing the `core` tests
The core tests will check the functionality of the `add(Object)` and `get(int)` methods, so we will need to need to have a functioning array, a way to add `Object`s to it, and a way to retrieve them. We're going to cheat a little bit at first by allocating a huge array so that we don't have to deal with the challenge of copying data to a new array just yet.

For simplicity, I'll be showing code snippets with methods that haven't yet been implemented omitted, but you will need to have [method stubs](https://en.wikipedia.org/wiki/Method_stub) (minimal dummy implementations) for the code to compile.

```java
public class ArrayList implements List {
    private Object[] elementData;
    private int size;

    public ArrayList() {
        elementData = new Object[1000];
        size = 0;
    }

    @Override
    public boolean add(Object element) {
        elementData[size] = element;
        size++;
        return true;
    }

    @Override
    public Object get(int index) {
        return elementData[index];
    }
}
```

To test the code using only the `core` tests, we type `make INCLUDE_TAG="core"` in the terminal, and we see that the tests pass.

```
└─ JUnit Jupiter ✔
   └─ ListTests ✔
      └─ when new ✔
         └─ core tests ✔
            ├─ test add(Object), get(int) once ✔
            └─ test add(Object), get(int) multiple times ✔

Test run finished after 1407 ms
[         4 containers found      ]
[         0 containers skipped    ]
[         4 containers started    ]
[         0 containers aborted    ]
[         4 containers successful ]
[         0 containers failed     ]
[         2 tests found           ]
[         0 tests skipped         ]
[         2 tests started         ]
[         0 tests aborted         ]
[         2 tests successful      ]
[         0 tests failed          ]
```

Let's look at the code in more detail.

### Fields
We have declared two fields: `Object[] elementData` and `int size`. Neither is static; we want each instance of `ArrayList` to have its own copy of these variables. Both are private; they represent internal state that shouldn't be modifiable or even viewable by code external to our class. As a rule of thumb, all fields of a class should be made private unless you have a compelling reason to do otherwise.

The presence of an array in our class is self-explanatory, but it may not be obvious why a variable to keep track of the "size" of an `ArrayList` is necessary. The reason is that the "logical" size and the "physical" size of an `ArrayList` will not generally be the same. By logical size, I mean the number of elements that the `ArrayList` is currently storing, and by physical size, I mean the length of the array where the elements are stored.

When an `ArrayList` is first created, no elements have been stored yet, so its logical size is 0. We need to know the logical size at all times so that it can be reported to the user and so that we know where the next "free" slot in the array is located.

### The constructor
We have a single, no-argument constructor where we set the initial values of our instance variables. Recall that a constructor has the same name as the class and no return type. Our constructor is public because we want programmers to be able to create instances of `ArrayList` directly.

In the constructor, we create a new array of `Object`s with length 1000, and assign its reference to `elementData`. We also set `size` to 0 since no elements have been stored yet.

### `get(int)`
This method is the easiest since it simply retrieves the element at the corresponding position in the `ArrayList`. We will maintain a one-to-one correlation between the logical index (the index that the outside world uses) and the "physical" index (the index where the data is actually stored) to keep our design simple and maintain the invariant that all data in an array is stored contiguously (without gaps). If the user asks for the element at index 0, we look up that element in our array and return it.

As it stands now, however, there is a bit of a strange phenomenon that can be observed. Consider the following code:

```java
public static void main(String[] args) {
    ArrayList alpha = new ArrayList();
    Object oscar = alpha.get(0);
    System.out.println(oscar);
}
```

The output is simply
```
null
```

We haven't stored any `Object`s in our `ArrayList`, but we are able to request the element at index 0 without an exception occurring. This is because we haven't done any kind of validation of the index the programmer provided to us. This certainly represents a mistake on the programmer's part because it makes no sense to ask for an element in an `ArrayList` without nothing in it, but it passes silently, and would likely manifest itself in some error that occurred down the line. It would be better to check that the index is valid and throw an exception if it is not, but for now, we will ignore this issue for the sake of simplicity.

### `add(Object)`
This method appends an element to the logical end of our `ArrayList`. For example, if the logical contents of our `ArrayList` were `[alpha, bravo, charlie]`, after invoking `add(delta)` it would be `[alpha, bravo, charlie, delta]`.

Clearly, if `add` were repeatedly invoked, we would run out of space in our array, but we're ignoring that for our first pass.

Basically, we need to do three things in `add`: store the provided `Object` in the first free slot in the array, increment the size of the array, and return `true`.

Because we store all data contiguously, and because indexing in Java starts at 0, there is always a direct correspondence between the `size` of the `ArrayList` and the first free index in `elementData`. Very nice!

Incrementing `size` is also easy, as is returning `true`. But, why return `true` at all? Why doesn't `add(Object)` have a return type of `void` like `add(int, Object)`? The reason is that the for-realsies [`List`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html) interface is an extension of the [`Collection`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html) interface, which is meant to be implemented by a wide variety of data structures. For some kinds of collections, like a [`Set`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Set.html), `add`ing an element under certain conditions doesn't make sense, so `add` can sometimes return `false`. However, `add`ing will always succeed for an `ArrayList`, so we always return `true`.

`add(int, Object)` is `void` because it is only in the `List` interface, not the `Collection` interface.

### Fixing `add(Object)`
Let's fix `add(Object)` so that it can correctly acquire more storage when necessary. First, let's modify the constructor to only allocate an array of length 10, and then see what happens if we run the tests again.

```java
public ArrayList() {
    elementData = new Object[10];
    size = 0;
}
```

```
└─ JUnit Jupiter ✔
   └─ ListTests ✔
      └─ when new ✔
         └─ core tests ✔
            ├─ test add(Object), get(int) once ✔
            └─ test add(Object), get(int) multiple times ✘ Index 10 out of bounds for length 10

Failures (1):
  JUnit Jupiter:ListTests:when new:core tests:test add(Object), get(int) multiple times
    MethodSource [className = 'org.roundrockisd.stonypoint.test.ListTests$WhenNew$CoreTests', methodName = 'addGetMultiple', methodParameterTypes = '']
    => java.lang.ArrayIndexOutOfBoundsException: Index 10 out of bounds for length 10
       org.roundrockisd.stonypoint.util.ArrayList.add(ArrayList.java:23)
       org.roundrockisd.stonypoint.test.ListTests.add(ListTests.java:60)
       org.roundrockisd.stonypoint.test.ListTests$WhenNew$CoreTests.lambda$addGetMultiple$0(ListTests.java:148)
       java.base/java.util.stream.Streams$RangeIntSpliterator.forEachRemaining(Streams.java:104)
       java.base/java.util.stream.IntPipeline$Head.forEachOrdered(IntPipeline.java:603)
       org.roundrockisd.stonypoint.test.ListTests$WhenNew$CoreTests.addGetMultiple(ListTests.java:147)
       java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
       java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
       java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
       java.base/java.lang.reflect.Method.invoke(Method.java:566)
       [...]

Test run finished after 1368 ms
[         4 containers found      ]
[         0 containers skipped    ]
[         4 containers started    ]
[         0 containers aborted    ]
[         4 containers successful ]
[         0 containers failed     ]
[         2 tests found           ]
[         0 tests skipped         ]
[         2 tests started         ]
[         0 tests aborted         ]
[         1 tests successful      ]
[         1 tests failed          ]
```

Yikes. That's a lot of output. Let's make some sense of it. There are two sections of output that you'll see if all tests succeed: the "test tree" and the test summary. If there are test failures, you'll see a failure list in between the test tree and test summary.

We currently have one failure which is marked in both the test tree and indicated in the failure list: `test add(Object), get(int) multiple times ✘ Index 10 out of bounds for length 10`. The name of the failed test is `add(Object), get(int) multiple times` and the error message is `Index 10 out of bounds for length 10`.

The failure list will give us more information about what went wrong. It gives the name of the failed test followed by a *stack trace*: a list of the methods that have been called but not yet returned with the most recently called method on top. The exception that caused the failure is directly above the stack trace: `=> java.lang.ArrayIndexOutOfBoundsException: Index 10 out of bounds for length 10`. The message indicates that we attempted to access index 10 in an array of length 10, which is out of bounds because the valid indices for an array of length 10 are 0 through 9.

The top line of the stack trace is `org.roundrockisd.stonypoint.util.ArrayList.add(ArrayList.java:23)`. This means that the `add` method in the class `ArrayList` was the most recently called method, and the exception occurred on line 23 of the file `ArrayList.java`. Line 23 is `elementData[size] = element;`, the attempted array assignment that failed because we ran out of space.

Now that we have some idea of how to interpret errors, let's fix it. Pseudocode for the fix might look something like

```java
@Override
public boolean add(Object element) {
    if (/* out of space in elementData */) {
        // create array with more storage
        // copy contents of old to new array
        // assign the reference of the new array to elementData
    }
    elementData[size] = element;
    size++;
    return true;
}
```

Sketching out pseudocode in comments can be a really useful technique because it helps you remember the high-level strategy before you dive into the weeds and sort out the details.

The first piece to sort out is formalizing "out of space in `elementData`". One way to do this is to consider the opposite condition, and then reverse it: if we have enough space to add an element to `elementData`, then `size` must be less than `elementData.length`. Therefore, if we *don't* have enough space, then it must *not* be true that `size` is less than `elementData.length`. A common mistake is to negate an expression of the form `A < B` by writing `A > B`, but this is incorrect as it could also be the case that `A == B`. The correct negation of `A < B` is `A >= B`. Thus, the negation of `size < elementData.length` is `size >= elementData.length`.

```java
if (size >= elementData.length) {
    // create array with more storage
    // copy contents of old to new array
    // assign the reference of the new array to elementData
}
```

Next, we need to create an array with more storage space. How much more? We're adding a single element, so it may seem like the best option is to create an array with one extra slot: `new Object[size + 1]`. There are drawbacks to this approach, however. While we (almost) never have more space in our array than we need, every time an element is added, a copy of the entire array must be made. The time cost of making such a copy will be proportional to the current size of the backing array.

We will utilize a different strategy: doubling the size of the array. While we will usually be using more space than we strictly need, adding new elements to the array will usually be cheap timewise.

The differences between these two strategies illustrate a recurring concept in computer science: a time-space tradeoff. We are often presented with situations where we can increase the amount of space an algorithm uses to save time, or we can decrease space usage by increasing running time.

Creating a new array with twice the size is simple.

```java
if (size >= elementData.length) {
    // create array with more storage
    Object[] newElementData = new Object[size * 2];
    // copy contents of old to new array
    // assign the reference of the new array to elementData
}
```

There is an edge case here that could trip us up in the future. If we were to revise our `ArrayList` class to initially have a size of 0, our resizing code wouldn't work because `0 * 2 ==> 0`, so we would just get an `ArrayIndexOutOfBoundsException` when attempting to add to the `ArrayList`. It would probably be a good idea to create a new array of length `size * 2 + 1`, but we won't worry about that.

This edge case illustrates how errors can creep into code over time. A section of code written with unstated assumptions for its correctness can fail if those assumptions become false in the future.

Next, we need to copy all of the values from the old array to the new one. The easiest way to do this is with a for loop.

```java
if (size >= elementData.length) {
    // create array with more storage
    Object[] newElementData = new Object[size * 2];
    // copy contents of old to new array
    for (int i = 0; i < size; i++) {
        newElementData[i] = elementData[i];
    }
    // assign the reference of the new array to elementData
}
```

Finally, we need to assign the reference held by `newElementData` to `elementData`. Otherwise, `elementData` will still point to the old array.

```java
if (size >= elementData.length) {
    // create array with more storage
    Object[] newElementData = new Object[size * 2];
    // copy contents of old to new array
    for (int i = 0; i < size; i++) {
        newElementData[i] = elementData[i];
    }
    // assign the reference of the new array to elementData
    elementData = newElementData;
}
```

In full, our `add(Object)` method (with comments removed) now looks like

```java
@Override
public boolean add(Object element) {
    if (size >= elementData.length) {
        Object[] newElementData = new Object[size * 2];
        for (int i = 0; i < size; i++) {
            newElementData[i] = elementData[i];
        }
        elementData = newElementData;
    }
    elementData[size] = element;
    size++;
    return true;
}
```

Running `make INCLUDE_TAG='core'` again, we see that both core tests pass.

Making a new array containing the contents of another array is sufficiently common that there is a method in the standard library for it: [copyOf](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#copyOf(T%5B%5D,int)). We could have instead written

```java
@Override
public boolean add(Object element) {
    if (size >= elementData.length) {
        elementData = Arrays.copyOf(elementData, size * 2);
    }
    elementData[size] = element;
    size++;
    return true;
}
```

We should avoid reinventing the wheel, so the second option is the better one. I simply showed how to do it manually for demonstration purposes.

## `basic` tests
The tests with the `basic` tag cover all functionality other than that related to exceptions and iterators.

### `size()`
The `size` method is pretty straightforward.

```java
@Override
public int size() {
    return size;
}
```

We can have methods and fields with the same name in a class because they are always used in distinct ways: if `size` appears followed by `()`, then we must be referring to the method; if it appears by itself, we must be referring to the field.

We execute the command `make INCLUDE_TAG='core|basic'` to run both `core` and `basic` tests. After implementing `size`, we only have six failures.

### `set(int, Object)`
This method replaces the Object at a given index with the given object and returns the old object. For example, if an object with reference `@0` were at index 2 in our `ArrayList`, and we invoked `set(2, alpha)`, where `alpha` holds reference `@1`, then our `ArrayList` would now have `@1` at index 2 and would return `@0`.

For example, the code

```java
public static void main(String[] args) {
    ArrayList alpha = new ArrayList();
    
    Object bravo = new Object();
    Object charlie = new Object();
    Object delta = new Object();
    
    alpha.add(bravo);
    alpha.add(charlie);
    
    System.out.println(alpha.get(1) == charlie); // true
    
    Object echo = alpha.set(1, delta);

    System.out.println(alpha.get(1) == charlie); // false
    System.out.println(alpha.get(1) == delta); // true
    System.out.println(echo == charlie); // true
}
```

should print

```
true
false
true
true
```

Implementing `set` is pretty simple.

```java
@Override
public Object set(int index, Object element) {
    Object old = elementData[index];
    elementData[index] = element;
    return old;
}
```

We need to use the variable `old` to save the previous value held by the `ArrayList` before we overwrite it with the new value. Otherwise, we wouldn't be able to return it.

After we run `make INCLUDE_TAG='core|basic'` in the terminal, we see that the only method that tests `set` has passed: `test set(int, Object) ✔`.

### `add(int, Object)`
How does [`add(int, Object)`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ArrayList.html#add(int,E)) differ from `add(Object)`? The latter always appends the new element to the end of the `ArrayList`, while the former inserts it into the specified index, "pushing" elements to the right.

Suppose we have an `ArrayList` with these contents:

0 | 1 | 2
--- | --- | ---
A | B | C

After invoking `add(1, D)`, the `ArrayList` would look like

0 | 1 | 2 | 3
--- | --- | --- | ---
A | D | B | C

Note how `D` now occupies index 1, and the previous contents have moved right.

If our backing array is already full, then we'll need to enlarge the array and copy the contents, just as we did for `add(Object)`. We'll just copy and paste the logic from it.

```java
if (size >= elementData.length) {
    elementData = Arrays.copyOf(elementData, size * 2);
}
```

As a rule of thumb, if you find yourself copying and pasting code, you're probably doing something wrong. We'll let it stand for now, and loop back once we've finished `add(int, Object)`.

Now that we know that we have enough room in our array for one more, we can focus on the logic. The easiest strategy is to copy each element to the right, then place the new element at the given index. We don't need to touch any values before the given index.

```java
for (int i = size; i > index; i--) {
    elementData[i] = elementData[i - 1];
}
elementData[index] = element;
```

Don't forget to increment `size`.

```java
size++;
```

Putting it all together, we have

```java
@Override
public void add(int index, Object element) {
    if (size >= elementData.length) {
        elementData = Arrays.copyOf(elementData, size * 2);
    }

    for (int i = size; i > index; i--) {
        elementData[i] = elementData[i - 1];
    }
    elementData[index] = element;
    size++;
}
```

Running the command `make INCLUDE_TAG='core|basic'` in the terminal, we're down to only two failed tests, those for `remove`.