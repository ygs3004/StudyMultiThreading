package thread.coordination.interrupt;

import java.math.BigInteger;

public class TerminationWithMainThreadExample {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new LongComputationTask(new BigInteger("20000000"), new BigInteger("100000000000")));

        // 메인 쓰레드가 종료될때 함께 종료
        // thread.setDaemon(true);

        thread.start();
        Thread.sleep(100);
        thread.interrupt();
    }

    private static class LongComputationTask implements Runnable{

        private BigInteger base;
        private BigInteger power;

        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + " * " + power + " = " + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;
            for(BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                // 쓰레드 내부에서 체크
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Thread is interrupted");
                    return BigInteger.ZERO;
                }

                result = result.multiply(base);
            }
            return result;
        }

    }

}
