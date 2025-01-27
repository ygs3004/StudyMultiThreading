package thread.creation.bankExample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Main {

    public static void main(String[] args) {
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(Vault.MAX_PASSWORD));
        List<Thread> threads = new ArrayList<>();

        threads.add(new AscendingHackerThread(vault));
        threads.add(new DescendingHackerThread(vault));
        threads.add(new PoliceThread());

        for(Thread thread : threads) {
            thread.start();
        }
    }
}
