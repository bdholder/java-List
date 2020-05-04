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