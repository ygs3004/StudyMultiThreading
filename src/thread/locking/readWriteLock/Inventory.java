package thread.locking.readWriteLock;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Inventory {

    private static final int HIGHEST_PRICE = 1000;

    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();
        Random random = new Random();

        for (int i = 0; i < 100000; i++) {
            inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        }

        Thread writer = new Thread(() -> {
            while (true) {
                inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
                inventoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        writer.setDaemon(true);
        writer.start();

        int numberOfThreads = 7;
        List<Thread> readers = new ArrayList<>();

        for(int readerIdx = 0; readerIdx < numberOfThreads; readerIdx++) {
            Thread reader = new Thread(() -> {
               for(int i = 0; i < 100000; i++) {
                   int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
                   int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                   inventoryDatabase.getNumberOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
               }
            });

            reader.setDaemon(true);
            readers.add(reader);
        }

        long startReadingTime = System.currentTimeMillis();

        readers.forEach(Thread::start);
        for (Thread reader : readers) {
            reader.join();
        }

        long endReadingTime = System.currentTimeMillis();

        System.out.println(String.format("Reading time: %d ms", endReadingTime - startReadingTime));

    }

    public static class InventoryDatabase{
        private final TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private final ReentrantLock lock = new ReentrantLock();
        private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        private final Lock readLock = reentrantReadWriteLock.readLock();
        private final Lock writeLock = reentrantReadWriteLock.writeLock();

        private int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
            // lock.lock();
            readLock.lock();
            try{
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                Integer toKey = priceToCountMap.floorKey(upperBound);

                if (fromKey == null || toKey == null) {
                    return 0;
                }

                NavigableMap<Integer, Integer> rangeOfPrice = priceToCountMap.subMap(fromKey, true, toKey, true);
                int sum = 0;
                for (int numberOfItemsForPrice : rangeOfPrice.values()) {
                    sum += numberOfItemsForPrice;
                }

                return sum;

            }finally {
                // lock.unlock();
                readLock.unlock();
            }

        }

        private void addItem(int price) {
            // lock.lock();
            writeLock.lock();
            try{
                priceToCountMap.compute(price, (key, value) -> value == null ? 1 : value + 1);
            }finally {
                // lock.unlock();
                writeLock.unlock();
            }
        }

        private void removeItem(int price) {
            // lock.lock();
            writeLock.lock();
            try{
                priceToCountMap.compute(price, (key, value) -> value == null || value == 1 ? null : value - 1);
            }finally {
                // lock.unlock();
                writeLock.unlock();
            }
        }
    }

}
