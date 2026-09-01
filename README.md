# LRU-Cache-Visualizer
# LRU Cache Implementation Using Circular Doubly Linked List and HashMap

## 1. What is Caching?

**Caching** is a technique of storing frequently accessed data in a temporary, high-speed storage area called a **cache**.

The main purpose of caching is to **reduce access time and improve application performance**.

Instead of repeatedly retrieving data from a slower source, the system first checks whether the required data is available in the cache.

### Basic Working of Cache

```text
User/Application
       |
       v
   Check Cache
    /       \
  Found    Not Found
   |          |
   v          v
Return     Get from
Data       Main Storage
              |
              v
        Store in Cache
              |
              v
          Return Data
```

### Why is caching needed?

Caching helps to:

* Reduce response time
* Reduce database and server load
* Improve application performance
* Reduce network traffic
* Improve scalability
* Provide faster access to frequently used data

---

## Cache Usage in Different Use Cases

### A. Caching in Computer Organisation

In computer organisation, the CPU is much faster than main memory (RAM).

If the CPU had to access RAM for every instruction or piece of data, the CPU would spend a lot of time waiting.

Therefore, a small and fast memory called **CPU Cache** is placed between the CPU and RAM.

```text
CPU
 |
 v
L1 Cache
 |
 v
L2 Cache
 |
 v
L3 Cache
 |
 v
RAM
 |
 v
Secondary Storage
```

CPU caches commonly have multiple levels:

* **L1 Cache** – Smallest and fastest
* **L2 Cache** – Larger but slightly slower
* **L3 Cache** – Larger and usually shared between CPU cores

### Example

Suppose the CPU repeatedly needs the value of variable `x`.

Instead of accessing RAM every time:

```text
First access:
CPU → Cache Miss → RAM → Cache → CPU

Later accesses:
CPU → Cache Hit → CPU
```

This significantly improves execution speed.

---

### B. Caching in Web Applications

Web applications also use caching to avoid repeatedly fetching the same data.

For example, consider an online shopping application.

A product page may contain:

```text
Product Name
Price
Description
Image
Reviews
```

If thousands of users request the same product information, retrieving it from the database for every request can create a large load.

Instead, frequently requested data can be stored in a cache.

```text
User
 |
 v
Web Server
 |
 v
Cache
 |
 |-- Cache Hit → Return Data
 |
 |-- Cache Miss
          |
          v
       Database
          |
          v
     Store in Cache
          |
          v
       Return Data
```

### Benefits

For example:

Without caching:

```text
1000 requests
      ↓
1000 database queries
```

With caching:

```text
1000 requests
      ↓
Cache handles most requests
      ↓
Only a few requests reach database
```

This reduces database load and improves response time.

---

## 2. Introduction to Redis and Memcached

### Redis

**Redis** is an in-memory data store commonly used as a cache, database, message broker, and for other high-performance data-processing tasks.

Redis stores data primarily in memory, which allows very fast read and write operations.

### Important Features of Redis

* In-memory storage
* Key-value data model
* Supports strings, lists, sets, sorted sets, hashes, streams, etc.
* Supports data persistence
* Supports replication
* Supports transactions
* Supports pub/sub
* Supports TTL (Time To Live)
* Supports multiple cache eviction policies

### Example

```text
Key       Value
-----------------------
user:101  "Sai"
user:102  "Pranathi"
```

A web application can retrieve:

```text
GET user:101
```

and Redis can return the value quickly.

---

### Memcached

**Memcached** is a simple, high-performance, distributed in-memory caching system.

It is primarily designed to cache frequently accessed data and reduce database load.

### Important Features of Memcached

* In-memory key-value cache
* Very fast
* Simple architecture
* Distributed caching support
* Supports expiration time
* Simple data model
* Mainly used as a cache rather than a persistent database

### Redis vs Memcached

| Feature           | Redis                              | Memcached                   |
| ----------------- | ---------------------------------- | --------------------------- |
| Data model        | Rich data structures               | Simple key-value            |
| Persistence       | Supported                          | No                          |
| Replication       | Supported                          | More limited/simple         |
| Pub/Sub           | Supported                          | Not a core feature          |
| Data structures   | Strings, Lists, Sets, Hashes, etc. | Basic values                |
| Complexity        | More feature-rich                  | Simpler                     |
| Main purpose      | Cache + data store                 | Cache                       |
| Eviction policies | Multiple                           | Mainly LRU-related policies |

