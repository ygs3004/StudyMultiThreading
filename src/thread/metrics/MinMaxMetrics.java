package thread.metrics;

public class MinMaxMetrics {

    private volatile long minValue;
    private volatile long maxValue;

    public MinMaxMetrics() {
        this.maxValue = Long.MIN_VALUE;
        this.minValue = Long.MAX_VALUE;
    }

    public void addSample(long newSample) {
        synchronized (this) {
            this.minValue = Math.min(newSample, this.minValue);
            this.maxValue = Math.max(newSample, this.maxValue);
        }
    }

    public long getMin() {
        return this.minValue;
    }

    public long getMax() {
        return this.minValue;
    }

}
