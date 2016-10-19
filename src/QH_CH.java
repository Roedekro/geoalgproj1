import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class QH_CH {

	
	public ArrayList<Point2D> findHull(List<Point2D> p) {
		
		// Sort to find smallest and largest x
		//Arrays.sort(p);
		
		Collections.sort(p);
		
		Point2D p1, p2;
		p1 = p.get(0);
		p2 = p.get(p.size()-1);
		p.remove(p.size()-1);
		p.remove(0);
		
		// Copy for lower hull
		ArrayList<Point2D> pA2 = new ArrayList<Point2D>();
		pA2.addAll(p);
		
		ArrayList<Point2D> upper = QuickUpperHull(p1,p2,p);
		ArrayList<Point2D> lower = QuickLowerHull(p1,p2,pA2);
		
		/*System.out.println("P1 is " +p1.id);
		for(int i = 0; i < upper.size(); i++) {
			System.out.println(upper.get(i).id);
		}
		System.out.println("P2 is " +p2.id);
		for(int i = 0; i < lower.size(); i++) {
			System.out.println(lower.get(i).id);
		}*/
		
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		ret.add(p1);
		Collections.sort(upper);
		ret.addAll(upper);
		ret.add(p2);
		Collections.sort(lower);
		Collections.reverse(lower);
		ret.addAll(lower);
		
		return ret;
		
	}
	
	public ArrayList<Point2D> QuickUpperHull(Point2D p1, Point2D p2, List<Point2D> p) {
		
		//System.out.println("Upper Hull for p1= "+p1.id+" and p2="+p2.id);
		
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		
		// Calculate the line between p1 and p2
		
		// First find the slope
		float a = (p2.y-p1.y) / (p2.x - p1.x);
		
		// Now use the formula y - y1 = a ( x - x1)
		float b = p1.x*-1 * a; // y - y1 = a x - a x1
		b = b + p1.y; // y = a x - a x1 + y1
		
		// Now scan through p, finding the point above the line with the largest
		// distance to the line
		
		Point2D max = null;
		float maxDistance = 0;
		ArrayList<Point2D> remove = new ArrayList<Point2D>();
		for(int i = 0; i < p.size(); i++) {
			Point2D current = p.get(i);
			float y = current.x * a + b;
			if(current.y > y) {
				// Calculate distance to line
				// Take 2x the area of the triangle formed by the 3 points, 
				// and divide by distance of the line.
				// See https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
				float d = (float) (Math.abs((p2.y-p1.y)*current.x - (p2.x-p1.x)*current.y + p2.x*p1.y
						- p2.y*p1.y) / Math.sqrt(Math.pow(p2.y-p1.y, 2)+Math.pow(p2.x-p1.x, 2)));
				//System.out.println("Point "+current.id+" d="+d);
				if(d > maxDistance) {
					maxDistance = d;
					max = current;
				}
			}
			else {
				// Point is on or below the line
				// Add to remove, to be pruned afterwards
				//System.out.println("Removed "+current.id);
				remove.add(current);
			}
		}
		
		p.remove(max);
		p.removeAll(remove);
		
		// Done?
		if(max == null) return ret;
		
		//System.out.println("Found max="+max.id +" for p1="+p1.id+" and p2="+p2.id);
		
		// Calculate the line from p1 to max, and from max to p2
		float maxa1 = (max.y-p1.y) / (max.x - p1.x);
		float maxa2 = (p2.y-max.y) / (p2.x - max.x);
		
		float maxb1 = p1.x*-1 * maxa1; // y - y1 = a x - a x1
		maxb1 = maxb1 + p1.y; // y = a x - a x1 + y1
		
		float maxb2 = max.x*-1 * maxa2; // y - y1 = a x - a x1
		maxb2 = maxb2 + max.y; // y = a x - a x1 + y1
		
		
		ArrayList<Point2D> left = new ArrayList<Point2D>();
		ArrayList<Point2D> right = new ArrayList<Point2D>();
		// Prune p so that points within the triangle is removed
		for(int i = 0; i < p.size(); i++) {
			Point2D current = p.get(i);
			if(current.x <= max.x) {
				float y1 = current.x * maxa1 + maxb1;
				if(current.y > y1) {
					left.add(current);
				}
				else {
					//System.out.println("Removed "+current.id);
				}
			}
			else {
				float y2 = current.x * maxa2 + maxb2;
				if(current.y > y2) {
					right.add(current);
				}
				else {
					//System.out.println("Removed "+current.id);
				}
			}			
		}
		
		// Add it all up, recurse if necessary
		ret.add(max);
		if(left.size() > 0) {
			ret.addAll(QuickUpperHull(p1, max, left));
		}
		if(right.size() > 0) {
			ret.addAll(QuickUpperHull(max, p2, right));
		}
		
		return ret;
		
	}
	
	public ArrayList<Point2D> QuickLowerHull(Point2D p1, Point2D p2, List<Point2D> p) {
		
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		
		// Calculate the line between p1 and p2
		
		// First find the slope
		float a = (p2.y-p1.y) / (p2.x - p1.x);
		
		// Now use the formula y - y1 = a ( x - x1)
		float b = p1.x*-1 * a; // y - y1 = a x - a x1
		b = b + p1.y; // y = a x - a x1 + y1
		
		// Now scan through p, finding the point below the line with the largest
		// distance to the line
		
		Point2D max = null;
		float maxDistance = 0;
		ArrayList<Point2D> remove = new ArrayList<Point2D>();
		for(int i = 0; i < p.size(); i++) {
			Point2D current = p.get(i);
			float y = current.x * a + b;
			if(current.y < y) {
				// Calculate distance to line
				// Take 2x the area of the triangle formed by the 3 points, 
				// and divide by distance of the line.
				// See https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
				float d = (float) (Math.abs((p2.y-p1.y)*current.x - (p2.x-p1.x)*current.y + p2.x*p1.y
						- p2.y*p1.y) / Math.sqrt(Math.pow(p2.y-p1.y, 2)+Math.pow(p2.x-p1.x, 2)));
				if(d > maxDistance) {
					maxDistance = d;
					max = current;
				}
			}
			else {
				// Point is on or above the line
				// Add to remove, to be pruned afterwards
				remove.add(current);
			}
		}
		
		p.remove(max);
		p.removeAll(remove);
		
		// Done?
		if(max == null) return ret;
		
		// Calculate the line from p1 to max, and from max to p2
		float maxa1 = (max.y-p1.y) / (max.x - p1.x);
		float maxa2 = (p2.y-max.y) / (p2.x - max.x);
		
		float maxb1 = p1.x*-1 * maxa1; // y - y1 = a x - a x1
		maxb1 = maxb1 + p1.y; // y = a x - a x1 + y1
		
		float maxb2 = max.x*-1 * maxa2; // y - y1 = a x - a x1
		maxb2 = maxb2 + max.y; // y = a x - a x1 + y1
		
		
		ArrayList<Point2D> left = new ArrayList<Point2D>();
		ArrayList<Point2D> right = new ArrayList<Point2D>();
		// Prune p so that points within the triangle is removed
		for(int i = 0; i < p.size(); i++) {
			Point2D current = p.get(i);
			if(current.x <= max.x) {
				float y1 = current.x * maxa1 + maxb1;
				if(current.y < y1) {
					left.add(current);
				}
			}
			else {
				float y2 = current.x * maxa2 + maxb2;
				if(current.y < y2) {
					right.add(current);
				}
			}			
		}
		
		// Add it all up, recurse if necessary
		ret.add(max);
		if(left.size() > 0) {
			ret.addAll(QuickLowerHull(p1, max, left));
		}
		if(right.size() > 0) {
			ret.addAll(QuickLowerHull(max, p2, right));
		}
		
		return ret;
		
	}
}
