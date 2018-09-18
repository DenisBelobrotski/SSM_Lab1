import java.math.BigDecimal;
import java.math.BigInteger;

public class Main {

    private static final int N = 1000;

    public static void main(String... args){

        BigInteger M = (new BigInteger("2")).shiftLeft(30);
        int K = 64;
        BigInteger a, b, c, d;
        a = b = new BigInteger("79507");
        c = d = new BigInteger("262147");

        BigDecimal[] randomNumbers1 = multiplicativeCongruentialMethod(a, b, M);
        BigDecimal[] randomNumbers2 = multiplicativeCongruentialMethod(c, d, M);

        BigDecimal[] randomNumbers3 = macLarenMarsagliaMethod(K, randomNumbers1, randomNumbers2);


        System.out.println("Kolmogorov's test 1: " + testKolmogorov(randomNumbers1));
        System.out.println("Pearson's test 1: " + testPearson(randomNumbers1));

        System.out.println("Kolmogorov's test 2: " + testKolmogorov(randomNumbers3));
        System.out.println("Pearson's test 2: " + testPearson(randomNumbers3));
    }

    private static BigDecimal[] multiplicativeCongruentialMethod(BigInteger firstBufferElement, BigInteger beta, BigInteger M) {
        BigInteger[] integerBuffer = new BigInteger[N];
        BigDecimal[] resultArray = new BigDecimal[N];
        BigDecimal decimalM = new BigDecimal(M);

        integerBuffer[0] = firstBufferElement;
        resultArray[0] = (new BigDecimal(integerBuffer[0])).divide(decimalM);

        BigInteger tmp;

        for (int i = 1; i < N; i++) {
            tmp = beta.multiply(integerBuffer[i - 1]);
            integerBuffer[i] = tmp.subtract(tmp.divide(M).multiply(M));
            resultArray[i] = (new BigDecimal(integerBuffer[i])).divide(decimalM);
        }

        return resultArray;
    }

    private static BigDecimal[] macLarenMarsagliaMethod(int K, BigDecimal[] firstInitialSequence, BigDecimal[] secondInitialSequence) {
        BigDecimal decimalK = new BigDecimal(K);
        BigDecimal[] auxilaryTable = new BigDecimal[K];
        BigDecimal[] result = new BigDecimal[N];

        for (int i = 0; i < K; i++) {
            auxilaryTable[i] = firstInitialSequence[i];
        }

        for (int t = 0; t < N - K; t++) {
            int s = secondInitialSequence[t].multiply(decimalK).intValue();
            result[t] = auxilaryTable[s];
            auxilaryTable[s] = firstInitialSequence[t + K];
        }

        for (int t = N - K; t < N; t++) {
            result[t] = firstInitialSequence[t];
        }

        return result;
    }

    private static boolean testKolmogorov(BigDecimal[] randomSequence) {
        double max = 0;
        final double DELTA = 1.36;

        for (int i = 0; i < N; i++) {
            double theoreticalFunctionResult = randomSequence[i].doubleValue();
            double empiricalFunctionResult = calcEmpiricalFunction(randomSequence, theoreticalFunctionResult);
            max = Math.max(max, Math.abs(empiricalFunctionResult - theoreticalFunctionResult));
        }

        return Math.sqrt(N) * max < DELTA;

    }

    private static double calcEmpiricalFunction(BigDecimal[] randomSequence, double theoreticalFunctionResult) {
        int result = 0;

        for (int i = 0; i < N; i++) {
            if (randomSequence[i].doubleValue() <= theoreticalFunctionResult) {
                result++;
            }
        }

        return (double)result / N;
    }

    private static boolean testPearson(BigDecimal[] randomSequence) {
        final double DELTA = 19.675;
        final int K = 11;
        final BigDecimal decimalK = new BigDecimal(K);

        int[] frequencies = new int[K];
        double squaredChi = 0;

        for (int i = 0; i < N; i++) {
            frequencies[randomSequence[i].multiply(decimalK).intValue()]++;
        }

        double tmpNumerator;
        for (int i = 0; i < K; i++) {
            tmpNumerator = frequencies[i] - (double)N / K;
            squaredChi += tmpNumerator * tmpNumerator / ((double)N / K);
        }

        return squaredChi < DELTA;
    }

    private static void printRandomSequence(BigDecimal[] randomSequence) {
        for (BigDecimal current: randomSequence) {
            System.out.println(current);
        }
    }

    private static void printRandomSequencesSubtract(BigDecimal[] firstSequence, BigDecimal[] secondSequence) {
        for (int i = 0; i < N; i++) {
            System.out.println(firstSequence[i].subtract(secondSequence[i]));
        }
    }
}

