import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BulkTester {

    private static Random rand = new Random();
    private static DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);

    public static void main(String[] args) throws IOException {
        // Read in the algorithm and rerun rate to use
        String algorithm = args[0];
        int reruns = Integer.parseInt(args[1]);

        // Setup the outputfile
        String outputPath = String.format("bulktest_%s_%d.csv",
                algorithm, System.currentTimeMillis());
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputPath)));

        // Write the header to the file
        String[] header = new String[] {
            "input",
            "reruns",
            "test1 avg(ms)",
            "test1 std.dev(ms)",
            "test2 avg(ms)",
            "test2 std.dev(ms)",
            "test3 avg(ms)",
            "test3 std.dev(ms)",
        };
        //bw.write(String.join(",", header) + "\r\n");

        // Read in the input sizes
        int[] inputSizes = new int[args.length-2];
        for (int i = 2; i < args.length; i++) {
            inputSizes[i-2] = Integer.parseInt(args[i]);
        }

        // For each input size we run the tests
        for (int i = 0; i < inputSizes.length; i++) {
            System.out.println("Running tests for input size: " + df.format(inputSizes[i]));

            long[] test1Results = new long[reruns];
            long[] test2Results = new long[reruns];
            long[] test3Results = new long[reruns];

            // Rerun tests for each test-set
            for (int j = 0; j < reruns; j++) {
                System.out.println("Running rerun " + (j+1) + "/" + reruns);

                // Generate the input
                List<Point2D> test1Input = generateTest1Input(inputSizes[i]);
                List<Point2D> test2Input = generateTest2Input(inputSizes[i]);
                List<Point2D> test3Input = generateTest3Input(inputSizes[i]);

                // Run the tests for the algorithm
                if (algorithm.equals("a")) {
                    INC_CH inc = new INC_CH();

                    long test1StartTime = System.currentTimeMillis();
                    inc.findHull(test1Input);
                    long test1Time = System.currentTimeMillis() - test1StartTime;
                    test1Results[j] = test1Time;

                    long test2StartTime = System.currentTimeMillis();
                    inc.findHull(test2Input);
                    long test2Time = System.currentTimeMillis() - test2StartTime;
                    test2Results[j] = test2Time;

                    long test3StartTime = System.currentTimeMillis();
                    inc.findHull(test3Input);
                    long test3Time = System.currentTimeMillis() - test3StartTime;
                    test3Results[j] = test3Time;
                } else if (algorithm.equals("b")) {
                    QH_CH qh = new QH_CH();

                    long test1StartTime = System.currentTimeMillis();
                    qh.findHull(test1Input);
                    long test1Time = System.currentTimeMillis() - test1StartTime;
                    test1Results[j] = test1Time;

                    long test2StartTime = System.currentTimeMillis();
                    qh.findHull(test2Input);
                    long test2Time = System.currentTimeMillis() - test2StartTime;
                    test2Results[j] = test2Time;

                    long test3StartTime = System.currentTimeMillis();
                    qh.findHull(test3Input);
                    long test3Time = System.currentTimeMillis() - test3StartTime;
                    test3Results[j] = test3Time;
                } else if (algorithm.equals("c")) {
                    MbC_CH mbc = new MbC_CH();

                    long test1StartTime = System.currentTimeMillis();
                    mbc.findHull(test1Input);
                    long test1Time = System.currentTimeMillis() - test1StartTime;
                    test1Results[j] = test1Time;

                    long test2StartTime = System.currentTimeMillis();
                    mbc.findHull(test2Input);
                    long test2Time = System.currentTimeMillis() - test2StartTime;
                    test2Results[j] = test2Time;

                    long test3StartTime = System.currentTimeMillis();
                    mbc.findHull(test3Input);
                    long test3Time = System.currentTimeMillis() - test3StartTime;
                    test3Results[j] = test3Time;
                }
            }

            // Calculate the average for each test
            double test1Avg = getAverage(test1Results);
            double test2Avg = getAverage(test2Results);
            double test3Avg = getAverage(test3Results);

            // Calculate the standard deviation for each test
            double test1StdDev = getStandardDeviation(test1Results);
            double test2StdDev = getStandardDeviation(test2Results);
            double test3StdDev = getStandardDeviation(test3Results);

            // Write the results to the file
            String[] line = new String[] {
                Integer.toString(inputSizes[i]),
                Integer.toString(reruns),
                Double.toString(test1Avg),
                Double.toString(test1StdDev),
                Double.toString(test2Avg),
                Double.toString(test2StdDev),
                Double.toString(test3Avg),
                Double.toString(test3StdDev)
            };
            //bw.write(String.join(",", line) + "\r\n");
        }

        // Close the file
        bw.close();
    }

    private static double getAverage(long[] arr) {
        double avg = 0;

        for (int i = 0; i < arr.length; i++) {
            avg += ((double)1/arr.length) * arr[i];
        }

        return avg;
    }

    private static double getStandardDeviation(long[] arr) {
        double avg = getAverage(arr);

        double[] sqDiff = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            sqDiff[i] = Math.pow(arr[i] - avg, 2);
        }

        double variance = 0;
        for (int i = 0; i < sqDiff.length; i++) {
            variance += ((double)1/sqDiff.length) * sqDiff[i];
        }

        return Math.sqrt(variance);
    }

    private static List<Point2D> generateTest1Input(int inputSize) {
        List<Point2D> input = new ArrayList<>();

        for (int i = 0; i < inputSize; i++) {
            float x = rand.nextFloat() * 100;
            float y = rand.nextFloat() * 100;
            input.add(new Point2D(x, y, i+1));
        }

        return input;
    }

    private static List<Point2D> generateTest2Input(int inputSize) {
        List<Point2D> input = new ArrayList<>();

        for (int i = 0; i < inputSize; i++) {
            float rad = rand.nextFloat() * 50;
            float angle = rand.nextFloat() * 360;
            float x = (float) (50 + rad * Math.cos(angle));
            float y = (float) (50 + rad * Math.sin(angle));
            input.add(new Point2D(x, y, i+1));
        }

        return input;
    }

    private static List<Point2D> generateTest3Input(int inputSize) {
        List<Point2D> input = new ArrayList<>();

        for (int i = 0; i < inputSize; i++) {
            float x = rand.nextFloat() * 100;
            float y = (float) Math.pow(x,2);
            input.add(new Point2D(x, y, i+1));
        }

        return input;
    }

}
