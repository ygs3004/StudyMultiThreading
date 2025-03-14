package thread.raceCondition;

public class RaceCondition {

    public static void main(String[] args) {
        SharedClass sharedClass = new SharedClass();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sharedClass.increment();
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sharedClass.checkForDataRace();
            }
        });

        thread1.start();
        thread2.start();
    }

    private static class SharedClass {
        private volatile int x = 0;
        private volatile int y = 0;

        public void increment() {
            // 컴파일러가 논리적으로 문제가 없다면 비순차적으로 명령을 처리하는 경우가 발생할 수 있음
            // 싱글 스레드일 경우 문제 없으나 멀티 쓰레드일 경우 문제 발생 가능
            x++;
            y++;
        }
        
        public void checkForDataRace() {
            if (y > x) {
                System.out.println("y > x 데이터 경쟁상태 감지");
            }
        }
    }

}
