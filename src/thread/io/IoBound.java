package thread.io;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IoBound {

    private static final int NUMBER_OF_TASKS = 10_000;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Press enter to start");
        s.nextLine();
        System.out.printf("Running %d tasks\n", NUMBER_OF_TASKS);

        long startTime = System.currentTimeMillis();
        performTasks();
        long endTime = System.currentTimeMillis();
        System.out.printf("Tasks took %dms to complete\n", endTime - startTime);
    }

    private static void performTasks() {

        try (ExecutorService executorService = Executors.newFixedThreadPool(1000)) {
            for (int i = 0; i < NUMBER_OF_TASKS; i++) {
                executorService.submit(() -> {
                    for(int j = 0; j < 100; j++) blockingIoOperation(); // context switching cost 발생
                });
            }
        }

    }

    private static void blockingIoOperation() {
        System.out.println("Executing a blocking task from thread: " + Thread.currentThread());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}