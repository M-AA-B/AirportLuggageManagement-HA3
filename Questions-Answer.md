# Theory Answers

---

## 1. Comparison of Concurrency Models (Pros & Cons)

| **Model** | **Description** | **Pros (Advantages)** | **Cons (Disadvantages)** |
|------------|-----------------|------------------------|---------------------------|
| **Thread-based** | Multiple threads share the same memory inside one process. | • Easy to implement for moderate workloads.<br>• Direct data sharing.<br>• Good OS-level support in Java. | • Complex synchronization.<br>• Possible deadlocks and race conditions.<br>• Context-switching overhead. |
| **Process-based** | Each process has its own memory space and runs independently. | • High isolation.<br>• Crashes are contained within one process.<br>• Easier debugging. | • Expensive inter-process communication (IPC).<br>• Higher memory/CPU usage.<br>• Slower to start and communicate. |
| **Event-driven / Asynchronous** | Single thread handles many tasks using non-blocking I/O or callbacks. | • Excellent for I/O-bound tasks.<br>• Low resource consumption.<br>• Avoids blocking waits. | • Harder debugging due to callbacks.<br>• Not ideal for heavy CPU tasks.<br>• Complex design for state handling. |
| **Actor Model** | Independent actors exchange immutable messages (no shared state). | • Scalable and fault-tolerant.<br>• No deadlocks.<br>• Great for distributed systems. | • Learning curve.<br>• Message overhead.<br>• Integration with traditional threading is tricky. |

**In this project:**  
We use the **Thread-based model** through Java’s `ExecutorService` and `Semaphore` for safe parallel task execution and queue management.

---

## 2. Concurrency vs Parallelism

| **Aspect** | **Concurrency** | **Parallelism** |
|-------------|----------------|-----------------|
| **Definition** | Managing multiple tasks **at once** by interleaving execution. | Performing multiple tasks **at the same exact time**. |
| **Goal** | Responsiveness and better task coordination. | Faster computation and higher throughput. |
| **Hardware Need** | Works even on a single-core CPU. | Requires multiple cores or processors. |
| **Focus** | Managing task interaction and state. | Maximizing computation speed. |
| **Example** | Handling many AGV arrivals using threads and queues. | Charging several AGVs simultaneously at different stations. |

 **In this project:**  
Our system is *concurrent* (threads coordinate arrivals and queues) and partially *parallel* (stations charge AGVs on multiple CPU cores).

---

## 3. Blocking vs Non-Blocking Concurrency Algorithms

| **Type** | **Definition** | **Mechanism** | **Examples** | **Pros** | **Cons** |
|-----------|----------------|----------------|---------------|-----------|-----------|
| **Blocking** | A thread waits (blocks) until a resource becomes available. | Uses locks, monitors, or semaphores. | `synchronized`, `wait() / notify()`, `Semaphore.acquire()` | • Simple logic.<br>• Easy correctness proof. | • Reduced throughput.<br>• Deadlock risk.<br>• Poor scaling under contention. |
| **Non-Blocking** | Threads never wait; they retry using atomic operations. | Uses `CAS` (Compare-And-Set) and atomic classes. | `AtomicInteger`, `ConcurrentLinkedQueue`, `compareAndSet()` | • Highly scalable.<br>• No deadlocks.<br>• Better performance under high concurrency. | • Harder to design and debug.<br>• Busy-waiting can waste CPU cycles. |

**In this project:**  
- **Blocking concurrency** → `Semaphore` in `ChargingStation` ensures only N AGVs charge simultaneously (others wait).  
- **Non-blocking concurrency** could be introduced with lock-free queues for arrivals to further improve throughput.

---

## Summary
- **Concurrency** = many tasks progressing together.  
- **Parallelism** = tasks running simultaneously.  
- **Blocking algorithms** make threads wait for shared resources.  
- **Non-blocking algorithms** rely on atomic operations to avoid waiting.  
- Our design effectively uses **blocking concurrency** to simulate real-world AGV queues and limited charging resources.

