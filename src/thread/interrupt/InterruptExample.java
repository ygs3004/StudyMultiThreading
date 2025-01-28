package thread.interrupt;

public class InterruptExample {

    public static void main(String[] args) {
        Thread thread = new Thread(new BlockingTest());
        thread.start();
        thread.interrupt();
    }

    private static class BlockingTest implements Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted ");
            }
        }
    }

}
