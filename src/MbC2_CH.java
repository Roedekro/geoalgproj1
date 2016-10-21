import java.util.ArrayList;
import java.util.Collections;


public class MbC2_CH {

	// Tried 0.00001, but didnt work. Also tried 0.00005 and 0.0001
	public float ERROR_MARGIN = 0.001f;
	
	public ArrayList<Point2D> findHull(ArrayList<Point2D> pList) {
		
		// Copy because we remove from them
		@SuppressWarnings("unchecked")
		ArrayList<Point2D> copyUpper = (ArrayList<Point2D>) pList.clone();
		@SuppressWarnings("unchecked")
		ArrayList<Point2D> copyLower = (ArrayList<Point2D>) pList.clone();
		ArrayList<Point2D> upper = findUpperHull(copyUpper);
		//System.out.println("LOWER HULL! ===========");
		ArrayList<Point2D> lower = findLowerHull(copyLower);
		if(upper.size() > 0) upper.remove(upper.size()-1);		
		if(lower.size() > 0) lower.remove(0);
		Collections.reverse(lower);
		upper.addAll(lower);
		return upper;
	}
	
	public ArrayList<Point2D> findUpperHull(ArrayList<Point2D> pList) {
		
		// Use selection to find median element with value m
		Selection select = new Selection();
		int goal = pList.size() / 2;
		if(pList.size() % 2 != 0) goal = goal + 1;
		float m = select.select(pList, goal);
		
		// Split into left and right according to the median
		ArrayList<Point2D> leftList = new ArrayList<Point2D>();
		ArrayList<Point2D> rightList = new ArrayList<Point2D>();
		Point2D leftmost = null;
		Point2D rightmost = null;
		for(Point2D p : pList) {
			if(p.x <= m) leftList.add(p);
			else rightList.add(p);
			if(leftmost == null || p.x < leftmost.x) leftmost = p;
			if(rightmost == null || p.x > rightmost.x) rightmost = p;
		}
		
		// Prune points beneath the line between leftmost and rightmost
		float a = (rightmost.y-leftmost.y) / (rightmost.x - leftmost.x);	
		float b = leftmost.x*-1 * a; 
		b = b + leftmost.y; 
		
		ArrayList<Point2D> remove = new ArrayList<Point2D>();
		for(Point2D p : pList) {
			if(p.id != leftmost.id && p.id != rightmost.id) {
				float y = a * p.x + b;
				if(p.y <= y - ERROR_MARGIN) remove.add(p);	
			}	
		}
		
		pList.removeAll(remove);
		leftList.removeAll(remove);
		rightList.removeAll(remove);
		if(pList.size() == 1) {
			ArrayList<Point2D> ret = new ArrayList<Point2D>();
			ret.add(pList.get(0));
			return ret;
		}
		else if(pList.size() == 0) {
			ArrayList<Point2D> ret = new ArrayList<Point2D>();
			return ret;
		}
		
		ArrayList<Point2D> bridge = lp2(pList, m);
		Point2D left = bridge.get(0);
		Point2D right = bridge.get(1);
		
		//System.out.println("Found bridge between "+left.id+ " and "+right.id);
		
		// Prune all points beneath the bridge
		for(Point2D p : pList) {
			if(p.x >= left.x && p.id != left.id && p.x <= m) leftList.remove(p);
			if(p.x > m && p.id != right.id && p.x <= right.x ) {
				rightList.remove(p);
			}
		}		
		
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		if(leftList.size() <= 1) {
			//System.out.println("Added "+left.id);
			ret.add(left);
		}
		else {
			//System.out.println("Recursing left on size "+leftList.size()+" for "+left.id);
			ret.addAll(findUpperHull(leftList));
		}
		if(rightList.size() <= 1) {
			//System.out.println("Added "+right.id);
			ret.add(right);
		}
		else {
			//System.out.println("Recursing right on size "+rightList.size() +" for "+right.id);
			ret.addAll(findUpperHull(rightList));
		}

		return ret;
	}
	
	
	
