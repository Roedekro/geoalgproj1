import java.util.*;

public class CSY_CH {

    public static List<Point2D> findHull(List<Point2D> p) {
        // Find the left-most x-coordinate in the input
        Point2D minPoint = null;
        for (Point2D point : p) {
            if (minPoint == null || point.x < minPoint.x) {
                minPoint = point;
            }
        }

        // Find the right-most x-coordinate in the input
        Point2D maxPoint = null;
        for (Point2D point : p) {
            if (maxPoint == null || point.x > maxPoint.x) {
                maxPoint = point;
            }
        }

        // Create a duplicate list of points where the y-coordinate is inverted
        List<Point2D> invertedPoints = new ArrayList<>();
        for (Point2D point : p) {
            invertedPoints.add(new Point2D(point.x, -point.y, point.id));
        }
        Point2D invertedMinPoint = new Point2D(minPoint.x, -minPoint.y, minPoint.id);
        Point2D invertedMaxPoint = new Point2D(maxPoint.x, -maxPoint.y, maxPoint.id);

        // Get the lower and upper hull
        List<Point2D> lowerHull = findUpperHull(invertedPoints, invertedMinPoint, invertedMaxPoint);
        List<Point2D> upperHull = findUpperHull(p, minPoint, maxPoint);

        // Merge the two results and return the unique set of points
        lowerHull.addAll(upperHull);
        return new ArrayList<>(new HashSet<>(lowerHull));
    }

    /**
     * Finds the upper hull using Chan, Snoeyick and Yap’s convex hull algorithm.
     * @param p The input set of points.
     * @param p1 The point with the smallest x-coordinate in P.
     * @param p2 The point with the largest x-coordinate in P.
     * @return The upper hull of P.
     */
    public static List<Point2D> findUpperHull(List<Point2D> p,
                                              Point2D p1, Point2D p2) {
        // Discard any point that lies below the line going through p1 and p2
        List<Point2D> newP = new ArrayList<>();
        for (Point2D pointInP : p) {
            if (isAboveLine(p1, p2, pointInP)) newP.add(pointInP);
        }
        p = newP;

        // Simply return the line if p is empty now
        if (p.isEmpty()) return new ArrayList<>(Arrays.asList(p1, p2));

        // If p contains a single point then return the simple upper hull
        if (p.size() == 1) return new ArrayList<>(Arrays.asList(p1, p.get(0), p2));

        // Create pairs of points along with their slopes
        List<PairWithSlope> pointPairs = createHigherPointPairsWithSlopes(p);

        // Get the median of the array of slopes
        float medianOfMedians = getMedian(pointPairs);

        // Find the point that maximizes p.y - medianOfMedians * p.x
        Point2D maxPoint = null;
        float lastMax = -Float.MAX_VALUE;
        for (Point2D pointInP : p) {
            float val = pointInP.y - medianOfMedians * pointInP.x;
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
            if (pointInP.x < maxPoint.x) {
                leftPoints.add(pointInP);
            } else if (pointInP.x >= maxPoint.x) {
                rightPoints.add(pointInP);
            }
        }

        // Prune the left points for pairs with slopes larger than the slope slope
        Map<Pair,Float> leftPointsWithSlopes = getUpperSlopeForAllPairs(leftPoints);
        for (Map.Entry<Pair,Float> leftPointWithSlope : leftPointsWithSlopes.entrySet()) {
            if (leftPointWithSlope.getValue() < medianOfMedians) {
                leftPoints.remove(leftPointWithSlope.getKey().p2);
            }
        }

        // Prune the right points for pairs with slopes smaller than the slope slope
        Map<Pair,Float> rightPointsWithSlopes = getUpperSlopeForAllPairs(rightPoints);
        for (Map.Entry<Pair,Float> rightPointWithSlope : rightPointsWithSlopes.entrySet()) {
            if (rightPointWithSlope.getValue() > medianOfMedians) {
                rightPoints.remove(rightPointWithSlope.getKey().p1);
            }
        }

        // Check if the middle point is now the right-most point
        if (maxPoint.equals(p2)) return findUpperHull(leftPoints, p1, p2);

        // Check if the middle point is now the left-most point
        if (maxPoint.equals(p1)) return findUpperHull(rightPoints, p1, p2);

        // Recurse on both halves and merge the result
        List<Point2D> leftRecur = findUpperHull(leftPoints, p1, maxPoint);
        List<Point2D> rightRecur = findUpperHull(rightPoints, maxPoint, p2);
        leftRecur.addAll(rightRecur);
        return new ArrayList<>(new HashSet<>(leftRecur));
    }

