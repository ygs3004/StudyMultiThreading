package thread.multiThread;

import java.io.*;
import java.util.*;

public class ThreadSafeQueueExample {

    private final static String INPUT_FILE = MatricesGenerator.OUTPUT_FILE;
    private final static String OUTPUT_FILE = "./out/matrices_result.txt";
    private final static int N = MatricesGenerator.N;

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        MatricesReaderProducer matricesReader = new MatricesReaderProducer(new FileReader(inputFile), threadSafeQueue);
        MatricesMultiplierConsumer matricesConsumer = new MatricesMultiplierConsumer(new FileWriter(outputFile), threadSafeQueue);

        matricesConsumer.start();
        matricesReader.start();
    }

    private static void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
        for(int r = 0; r < N; r++) {
            StringJoiner stringJoiner = new StringJoiner(", ");
            for(int c = 0; c < N; c++) {
                stringJoiner.add(String.format("%.2f", matrix[r][c]));
            }

            fileWriter.write(stringJoiner.toString());
            fileWriter.write('\n');
        }
        fileWriter.write('\n');
    }

    private static class MatricesMultiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter fileWriter;

        public MatricesMultiplierConsumer(FileWriter fileWriter, ThreadSafeQueue queue) {
            this.fileWriter = fileWriter;
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                MatricesPair matricesPair = queue.remove();
                if(matricesPair == null) {
                    System.out.println("queue 에 읽을 수 있는 행렬이 없습니다. consumer 를 종료합니다.");
                    break;
                }

                float[][] result = multiplyMatrices(matricesPair.matrix1, matricesPair.matrix2);
                try {
                    saveMatrixToFile(fileWriter, result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private float[][] multiplyMatrices(float[][] m1, float[][] m2) {
            float[][] result = new float[N][N];
            for(int r = 0; r < N; r++) {
                for(int c = 0; c < N; c++) {
                    for(int k = 0; k < N; k++) {
                        result[r][c] += m1[r][k] * m2[k][c];
                    }
                }
            }
            return result;
        }

    }

    private static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatricesReaderProducer(FileReader reader, ThreadSafeQueue queue) {
            this.scanner = new Scanner(reader);
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                float[][] matrix1 = readMatrix();
                float[][] matrix2 = readMatrix();

                if (matrix1 == null || matrix2 == null) {
                    queue.terminate();
                    System.out.println("terminated!");
                    return;
                }

                MatricesPair matricesPair = new MatricesPair();
                matricesPair.matrix1 = matrix1;
                matricesPair.matrix2 = matrix2;
                queue.add(matricesPair);
            }

        }

        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for(int r = 0; r < N; r++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                String[] line = scanner.nextLine().split(", ");
                for(int c = 0; c < N; c++) {
                    matrix[r][c] = Float.valueOf(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }
    }

    private static class ThreadSafeQueue{
        private Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminated = false;
        private static final int CAPACITY = 5;

        private synchronized void add(MatricesPair matricesPair) {
            while (queue.size() == CAPACITY) {
                try{
                    wait();
                }catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            queue.add(matricesPair);
            isEmpty = false;
            notify();
        }

        private synchronized MatricesPair remove() {
            MatricesPair matricesPair = null;
            while (isEmpty && !isTerminated) {
                try{
                    wait();
                }catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if(queue.isEmpty() && isTerminated) {
                return null;
            }

            if (queue.size() == 1) {
                isEmpty = true;
            }

            System.out.println("queue size: " + queue.size());

            matricesPair = queue.remove();
            if(queue.size() == CAPACITY - 1) {
                notifyAll();
            }
            return matricesPair;
        }

        private synchronized void terminate() {
            isTerminated = true;
            notifyAll();
        }
    }

    private static class MatricesPair{
        private float[][] matrix1;
        private float[][] matrix2;
    }
}