	public ArrayList<Point2D> lp2(ArrayList<Point2D> pList, float m) {
		
		float a = 0;
		float b = 0;
		for(int i = 0; i < pList.size(); i++) {
			Point2D p = pList.get(i);
			// Initialize
			if(i == 0) {
				Point2D ab = lp1(pList,i,p,m);
				a = ab.x;
				b = ab.y;
			}
			else {
				if(p.y <= (a * p.x + b)) {
					// Nothing
				}
				else {
					// P violates a,b but luckily the new a,b
					// is on the line symbolized by p
					Point2D ab = lp1(pList,i,p,m);
					a = ab.x;
					b = ab.y;
				}
			}		
		}
		
		//System.out.println("For m="+m+" found a="+a+" and b="+b);
		
		Point2D left = null;
		Point2D right = null;
		for(Point2D p : pList) {
			float y = a * p.x + b;
			if(p.y == y
				|| (p.y < y && p.y + ERROR_MARGIN >= y)
				|| (p.y > y && p.y - ERROR_MARGIN <= y)) {
				if(left == null || p.x < left.x) {
					left = p;
				}
				if(right == null || p.x > right.x) {
					right = p;
				}
			}
		}
		
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		ret.add(left);
		ret.add(right);
		return ret;
	}
	
	
	/*
	 * 1-dimensional Linear Program
	 * Input: p defines a line along which we solve
	 * pList defines constraints on p, reducing the feasability region
	 * m is the x value along which we want to minimize the program
	 * Output: Point2D x = a, y = b.
	 * 
	 * Example the point 1,1 and m = 2, defines an equation
	 * 1 => a * 1 + b
	 * > 1 = a * 1 + b
	 * > b = a * -1 + 1
	 * This forms a line in the coordinate set of (a,b)
	 * Along which we place constraints.
	 * This is all bounded by a=-100 to a=100.
	 */
	
	public Point2D lp1(ArrayList<Point2D> pList, int n, Point2D p, float m) {
		
		// b = a * -x + y
		float x = p.x * -1;
		float y = p.y;
		
		float vmin = -100;
		float vmax = 100;
		// Run through all the points, making lines that represents them
		// Find the intersections of these lines with our main line
		// Update vmin and vmax as we go
		for(int i = 0; i < n; i++) {
			Point2D point = pList.get(i);
			float pX = point.x * -1;
			float pY = point.y;
			
			// a = (y - pY) / (pX - x)
			float intersection = (y - pY) / (pX - x);
			//System.out.println("Intersection found "+intersection);
			if(point.x > p.x && intersection > vmin) vmin = intersection;
			else if(point.x < p.x && intersection < vmax) vmax = intersection;

		}
		
		// Determine which of vmax and vmin minimizes	
		// C = a * m + b
		//System.out.println("Max="+vmax);
		//System.out.println("Min="+vmin);
		float bmin = vmin*x+y;
		float bmax = vmax*x+y;
		float cMin = vmin * m + bmin;
		float cMax = vmax * m + bmax;
		//System.out.println("CMax="+cMax);
		//System.out.println("CMin="+cMin);
		
		Point2D ret;
		if(cMin > cMax) ret = new Point2D(vmax,bmax,1);
		else ret = new Point2D(vmin,bmin,1);
		
		return ret;
		
	}
	
