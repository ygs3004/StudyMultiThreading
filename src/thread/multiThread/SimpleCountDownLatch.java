package thread.multiThread;

public class SimpleCountDownLatch {
    private int count;

    public SimpleCountDownLatch(int count) {
        this.count = count;
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
    }

    public synchronized void await() throws InterruptedException {
        while(count != 0) {
            wait();
        }
    }

    public synchronized void countDown() {
        if(count == 0) return;

        count--;
        if(count == 0) {
            notifyAll();
        }
    }

    public int getCount() {
        return count;
    }
}
