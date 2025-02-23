package thread.dataShare;

public class StackManagement {

    public static void main(String[] args) {
        int x = 1;
        int y = 2;
        // 스택 프레임 추가
        int result = sum(x, y);
    }

    private static int sum(int a, int b) {
        int s = a + b;
        return s;
    }

}
