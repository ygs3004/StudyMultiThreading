package thread.creation;

public class ExtendsThreadExample {

    public static void main(String[] args) {
        Thread thread = new NewThread();
        thread.start();
    }

    private static class NewThread extends Thread {
        @Override
        public void run() {
            System.out.println("New Class Thread: " + this.getName());
        }
    }

}