### When to use Redis?

Use Redis when you need:

* Advanced data structures
* Persistence
* Pub/Sub
* Distributed applications
* Counters
* Session storage
* Leaderboards
* More advanced caching requirements

### When to use Memcached?

Use Memcached when:

* You need a simple cache
* Data does not need persistence
* Simple key-value storage is sufficient
* You want a lightweight caching solution

---

# 3. Cache Eviction Strategies

A cache has a **limited amount of memory**.

When the cache becomes full and a new item needs to be inserted, some existing item must be removed.

The process of deciding **which item should be removed** is called **cache eviction**.

### Why is cache eviction needed?

Consider a cache that can store only 3 items:

```text
Cache Capacity = 3

[A] [B] [C]
```

Now we need to insert `D`.

There is no free space.

Therefore, one of:

```text
A
B
C
```

must be removed.

The eviction strategy decides which one.

---

## Common Cache Eviction Strategies

### 1. FIFO – First In, First Out

The item that entered the cache first is removed first.

```text
A → B → C

Insert D

Remove A

B → C → D
```

### When to use FIFO?

FIFO is useful when:

* Simplicity is important
* The oldest items are more likely to become irrelevant
* Access frequency does not matter

### Disadvantage

It does not consider how frequently or recently an item is used.

---

## 2. LRU – Least Recently Used

LRU removes the item that has **not been used for the longest time**.

Example:

```text
Cache:

A B C

Access A

B C A
```

If the cache is full and `D` is inserted:

```text
B C A D
```

`B` is removed because it is the least recently used item.

### When to use LRU?

LRU is useful when:

* Recently accessed data is likely to be accessed again
* Applications have temporal locality
* You want a good general-purpose caching strategy

### Examples

LRU can be used for:

* Browser caches
* Database buffer caches
* Operating systems
* Web applications
* API response caching

---

## 3. LFU – Least Frequently Used

LFU removes the item that has been accessed the **fewest number of times**.

Example:

```text
A → accessed 10 times
B → accessed 2 times
C → accessed 5 times
```

If eviction is required:

```text
B
```

will be removed.

### When to use LFU?

LFU is useful when:

* Frequently accessed data should remain in the cache
* Some data is consistently more popular than other data

### Disadvantage

A previously popular item may remain in the cache even after it becomes irrelevant.

---

## 4. Random Replacement

A randomly selected item is removed.

```text
[A] [B] [C]

Insert D

Randomly remove B

[A] [C] [D]
```

### When to use?

Useful when:

* Very simple implementation is required
* Exact access history is not important
* Low overhead is preferred

---

## 5. MRU – Most Recently Used

MRU removes the item that was used **most recently**.

This is essentially the opposite of LRU.

### When to use?

MRU can be useful in workloads where recently accessed data is unlikely to be needed again soon.

---

## Comparison

| Strategy | Removes               | Best suited for             |
| -------- | --------------------- | --------------------------- |
| FIFO     | Oldest item           | Simple workloads            |
| LRU      | Least recently used   | General-purpose caching     |
| LFU      | Least frequently used | Popularity-based workloads  |
| Random   | Random item           | Simple/low-overhead systems |
| MRU      | Most recently used    | Specific access patterns    |

### Why choose LRU?

LRU is one of the most commonly used cache eviction strategies because many applications exhibit **temporal locality**.

> If data was accessed recently, there is a good chance it will be accessed again soon.

---

# 4. UML Class Diagram for LRU Cache

An LRU Cache can be efficiently implemented using:

1. **HashMap**
2. **Circular Doubly Linked List**

The HashMap provides fast lookup, while the Circular Doubly Linked List maintains the usage order.

### UML Diagram

