package thread.locking;

import java.util.Random;

public class Deadlock {

    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        Thread trainAThread = new Thread(new TrainA(intersection));
        Thread trainBThread = new Thread(new TrainB(intersection));

        trainAThread.start();
        trainBThread.start();
    }

    private static class TrainA implements Runnable {
        private final Intersection intersection;
        private final Random random = new Random();

        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                    intersection.takeRoadA();
                } catch (InterruptedException e) {
                }
            }
        }
    }


    private static class TrainB implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                    intersection.takeRoadB();
                } catch (InterruptedException e) {
                }
            }
        }
    }


    private static class Intersection{
        private Object roadA = new Object();
        private Object roadB = new Object();

        private void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A는 다음 쓰레드에 의해 잠겨있습니다: " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("기차가 road A를 통과중입니다.");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        /**
        * 락 순서를 동일하게 유지해야 순환종속성을 막고 데드락을 예방할 수 있다.
        * */
        private void takeRoadB() {
            synchronized (roadA) {
            // synchronized (roadB) {
                System.out.println("Road B는 다음 쓰레드에 의해 잠겨있습니다: " + Thread.currentThread().getName());

                synchronized (roadB) {
                // synchronized (roadA) {
                    System.out.println("기차가 road B를 통과중입니다.");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

}
