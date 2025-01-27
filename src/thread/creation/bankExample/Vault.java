package thread.creation.bankExample;

class Vault{

    private int password;
    static final int MAX_PASSWORD = 9999;

    public Vault(int password) {
        this.password = password;
    }

    public boolean isCorrectPassword(int guess) {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {

        }
        return this.password == guess;
    }
}