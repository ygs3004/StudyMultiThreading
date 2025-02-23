package thread.dataShare;

public class ShareProblem {

    public static void main(String[] args) throws InterruptedException {
        InventoryCountry inventoryCountry = new InventoryCountry();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCountry);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCountry);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("items: " + inventoryCountry.items); // => 0 이 아님, ++, -- 코드 동작에서 공유된 items 값의 변화로 인해 예상하지 못한 값의 상태가 적용됨
    }

    private static class IncrementingThread extends Thread {

        private final InventoryCountry inventoryCountry;

        public IncrementingThread(InventoryCountry inventoryCountry) {
            this.inventoryCountry = inventoryCountry;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCountry.increment();
            }
        }

    }


    private static class DecrementingThread extends Thread {

        private final InventoryCountry inventoryCountry;

        public DecrementingThread(InventoryCountry inventoryCountry) {
            this.inventoryCountry = inventoryCountry;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCountry.decrement();
            }
        }

    }


    private static class InventoryCountry{
        private int items = 0;

        public void increment() {
            items++;
            // ++ 의 동작
            // 1. 현재값을 얻는다
            // 2. 현재의 값에 1을 더한다..
            // 3. 현재의 값을 items 에 저장한다.
        }

        public void decrement() {
            items--;
            // -- 의 동작
            // 1. 현재값을 얻는다
            // 2. 현재의 값에 1을 뺀다..
            // 3. 현재의 값을 items 에 저장한다.
        }
    }




}
