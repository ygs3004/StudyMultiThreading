package thread.creation.bankExample;

class DescendingHackerThread extends HackerThread{

    public DescendingHackerThread(Vault vault) {
        super(vault);
    }

    @Override
    public void run() {
        for (int guess = Vault.MAX_PASSWORD; guess >= 0; guess--) {
            if (vault.isCorrectPassword(guess)) {
                System.out.println(this.getName() + " 금고 비밀번호를 알아냈다 >>> " + guess);
                System.exit(0);
            }
        }
    }

}
