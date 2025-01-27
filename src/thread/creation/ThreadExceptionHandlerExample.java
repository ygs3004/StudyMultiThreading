package thread.creation;

public class ThreadExceptionHandlerExample {

    public static void main(String[] args) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Some Runtime Exception");
            }
        });

        thread.setName("Example Thread");

        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Exception in thread: " + t.getName());
                System.out.println("error: " + e.getMessage());
            }
        });
        thread.start();
    }

}