```text
+----------------------------------+
|            LRUCache              |
+----------------------------------+
| - capacity : int                 |
| - map : HashMap<Integer, Node>   |
| - list : CircularDLL             |
+----------------------------------+
| + LRUCache(capacity : int)       |
| + get(key : int) : int          |
| + put(key : int, value : int)    |
| + display() : void               |
+----------------+-----------------+
                 |
                 | uses
                 v
+----------------------------------+
|          CircularDLL             |
+----------------------------------+
| - head : Node                    |
+----------------------------------+
| + addFirst(node : Node) : void   |
| + remove(node : Node) : void     |
| + removeLast() : Node            |
| + moveToFront(node : Node)       |
| + display() : void               |
+----------------+-----------------+
                 |
                 | contains
                 v
+----------------------------------+
|              Node                |
+----------------------------------+
| - key : int                      |
| - value : int                    |
| - prev : Node                    |
| - next : Node                    |
+----------------------------------+
```

### Data Structure Arrangement

```text
HashMap
+---------+----------------+
| Key     | Node Address   |
+---------+----------------+
| 10      | ──────────────┐|
| 20      | ────────────┐ ||
| 30      | ──────────┐ | ||
+---------+           | | ||
                      ↓ ↓ ↓

Circular Doubly Linked List

       +------+     +------+     +------+
       |  30  | <-> |  20  | <-> |  10  |
       +------+     +------+     +------+
          ↑                            |
          |____________________________|
          
       Most Recent             Least Recent
```

Here:

* **Head** = Most Recently Used (MRU)
* **Tail** = Least Recently Used (LRU)

When an item is accessed, it is moved to the front.

When the cache is full, the last node is removed.

---

# 5. Why Use Circular Doubly Linked List?

A combination of **HashMap + Circular Doubly Linked List** is preferred for an efficient LRU Cache.

## Why not Array?

Arrays have some limitations.

Suppose:

```text
[A] [B] [C] [D]
```

If `B` becomes recently used, we may need to move it to the front.

This can require shifting multiple elements.

Therefore, operations can take **O(n)** time.

---

## Why not Singly Linked List?

In a singly linked list:

```text
A → B → C → D
```

Each node only knows its next node.

If we want to remove `C`, we need to find its previous node `B`.

That requires traversal.

Therefore, deletion can take **O(n)** time.

---

## Advantage of Doubly Linked List

A doubly linked list stores:

```text
prev
 ↓
[A]
 ↓
next
```

Each node knows both:

```text
previous node
next node
```

Therefore, if we already have the node reference, it can be removed directly.

```text
A <-> B <-> C <-> D

Remove C:

A <-> B <-> D
```

This takes **O(1)** time.

---

## Why Circular Doubly Linked List?

A circular doubly linked list connects the last node back to the first node.

```text
        +--------------------+
        |                    |
        v                    |
A <-> B <-> C <-> D --------+
```

Advantages:

* Easy access to first and last nodes
* No `null` at the ends
* Simplifies insertion and deletion
* Efficient movement of nodes
* Convenient handling of the LRU and MRU ends

### Complexity

With:

```text
HashMap + Doubly Linked List
```

we can achieve:

| Operation       |    Time Complexity |
| --------------- | -----------------: |
| `get()`         |               O(1) |
| `put()`         |               O(1) |
| Remove node     |               O(1) |
| Insert at front |               O(1) |
| Search node     | O(1) using HashMap |

Overall:

**Time Complexity: O(1) average for `get()` and `put()`**

**Space Complexity: O(capacity)**

---

# 6. Java Code – LRU Cache Using Circular Doubly Linked List and HashMap

