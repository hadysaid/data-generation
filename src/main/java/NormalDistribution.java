import java.util.Random;

public class NormalDistribution {

    private Random random;

    private int mean;

    private double standardDeviation;

    public NormalDistribution(int mean, int variance) {
        this.mean = mean;
        this.standardDeviation = Math.sqrt(variance);
        this.random = new Random();
    }

    public int generate() {
        return (int) (mean + random.nextGaussian() * standardDeviation);
    }
}

