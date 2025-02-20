package thread.performance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessing {

    static final String currentRelativePath = Paths.get("").toAbsolutePath().toString();
    static final String SOURCE_FILE = currentRelativePath + "\\src\\thread\\performance\\resources\\many-flowers.jpg";
    static final String DESTINATION_FILE = currentRelativePath + "\\src\\thread\\performance\\resources\\result-many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();

        // recolorSingleThreaded(originalImage, resultImage);
        int numberOfThreads = 4;
        recolorMultiThreaded(originalImage, resultImage, numberOfThreads);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("쓰레드 소요시간: " + duration);

        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);
    }

    private static void recolorMultiThreaded(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / numberOfThreads; // 이미지를 가로로 분할하여 쓰레드별 처리

        for(int i = 0; i < numberOfThreads; i++) {
            final int threadMultiplier = i;
            Thread thread = new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height * threadMultiplier;
                recolorImage(originalImage, resultImage, leftCorner, topCorner, width, height);
            });

            threads.add(thread);
        }

        for(Thread thread : threads) {
            thread.start();
        }

        for(Thread thread : threads) {
            try{
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    private static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    private static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for(int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }
    }

    private static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y){
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        boolean isShady = isShadeOfGray(red, green, blue);

        int newRed = isShady ? Math.min(255, red + 10) : red;
        int newGreen = isShady ? Math.max(0, green - 80) : green;
        int newBlue = isShady ? Math.max(0, blue - 20) : blue;
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    private static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    private static boolean isShadeOfGray(int red, int green, int blue) {
        int useColorDiff = 30;
        return Math.abs(red - green) < useColorDiff && Math.abs(red - blue) < useColorDiff && Math.abs(blue - green) < useColorDiff;
    }

    private static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000; // ARGB Alpha 값
        return rgb;
    }

    private static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    private static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    private static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }

}
