package thread.metrics;

import java.util.Random;

public class MetricsAverage {

    public static void main(String[] args) {

        Metrics metrics = new Metrics();

        BusinessLogic businessLogicThread1 = new BusinessLogic(metrics);
        BusinessLogic businessLogicThread2 = new BusinessLogic(metrics);
        MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);

        businessLogicThread1.start();
        businessLogicThread2.start();
        metricsPrinter.start();
    }

    private static class MetricsPrinter extends Thread{

        private Metrics metrics;

        public MetricsPrinter(Metrics metrics){
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }

                double currentAverage = metrics.getAverage();
                System.out.println("현재 Average: " + currentAverage);
            }
        }

    }

    private static class BusinessLogic extends Thread {
        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                // long start = System.currentTimeMillis();
                // try {
                //     Thread.sleep(random.nextInt(10));
                // } catch (InterruptedException e) {
                // }
                // long end = System.currentTimeMillis();
                // metrics.addSample(end-start);
                metrics.addSample(10);
            }
        }
    }

    private static class Metrics{
        private long count = 0;
        // volatile -> 원시형의 원사정을 보장
        private volatile double average = 0.0;

        public synchronized void addSample(long sample) {
            double currentSum = average * count;
            count++;
            average = (currentSum + sample) / count;
        }

        public double getAverage() {
            return average;
        }
    }

}
