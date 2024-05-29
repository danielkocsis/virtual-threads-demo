package com.danielkocsis.virtualthreads.loom;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Recommended articles on the topic:
 * - https://docs.oracle.com/en/java/javase/21/core/concurrency.html
 * - https://docs.oracle.com/en/java/javase/22/core/concurrency.html
 * - https://blogs.oracle.com/javamagazine/post/java-loom-virtual-threads-platform-threads
 * - https://blog.rockthejvm.com/ultimate-guide-to-java-virtual-threads/
 */
@Slf4j
public class VirtualThreadsTest {
    private final Random random = new Random();

    @Test
    public void platformThreadsMemoryUsageTest() {
        assertThrows(OutOfMemoryError.class, () -> {
            for (int i = 0; i < 100_000; i++) {
                Thread.ofPlatform().start(() -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(1L));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }, "A miracle happened :)");
    }

    @Test
    public void virtualThreadsMemoryUsageTest() {
        for (int i = 0; i < 100_000; i++) {
            Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(Duration.ofSeconds(1L));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    public void startBlockingTaskOnPlatformThread() throws InterruptedException {
        Thread thread = Thread.ofPlatform().start(() -> blockingTestTask("Task1"));
        thread.join();

        assertEquals(Thread.State.TERMINATED, thread.getState());
    }

    @Test
    public void startBlockingTaskOnVirtualThread() throws InterruptedException {
        Thread thread = Thread.startVirtualThread(() -> blockingTestTask("Task1"));
        thread.join();

        assertEquals(Thread.State.TERMINATED, thread.getState());
    }

    @Test
    public void startVirtualThreadsWithNameTest() throws InterruptedException {
        Thread thread = Thread.ofVirtual().name("Thread-Name").start(() -> blockingTestTask("Task-Name"));
        thread.join();

        assertEquals(Thread.State.TERMINATED, thread.getState());
    }

    @Test
    @SneakyThrows
    public void virtualThreadFactoryTest() {
        List<Future<Boolean>> futures = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(1, 4).forEach(i -> futures.add(executor.submit(() -> blockingTestTask(STR."Task\{i}"))));

            for (Future<Boolean> future : futures) {
                future.get();
                assertTrue(future.isDone());
            }
        }
    }

    @Test
    @SneakyThrows
    public void threadFactoryTest() {
        List<Future<Boolean>> futures = new ArrayList<>();
        ThreadFactory factory = Thread.ofVirtual().name("task-", 0).factory();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        log.info(STR."Available processors are \{availableProcessors}");

        try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
            IntStream
                    .range(0, availableProcessors * 2)
                    .forEach(i -> futures.add(executor.submit(() -> blockingTestTask(STR."Task\{i}"))));
        }

        for (Future<Boolean> future : futures) {
            assertTrue(future.isDone());
        }
    }

    /**
     * A virtual thread cannot be unmounted during blocking operations when it is pinned to its carrier.
     * A virtual thread is pinned in the following situations:
     * - The virtual thread runs code inside a synchronized block or method
     * - The virtual thread runs a native method or a foreign function
     * @throws InterruptedException
     */
    @Test
    public void threadPinningTest() throws InterruptedException {
        Thread thread1 = Thread.ofVirtual().name("SynchronizedBlockingTask").start(() -> synchronizedBlockingTestTask("SynchronizedBlockingTask"));
        Thread thread2 = Thread.ofVirtual().name("BlockingTask").start(() -> blockingTestTask("BlockingTask"));

        thread1.join();
        thread2.join();

        assertEquals(Thread.State.TERMINATED, thread1.getState());
        assertEquals(Thread.State.TERMINATED, thread2.getState());
    }

    @Test
    public void avoidThreadPinningTest() throws InterruptedException {
        Lock lock = new ReentrantLock();

        Thread thread1 = Thread.ofVirtual().name("LockedBlockingTask").start(() -> lockedBlockingTestTask("LockedTask", lock));
        Thread thread2 = Thread.ofVirtual().name("BlockingTask").start(() -> blockingTestTask("BlockingTask"));

        thread1.join();
        thread2.join();

        assertEquals(Thread.State.TERMINATED, thread1.getState());
        assertEquals(Thread.State.TERMINATED, thread2.getState());
    }


    @Test
    public void threadLocalTest() throws InterruptedException {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();

        var thread1 = Thread.ofVirtual().name("thread-1").start(() -> {
            threadLocal.set("thread-1");
            log.info(STR."ThreadLocal value: \{threadLocal.get()} on \{Thread.currentThread()}");
            sleep(Duration.ofSeconds(1L));
            log.info(STR."ThreadLocal value: \{threadLocal.get()} on \{Thread.currentThread()}");
        });

        var thread2 = Thread.ofVirtual().name("thread-2").start(() -> {
            threadLocal.set("thread-2");
            log.info(STR."ThreadLocal value: \{threadLocal.get()} on \{Thread.currentThread()}");
            sleep(Duration.ofSeconds(1L));
            log.info(STR."ThreadLocal value: \{threadLocal.get()} on \{Thread.currentThread()}");
        });

        thread1.join();
        thread2.join();

        assertEquals(Thread.State.TERMINATED, thread1.getState());
        assertEquals(Thread.State.TERMINATED, thread2.getState());
    }

    /**
     * Sources:
     * - https://openjdk.org/jeps/462
     * - https://docs.oracle.com/en/java/javase/22/core/structured-concurrency.html#GUID-CAC99F0A-8C9F-47D3-80BE-FFEBE7F2E300
     */
    @Test
    public void structuredConcurrencyWhenAllTasksFinishTest() throws InterruptedException {
        List<StructuredTaskScope.Subtask<Boolean>> subtasks = new ArrayList<>();

        try (var scope = new StructuredTaskScope()) {
            IntStream.range(0, 4).forEach(i -> subtasks.add(scope.fork(() -> blockingTestTask(STR."Task\{i}"))));

            scope.join();
        }

        subtasks.stream().forEach(t -> log.info(STR."Task State: \{t.state()}"));
    }

    /**
     * Source: https://docs.oracle.com/en/java/javase/22/core/structured-concurrency.html#GUID-97F95BF4-A1E2-40A3-8439-6BB4E3D5C422
     * Issues with thread locals:
     * - Unconstrained mutability
     * - Unbounded Lifetime
     * - Expensive Inheritance
     */
    @Test
    public void structuredConcurrencyWhenQuickestFinishesTest() throws InterruptedException {
        List<StructuredTaskScope.Subtask<Boolean>> subtasks = new ArrayList<>();

        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<>()) {
            IntStream.range(0, 4).forEach(i -> subtasks.add(scope.fork(() -> blockingTestTask(STR."Task\{i}"))));

            scope.join();
        }

        subtasks.stream().forEach(t -> log.info(STR."Task State: \{t.state()}"));
    }

