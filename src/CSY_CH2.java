import java.util.*;

public class CSY_CH2 {

    public static List<Point2D> findHull(List<Point2D> points) {
        // Get the left-most and right-most points
        Point2D minPoint = getLeftMostPoint(points);
        Point2D maxPoint = getRightMostPoint(points);

        // Create a duplicate list of points where the y-coordinate is inverted
        List<Point2D> invertedPoints = getInvertedPoints(points);
        Point2D invertedMinPoint = new Point2D(minPoint.x, -minPoint.y, minPoint.id);
        Point2D invertedMaxPoint = new Point2D(maxPoint.x, -maxPoint.y, maxPoint.id);

        // Get the lower and upper hull
        List<Point2D> lowerHull = findUpperHull(invertedPoints, invertedMinPoint, invertedMaxPoint);
        List<Point2D> upperHull = findUpperHull(points, minPoint, maxPoint);

        // Merge the two results and return the unique set of points
        lowerHull.addAll(upperHull);
        return new ArrayList<>(new HashSet<>(lowerHull));
    }

    private static Point2D getLeftMostPoint(List<Point2D> points) {
        Point2D minPoint = null;

        for (Point2D point : points) {
            if (minPoint == null || point.x < minPoint.x) {
                minPoint = point;
            }
        }

        return minPoint;
    }

    private static Point2D getRightMostPoint(List<Point2D> points) {
        Point2D maxPoint = null;

        for (Point2D point : points) {
            if (maxPoint == null || point.x > maxPoint.x) {
                maxPoint = point;
            }
        }

        return maxPoint;
    }

    private static List<Point2D> getInvertedPoints(List<Point2D> points) {
        List<Point2D> invertedPoints = new ArrayList<>();

        for (Point2D point : points) {
            invertedPoints.add(new Point2D(point.x, -point.y, point.id));
        }

        return invertedPoints;
    }

    /**
     * Finds the upper hull using Chan, Snoeyick and Yap’s convex hull algorithm.
     * @param points The input set of points.
     * @param left The point with the smallest x-coordinate in P.
     * @param right The point with the largest x-coordinate in P.
     * @return The upper hull of P.
     */
    public static List<Point2D> findUpperHull(List<Point2D> points,
                                              Point2D left, Point2D right) {
        // Prune any point that lies below the line going through p1 and p2
        List<Point2D> upperPoints = new ArrayList<>();
        for (Point2D point : points) {
            if (isAboveLine(left, right, point)) upperPoints.add(point);
        }

        // Simply return the line if upperPoints is empty
        if (upperPoints.isEmpty()) return new ArrayList<>(Arrays.asList(left, right));

        // If upperPoints contains a single point then return the simple upper hull
        if (upperPoints.size() == 1) return new ArrayList<>(Arrays.asList(left, upperPoints.get(0), right));

        // Randomly pair the points into n/2 pairs and get their slopes
        float[] pairSlopes = getRandomPairSlopes(upperPoints);

        // Find the median slope of all pairs
        float medianSlope = getMedian(pairSlopes);

        // Find the point that maximizes p.y - medianOfMedians * p.x
        Point2D maxSlopePoint = findMaxSlopePoint(upperPoints, medianSlope);

        // Partition the dataset into two lists (one containing points with
        // x-coordinates lower than maxPoint, and one containing points that
        // have a higher x-coordinate than maxPoint
        List<Point2D> leftPoints = new ArrayList<>();
        List<Point2D> rightPoints = new ArrayList<>();
        for (Point2D point : upperPoints) {
            // Check which list the point should be partitioned into
            if (point.x < maxSlopePoint.x) {
                leftPoints.add(point);
            } else if (point.x > maxSlopePoint.x) {
                rightPoints.add(point);
            }
        }

        // Prune the left points for the largest points in pairs with
        // slopes smaller than the median slope
        List<Point2D> prunedLeftPoints = new ArrayList<>(leftPoints);
        boolean[] leftSkips = new boolean[leftPoints.size()];
        for (int i = 0; i < (leftPoints.size() - 1) && !leftSkips[i]; i++) {
            for (int j = i + 1; (j < leftPoints.size()) && !leftSkips[j]; j++) {
                Pair pair = new Pair(leftPoints.get(i), leftPoints.get(j));
                if (pair.getSlope() < medianSlope) {
                    leftSkips[leftPoints.indexOf(pair.getLargestPoint())] = true;
                    prunedLeftPoints.remove(pair.getLargestPoint());
                }
            }
        }

        // Prune the right points for the smallest points in pairs with
        // slopes larger than the median slope
        List<Point2D> prunedRightPoints = new ArrayList<>(rightPoints);
        boolean[] rightSkips = new boolean[rightPoints.size()];
        for (int i = 0; (i < rightPoints.size() - 1) && !rightSkips[i] ; i++) {
            for (int j = i +1; (j < rightPoints.size()) && !rightSkips[j]; j++) {
                Pair pair = new Pair(rightPoints.get(i), rightPoints.get(j));
                if (pair.getSlope() > medianSlope) {
                    rightSkips[rightPoints.indexOf(pair.getSmallestPoint())] = true;
                    prunedRightPoints.remove(pair.getSmallestPoint());
                }
            }
        }

        // Check if the middle point is now the right-most point
        if (maxSlopePoint.equals(right)) return findUpperHull(prunedLeftPoints, left, right);

        // Check if the middle point is now the left-most point
        if (maxSlopePoint.equals(left)) return findUpperHull(rightPoints, left, right);

        // Recurse on both halves and merge the result
        List<Point2D> leftRecur = findUpperHull(leftPoints, left, maxSlopePoint);
        List<Point2D> rightRecur = findUpperHull(rightPoints, maxSlopePoint, right);
        leftRecur.addAll(rightRecur);
        return new ArrayList<>(new HashSet<>(leftRecur));
    }

