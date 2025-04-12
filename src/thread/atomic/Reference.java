package thread.atomic;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class Reference {

    public static void main(String[] args) throws InterruptedException {

        // StandardStack<Integer> stack = new StandardStack<>();
        LockFreeStack<Integer> stack = new LockFreeStack<>();

        Random random = new SecureRandom();
        for (int i = 0; i < 100000; i++) {
            stack.push(random.nextInt());
        }

        List<Thread> threads = new ArrayList<>();

        int pushingThreads = 2;
        int poppingThreads = 2;

        for(int i = 0; i < pushingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });

            thread.setDaemon(true);
            threads.add(thread);
        }

        for(int i = 0; i < poppingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });

            thread.setDaemon(true);
            threads.add(thread);
        }

        for(Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(3000);

        System.out.printf("3초간 수행 횟수: %s \n", stack.getCounter());
    }

    private static class LockFreeStack<T>{
        private AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private AtomicLong count = new AtomicLong(0);

        public void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            while (true) {
                StackNode<T> currentHead = head.get();
                newHead.next = currentHead;
                if (head.compareAndSet(currentHead, newHead)) {
                    break;
                }else{
                    LockSupport.parkNanos(1);
                }
            }
            count.incrementAndGet();
        }

        public T pop() {
            StackNode<T> currentHead = head.get();
            StackNode<T> newHead;

            while(currentHead != null) {
                newHead = currentHead.next;
                if (head.compareAndSet(currentHead, newHead)) {
                    break;
                }else{
                    LockSupport.parkNanos(1);
                    currentHead = head.get();
                }
            }

            count.incrementAndGet();
            return currentHead != null ? currentHead.value : null;
        }

        public long getCounter() {
            return count.get();
        }
    }

    private static class StandardStack<T>{
        private StackNode<T> head;
        private long counter = 0;

        public synchronized void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            newHead.next = head;
            head = newHead;
            counter++;
        }

        public synchronized T pop() {
            if(head == null){
                counter++;
                return null;
            }

            T value = head.value;
            head = head.next;
            counter++;
            return value;
        }

        public long getCounter() {
            return counter;
        }
    }

    private static class StackNode<T>{
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
            // this.next = next;
        }

    }


}