    private static Map<Pair,Float> getUpperSlopeForAllPairs(List<Point2D> points) {
        Map<Pair,Float> pairSlopes = new HashMap<>();

        for (int i = 0; i < points.size() - 1; i++) {
            for (int j = i + 1; j < points.size(); j++) {
                if (points.get(i).x < points.get(j).x) {
                    float slope = (points.get(j).y - points.get(i).y) / (points.get(j).x - points.get(i).x);
                    pairSlopes.put(new Pair(points.get(i), points.get(j)), slope);
                } else if (points.get(i).x > points.get(j).x) {
                    float slope = (points.get(i).y - points.get(j).y) / (points.get(i).x - points.get(j).x);
                    pairSlopes.put(new Pair(points.get(j), points.get(i)), slope);
                }
            }
        }

        return pairSlopes;
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
     * Creates a list of randomly paired points and their slopes.
     * The points are ordered such that p1's x-coordinate is always lower
     * than p2's x-coordinate.
     * @param points The list of points to pair.
     * @return A list of randomly paired points and their slopes.
     */
    private static List<PairWithSlope> createHigherPointPairsWithSlopes(List<Point2D> points) {
        Collections.shuffle(points); // Shuffle the list for randomness

        List<PairWithSlope> pointPairs = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i += 2) {
            // Store the pair such that the point with the lowest x-coordinate is on the left
            if (points.get(i).x < points.get(i+1).x) {
                pointPairs.add(new PairWithSlope(points.get(i), points.get(i+1)));
            } else if (points.get(i).x > points.get(i+1).x){
                pointPairs.add(new PairWithSlope(points.get(i+1), points.get(i)));
            }
        }

        return pointPairs;
    }

    /**
     * Returns the median for a list of paired points and their slopes.
     * @param pointPairs The list to search for the median in.
     * @return The median for the list.
     */
    private static float getMedian(List<PairWithSlope> pointPairs) {
        // Sort the pairs based on their medians
        Collections.sort(pointPairs);

        // Check if the size of the list is even
        if (pointPairs.size() % 2 == 0) {
            // If the list is the even then the median is the average of the two
            // middle values
            return (pointPairs.get(pointPairs.size() / 2).slope +
                    pointPairs.get(pointPairs.size()-1).slope) / 2;
        } else {
            // If the list is odd then the median is the middle value
            return pointPairs.get(pointPairs.size() / 2).slope;
        }
    }

    private static class PairWithSlope implements Comparable<PairWithSlope> {

        public Point2D p1;
        public Point2D p2;
        public float slope;

        public PairWithSlope(Point2D p1, Point2D p2) {
            this.p1 = p1;
            this.p2 = p2;
            slope = (p2.y - p1.y) / (p2.x - p1.x);
        }

        @Override
        public int compareTo(PairWithSlope other) {
            return Float.compare(slope, other.slope);
        }

    }

    private static class Pair {

        public Point2D p1;
        public Point2D p2;

        public Pair(Point2D p1, Point2D p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (!p1.equals(pair.p1)) return false;
            return p2.equals(pair.p2);

        }

        @Override
        public int hashCode() {
            int result = p1.hashCode();
            result = 31 * result + p2.hashCode();
            return result;
        }

    }

}
