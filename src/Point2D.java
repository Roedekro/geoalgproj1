
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

		return id == point2D.id;

	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "Point2D{" +
				"x=" + x +
				", y=" + y +
				", id=" + id +
				'}';
	}

}