    /**
     * Source:
     * - https://docs.oracle.com/en/java/javase/22/core/scoped-values.html
     * - https://openjdk.org/jeps/464
     */
    @Test
    public void scopedValueTest() {
        ScopedValue<String> SCOPED_VALUE = ScopedValue.newInstance();

        ScopedValue.where(SCOPED_VALUE, "task-1").run(() -> {
            try (var scope = new StructuredTaskScope<>()) {
                scope.fork(() -> scopedBlockingTestTask(SCOPED_VALUE));
                scope.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        ScopedValue.where(SCOPED_VALUE, "task-2").run(() -> {
            try (var scope = new StructuredTaskScope<>()) {
                scope.fork(() -> scopedBlockingTestTask(SCOPED_VALUE));
                scope.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        ScopedValue.where(SCOPED_VALUE, "task-3").run(() -> scopedBlockingTestTask(SCOPED_VALUE));
    }

    // Test helper methods
    private boolean scopedBlockingTestTask(ScopedValue<String> scopedValue) {
        log.info(STR."Started blocking task \{scopedValue.get()} on \{Thread.currentThread()}");
        sleep(Duration.ofMillis(random.nextInt(1000)));
        log.info(STR."Finished blocking task \{scopedValue.get()} on \{Thread.currentThread()}");

        return true;
    }

    synchronized void synchronizedBlockingTestTask(String taskName) {
        blockingTestTask(taskName);
    }

    @SneakyThrows
    void lockedBlockingTestTask(String taskName, Lock lock) {
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                blockingTestTask(taskName);
            } finally {
                lock.unlock();
            }
        }
    }

    private boolean blockingTestTask(String taskName) {
        log.info(STR."Started blocking task \{taskName} on \{Thread.currentThread()}");
        sleep(Duration.ofMillis(random.nextInt(1000)));
        log.info(STR."Finished blocking task \{taskName} on \{Thread.currentThread()}");
        return true;
    }

    @SneakyThrows
    private void sleep(Duration interval) {
        Thread.sleep(interval);
    }
}
