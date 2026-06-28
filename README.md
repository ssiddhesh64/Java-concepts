# Java Advanced Concepts & Refactoring Practice

Welcome to the **Java Advanced Concepts & Refactoring Practice** repository. This codebase serves as an educational sandbox designed to master modern Java (Java 17+), concurrency, advanced stream lifecycles, collections internals, and SOLID refactoring principles.

Each file in this repository is a self-contained programming challenge, debugging round, or refactoring drill.

---

## 🗺️ Repository Map & Index

Below is a complete index of all educational files in this repository, mapped to their target concepts and key learning takeaways:

### 🧵 Concurrency & Asynchronous Programming

| File | Concept Taught | Why It Was Written | Key Lesson |
| :--- | :--- | :--- | :--- |
| [OneShotLatch.java](file:///Users/siddheshsawant/Desktop/java/OneShotLatch.java) | Wait & Notify Coordination | Show primitive thread signaling. | Always check condition variables in a `while`-loop (not an `if`) to handle spurious wakeups. |
| [CountDownLatchExample.java](file:///Users/siddheshsawant/Desktop/java/CountDownLatchExample.java) | Latch Synchronization | Coordinate service startup threads. | Ensure counting collections are thread-safe (e.g. `ConcurrentHashMap.newKeySet()`). |
| [BoundedQueue.java](file:///Users/siddheshsawant/Desktop/java/BoundedQueue.java) | Bounded Coordination | Custom producer-consumer queue. | Call `lock.lock()` outside `try`, use `signal()` instead of `signalAll()` to reduce context switches, and propagate `InterruptedException`. |
| [ConcurrencyBug.java](file:///Users/siddheshsawant/Desktop/java/ConcurrencyBug.java) | Collection Thread Safety | Expose race conditions in lists. | Concurrent writes to standard collections (like `ArrayList`) cause data loss. Use `Collections.synchronizedList()`. |
| [ConcurrencyLimiterChallenge.java](file:///Users/siddheshsawant/Desktop/java/ConcurrencyLimiterChallenge.java) | Semaphore Rate Limiting | Throttle concurrent execution. | Restrict execution concurrency via `Semaphore`. Always release in a `finally` block. |
| [ConcurrentCache.java](file:///Users/siddheshsawant/Desktop/java/ConcurrentCache.java) | Atomic Updates | Thread-safe caching. | Check-then-act operations are not atomic. Use `ConcurrentHashMap.computeIfAbsent()`. |
| [ConcurrentDataProcessor.java](file:///Users/siddheshsawant/Desktop/java/ConcurrentDataProcessor.java) | ExecutorService & Pools | Manage thread lifecycles. | Do not spawn manual threads in a loop. Use a managed executor pool. |
| [CustomThreadPoolChallenge.java](file:///Users/siddheshsawant/Desktop/java/CustomThreadPoolChallenge.java) | Thread Pool Policies | Customize pool rejections. | Define thread factories and custom rejected execution policies (e.g. blocking rejection). |
| [DoubleCheckedLockingSingleton.java](file:///Users/siddheshsawant/Desktop/java/DoubleCheckedLockingSingleton.java) | Lazy Initialization | Thread-safe singletons. | Use double check-then-act. The `volatile` keyword is mandatory to prevent instruction reordering. |
| [FutureFetchAndCalculate.java](file:///Users/siddheshsawant/Desktop/java/FutureFetchAndCalculate.java) | CompletableFuture Pipeline | Combine async results. | Use `thenCombine` to merge results and `exceptionally` at the pipeline end. |
| [RacingFuturesChallenge.java](file:///Users/siddheshsawant/Desktop/java/RacingFuturesChallenge.java) | Race Coordination | Consume fastest result. | Use `CompletableFuture.anyOf()`. Handle exceptions carefully so errors don't hide successes. |
| [ScatterGatherChallenge.java](file:///Users/siddheshsawant/Desktop/java/ScatterGatherChallenge.java) | Scatter-Gather Pattern | Parallel API calling. | Trigger requests in parallel, await with a timeout, and merge results. |
| [SecurityContextHolder.java](file:///Users/siddheshsawant/Desktop/java/SecurityContextHolder.java) | ThreadLocal Storage | Thread-scoped data. | Always clean up using `ThreadLocal.remove()` in a `finally` block to prevent leaks in thread pools. |
| [PaymentServiceRefactor.java](file:///Users/siddheshsawant/Desktop/java/PaymentServiceRefactor.java) | Async Non-Blocking Retries | Retry async tasks cleanly. | Avoid blocking loops (`Thread.sleep`). Recursively chain delayed retries via `ScheduledExecutorService`. |
| [NestedTaskProcessor.java](file:///Users/siddheshsawant/Desktop/java/NestedTaskProcessor.java) | Thread Pool Starvation Deadlock | Expose nested task dependency hangs. | Do not submit dependent parent and subtasks to the same fixed thread pool. Solve by isolating pools or using non-blocking future pipelines. |
| [SimpleCircuitBreaker.java](file:///Users/siddheshsawant/Desktop/java/SimpleCircuitBreaker.java) | Lock-Free Circuit Breaker | Safe multi-threaded state transitions. | Use `AtomicReference` and `compareAndSet()` to perform atomic state transitions (CLOSED, OPEN, HALF_OPEN) and avoid `synchronized` bottlenecks. Ensure concurrent requests fast-fail during half-open probing. |
| [ServerStatusManager.java](file:///Users/siddheshsawant/Desktop/java/ServerStatusManager.java) | Volatile Array Mechanics | Expose array element thread-visibility bug. | Volatile array references do not make the individual elements volatile. Solve by using flat collections like `AtomicIntegerArray` to prevent wrapper object allocation overheads. |

### 🌊 Streams, Lambdas & Optionals

| File | Concept Taught | Why It Was Written | Key Lesson |
| :--- | :--- | :--- | :--- |
| [CheckedExceptionPropagator.java](file:///Users/siddheshsawant/Desktop/java/CheckedExceptionPropagator.java) | Stream Checked Exceptions | Propagate exceptions. | Wrap checked exceptions inside streams and unwrap them outside. |
| [CheckedExceptionsStreams.java](file:///Users/siddheshsawant/Desktop/java/CheckedExceptionsStreams.java) | Stream Exception Handling | Manage throwing functions. | Wrap checked exceptions in runtime wrappers to fit stream signatures. |
| [CustomCollector.java](file:///Users/siddheshsawant/Desktop/java/CustomCollector.java) | Custom Collectors | Aggregation mechanics. | Implement a thread-safe `Collector` using suppliers, accumulators, and combiners. |
| [ResilientStreamCollector.java](file:///Users/siddheshsawant/Desktop/java/ResilientStreamCollector.java) | Exception Partitioning | Stream resilience. | Group successes and failures separately instead of immediately aborting. |
| [StreamPractice.java](file:///Users/siddheshsawant/Desktop/java/StreamPractice.java) | Optional Navigation | Safe reference chaining. | Use monadic `Optional.map` to navigate nullable properties safely. |
| [StreamReuseDebug.java](file:///Users/siddheshsawant/Desktop/java/StreamReuseDebug.java) | Stream Lifecycle | Single-use pipelines. | Streams cannot be reused once a terminal operation is called. |
| [Teeing.java](file:///Users/siddheshsawant/Desktop/java/Teeing.java) | Double Aggregations | Collectors.teeing() | Run two separate collectors and merge them. Use type witnesses to solve compiler bugs. |
| [GroupBy.java](file:///Users/siddheshsawant/Desktop/java/GroupBy.java) | Grouping Collectors | Group elements into Maps. | Use `Collectors.groupingBy()` with downstream collectors for aggregations. |
| [PartitionBySalary.java](file:///Users/siddheshsawant/Desktop/java/PartitionBySalary.java) | Partitioning Collectors | Split stream by predicate. | `Collectors.partitioningBy()` always yields a `Map<Boolean, List<T>>`. |

### 📦 Collections & Memory Internals

| File | Concept Taught | Why It Was Written | Key Lesson |
| :--- | :--- | :--- | :--- |
| [HashSetDebug.java](file:///Users/siddheshsawant/Desktop/java/HashSetDebug.java) | Hash Key Immutability | Prevent hash bucket loss. | HashSet/HashMap keys must be immutable; changing properties alters the hashcode. |
| [MergeMaps.java](file:///Users/siddheshsawant/Desktop/java/MergeMaps.java) | Map Merging | Atomic map collision merges. | Use `Map.merge()` with method references (e.g. `Math::max`). |
| [SubListExample.java](file:///Users/siddheshsawant/Desktop/java/SubListExample.java) | SubList Views & Leaks | SubList memory overheads. | `List.subList()` is a view. Modifying parent list throws `ConcurrentModificationException`. Detach using `new ArrayList<>(subList)` to avoid memory leaks. |
| [ComparatorExample.java](file:///Users/siddheshsawant/Desktop/java/ComparatorExample.java) | Comparator Chaining | Declarative sorting. | Chain sorting criteria using `Comparator.comparing` and `thenComparing`. |
| [UnboxingDebug.java](file:///Users/siddheshsawant/Desktop/java/UnboxingDebug.java) | Auto-Unboxing NPE | Avoid unboxing crashes. | Unboxing a null wrapper type (e.g. `Integer`) to a primitive `int` throws a `NullPointerException`. |
| [PrecisionDebug.java](file:///Users/siddheshsawant/Desktop/java/PrecisionDebug.java) | Float Precision | Float calculation limits. | Do not use `double`/`float` for currencies. Use `BigDecimal` or epsilon thresholds. |
| [DateFormatDebug.java](file:///Users/siddheshsawant/Desktop/java/DateFormatDebug.java) | Thread Safety | Safe date formatting. | `SimpleDateFormat` is NOT thread-safe. Use `DateTimeFormatter`. |
| [SimpleTtlCache.java](file:///Users/siddheshsawant/Desktop/java/SimpleTtlCache.java) | Thread-Safe TTL Cache | Concurrent TTL evictions. | Use `ConcurrentHashMap` for high-throughput reads/writes. Use `removeIf()` on the entrySet to avoid `ConcurrentModificationException` inside scheduled cleanup tasks. |

### 🏛️ SOLID Architecture & Modern Java

| File | Concept Taught | Why It Was Written | Key Lesson |
| :--- | :--- | :--- | :--- |
| [DependencyInjectionChallenge.java](file:///Users/siddheshsawant/Desktop/java/DependencyInjectionChallenge.java) | Custom DI Container | Reflection wiring. | Inject instances dynamically via Reflection and custom annotations. |
| [OrderProcessor.java](file:///Users/siddheshsawant/Desktop/java/OrderProcessor.java) | Procedural Code Review | Expose code smells. | High nesting and multiple concerns violate SRP and DIP. |
| [OrderProcessor2.java](file:///Users/siddheshsawant/Desktop/java/OrderProcessor2.java) | Decoupling & Enums | Clean refactored service. | Encapsulate custom rates in enums and dependencies in interfaces. |
| [ReportGeneratorRefactor.java](file:///Users/siddheshsawant/Desktop/java/ReportGeneratorRefactor.java) | Stateless Exporters | Service separation. | Separate dynamic state (records) from stateless service classes. |
| [UserRegistrationServiceRefactor.java](file:///Users/siddheshsawant/Desktop/java/UserRegistrationServiceRefactor.java) | Modern Java 17 | Clean registration pipeline. | Replaced legacy classes with `LocalDate`, `HexFormat`, and record constructs. |
| [ValidationFrameworkChallenge.java](file:///Users/siddheshsawant/Desktop/java/ValidationFrameworkChallenge.java) | Reflection Validation | Annotation constraints. | Validate constraints dynamically using Reflection. |
| [Streams.java](file:///Users/siddheshsawant/Desktop/java/Streams.java) | Sealed Classes & Records | Pattern matching. | Use `sealed` structures to limit subclasses, and pattern matching `instanceof` to avoid casting. |
| [EnumPractice.java](file:///Users/siddheshsawant/Desktop/java/EnumPractice.java) | Polymorphic Enums | Dynamic enum behaviors. | Define abstract methods on enums to enforce custom constant behaviors. |

---

## 🧠 Core Engineering Pillars

### 1. Concurrency & Synchronization
* **ReentrantLock vs. Synchronized**: Manual lock coordination via `lock.lock()` outside `try` blocks ensures thread control, allowing condition signaling (`notFull.signal()`) to optimize thread context switching.
* **Double-Checked Locking**: Implements thread-safe singletons using a local variable read, synchronization, a double-check, and the `volatile` keyword to enforce memory fences and prevent instruction reordering.
* **ThreadLocal Lifecycles**: Since threads are pooled and reused in application servers, `ThreadLocal` storage must be cleaned up via `.remove()` inside a `finally` block to prevent classloader memory leaks.

### 2. Streams & Advanced Collections
* **Lazy Evaluation**: Intermediate operations (like `map` and `filter`) do not execute until a terminal operation (like `toList()`, `sum()`, or `collect()`) is called.
* **Collectors.teeing**: Aggregates stream data into two separate directions (e.g. min and max) in a single pass. Guiding type inference via type witnesses (e.g., `Comparator.<Integer>naturalOrder()`) prevents nested compilation errors.
* **SubList Views**: `List.subList()` holds a strong reference to the backing array of the parent list. If the sublist is cached, the parent list cannot be garbage collected, creating a memory leak. Detach it by copying: `new ArrayList<>(subList)`.

### 3. SOLID Design & Decoupling
* **Single Responsibility (SRP)**: Keep database, notifications, and validators out of main business services.
* **Dependency Inversion (DIP)**: Services must depend on abstractions (interfaces) rather than concrete implementations (e.g. `PaymentGateway` instead of `StripeGateway`).
* **Stateless Services**: Service classes should never hold processing state (like a retry counter or dynamic transaction data) in instance variables. Pass data dynamically as method parameters to maintain thread-safety.

---

## 🛠️ How to Compile and Run

Because all files reside in the default package, compiling them all together (e.g., `javac *.java`) will trigger duplicate class errors due to shared names (e.g., `Employee`, `User`).

To compile and run files **individually**:

```bash
# Compile a specific file
javac Streams.java

# Run the compiled class
java Streams
```
