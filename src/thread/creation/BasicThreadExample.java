package thread.creation;

public class BasicThreadExample {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("New Worker Thread 내부 : " + Thread.currentThread().getName());
                System.out.println("현재 Thread Priority:" + Thread.currentThread().getPriority());
            }
        });

        thread.setName("New Worker Thread");
        thread.setPriority(Thread.MAX_PRIORITY);

        System.out.println("쓰레드 시작 전 쓰레드: " + Thread.currentThread().getName());
        thread.start();
        System.out.println("쓰레드 시작 후 쓰레드: " + Thread.currentThread().getName());
    }

}
