
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
}
