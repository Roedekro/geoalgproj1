
public class Point2D implements Comparable<Point2D> {
	
	float x;
	float y;
	int id;
	
	public Point2D (float x, float y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public int compareTo(Point2D other) {
		if(x == other.x) {
			return Float.compare(y, other.y);
		}
		else {
			return Float.compare(x, other.x);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Point2D point2D = (Point2D) o;

		if (Float.compare(point2D.x, x) != 0) return false;
		return Float.compare(point2D.y, y) == 0;

	}

	@Override
	public int hashCode() {
		int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
		result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
		return result;
	}

}