```java
import java.util.HashMap;
import java.util.Map;

/*
 * Node represents one entry in the LRU Cache.
 *
 * Each node stores:
 * 1. key   - unique key of the cache entry
 * 2. value - value associated with the key
 * 3. prev  - previous node
 * 4. next  - next node
 */
class Node {

    int key;
    int value;

    Node prev;
    Node next;

    Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}


/*
 * Circular Doubly Linked List
 *
 * The list maintains the order of recently used elements.
 *
 * head -> Most Recently Used (MRU)
 * tail -> Least Recently Used (LRU)
 *
 * Since the list is circular:
 *
 * head.prev = tail
 * tail.next = head
 */
class CircularDLL {

    Node head = null;


    /*
     * Add a node at the beginning of the list.
     *
     * The new node becomes the Most Recently Used node.
     *
     * Time Complexity: O(1)
     */
    void addFirst(Node node) {

        // If list is empty
        if (head == null) {

            head = node;

            // Circular connection
            head.next = head;
            head.prev = head;
        }

        else {

            // Current tail
            Node tail = head.prev;

            // Connect new node with head
            node.next = head;
            node.prev = tail;

            // Update old head and tail
            tail.next = node;
            head.prev = node;

            // New node becomes head
            head = node;
        }
    }


    /*
     * Remove a given node from the list.
     *
     * Time Complexity: O(1)
     *
     * No traversal is required because we already
     * have the reference to the node.
     */
    void remove(Node node) {

        // If there is only one node
        if (node.next == node) {

            head = null;
        }

        else {

            node.prev.next = node.next;
            node.next.prev = node.prev;

            // If the removed node is head,
            // move head to the next node.
            if (node == head) {
                head = node.next;
            }
        }

        // Disconnect the removed node
        node.next = null;
        node.prev = null;
    }


    /*
     * Move an existing node to the front.
     *
     * This means the node becomes the Most Recently Used node.
     *
     * Time Complexity: O(1)
     */
    void moveToFront(Node node) {

        // Already the first node
        if (node == head) {
            return;
        }

        remove(node);
        addFirst(node);
    }


    /*
     * Remove and return the last node.
     *
     * The last node is the Least Recently Used node.
     *
     * Time Complexity: O(1)
     */
    Node removeLast() {

        if (head == null) {
            return null;
        }

        // Tail is previous node of head
        Node tail = head.prev;

        remove(tail);

        return tail;
    }


    /*
     * Display cache contents from
     * Most Recently Used to Least Recently Used.
     */
    void display() {

        if (head == null) {
            System.out.println("Cache is empty.");
            return;
        }

        Node current = head;

        System.out.print("Cache: ");

        do {

            System.out.print(
                "(" + current.key + "," + current.value + ") "
            );

            current = current.next;

        } while (current != head);

        System.out.println();
    }
}


/*
 * LRU Cache
 *
 * Uses:
 *
 * 1. HashMap
 *    - Provides O(1) average lookup.
 *
 * 2. Circular Doubly Linked List
 *    - Maintains the order of recently used nodes.
 *
 * Head = Most Recently Used
 * Tail = Least Recently Used
 */
class LRUCache {

    private int capacity;

    /*
     * HashMap stores:
     *
     * key -> corresponding Node
     *
     * This allows us to find a node in O(1) average time.
     */
    private Map<Integer, Node> map;

    /*
     * Circular Doubly Linked List
     * maintains usage order.
     */
    private CircularDLL list;


    /*
     * Constructor
     */
    LRUCache(int capacity) {

        this.capacity = capacity;

        map = new HashMap<>();

        list = new CircularDLL();
    }


    /*
     * Get value associated with the key.
     *
     * If key exists:
     * 1. Get node from HashMap.
     * 2. Move node to the front.
     * 3. Return value.
     *
     * If key does not exist:
     * Return -1.
     *
     * Time Complexity: O(1) average
     */
    int get(int key) {

        if (!map.containsKey(key)) {
            return -1;
        }

        Node node = map.get(key);

        // Mark this node as recently used
        list.moveToFront(node);

        return node.value;
    }


    /*
     * Insert or update a key-value pair.
     *
     * Cases:
     *
     * 1. Key already exists
     *    -> Update value
     *    -> Move node to front
     *
     * 2. Key does not exist
     *    -> Create new node
     *    -> Add to front
     *
     * 3. Cache becomes full
     *    -> Remove least recently used node
     *
     * Time Complexity: O(1) average
     */
    void put(int key, int value) {

        /*
         * Case 1:
         * Key already exists.
         */
        if (map.containsKey(key)) {

            Node node = map.get(key);

            // Update value
            node.value = value;

            // Mark as recently used
            list.moveToFront(node);

            return;
        }


        /*
         * Case 2:
         * Create a new node.
         */
        Node newNode = new Node(key, value);

        /*
         * Add the node to the front.
         * It becomes the Most Recently Used node.
         */
        list.addFirst(newNode);

        /*
         * Store the node in HashMap.
         */
        map.put(key, newNode);


        /*
         * Case 3:
         * If cache exceeds capacity,
         * remove the Least Recently Used node.
         */
        if (map.size() > capacity) {

            Node lruNode = list.removeLast();

            map.remove(lruNode.key);
        }
    }


    /*
     * Display cache contents.
     */
    void display() {

        list.display();
    }
}


/*
 * Main class
 *
 * Demonstrates the working of the LRU Cache.
 */
public class Main {

    public static void main(String[] args) {

        /*
         * Create an LRU Cache with capacity 3.
         */
        LRUCache cache = new LRUCache(3);


        /*
         * Insert three elements.
         */
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);

        System.out.println("After inserting 1, 2 and 3:");
        cache.display();


        /*
         * Access key 1.
         *
         * Key 1 becomes the Most Recently Used item.
         */
        System.out.println("\nAccess key 1:");
        System.out.println("Value = " + cache.get(1));

        cache.display();


        /*
         * Insert key 4.
         *
         * Cache is already full.
         *
         * Key 2 is the Least Recently Used item,
         * so key 2 will be removed.
         */
        System.out.println("\nInsert key 4:");

        cache.put(4, 400);

        cache.display();


        /*
         * Try to access key 2.
         *
         * Key 2 was removed during eviction.
         */
        System.out.println("\nAccess key 2:");

        System.out.println("Value = " + cache.get(2));


        /*
         * Access key 3.
         */
        System.out.println("\nAccess key 3:");

        System.out.println("Value = " + cache.get(3));

        cache.display();
    }
}
```

