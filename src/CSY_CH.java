import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CSY_CH {

    /**
     * Finds the upper hull using Chan, Snoeyick and Yap’s convex hull algorithm.
     * @param p The input set of points.
     * @param p1 The point with the smallest x-coordinate in P.
     * @param p2 The point with the largest x-coordinate in P.
     * @return The upper hull of P.
     */
    public static List<Point2D> findUpperHull(ArrayList<Point2D> p,
                                              Point2D p1, Point2D p2) {
        // Discard any point that lies below the line going through p1 and p2
        for (Point2D pointInP : p) {
            if (!isAboveLine(p1, p2, pointInP)) p.remove(pointInP);
        }

        // Simply return the line if p is empty now
        if (p.isEmpty()) return Arrays.asList(p1, p2);

        // If p contains a single point then return the simple upper hull
        if (p.size() == 1) return Arrays.asList(p1, p.get(0), p2);

        // Create pairs of points along with their medians
        List<PairWithMedian> pointPairs = createPointPars(p);

        // Get the median of the array of medians
        double medianOfMedians = getMedian(pointPairs);

        // Find the point that maximizes p.y - medianOfMedians * p.x
        Point2D maxPoint = null;
        double lastMax = Double.MIN_VALUE;
        for (Point2D pointInP : p) {
            double val = pointInP.y - medianOfMedians * pointInP.x;
            if (val > lastMax) {
                maxPoint = pointInP;
                lastMax = val;
            }
        }

        // Partition the dataset into two lists (one containing points with
        // x-coordinates lower than maxPoint, and one containing points that
        // have a higher x-coordinate than maxPoint
        List<Point2D> leftPoints = new ArrayList<>();
        List<Point2D> rightPoints = new ArrayList<>();
        for (Point2D pointInP : p) {
            // Check which list the point should be partitioned into
            if (pointInP.x <= maxPoint.x) {

            } else {

            }
        }

        return null;
    }

    /**
     * Checks if the given point p is above the line going through p1 and p2.
     * @param p1 The first point of the line.
     * @param p2 The second point of the line.
     * @param p The point to check if it's above the line.
     * @return True if the point is above the line, false otherwise.
     */
    private static boolean isAboveLine(Point2D p1, Point2D p2, Point2D p) {
        double slope = (p2.y - p1.y) / (p2.x - p1.x);
        double b = slope * -p1.x + p1.y;
        return p.y > (slope * p.x + b);
    }

    /**
     * Creates a list of randomly paired points and their medians.
     * The points are ordered such that p1's x-coordinate is always lower
     * than p2's x-coordinate.
     * @param points The list of points to pair.
     * @return A list of randomly paired points and their medians.
     */
    private static List<PairWithMedian> createPointPars(List<Point2D> points) {
        Collections.shuffle(points); // Shuffle the list for randomness
        List<PairWithMedian> pointPairs = new ArrayList<>();
        for (int i = 0; i < points.size(); i = i+2) {
            // Store the pair such that the point with the lowest x-coordinate is on the left
            if (points.get(i).x < points.get(i+1).x) {
                pointPairs.add(new PairWithMedian(points.get(i), points.get(i+1)));
            } else {
                pointPairs.add(new PairWithMedian(points.get(i+1), points.get(i)));
            }
        }
        return pointPairs;
    }

    /**
     * Returns the median for a list of paired points and their medians.
     * @param pointPairs The list to search for the median in.
     * @return The index of the median for the list.
     */
    private static double getMedian(List<PairWithMedian> pointPairs) {
        // Sort the pairs based on their medians
        Collections.sort(pointPairs);

        // Check if the size of the list is even
        if (pointPairs.size() % 2 == 0) {
            // If the list is the even then the median is the average of the two
            // middle values
            return (pointPairs.get(pointPairs.size() / 2).median +
                    pointPairs.get(pointPairs.size()-1).median) / 2;
        } else {
            // If the list is odd then the median is the middle value
            return pointPairs.get(pointPairs.size() / 2).median;
        }
    }

    private static class PairWithMedian implements Comparable<PairWithMedian> {

        public Point2D p1;
        public Point2D p2;
        public double median;

        public PairWithMedian(Point2D p1, Point2D p2) {
            this.p1 = p1;
            this.p2 = p2;
            median = (p2.y - p1.y) / (p2.x - p1.x);
        }

        @Override
        public int compareTo(PairWithMedian other) {
            return Double.compare(median, other.median);
        }

    }

}
