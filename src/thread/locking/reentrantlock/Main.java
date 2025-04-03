package thread.locking.reentrantlock;

import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("암호화폐 금액");

        GridPane grid = createGrid();
        Map<String, Label> cryptoLabels = createCryptoPriceLabels();

        addLabelsToGrid(cryptoLabels, grid);

        double width = 300;
        double height = 250;

        StackPane root = new StackPane();
        Rectangle background = createBackGroundRectangleWithAnimation(width, height);

        root.getChildren().add(background);
        root.getChildren().add(grid);

        primaryStage.setScene(new Scene(root, width, height));

        PricesContainer pricesContainer = new PricesContainer();
        PriceUpdater priceUpdater = new PriceUpdater(pricesContainer);

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pricesContainer.getLockObject().tryLock()) {
                    try{
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                        }

                        Label bitCoinLabel = cryptoLabels.get("BTC");
                        bitCoinLabel.setText((String.valueOf(pricesContainer.getBitcoinPrice())));

                        Label etherLabel = cryptoLabels.get("ETC");
                        etherLabel.setText((String.valueOf(pricesContainer.getEtherPrice())));

                        Label liteCoinLabel = cryptoLabels.get("LTC");
                        liteCoinLabel.setText((String.valueOf(pricesContainer.getLitecoinPrice())));

                        Label bitCoinCashLabel = cryptoLabels.get("BCH");
                        bitCoinCashLabel.setText((String.valueOf(pricesContainer.getBitcoinCashPrice())));

                        Label rippleLabel = cryptoLabels.get("XRP");
                        rippleLabel.setText((String.valueOf(pricesContainer.getRipplePrice())));
                    }finally {
                        pricesContainer.getLockObject().unlock();
                    }
                }
            }
        };

        animationTimer.start();
        priceUpdater.start();
        primaryStage.show();
    }

    private Map<String, Label> createCryptoPriceLabels() {
        Label bitCoinPrice = new Label("0");
        bitCoinPrice.setId("BTC");

        Label etherPrice = new Label("0");
        etherPrice.setId("ETC");

        Label liteCoinPrice = new Label("0");
        liteCoinPrice.setId("LTC");

        Label bitcoinCashPrice = new Label("0");
        bitcoinCashPrice.setId("BCH");

        Label ripplePrice = new Label("0");
        ripplePrice.setId("XRP");

        Map<String, Label> cryptoLabels = new HashMap<>();
        cryptoLabels.put("BTC", bitCoinPrice);
        cryptoLabels.put("ETC", etherPrice);
        cryptoLabels.put("LTC", liteCoinPrice);
        cryptoLabels.put("BCH", bitcoinCashPrice);
        cryptoLabels.put("XRP", ripplePrice);

        return cryptoLabels;
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);;
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private void addLabelsToGrid(Map<String, Label> labels, GridPane grid){
        int row = 0;
        for (Map.Entry<String, Label> entry : labels.entrySet()) {
            String cryptoName = entry.getKey();
            Label nameLabel = new Label(cryptoName);
            nameLabel.setTextFill(Color.BLUE);
            nameLabel.setOnMousePressed(event -> nameLabel.setTextFill(Color.RED));
            nameLabel.setOnMouseReleased(event -> nameLabel.setTextFill(Color.BLUE));

            grid.add(nameLabel, 0, row);
            grid.add(entry.getValue(), 1, row);

            row++;
        }
    }

    private Rectangle createBackGroundRectangleWithAnimation(double width, double height) {
        Rectangle background = new Rectangle(width, height);
        FillTransition fillTransition = new FillTransition(Duration.millis(1000), background, Color.LIGHTGREEN, Color.LIGHTBLUE);
        fillTransition.setCycleCount(Timeline.INDEFINITE);
        fillTransition.setAutoReverse(true);
        fillTransition.play();
        return background;
    }

    private static class PricesContainer {

        private Lock lockObject = new ReentrantLock();

        private double bitcoinPrice;
        private double etherPrice;
        private double litecoinPrice;
        private double bitcoinCashPrice;
        private double ripplePrice;

        public Lock getLockObject() {
            return lockObject;
        }

        public void setLockObject(Lock lockObject) {
            this.lockObject = lockObject;
        }

        public double getBitcoinPrice() {
            return bitcoinPrice;
        }

        public void setBitcoinPrice(double bitcoinPrice) {
            this.bitcoinPrice = bitcoinPrice;
        }

        public double getEtherPrice() {
            return etherPrice;
        }

        public void setEtherPrice(double etherPrice) {
            this.etherPrice = etherPrice;
        }

        public double getLitecoinPrice() {
            return litecoinPrice;
        }

        public void setLitecoinPrice(double litecoinPrice) {
            this.litecoinPrice = litecoinPrice;
        }

        public double getBitcoinCashPrice() {
            return bitcoinCashPrice;
        }

        public void setBitcoinCashPrice(double bitcoinCashPrice) {
            this.bitcoinCashPrice = bitcoinCashPrice;
        }

        public double getRipplePrice() {
            return ripplePrice;
        }

        public void setRipplePrice(double ripplePrice) {
            this.ripplePrice = ripplePrice;
        }
    }

    private static class PriceUpdater extends Thread {

        private PricesContainer pricesContainer;
        private Random random = new Random();

        private PriceUpdater(PricesContainer pricesContainer) {
            this.pricesContainer = pricesContainer;
        }

        @Override
        public void run() {
            while (true) {
                pricesContainer.getLockObject().lock();

                try{

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }

                    pricesContainer.setBitcoinPrice(random.nextInt(20000));
                    pricesContainer.setEtherPrice(random.nextInt(2000));
                    pricesContainer.setLitecoinPrice(random.nextInt(500));
                    pricesContainer.setBitcoinCashPrice(random.nextInt(5000));
                    pricesContainer.setRipplePrice(random.nextDouble());
                }finally {
                    pricesContainer.getLockObject().unlock();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

}