## Expected Output

```text
After inserting 1, 2 and 3:
Cache: (3,300) (2,200) (1,100)

Access key 1:
Value = 100
Cache: (1,100) (3,300) (2,200)

Insert key 4:
Cache: (4,400) (1,100) (3,300)

Access key 2:
Value = -1

Access key 3:
Value = 300
Cache: (3,300) (4,400) (1,100)
```

---

# How the LRU Cache Works

Suppose the capacity is `3`.

### Step 1: Insert 1

```text
(1)
```

### Step 2: Insert 2

```text
(2) → (1)
```

`2` is most recently used.

### Step 3: Insert 3

```text
(3) → (2) → (1)
```

Here:

```text
MRU                LRU
 ↓                  ↓
(3) → (2) → (1)
```

### Step 4: Access 1

When we access `1`, it becomes most recently used.

```text
(1) → (3) → (2)
```

### Step 5: Insert 4

The cache is full.

The LRU item is `2`.

Therefore:

```text
Remove 2

(4) → (1) → (3)
```

This is why the combination of **HashMap + Circular Doubly Linked List** is powerful.

---

# Why HashMap + Circular DLL?

Using only a HashMap:

```text
HashMap
key → value
```

gives fast lookup, but it does not maintain the order of recently used elements.

Using only a linked list:

```text
A → B → C → D
```

maintains order, but searching for a particular key can take **O(n)**.

Combining both gives:

```text
             LRU Cache
                 |
       +---------+---------+
       |                   |
    HashMap             Circular DLL
       |                   |
   Fast lookup        Usage ordering
       |                   |
      O(1)                O(1)
```

Therefore:

> **HashMap provides fast access, while the Circular Doubly Linked List provides fast insertion, deletion, and maintenance of LRU order.**

Together, they provide **O(1) average time complexity for both `get()` and `put()` operations**.

---

# Conclusion

Caching is an important technique for improving system performance by storing frequently accessed data in fast storage.

Redis and Memcached are popular in-memory caching technologies used in web applications.

Different eviction strategies such as **FIFO, LRU, LFU, MRU, and Random Replacement** are used when the cache becomes full.

For an **LRU Cache**, the combination of a **HashMap and Circular Doubly Linked List** is highly efficient:

* HashMap → O(1) average lookup
* Circular DLL → O(1) insertion and deletion
* Head → Most Recently Used
* Tail → Least Recently Used
* Overall `get()` → O(1) average
* Overall `put()` → O(1) average
* Space → O(capacity)

This design is widely used as a standard approach for implementing an efficient LRU Cache.