    /**
     * Randomly pairs together n/2 points and returns their slopes.
     * @param points The points to pair together.
     * @return The slopes of all the pairs.
     */
    private static float[] getRandomPairSlopes(List<Point2D> points) {
        Collections.shuffle(points);

        float[] slopes = new float[points.size() / 2];
        for (int i = 0; i < points.size() - 1; i += 2) {
            Pair pair = new Pair(points.get(i), points.get(i+1));
            slopes[i/2] = pair.getSlope();
        }

        return slopes;
    }

    /**
     * Returns the median of an array of floats.
     * @param numbers The array to find the median from.
     * @return The median of the array.
     */
    private static float getMedian(float[] numbers) {
        Arrays.sort(numbers);

        float median;
        if (numbers.length % 2 == 0) {
            median = (numbers[numbers.length / 2] + numbers[(numbers.length - 1) / 2]) / 2;
        } else {
            median = numbers[numbers.length / 2];
        }

        return median;
    }

    /**
     * Finds the point in points closest to the median slope.
     * @param points The points to search.
     * @param medianSlope The median slope to come close to.
     * @return The point that maximizes the median slope.
     */
    private static Point2D findMaxSlopePoint(List<Point2D> points, float medianSlope) {
        Point2D maxSlopePoint = null;

        float lastMax = -Float.MAX_VALUE;
        for (Point2D point : points) {
            float val = point.y - medianSlope * point.x;
            if (val > lastMax) {
                maxSlopePoint = point;
                lastMax = val;
            }
        }

        return maxSlopePoint;
    }

    /**
     * Checks if the given point p is above the line going through p1 and p2.
     * @param p1 The first point of the line.
     * @param p2 The second point of the line.
     * @param p The point to check if it's above the line.
     * @return True if the point is above the line, false otherwise.
     */
    private static boolean isAboveLine(Point2D p1, Point2D p2, Point2D p) {
        float slope = (p2.y - p1.y) / (p2.x - p1.x);
        float b = slope * -p1.x + p1.y;
        return p.y > (slope * p.x + b);
    }

    /**
     * Represents a pair of Point2D points sorted based on
     * their x-coordinates.
     */
    private static class Pair implements Comparable<Pair> {

        private Point2D smallest;
        private Point2D largest;
        private float slope;

        public Pair(Point2D p1, Point2D p2) {
            // Determine which point is the smallest (in terms of x-coordinates)
            if (p1.x < p2.x) {
                smallest = p1;
                largest = p2;
            } else {
                smallest = p2;
                largest = p1;
            }

            // Calculate the slope between the two points
            if (p1.x == p2.x && p1.y == p2.y) {
                slope = 0;
            } else {
                slope = (largest.y - smallest.y) / (largest.x - smallest.x);
            }
        }

        public Point2D getSmallestPoint() {
            return smallest;
        }

        public Point2D getLargestPoint() {
            return largest;
        }

        public float getSlope() { return slope; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (!smallest.equals(pair.smallest)) return false;
            return largest.equals(pair.largest);
        }

        @Override
        public int hashCode() {
            int result = smallest.hashCode();
            result = 31 * result + largest.hashCode();
            return result;
        }

        @Override
        public int compareTo(Pair pair) {
            return Float.compare(slope, pair.getSlope());
        }

    }

}