	public ArrayList<Point2D> findLowerHull(ArrayList<Point2D> pList) {
		
		// Use selection to find median element with value m
		Selection select = new Selection();
		int goal = pList.size() / 2;
		if(pList.size() % 2 != 0) goal = goal + 1;
		float m = select.select(pList, goal);
		
		// Split into left and right according to the median
		ArrayList<Point2D> leftList = new ArrayList<Point2D>();
		ArrayList<Point2D> rightList = new ArrayList<Point2D>();
		Point2D leftmost = null;
		Point2D rightmost = null;
		for(Point2D p : pList) {
			if(p.x <= m) leftList.add(p);
			else rightList.add(p);
			if(leftmost == null || p.x < leftmost.x) leftmost = p;
			if(rightmost == null || p.x > rightmost.x) rightmost = p;
		}
		
		// Prune points beneath the line between leftmost and rightmost
		float a = (rightmost.y-leftmost.y) / (rightmost.x - leftmost.x);	
		float b = leftmost.x*-1 * a; 
		b = b + leftmost.y; 
		
		ArrayList<Point2D> remove = new ArrayList<Point2D>();
		for(Point2D p : pList) {
			if(p.id != leftmost.id && p.id != rightmost.id) {
				float y = a * p.x + b;
				if(p.y >= y + ERROR_MARGIN) remove.add(p);
			}		
		}
		
		pList.removeAll(remove);
		leftList.removeAll(remove);
		rightList.removeAll(remove);
		if(pList.size() == 1) {
			ArrayList<Point2D> ret = new ArrayList<Point2D>();
			ret.add(pList.get(0));
			return ret;
		}
		else if(pList.size() == 0) {
			ArrayList<Point2D> ret = new ArrayList<Point2D>();
			return ret;
		}
		
		ArrayList<Point2D> bridge = lp2Lower(pList, m);
		Point2D left = bridge.get(0);
		Point2D right = bridge.get(1);
		
		//System.out.println("Found bridge between "+left.id+ " and "+right.id);
		
		// Prune all points above the bridge
		for(Point2D p : pList) {
			if(p.x >= left.x && p.id != left.id && p.x <= m) leftList.remove(p);
			if(p.x > m && p.id != right.id && p.x <= right.x ) {
				rightList.remove(p);
			}
		}		
		
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		if(leftList.size() <= 1) {
			//System.out.println("Added "+left.id);
			ret.add(left);
		}
		else {
			//System.out.println("Recursing left on size "+leftList.size()+" for "+left.id);
			ret.addAll(findLowerHull(leftList));
		}
		if(rightList.size() <= 1) {
			//System.out.println("Added "+right.id);
			ret.add(right);
		}
		else {
			//System.out.println("Recursing right on size "+rightList.size() +" for "+right.id);
			ret.addAll(findLowerHull(rightList));
		}

		return ret;
	}
	
	public ArrayList<Point2D> lp2Lower(ArrayList<Point2D> pList, float m) {
		
		float a = 0;
		float b = 0;
		for(int i = 0; i < pList.size(); i++) {
			Point2D p = pList.get(i);
			// Initialize
			if(i == 0) {
				Point2D ab = lp1Lower(pList,i,p,m);
				a = ab.x;
				b = ab.y;
			}
			else {
				if(p.y >= (a * p.x + b)) {
					// Nothing
				}
				else {
					// P violates a,b but luckily the new a,b
					// is on the line symbolized by p
					Point2D ab = lp1Lower(pList,i,p,m);
					a = ab.x;
					b = ab.y;
				}
			}		
		}
		
		//System.out.println("For m="+m+" found a="+a+" and b="+b);
		
		Point2D left = null;
		Point2D right = null;
		for(Point2D p : pList) {
			float y = a * p.x + b;
			if(p.y == y
				|| (p.y < y && p.y + ERROR_MARGIN >= y)
				|| (p.y > y && p.y - ERROR_MARGIN <= y)) {
				if(left == null || p.x < left.x) {
					left = p;
				}
				if(right == null || p.x > right.x) {
					right = p;
				}
			}
		}
		
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		ret.add(left);
		ret.add(right);
		return ret;
	}
	
	public Point2D lp1Lower(ArrayList<Point2D> pList, int n, Point2D p, float m) {
		
		// b = a * -x + y
		float x = p.x * -1;
		float y = p.y;
		
		float vmin = 100;
		float vmax = -100;
		// Run through all the points, making lines that represents them
		// Find the intersections of these lines with our main line
		// Update vmin and vmax as we go
		for(int i = 0; i < n; i++) {
			Point2D point = pList.get(i);
			float pX = point.x * -1;
			float pY = point.y;
			
			// a = (y - pY) / (pX - x)
			float intersection = (y - pY) / (pX - x);
			//System.out.println("Intersection found "+intersection);
			if(point.x > p.x && intersection < vmin) vmin = intersection;
			else if(point.x < p.x && intersection > vmax) vmax = intersection;

		}
		
		// Determine which of vmax and vmin minimizes	
		// C = a * m + b
		//System.out.println("Max="+vmax);
		//System.out.println("Min="+vmin);
		float bmin = vmin*x+y;
		float bmax = vmax*x+y;
		float cMin = vmin * m + bmin;
		float cMax = vmax * m + bmax;
		//System.out.println("CMax="+cMax);
		//System.out.println("CMin="+cMin);
		
		Point2D ret;
		if(cMin < cMax) ret = new Point2D(vmax,bmax,1);
		else ret = new Point2D(vmin,bmin,1);
		
		return ret;
		
	}
}
