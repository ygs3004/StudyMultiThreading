package thread.creation.bankExample;

class AscendingHackerThread extends HackerThread{

    public AscendingHackerThread(Vault vault) {
        super(vault);
    }

    @Override
    public void run() {
        for (int guess = 0; guess <= Vault.MAX_PASSWORD; guess++) {
            if (vault.isCorrectPassword(guess)) {
                System.out.println(this.getName() + " 금고 비밀번호를 알아냈다 >>> " + guess);
                System.exit(0);
            }
        }
    }

}
