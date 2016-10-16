import java.util.ArrayList;


public class MbC_CH {

	public ArrayList<Point2D> findHull(ArrayList<Point2D> pList) {
		
		ArrayList<Point2D> upper = findUpperHull(pList);
		ArrayList<Point2D> lower = findLowerHull(pList);
		upper.remove(upper.size()-1);		
		lower.remove(lower.size()-1);
		upper.addAll(lower);
		return upper;
	}
	
	public ArrayList<Point2D> findUpperHull(ArrayList<Point2D> pList) {
		
		// Use selection to find median element with value m
		float m = 10;
		ArrayList<Point2D> leftList = new ArrayList<Point2D>();
		ArrayList<Point2D> rightList = new ArrayList<Point2D>();
		for(Point2D p : pList) {
			if(p.x <= m) leftList.add(p);
			else rightList.add(p);
		}	
		
		ArrayList<Point2D> bridge = lp2(pList, m);
		Point2D left = bridge.get(0);
		Point2D right = bridge.get(1);
		
		ArrayList<Point2D> remove = new ArrayList<Point2D>();
		
		// Prune all points beneath the bridge
		for(Point2D p : pList) {
			if(p.x == left.x && p.id != left.id) leftList.remove(p);
			if(p.x > left.x && p.x <= right.x ) {
				remove.add(p);
			}
		}	
		rightList.removeAll(remove);	
		
		// MULIGT PROBLEM - ER BROEN TILFØJET ELLER TIL STEDE I BEGGE?
		
		ArrayList<Point2D> retLeft = findUpperHull(leftList);
		ArrayList<Point2D> retRight = findUpperHull(rightList);
		
		retLeft.addAll(retRight);
		return retLeft;
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
		
		Point2D left = null;
		Point2D right = null;
		for(Point2D p : pList) {
			if(p.y == a * p.x + b) {
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
		
		
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		return ret;
	}
}
