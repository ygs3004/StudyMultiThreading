package thread.dataShare;

public class SynchronizedCriticalSection {

    public static void main(String[] args) throws InterruptedException {
        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("items: " + inventoryCounter.getItems()); // => 0 이 아님, ++, -- 코드 동작에서 공유된 items 값의 변화로 인해 예상하지 못한 값의 상태가 적용됨
    }

    private static class IncrementingThread extends Thread {

        private final InventoryCounter inventoryCounter;

        public IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                // inventoryCounter.increment();
                inventoryCounter.lockIncrement();
            }
        }

    }


    private static class DecrementingThread extends Thread {

        private final InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                // inventoryCounter.decrement();
                inventoryCounter.lockDecrement();
            }
        }

    }


    private static class InventoryCounter{
        private int items = 0;

        Object lock = new Object();

        public void lockIncrement() {
            synchronized (this.lock) {
                items++;
            }
        }

        public void lockDecrement() {
            synchronized (this.lock) {
                items--;
            }
        }

        public synchronized void increment() {
            items++;
            // ++ 의 동작
            // 1. 현재값을 얻는다
            // 2. 현재의 값에 1을 더한다..
            // 3. 현재의 값을 items 에 저장한다.
        }

        public synchronized void decrement() {
            items--;
            // -- 의 동작
            // 1. 현재값을 얻는다
            // 2. 현재의 값에 1을 뺀다..
            // 3. 현재의 값을 items 에 저장한다.
        }

        public int getItems() {
            return this.items;
        }
    }

}
